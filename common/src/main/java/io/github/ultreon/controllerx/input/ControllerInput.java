package io.github.ultreon.controllerx.input;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.impl.ScreenAccessImpl;
import io.github.libsdl4j.api.gamecontroller.SDL_GameController;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerButton;
import io.github.ultreon.controllerx.*;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.gui.ControllerInputHandler;
import io.github.ultreon.controllerx.gui.ControllerToast;
import io.github.ultreon.controllerx.gui.widget.ItemSlot;
import io.github.ultreon.controllerx.impl.*;
import io.github.ultreon.controllerx.input.keyboard.KeyboardLayout;
import io.github.ultreon.controllerx.injection.CreativeModeInventoryScreenInjection;
import io.github.ultreon.controllerx.mixin.accessors.AbstractSelectionListAccessor;
import io.github.ultreon.controllerx.mixin.accessors.ScreenAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.joml.Vector2f;

import java.time.Duration;
import java.util.BitSet;

import static io.github.libsdl4j.api.event.SdlEventsConst.SDL_PRESSED;
import static io.github.libsdl4j.api.gamecontroller.SdlGamecontroller.*;

public class ControllerInput extends Input {
    private final Vector2f leftStick = new Vector2f();
    private final Vector2f rightStick = new Vector2f();

    private final BitSet pressedButtons = new BitSet(SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX);
    private final BitSet justPressedButtons = new BitSet(ControllerButton.values().length);
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
    private boolean dPadDisabled;
    private float destroyDelay = 0;
    private boolean screenWasOpen;
    private boolean wasItemUsePressed = false;

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

            this.leftImpulse = -context.movePlayer.getAction().get2DValue().x;
            this.forwardImpulse = -context.movePlayer.getAction().get2DValue().y;

            player.setXRot(Mth.clamp((float) (player.getXRot() + context.lookPlayer.getAction().get2DValue().y * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10), -90, 90));
            player.setYRot((float) (player.getYRot() + context.lookPlayer.getAction().get2DValue().x * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10));

            jumping = context.jump.getAction().isPressed();
            shiftKeyDown = context.sneak.getAction().isPressed();

//            if (context.use.getAction().isJustPressed() && GameApi.getRightClickDelay() == 0) {
//                GameApi.startUseItem();
//                this.wasItemUsePressed = true;
//            }
//
//            if (!context.use.getAction().isPressed() && wasItemUsePressed) {
//                GameApi.stopUseItem();
//                this.wasItemUsePressed = false;
//            }
            if (context.itemLeft.getAction().isJustPressed()) GameApi.scrollHotbar(-1);
            if (context.itemRight.getAction().isJustPressed()) GameApi.scrollHotbar(1);

            boolean flag = false;
            if (context.destroyBlock.getAction().isJustPressed()) flag = GameApi.startAttack();
            if (context.destroyBlock.getAction().isPressed() && player.getAbilities().mayBuild) {
                if (!player.getAbilities().mayBuild) return;

                if (destroyDelay == 0) GameApi.continueAttack(flag);
                if ((destroyDelay--) < 0) {
                    destroyDelay = player.getAbilities().mayBuild && !player.getAbilities().instabuild ? 0 : 5;
                }
            }

            flag = false;
            if (context.attack.getAction().isJustPressed()) flag = GameApi.startAttack();
            if (context.attack.getAction().isPressed()) GameApi.continueAttack(flag);

            for (KeyMapping keyMapping : mc.options.keyMappings) {
                Boolean b = this.doInput(mc, keyMapping);

                if (b == Boolean.TRUE) {
                    MixinClickHandler clickHandler = (MixinClickHandler) keyMapping;
                    clickHandler.controllerX$handleClick();
                }
            }

            if (context.gameMenu.getAction().isJustPressed()) {
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
            if (this.sdlController == null)
                return true;
        }

        if (!SDL_GameControllerGetAttached(sdlController)) {
            unsetController(0);
            return true;
        }

        for (@MagicConstant(valuesFromClass = SDL_GameControllerButton.class) int idx = SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_A; idx < SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX; idx++) {
            this.justPressedButtons.set(idx, false);
            boolean wasPressedBefore = this.pressedButtons.get(idx);
            boolean pressed = SDL_GameControllerGetButton(this.sdlController, idx) == SDL_PRESSED;
            this.pressedButtons.set(idx, pressed);

            if (pressed) {
                ControllerX.get().setInputType(InputType.CONTROLLER);
                if (!wasPressedBefore) this.justPressedButtons.set(idx, true);
            }
        }

