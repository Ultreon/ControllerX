package io.github.ultreon.controllerx.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.ultreon.libs.commons.v0.Either;
import com.ultreon.libs.functions.v0.misc.Mapper;
import io.github.libsdl4j.api.gamecontroller.SDL_GameController;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerButton;
import io.github.ultreon.controllerx.*;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.gui.ControllerInputHandler;
import io.github.ultreon.controllerx.gui.widget.ItemSlot;
import io.github.ultreon.controllerx.impl.*;
import io.github.ultreon.controllerx.input.keyboard.KeyboardLayout;
import io.github.ultreon.controllerx.mixin.accessors.AbstractSelectionListAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.BitSet;

import static io.github.libsdl4j.api.event.SdlEventsConst.SDL_PRESSED;
import static io.github.libsdl4j.api.gamecontroller.SdlGamecontroller.*;

@SuppressWarnings("MagicConstant")
public class ControllerInput extends Input {
    private final Vector2f leftStick = new Vector2f();
    private final Vector2f rightStick = new Vector2f();

    private final BitSet pressedButtons = new BitSet(SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX);
    private final BitSet justPressedButtons = new BitSet(ControllerButton.values().length);
    private boolean leftTriggerJustPressed = false;
    private boolean rightTriggerJustPressed = false;
    private SDL_GameController sdlController;
    private Controller controller;
    private final float[] oldAxes = new float[ControllerAxis.values().length];
    private final float[] axes = new float[ControllerAxis.values().length];
    private float delay;
    private long nextTick;
    private final ControllerX mod;
    private KeyboardLayout layout;
    private String virtualKeyboardValue = "";
    private boolean virtualKeyboardOpen;
    private long nextTickDpad;
    private int delayDpad;
    private boolean dPadDisabled;
    private int destroyDelay = 0;
    private boolean screenWasOpen;

    public ControllerInput(ControllerX mod) {
        this.mod = mod;

        pollEvents();
    }

