package io.github.ultreon.controllerx.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.ultreon.libs.commons.v0.Either;
import com.ultreon.libs.functions.v0.misc.Mapper;
import io.github.libsdl4j.api.gamecontroller.SDL_GameController;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerButton;
import io.github.libsdl4j.api.gamecontroller.SdlGamecontroller;
import io.github.ultreon.controllerx.Config;
import io.github.ultreon.controllerx.ControllerX;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.BitSet;

import static io.github.libsdl4j.api.event.SdlEventsConst.SDL_PRESSED;
import static io.github.libsdl4j.api.gamecontroller.SdlGamecontroller.*;

@SuppressWarnings("MagicConstant")
public class ControllerInput extends Input {
    private static final Vector2f ZERO = new Vector2f();
    private final Vector2f leftStick = new Vector2f();
    private final Vector2f rightStick = new Vector2f();

    private final BitSet pressedButtons = new BitSet(SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX);
    private final BitSet justPressedButtons = new BitSet(ControllerButton.values().length);
    private boolean leftTriggerJustPressed = false;
    private boolean rightTriggerJustPressed = false;
    private SDL_GameController sdlController;
    private Controller controller;
    private final float[] axes = new float[ControllerAxis.values().length];
    private float delay;
    private long nextTick;

    public void update() {
        for (int i = 0; i < SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX; i++) {
            this.justPressedButtons.set(i, false);
        }

        if (this.sdlController == null) {
            this.setController(0);
            if (this.sdlController == null) {
                return;
            }
        }

        SDL_GameControllerUpdate();

        for (int idx = 0; idx < SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX; idx++) {
            this.justPressedButtons.set(idx, false);
            boolean wasPressedBefore = this.pressedButtons.get(idx);
            boolean pressed = SDL_GameControllerGetButton(this.sdlController, idx) == SDL_PRESSED;
            this.pressedButtons.set(idx, pressed);

            if (pressed && !wasPressedBefore) {
                this.justPressedButtons.set(idx, true);
            }
        }

        for (int i = 0; i < ControllerAxis.values().length; i++) {
            ControllerAxis axis = ControllerAxis.values()[i];
            if (axis == ControllerAxis.LEFT_TRIGGER) {
                this.leftTriggerJustPressed = false;
                boolean wasPressedBefore = this.axes[i] > 0;
                boolean pressed = getAxis(axis) > 0;

                if (pressed && !wasPressedBefore) {
                    this.leftTriggerJustPressed = true;
                }
            }
            if (axis == ControllerAxis.RIGHT_TRIGGER) {
                this.rightTriggerJustPressed = false;
                boolean wasPressedBefore = this.axes[i] > 0;
                boolean pressed = getAxis(axis) > 0;

                if (pressed && !wasPressedBefore) {
                    this.rightTriggerJustPressed = true;
                }
            }
            this.axes[i] = getAxis(axis);
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Screen screen = mc.screen;

        this.leftStick.x = this.getAxis(ControllerAxis.LEFT_STICK_X);
        this.leftStick.y = this.getAxis(ControllerAxis.LEFT_STICK_Y);
        this.rightStick.x = this.getAxis(ControllerAxis.RIGHT_STICK_X);
        this.rightStick.y = this.getAxis(ControllerAxis.RIGHT_STICK_Y);
        double leftDeg = (Math.toDegrees(ZERO.angle(leftStick)) + 360) % 360;
        if (player != null && screen == null) {
            if (this.sdlController != null) {

                this.forwardImpulse = -this.leftStick.y;
                this.leftImpulse = -this.leftStick.x;

                player.setXRot((float) (player.getXRot() + this.rightStick.y * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10));
                player.setYRot((float) (player.getYRot() + this.rightStick.x * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10));

                jumping = this.isButtonPressed(ControllerButton.A);
                shiftKeyDown = this.isButtonPressed(ControllerButton.LEFT_STICK);
            }

            for (KeyMapping keyMapping : mc.options.keyMappings) {
                Boolean b = this.doInput(mc, keyMapping, eitherAxisOrBtn -> {
                    if (eitherAxisOrBtn.isLeftPresent()) {
                        ControllerAxis controllerAxis = eitherAxisOrBtn.getLeft();
                        if (controllerAxis == ControllerAxis.LEFT_TRIGGER) {
                            return this.leftTriggerJustPressed;
                        }
                        if (controllerAxis == ControllerAxis.RIGHT_TRIGGER) {
                            return this.rightTriggerJustPressed;
                        }
                        return null;
                    }
                    if (eitherAxisOrBtn.isRightPresent()) {
                        ControllerButton controllerButton = eitherAxisOrBtn.getRight();
                        return this.isButtonJustPressed(controllerButton);
                    }
                    return null;
                });

                if (b == Boolean.TRUE) {
                    MixinClickHandler clickHandler = (MixinClickHandler) keyMapping;
                    clickHandler.controllerX$handleClick();
                }
            }

            if (isButtonJustPressed(ControllerButton.BACK)) {
                mc.setScreen(new PauseScreen(true));
            }
        }
        if (screen != null) {
            handleScreen(mc, player, screen, leftDeg, this.axes[ControllerAxis.LEFT_STICK_X.ordinal()] != 0 || this.axes[ControllerAxis.LEFT_STICK_Y.ordinal()] != 0);
        }
    }

    private void handleScreen(Minecraft mc, LocalPlayer player, Screen screen, double leftDeg, boolean axisUsed) {
        if (player != null && screen instanceof InventoryScreen && isButtonJustPressed(ControllerButton.Y)) {
            player.closeContainer();
        }

        if (isButtonJustPressed(ControllerButton.A)) {
            screen.keyPressed(InputConstants.KEY_RETURN, 0, 0);
            screen.keyReleased(InputConstants.KEY_RETURN, 0, 0);
        }

        if (isButtonJustPressed(ControllerButton.B)) {
            screen.keyPressed(InputConstants.KEY_ESCAPE, 0, 0);
            screen.keyReleased(InputConstants.KEY_ESCAPE, 0, 0);
        } else if (isButtonJustPressed(ControllerButton.DPAD_UP)) {
            screen.keyPressed(InputConstants.KEY_UP, 0, 0);
            screen.keyReleased(InputConstants.KEY_UP, 0, 0);
        } else if (isButtonJustPressed(ControllerButton.DPAD_LEFT)) {
            screen.keyPressed(InputConstants.KEY_LEFT, 0, 0);
            screen.keyReleased(InputConstants.KEY_LEFT, 0, 0);
        } else if (isButtonJustPressed(ControllerButton.DPAD_DOWN)) {
            screen.keyPressed(InputConstants.KEY_DOWN, 0, 0);
            screen.keyReleased(InputConstants.KEY_DOWN, 0, 0);
        } else if (isButtonJustPressed(ControllerButton.DPAD_RIGHT)) {
            screen.keyPressed(InputConstants.KEY_RIGHT, 0, 0);
            screen.keyReleased(InputConstants.KEY_RIGHT, 0, 0);
        }

        if (nextTick < System.currentTimeMillis()) {
            if (delay > 0) {
                delay--;
                return;
            }
            nextTick = System.currentTimeMillis() + 50;
        } else {
            return;
        }

        if (leftStick.y < 0 && isYAxis()) {
            screen.keyPressed(InputConstants.KEY_UP, 0, 0);
            screen.keyReleased(InputConstants.KEY_UP, 0, 0);
            delay = 10;
        } else if (leftStick.x < 0 && isXAxis()) {
            screen.keyPressed(InputConstants.KEY_LEFT, 0, 0);
            screen.keyReleased(InputConstants.KEY_LEFT, 0, 0);
            delay = 10;
        } else if (leftStick.y > 0 && isYAxis()) {
            screen.keyPressed(InputConstants.KEY_DOWN, 0, 0);
            screen.keyReleased(InputConstants.KEY_DOWN, 0, 0);
            delay = 10;
        } else if (leftStick.x > 0 && isXAxis()) {
            screen.keyPressed(InputConstants.KEY_RIGHT, 0, 0);
            screen.keyReleased(InputConstants.KEY_RIGHT, 0, 0);
            delay = 10;
        }
    }

    private boolean isYAxis() {
        return Math.abs(leftStick.x) <= Math.abs(leftStick.y);
    }

    private boolean isXAxis() {
        return Math.abs(leftStick.x) > Math.abs(leftStick.y);
    }

    public float getAxis(ControllerAxis controllerAxis) {
        int axis = controllerAxis.sdlAxis();
        if (axis == -1) return 0f;
        float v = SDL_GameControllerGetAxis(sdlController, axis) / 32767f;

        float deadZone = Config.get().axisDeadZone;
        int signum = v > 0 ? 1 : -1;
        v = Math.abs(v);
        if (v < deadZone) {
            v = Math.max(0, (v - deadZone) / (1 - deadZone)) * signum;
        } else {
            v *= signum;
        }

        return v;
    }

    @SuppressWarnings("SameParameterValue")
    private void setController(int deviceIndex) {
        if (this.sdlController != null && !SdlGamecontroller.SDL_GameControllerGetAttached(sdlController)) {
            unsetController(deviceIndex);
            return;
        }
        this.sdlController = SDL_GameControllerOpen(deviceIndex);
        if (sdlController == null) return;

        short productId = SDL_GameControllerGetProduct(sdlController);
        short vendorId = SDL_GameControllerGetVendor(sdlController);
        String name = SDL_GameControllerName(sdlController);
        String mapping = SDL_GameControllerMapping(sdlController);

        this.controller = new Controller(sdlController, deviceIndex, productId, vendorId, name, mapping);

        ControllerEvent.CONTROLLER_CONNECTED.invoker().onConnectionStatus(this.controller);

        ControllerX.LOGGER.info("Controller {} connected", name);
    }

    private void unsetController(int deviceIndex) {
        if (controller == null || controller.deviceIndex() != deviceIndex) return;

        this.sdlController = null;
        this.controller = null;
        ControllerEvent.CONTROLLER_DISCONNECTED.invoker().onConnectionStatus(this.controller);

        ControllerX.LOGGER.info("Controller disconnected");

    }

    private void onButton(ControllerButton button, boolean pressed) {
        boolean wasPressedBefore = this.pressedButtons.get(button.ordinal());
        this.pressedButtons.set(button.ordinal(), pressed);

        if (pressed && !wasPressedBefore) {
            this.justPressedButtons.set(button.ordinal(), true);

            ControllerX.LOGGER.info("Button {} pressed", button);
        }

        ControllerEvent.CONTROLLER_BUTTON.invoker().onButton(button, pressed);
    }

    private void onAxis(ControllerAxis axis, float v) {
        switch (axis) {
            case LEFT_STICK_X -> this.leftStick.x = v;
            case LEFT_STICK_Y -> this.leftStick.y = v;
            case RIGHT_STICK_X -> this.rightStick.x = v;
            case RIGHT_STICK_Y -> this.rightStick.y = v;
            default -> {
            }
        }

        ControllerEvent.CONTROLLER_AXIS.invoker().onAxis(axis, v);
    }

    public @Nullable Controller getController() {
        return controller;
    }

    public @Nullable SDL_GameController getSDLController() {
        return sdlController;
    }

    public boolean isButtonPressed(ControllerButton button) {
        int idx = button.sdlButton();
        return SDL_GameControllerGetButton(sdlController, idx) == SDL_PRESSED;
    }

    public boolean isButtonJustPressed(ControllerButton button) {
        int idx = button.sdlButton();
        return this.justPressedButtons.get(idx);
    }

    public boolean isConnected() {
        return controller != null;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Boolean doInput(Minecraft mc, KeyMapping mapping, Mapper<Either<ControllerAxis, ControllerButton>, Boolean> controllerMapper) {
        if (mapping == mc.options.keyAttack) {
            return controllerMapper.map(Either.left(ControllerAxis.RIGHT_TRIGGER));
        }
        if (mapping == mc.options.keyUse) {
            return controllerMapper.map(Either.left(ControllerAxis.LEFT_TRIGGER));
        }
        if (mapping == mc.options.keyPickItem) {
            return controllerMapper.map(Either.right(ControllerButton.DPAD_UP));
        }
        if (mapping == mc.options.keyDrop) {
            return controllerMapper.map(Either.right(ControllerButton.DPAD_DOWN));
        }
        if (mapping == mc.options.keyPlayerList) {
            return controllerMapper.map(Either.right(ControllerButton.DPAD_LEFT));
        }
        if (mapping == mc.options.keyChat) {
            return controllerMapper.map(Either.right(ControllerButton.DPAD_RIGHT));
        }
        if (mapping == mc.options.keyInventory) {
            return controllerMapper.map(Either.right(ControllerButton.Y));
        }
        if (mapping == mc.options.keyShift) {
            return controllerMapper.map(Either.right(ControllerButton.RIGHT_STICK));
        }
        if (mapping == mc.options.keySwapOffhand) {
            return controllerMapper.map(Either.right(ControllerButton.X));
        }
        if (mapping == mc.options.keySprint) {
            return controllerMapper.map(Either.right(ControllerButton.LEFT_STICK));
        }
        if (mapping == mc.options.keyScreenshot) {
            return controllerMapper.map(Either.right(ControllerButton.MISC1));
        }
        return null;
    }
}