        for (int i = 0; i < ControllerAxis.values().length; i++) {
            ControllerAxis axis = ControllerAxis.values()[i];
            Float axisValue = getAxis0(axis);
            if (axisValue == null) axisValue = 0.0F;
            this.oldAxes[i] = this.axes[i];
            this.axes[i] = axisValue;

            if (axisValue != 0)
                ControllerX.get().setInputType(InputType.CONTROLLER);
        }
        return false;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void handleScreen(LocalPlayer player, Screen screen) {
        ControllerContext context = ControllerContext.get();

        if (context instanceof ChatControllerContext ctx) this.handleChat(screen, ctx);
        if (!(context instanceof MenuControllerContext ctx)) return;

        if (ctx.closeInventory.getAction().isJustPressed()) {
            player.closeContainer();
            return;
        }

        if (isVirtualKeyboardOpen() && isButtonJustPressed(ControllerButton.B))
            this.closeVirtualKeyboard();
        if (!isVirtualKeyboardOpen() && screen.getFocused() instanceof ControllerInputHandler handler && handler.handleInput(this))
            return;

        if (screen.getFocused() instanceof ItemSlot slot) {
            if (ctx.pickup.getAction().isJustPressed() || ctx.place.getAction().isJustPressed()) slot.pickUpOrPlace();
            if (ctx.split.getAction().isJustPressed() || ctx.putSingle.getAction().isJustPressed()) slot.splitOrPutSingle();
            if (ctx.drop.getAction().isJustPressed()) slot.drop();
        }

        if (screen instanceof CreativeModeInventoryScreen creativeScr) {
            if (ctx.prevPage.getAction().isJustPressed()) {
                ((CreativeModeInventoryScreenInjection) creativeScr).controllerX$prevPage();
                ((ScreenAccessor) creativeScr).getChildren().removeIf(w -> w instanceof ItemSlot);
                ((ScreenAccessor) creativeScr).getRenderables().removeIf(w -> w instanceof ItemSlot);

                Hooks.hookContainerSlots(creativeScr, new ScreenAccessImpl(creativeScr));
            } else if (ctx.nextPage.getAction().isJustPressed()) {
                ((CreativeModeInventoryScreenInjection) creativeScr).controllerX$nextPage();
                ((ScreenAccessor) creativeScr).getChildren().removeIf(w -> w instanceof ItemSlot);
                ((ScreenAccessor) creativeScr).getRenderables().removeIf(w -> w instanceof ItemSlot);
                Hooks.hookContainerSlots(creativeScr, new ScreenAccessImpl(creativeScr));
            }
        }

        if (ctx.activate.getAction().isJustPressed()) {
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

        float axisValue = ctx.scrollY.getAction().getAxisValue();
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

        if (ctx.dpadMove.getAction().getValue() == 0) dPadDisabled = false;
        if (!dPadDisabled) {
            if (ctx.back.getAction().isJustPressed()) {
                screen.keyPressed(InputConstants.KEY_ESCAPE, 0, 0);
                screen.keyReleased(InputConstants.KEY_ESCAPE, 0, 0);
                dPadDisabled = true;
            } else if (ctx.dpadMove.getAction().get2DValue().y > 0) {
                screen.keyPressed(InputConstants.KEY_UP, 0, 0);
                screen.keyReleased(InputConstants.KEY_UP, 0, 0);
                dPadDisabled = true;
            } else if (ctx.dpadMove.getAction().get2DValue().x < 0) {
                screen.keyPressed(InputConstants.KEY_LEFT, 0, 0);
                screen.keyReleased(InputConstants.KEY_LEFT, 0, 0);
                dPadDisabled = true;
            } else if (ctx.dpadMove.getAction().get2DValue().y < 0) {
                screen.keyPressed(InputConstants.KEY_DOWN, 0, 0);
                screen.keyReleased(InputConstants.KEY_DOWN, 0, 0);
                dPadDisabled = true;
            } else if (ctx.dpadMove.getAction().get2DValue().x > 0) {
                screen.keyPressed(InputConstants.KEY_RIGHT, 0, 0);
                screen.keyReleased(InputConstants.KEY_RIGHT, 0, 0);
                dPadDisabled = true;
            }
        }


        if (nextTick < System.currentTimeMillis()) {
            if (delay > 0) {
                delay -= Minecraft.getInstance().getDeltaFrameTime();
                return;
            }
            nextTick = System.currentTimeMillis() + 20;
        } else {
            return;
        }

        if (ctx.joystickMove.getAction().get2DValue().y < 0) {
            screen.keyPressed(InputConstants.KEY_UP, 0, 0);
            screen.keyReleased(InputConstants.KEY_UP, 0, 0);
            delay = 10;
        } else if (ctx.joystickMove.getAction().get2DValue().x < 0) {
            screen.keyPressed(InputConstants.KEY_LEFT, 0, 0);
            screen.keyReleased(InputConstants.KEY_LEFT, 0, 0);
            delay = 10;
        } else if (ctx.joystickMove.getAction().get2DValue().y > 0) {
            screen.keyPressed(InputConstants.KEY_DOWN, 0, 0);
            screen.keyReleased(InputConstants.KEY_DOWN, 0, 0);
            delay = 10;
        } else if (ctx.joystickMove.getAction().get2DValue().x > 0) {
            screen.keyPressed(InputConstants.KEY_RIGHT, 0, 0);
            screen.keyReleased(InputConstants.KEY_RIGHT, 0, 0);
            delay = 10;
        }
    }

    private void handleChat(Screen screen, ChatControllerContext chatContext) {
        if (!(screen instanceof ChatScreen)) return;

        EditBox val = screen.children().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast).findAny().orElse(null);
        if (chatContext.openKeyboard.getAction().isJustPressed()) {
            if (val != null) {
                this.openVirtualKeyboard(val.getValue(), input -> {
                    if (input == null) {
                        throw new IllegalArgumentException("Input cannot be null");
                    }

                    val.setValue(input);
                }, () -> {
                    assert Minecraft.getInstance().screen != null;
                    Minecraft.getInstance().screen.keyPressed(InputConstants.KEY_RETURN, 0, 0);
                });
            } else {
                ControllerX.LOGGER.warn("Chat screen does not contain any edit boxes.");
            }
        } else if (chatContext.send.getAction().isJustPressed()) {
            screen.keyPressed(InputConstants.KEY_RETURN, 0, 0);
            screen.keyReleased(InputConstants.KEY_RETURN, 0, 0);
        } else if (chatContext.close.getAction().isJustPressed()) {
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
        Float v = getAxis0(controllerAxis);
        if (v == null) return 0f;

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) {
            return v;
        }

        return 0;
    }