    @ApiStatus.Internal
    public void update() {
        if (pollEvents()) return;

        Minecraft mc = Minecraft.getInstance();

        if (mod.controllerInput.isVirtualKeyboardOpen()) {
            this.handleScreen(mc.player, mod.virtualKeyboard.getScreen());
            return;
        }

        if (screenWasOpen) {
            screenWasOpen = false;
            return;
        }

        this.leftStick.set(this.getJoystick(ControllerJoystick.Left));
        this.rightStick.set(this.getJoystick(ControllerJoystick.Right));
        if ((ControllerContext.get()) instanceof InGameControllerContext context) {
            LocalPlayer player = context.player();

            this.leftImpulse = -context.movePlayer.action().get2DValue().x;
            this.forwardImpulse = -context.movePlayer.action().get2DValue().y;

            player.setXRot((float) (player.getXRot() + context.lookPlayer.action().get2DValue().y * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10));
            player.setYRot((float) (player.getYRot() + context.lookPlayer.action().get2DValue().x * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10));

            jumping = context.jump.action().isPressed();
            shiftKeyDown = context.sneak.action().isPressed();

            if (context.use.action().isPressed() && GameApi.getRightClickDelay() == 0) {
                GameApi.startUseItem();
            }

            if (context instanceof BlockTargetControllerContext blockCtx) {
                boolean flag = false;
                if (blockCtx.destroyBlock.action().isJustPressed()) {
                    flag = GameApi.startAttack();
                }
                if (blockCtx.destroyBlock.action().isPressed()) {
                    if (player.getAbilities().mayBuild && !player.getAbilities().instabuild)
                        destroyDelay = 0;
                    if (!player.getAbilities().mayBuild) {
                        return;
                    }
                    GameApi.continueAttack(flag && destroyDelay == 0);
                    if (destroyDelay-- < 0)
                        destroyDelay = 0;
                }
            }
            if (context instanceof EntityTargetControllerContext entityCtx) {
                boolean flag = false;
                if (entityCtx.attack.action().isJustPressed()) {
                    flag = GameApi.startAttack();
                }
                if (entityCtx.attack.action().isPressed()) {
                    GameApi.continueAttack(flag);
                }
            }

            for (KeyMapping keyMapping : mc.options.keyMappings) {
                Boolean b = this.doInput(mc, keyMapping, eitherAxisOrBtn -> {
                    if (eitherAxisOrBtn.isLeftPresent()) {
                        ControllerAxis controllerAxis = eitherAxisOrBtn.getLeft();
                        if (controllerAxis == ControllerAxis.LeftTrigger) {
                            return this.leftTriggerJustPressed;
                        }
                        if (controllerAxis == ControllerAxis.RightTrigger) {
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

            if (context.gameMenu.action().isJustPressed()) {
                mc.setScreen(new PauseScreen(true));
            }
        } else {
            this.leftStick.set(0, 0);
            this.rightStick.set(0, 0);
            this.forwardImpulse = 0;
            this.leftImpulse = 0;
            this.jumping = false;
            this.shiftKeyDown = false;
        }
    }

    private boolean pollEvents() {
        for (int i = 0; i < SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX; i++) {
            this.justPressedButtons.set(i, false);
        }

        SDL_GameControllerUpdate();

        if (this.sdlController == null) {
            this.setController(0);
            if (this.sdlController == null) {
                return true;
            }
        }

        if (!SDL_GameControllerGetAttached(sdlController)) {
            unsetController(0);
            return true;
        }

        for (int idx = 0; idx < SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX; idx++) {
            this.justPressedButtons.set(idx, false);
            boolean wasPressedBefore = this.pressedButtons.get(idx);
            boolean pressed = SDL_GameControllerGetButton(this.sdlController, idx) == SDL_PRESSED;
            this.pressedButtons.set(idx, pressed);

            if (pressed) {
                ControllerX.get().setInputType(InputType.CONTROLLER);
            }

            if (pressed && !wasPressedBefore) {
                this.justPressedButtons.set(idx, true);
            }
        }

        for (int i = 0; i < ControllerAxis.values().length; i++) {
            ControllerAxis axis = ControllerAxis.values()[i];
            float axisValue = getAxis(axis);
            this.oldAxes[i] = this.axes[i];
            if (axis == ControllerAxis.LeftTrigger) {
                this.leftTriggerJustPressed = false;
                boolean wasPressedBefore = this.axes[i] > 0;
                boolean pressed = axisValue > 0;

                if (pressed && !wasPressedBefore) {
                    this.leftTriggerJustPressed = true;
                }
            }
            if (axis == ControllerAxis.RightTrigger) {
                this.rightTriggerJustPressed = false;
                boolean wasPressedBefore = this.axes[i] > 0;
                boolean pressed = axisValue > 0;

                if (pressed && !wasPressedBefore) {
                    this.rightTriggerJustPressed = true;
                }
            }
            this.axes[i] = axisValue;

            if (axisValue != 0) {
                ControllerX.get().setInputType(InputType.CONTROLLER);
            }
        }
        return false;
    }

    private void handleScreen(LocalPlayer player, Screen screen) {
        ControllerContext context = ControllerContext.get();

        if (context instanceof ChatControllerContext chatContext) {
            this.handleChat(screen, chatContext);
        }

        if (context instanceof InventoryMenuControllerContext inventoryContext && inventoryContext.closeInventory.action().isJustPressed()) {
            player.closeContainer();
            return;
        }

        if (!(context instanceof MenuControllerContext menuContext)) {
            return;
        }

        if (isVirtualKeyboardOpen()) {
            if (isButtonJustPressed(ControllerButton.B)) {
                this.closeVirtualKeyboard();
            }
        }

        if (!isVirtualKeyboardOpen()
                && screen.getFocused() instanceof ControllerInputHandler handler
                && handler.handleInput(this)) {
            return;
        }

        if (screen.getFocused() instanceof ItemSlot itemSlot && menuContext instanceof MenuOnSlotControllerContext menuOnSlotContext) {
            if (menuOnSlotContext.pickupOrPlace.action().isJustPressed()) itemSlot.pickUpOrPlace();
            if (menuOnSlotContext.splitOrPutSingle.action().isJustPressed()) itemSlot.splitOrPutSingle();
            if (menuOnSlotContext.drop.action().isJustPressed()) itemSlot.drop();
        }

        if (menuContext.activate.action().isJustPressed()) {
            if (screen.getFocused() instanceof EditBox editBox && !(screen instanceof ChatScreen)) {
                screen.setFocused(true);
                screen.setFocused(editBox);
                this.openVirtualKeyboard(editBox.getValue(), input -> {
                    if (input == null) {
                        throw new IllegalArgumentException("Input cannot be null");
                    }

                    editBox.setValue(input);
                });
            } else {
                screen.keyPressed(InputConstants.KEY_RETURN, 0, 0);
                screen.keyReleased(InputConstants.KEY_RETURN, 0, 0);
            }
        }

        float axisValue = menuContext.scrollY.action().getAxisValue();
        if (axisValue != 0) {
            axisValue = -axisValue;
            GuiEventListener focused = screen.getFocused();
            if (focused instanceof LayoutElement widget) {
                screen.mouseScrolled(widget.getX(), widget.getY(), axisValue);
            } else if (focused instanceof AbstractSelectionList<?> list) {
                AbstractSelectionListAccessor list1 = (AbstractSelectionListAccessor) list;
                screen.mouseScrolled(list1.getX0(), list1.getY0(), axisValue);
            } else if (focused != null) {
                screen.mouseScrolled(0, 0, axisValue);
            } else for (GuiEventListener widget : screen.children()) {
                if (widget instanceof LayoutElement w && widget.isFocused()) {
                    screen.mouseScrolled(w.getX(), w.getY(), axisValue);
                } else if (widget instanceof ContainerEventHandler container && container.isFocused()) {
                    if (container instanceof LayoutElement w) {
                        screen.mouseScrolled(w.getX(), w.getY(), axisValue);
                    } else {
                        screen.mouseScrolled(0, 0, axisValue);
                    }
                } else if (widget.isFocused()) {
                    screen.mouseScrolled(0, 0, axisValue);
                }
            }
        }

        if (menuContext.dpadMove.action().getValue() == 0) dPadDisabled = false;
        if (!dPadDisabled) {
            if (menuContext instanceof CloseableMenuControllerContext closeableMenuContext && closeableMenuContext.back.action().isJustPressed()) {
                screen.keyPressed(InputConstants.KEY_ESCAPE, 0, 0);
                screen.keyReleased(InputConstants.KEY_ESCAPE, 0, 0);
                dPadDisabled = true;
            } else if (menuContext.dpadMove.action().get2DValue().y > 0) {
                screen.keyPressed(InputConstants.KEY_UP, 0, 0);
                screen.keyReleased(InputConstants.KEY_UP, 0, 0);
                dPadDisabled = true;
            } else if (menuContext.dpadMove.action().get2DValue().x < 0) {
                screen.keyPressed(InputConstants.KEY_LEFT, 0, 0);
                screen.keyReleased(InputConstants.KEY_LEFT, 0, 0);
                dPadDisabled = true;
            } else if (menuContext.dpadMove.action().get2DValue().y < 0) {
                screen.keyPressed(InputConstants.KEY_DOWN, 0, 0);
                screen.keyReleased(InputConstants.KEY_DOWN, 0, 0);
                dPadDisabled = true;
            } else if (menuContext.dpadMove.action().get2DValue().x > 0) {
                screen.keyPressed(InputConstants.KEY_RIGHT, 0, 0);
                screen.keyReleased(InputConstants.KEY_RIGHT, 0, 0);
                dPadDisabled = true;
            }
        }


        if (nextTick < System.currentTimeMillis()) {
            if (delay > 0) {
                delay--;
                return;
            }
            nextTick = System.currentTimeMillis() + 20;
        } else {
            return;
        }

        if (menuContext.joystickMove.action().get2DValue().y < 0) {
            screen.keyPressed(InputConstants.KEY_UP, 0, 0);
            screen.keyReleased(InputConstants.KEY_UP, 0, 0);
            delay = 10;
        } else if (menuContext.joystickMove.action().get2DValue().x < 0) {
            screen.keyPressed(InputConstants.KEY_LEFT, 0, 0);
            screen.keyReleased(InputConstants.KEY_LEFT, 0, 0);
            delay = 10;
        } else if (menuContext.joystickMove.action().get2DValue().y > 0) {
            screen.keyPressed(InputConstants.KEY_DOWN, 0, 0);
            screen.keyReleased(InputConstants.KEY_DOWN, 0, 0);
            delay = 10;
        } else if (menuContext.joystickMove.action().get2DValue().x > 0) {
            screen.keyPressed(InputConstants.KEY_RIGHT, 0, 0);
            screen.keyReleased(InputConstants.KEY_RIGHT, 0, 0);
            delay = 10;
        }
    }

    private void handleChat(Screen screen, ChatControllerContext chatContext) {
        if (!(screen instanceof ChatScreen)) return;

        EditBox val = screen.children().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast).findAny().orElse(null);
        if (chatContext.openKeyboard.action().isJustPressed()) {
            if (val != null) {
                this.openVirtualKeyboard(val.getValue(), input -> {
                    if (input == null) {
                        throw new IllegalArgumentException("Input cannot be null");
                    }

                    val.setValue(input);
                }, () -> Minecraft.getInstance().screen.keyPressed(InputConstants.KEY_RETURN, 0, 0));
            } else {
                ControllerX.LOGGER.warn("Chat screen does not contain any edit boxes.");
            }
        } else if (chatContext.send.action().isJustPressed()) {
            screen.keyPressed(InputConstants.KEY_RETURN, 0, 0);
            screen.keyReleased(InputConstants.KEY_RETURN, 0, 0);
        } else if (chatContext.close.action().isJustPressed()) {
            screen.keyPressed(InputConstants.KEY_RETURN, 0, 0);
            screen.keyReleased(InputConstants.KEY_RETURN, 0, 0);
        }
    }

    public void closeVirtualKeyboard() {
        this.virtualKeyboardValue = "";
        this.virtualKeyboardOpen = false;
        ControllerX.get().virtualKeyboard.close();
    }

    public void openVirtualKeyboard(VirtualKeyboardEditCallback callback) {
        openVirtualKeyboard("", callback);
    }

    public void openVirtualKeyboard(@NotNull String value, VirtualKeyboardEditCallback callback) {
        if (!Config.get().enableVirtualKeyboard) return;

        this.virtualKeyboardValue = value;
        this.virtualKeyboardOpen = true;

        ControllerX.get().virtualKeyboard.open(callback, () -> callback.onInput(this.mod.virtualKeyboard.getScreen().getInput()));
    }

    public void openVirtualKeyboard(@NotNull String value, VirtualKeyboardEditCallback callback, VirtualKeyboardSubmitCallback submitCallback) {
        this.virtualKeyboardValue = value;
        this.virtualKeyboardOpen = true;

        ControllerX.get().virtualKeyboard.open(callback, submitCallback);
    }

    public @NotNull String getVirtualKeyboardValue() {
        return virtualKeyboardValue;
    }

    public boolean isVirtualKeyboardOpen() {
        return virtualKeyboardOpen;
    }

    public boolean isJoystickRight() {
        return leftStick.x > 0 && isXAxis();
    }

    public boolean isJoystickDown() {
        return leftStick.y > 0 && isYAxis();
    }

    public boolean isJoystickLeft() {
        return leftStick.x < 0 && isXAxis();
    }

    public boolean isJoystickUp() {
        return leftStick.y < 0 && isYAxis();
    }

    private boolean isYAxis() {
        return Math.abs(leftStick.x) <= Math.abs(leftStick.y);
    }

    private boolean isXAxis() {
        return Math.abs(leftStick.x) > Math.abs(leftStick.y);
    }

    public float getAxis(ControllerAxis controllerAxis) {
        if (controllerAxis == ControllerAxis.DpadX) {
            return isButtonPressed(ControllerButton.DPAD_LEFT) ? -1 : (isButtonPressed(ControllerButton.DPAD_RIGHT) ? 1 : 0);
        } else if (controllerAxis == ControllerAxis.DpadY) {
            return isButtonPressed(ControllerButton.DPAD_DOWN) ? -1 : (isButtonPressed(ControllerButton.DPAD_UP) ? 1 : 0);
        }

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

        if (v != 0) {
            ControllerX.get().setInputType(InputType.CONTROLLER);
        }

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) {
            return v;
        }

        return 0;
    }

    private float getOldAxis(ControllerAxis controllerAxis) {
        return oldAxes[controllerAxis.sdlAxis()];
    }

    public Vector2f getJoystick(ControllerJoystick joystick) {
        return joystick.getValue();
    }

    public float getTrigger(ControllerTrigger trigger) {
        return trigger.getValue();
    }

    @SuppressWarnings("SameParameterValue")
    private void setController(int deviceIndex) {
        this.sdlController = SDL_GameControllerOpen(deviceIndex);
        if (sdlController == null) return;

        short productId = SDL_GameControllerGetProduct(sdlController);
        short vendorId = SDL_GameControllerGetVendor(sdlController);
        String name = SDL_GameControllerName(sdlController);
        String mapping = SDL_GameControllerMapping(sdlController);

        this.controller = new Controller(sdlController, deviceIndex, productId, vendorId, name, mapping);

        ControllerEvent.CONTROLLER_CONNECTED.invoker().onConnectionStatus(this.controller);
        Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.TUTORIAL_HINT, Component.translatable("controllerx.toast.controller_connected.title"), Component.translatable("controllerx.toast.controller_connected.description", name)));

        ControllerX.LOGGER.info("Controller {} connected", name);
    }

    private void unsetController(int deviceIndex) {
        if (deviceIndex != this.controller.deviceIndex()) return;

        Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.PERIODIC_NOTIFICATION, Component.translatable("controllerx.toast.controller_disconnected.title"), Component.translatable("controllerx.toast.controller_disconnected.description", this.controller.name())));

        this.sdlController = null;
        this.controller = null;

        ControllerEvent.CONTROLLER_DISCONNECTED.invoker().onConnectionStatus(this.controller);
        ControllerX.get().forceSetInputType(InputType.KEYBOARD_AND_MOUSE, 10);

        ControllerX.LOGGER.info("Controller disconnected");
    }

    public @Nullable Controller getController() {
        return controller;
    }

    public @Nullable SDL_GameController getSDLController() {
        return sdlController;
    }

    public boolean isButtonPressed(ControllerButton button) {
        int idx = button.sdlButton();
        boolean pressed = SDL_GameControllerGetButton(sdlController, idx) == SDL_PRESSED;

        if (pressed) ControllerX.get().setInputType(InputType.CONTROLLER);
        if (ControllerX.get().getInputType() == InputType.CONTROLLER) return pressed;

        return false;
    }

    public boolean isButtonJustPressed(ControllerButton button) {
        int idx = button.sdlButton();

        boolean pressed = this.justPressedButtons.get(idx);
        if (pressed) ControllerX.get().setInputType(InputType.CONTROLLER);
        if (ControllerX.get().getInputType() == InputType.CONTROLLER) return pressed;

        return false;
    }

    public boolean isConnected() {
        return controller != null && SDL_GameControllerGetAttached(controller.sdlController());
    }

    public boolean isAvailable() {
        return isConnected() && ControllerX.get().getInputType() == InputType.CONTROLLER;
    }

    public Boolean doInput(Minecraft mc, KeyMapping mapping, Mapper<Either<ControllerAxis, ControllerButton>, Boolean> controllerMapper) {
        if (ControllerContext.get() instanceof InGameControllerContext context) {
            if (mapping == mc.options.keyPickItem) {
                return context.pickItem.action().isPressed();
            }
            if (mapping == mc.options.keyDrop) {
                return context.drop.action().isPressed();
            }
            if (mapping == mc.options.keyPlayerList) {
                return context.playerList.action().isPressed();
            }
            if (mapping == mc.options.keyChat) {
                return context.chat.action().isPressed();
            }
            if (mapping == mc.options.keyInventory) {
                return context.inventory.action().isJustPressed();
            }
            if (mapping == mc.options.keyShift) {
                return context.sneak.action().isPressed();
            }
            if (mapping == mc.options.keySwapOffhand) {
                return context.swapHands.action().isJustPressed();
            }
            if (mapping == mc.options.keySprint) {
                return context.run.action().isPressed();
            }
        }
        return null;
    }

    public boolean isAxisPressed(ControllerAxis axis) {
        return switch (axis) {
            case LeftStickX, LeftStickY -> isButtonPressed(ControllerButton.LEFT_STICK);
            case RightStickX, RightStickY -> isButtonPressed(ControllerButton.RIGHT_STICK);
            case LeftTrigger -> getAxis(ControllerAxis.LeftTrigger) > 0;
            case RightTrigger -> getAxis(ControllerAxis.RightTrigger) > 0;
            default -> false;
        };
    }

    public Vector2f tryGetAxis(ControllerAxis axis) {
        return switch (axis) {
            case LeftStickX, LeftStickY -> getJoystick(ControllerJoystick.Left);
            case RightStickX, RightStickY -> getJoystick(ControllerJoystick.Right);
            default -> new Vector2f(0, 0);
        };
    }

    public void updateScreen(Screen screen) {
        this.screenWasOpen = screen != null;

        if (pollEvents()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (screen != null) {
            if (virtualKeyboardOpen) {
                handleScreen(null, mod.virtualKeyboard.getScreen());
                return;
            }
            handleScreen(player, screen);
        }
    }

    public ControllerX getMod() {
        return mod;
    }

    public void setLayout(KeyboardLayout layout) {
        this.layout = layout;
    }

    public KeyboardLayout getLayout() {
        return layout;
    }

    public void handleVirtualKeyboardClosed(String value) {
        this.virtualKeyboardValue = value;
        this.virtualKeyboardOpen = false;
    }

    public boolean isTriggerJustPressed(ControllerAxis axis) {
        return getAxis(axis) > 0 && getOldAxis(axis) == 0;
    }
}