    private @Nullable Float getAxis0(ControllerAxis controllerAxis) {
        if (controllerAxis == ControllerAxis.DpadX) {
            return (float) (isButtonPressed(ControllerButton.DPadLeft) ? -1 : (isButtonPressed(ControllerButton.RPadRight) ? 1 : 0));
        } else if (controllerAxis == ControllerAxis.DpadY) {
            return (float) (isButtonPressed(ControllerButton.DPadDOwn) ? -1 : (isButtonPressed(ControllerButton.DPadUp) ? 1 : 0));
        }

        int axis = controllerAxis.sdlAxis();
        if (axis == -1) return null;
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
        Minecraft.getInstance().getToasts().addToast(new ControllerToast(Icon.AnyJoyStick, Component.translatable("controllerx.toast.controller_connected.title"), Component.translatable("controllerx.toast.controller_connected.description", name)).hideIn(Duration.ofSeconds(5)));

        ControllerX.LOGGER.info("Controller {} connected", name);
    }

    private void unsetController(@Range(from = 0, to = 256) int deviceIndex) {
        if (deviceIndex != this.controller.deviceIndex()) return;

        Minecraft.getInstance().getToasts().addToast(new ControllerToast(Icon.AnyJoyStick, Component.translatable("controllerx.toast.controller_disconnected.title"), Component.translatable("controllerx.toast.controller_disconnected.description", this.controller.name())).hideIn(Duration.ofSeconds(5)));

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
        if (button.isAxis()) return button.getValue();

        int idx = button.sdlButton();
        boolean pressed = SDL_GameControllerGetButton(sdlController, idx) == SDL_PRESSED;

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) return pressed;

        return false;
    }

    public boolean isButtonJustPressed(ControllerButton button) {
        if (button.isAxis()) {
            return getAxis(ControllerAxis.fromButton(button)) != 0 && getOldAxis(ControllerAxis.fromButton(button)) == 0;
        };

        int idx = button.sdlButton();

        boolean pressed = this.justPressedButtons.get(idx);
        if (ControllerX.get().getInputType() == InputType.CONTROLLER) return pressed;

        return false;
    }

    public boolean isConnected() {
        return controller != null && SDL_GameControllerGetAttached(controller.sdlController());
    }

    public boolean isAvailable() {
        return isConnected() && ControllerX.get().getInputType() == InputType.CONTROLLER;
    }

    public Boolean doInput(Minecraft mc, KeyMapping mapping) {
        if (ControllerContext.get() instanceof InGameControllerContext context) {
            if (mapping == mc.options.keyPickItem) {
                return context.pickItem.getAction().isPressed();
            }
            if (mapping == mc.options.keyDrop) {
                return context.drop.getAction().isPressed();
            }
            if (mapping == mc.options.keyPlayerList) {
                return context.playerList.getAction().isPressed();
            }
            if (mapping == mc.options.keyChat) {
                return context.chat.getAction().isPressed();
            }
            if (mapping == mc.options.keyInventory) {
                return context.inventory.getAction().isJustPressed();
            }
            if (mapping == mc.options.keyShift) {
                return context.sneak.getAction().isPressed();
            }
            if (mapping == mc.options.keySwapOffhand) {
                return context.swapHands.getAction().isJustPressed();
            }
            if (mapping == mc.options.keySprint) {
                return context.run.getAction().isPressed();
            }
            if (mapping == mc.options.keyUse) {
                return context.use.getAction().isPressed();
            }
        }
        return null;
    }

    public boolean isAxisPressed(ControllerAxis axis) {
        return switch (axis) {
            case LeftStickX, LeftStickY -> isButtonPressed(ControllerButton.LeftStickClick);
            case RightStickX, RightStickY -> isButtonPressed(ControllerButton.RightStickClick);
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

    public boolean hasAnyInput() {
        boolean hasButtonInput = !pressedButtons.isEmpty();
        boolean hasAxisInput = isAnyAxisUsed();

        return hasButtonInput || hasAxisInput;
    }

    private boolean isAnyAxisUsed() {
        for (float axis : axes) {
            if (axis != 0)
                return true;
        }

        return false;
    }
}
