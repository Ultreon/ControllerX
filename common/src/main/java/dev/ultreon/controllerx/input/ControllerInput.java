package dev.ultreon.controllerx.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.ultreon.mods.lib.client.gui.widget.BaseWidget;
import dev.architectury.impl.ScreenAccessImpl;
import io.github.libsdl4j.api.gamecontroller.SDL_GameController;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerAxis;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerButton;
import dev.ultreon.controllerx.*;
import dev.ultreon.controllerx.api.ControllerAction;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.api.ControllerMapping;
import dev.ultreon.controllerx.gui.ControllerInputHandler;
import dev.ultreon.controllerx.gui.ControllerToast;
import dev.ultreon.controllerx.gui.widget.ItemSlot;
import dev.ultreon.controllerx.impl.*;
import dev.ultreon.controllerx.input.dyn.*;
import dev.ultreon.controllerx.injection.CreativeModeInventoryScreenInjection;
import dev.ultreon.controllerx.input.keyboard.KeyboardLayout;
import dev.ultreon.controllerx.mixin.accessors.ScreenAccessor;
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
import org.joml.Vector2f;

import java.time.Duration;
import java.util.BitSet;

import static io.github.libsdl4j.api.event.SdlEventsConst.SDL_PRESSED;
import static io.github.libsdl4j.api.gamecontroller.SdlGamecontroller.*;

@SuppressWarnings("MagicConstant")
public class ControllerInput extends Input {
    @ApiStatus.Internal public static boolean moddedMappingsLoaded = false;
    private final Vector2f leftStick = new Vector2f();
    private final Vector2f rightStick = new Vector2f();

    private final BitSet pressedButtons = new BitSet(SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX);
    private SDL_GameController sdlController;
    private Controller controller;
    private final float[] oldAxes = new float[ControllerSignedFloat.values().length];
    private final float[] axes = new float[ControllerSignedFloat.values().length];
    private final Vector2f tmp = new Vector2f();
    private final ControllerX mod;
    private KeyboardLayout layout;
    private String virtualKeyboardValue = "";
    private boolean virtualKeyboardOpen;
    private boolean screenWasOpen;
    private InterceptCallback interceptCallback;
    private InterceptInvalidation interceptInvalidation = new InterceptInvalidation() {
        @Override
        public void onIntercept(InterceptCallback callback) {
            // Do nothing
        }

        @Override
        public boolean isStillValid() {
            return false;
        }
    };

    public ControllerInput(ControllerX mod) {
        this.mod = mod;

        pollEvents();
    }

    @ApiStatus.Internal
    public void update() {
        if (pollEvents()) return;

        Minecraft mc = Minecraft.getInstance();

        if (mod.input.isVirtualKeyboardOpen()) {
            this.handleScreen(mc.player, mod.virtualKeyboard.getScreen());
            return;
        }

        if (screenWasOpen) {
            screenWasOpen = false;
            return;
        }

        this.leftStick.set(this.getJoystick(ControllerVec2.LeftStick));
        this.rightStick.set(this.getJoystick(ControllerVec2.RightStick));
        if ((ControllerContext.get()) instanceof InGameControllerContext context) {
            LocalPlayer player = context.player();

            this.leftImpulse = -context.movePlayer.getAction().get2DValue().x;
            this.forwardImpulse = -context.movePlayer.getAction().get2DValue().y;

            player.setXRot(Mth.clamp((float) (player.getXRot() + context.lookPlayer.getAction().get2DValue().y * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10), -90, 90));
            player.setYRot((float) (player.getYRot() + context.lookPlayer.getAction().get2DValue().x * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10));

            jumping = context.jump.getAction().isPressed();
            shiftKeyDown = context.sneak.getAction().isPressed();

            if (context.itemLeft.getAction().isJustPressed()) GameApi.scrollHotbar(-1);
            if (context.itemRight.getAction().isJustPressed()) GameApi.scrollHotbar(1);

            for (KeyMapping keyMapping : mc.options.keyMappings) {
                boolean shouldClick = this.shouldClick(mc, keyMapping);

                if (shouldClick) {
                    keyMapping.clickCount++;
                }

                if (this.shouldRelease(mc, keyMapping)) {
                    keyMapping.clickCount = 0;
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
        SDL_GameControllerUpdate();

        if (ControllerX.get().input != null) {
            ControllerBoolean.pollAll();
        }

        if (this.sdlController == null) {
            this.setController(0);
            if (this.sdlController == null)
                return true;
        }

        if (!SDL_GameControllerGetAttached(sdlController)) {
            unsetController();
            return true;
        }

        for (@MagicConstant(valuesFromClass = SDL_GameControllerButton.class) int idx = SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_A; idx < SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX; idx++) {
            boolean pressed = SDL_GameControllerGetButton(this.sdlController, idx) == SDL_PRESSED;
            this.pressedButtons.set(idx, pressed);

            if (pressed) {
                ControllerX.get().setInputType(InputType.CONTROLLER);
            }
        }

        for (int i = 0; i < ControllerSignedFloat.values().length; i++) {
            ControllerSignedFloat axis = ControllerSignedFloat.values()[i];
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

        if (interceptInvalidation.isStillValid()) {
            boolean input = false;
            for (ControllerVec2 joystick : ControllerVec2.values()) {
                if (joystick.asBoolean().isJustPressed()) {
                    this.interceptCallback.onIntercept(new EventObject<>(EventType.JOYSTICK, joystick, joystick.get(this.tmp)));
                    input = true;
                }
            }

            for (ControllerSignedFloat axis : ControllerSignedFloat.values()) {
                if (axis.asBoolean().isJustPressed()) {
                    this.interceptCallback.onIntercept(new EventObject<>(EventType.AXIS, axis, axis.getValue()));
                    input = true;
                }
            }

            for (ControllerUnsignedFloat axis : ControllerUnsignedFloat.values()) {
                if (axis.asBoolean().isJustPressed()) {
                    this.interceptCallback.onIntercept(new EventObject<>(EventType.TRIGGER, axis, axis.getValue()));
                    input = true;
                }
            }

            for (ControllerBoolean button : ControllerBoolean.values()) {
                if (button.isJustPressed()) {
                    this.interceptCallback.onIntercept(new EventObject<>(EventType.BUTTON, button, true));
                    input = true;
                } else if (button.isJustReleased()) {
                    this.interceptCallback.onIntercept(new EventObject<>(EventType.BUTTON, button, false));
                }
            }

            if (input) {
                interceptInvalidation.onIntercept(this.interceptCallback);
            }

            return;
        }

        if (context instanceof ChatControllerContext ctx) this.handleChat(screen, ctx);
        if (!(context instanceof MenuControllerContext ctx)) return;

        if (ctx.closeInventory.getAction().isJustPressed()) {
            player.closeContainer();
            return;
        }

        if (isVirtualKeyboardOpen() && isButtonJustPressed(ControllerBoolean.B))
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
            } else if (screen.getFocused() instanceof BaseWidget baseWidget) {
                baseWidget.leftClick();
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
                screen.mouseScrolled(list.x0, list.y0, axisValue);
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

        if (ctx.back.getAction().isJustPressed() || ctx.close.getAction().isJustPressed()) {
            screen.keyPressed(InputConstants.KEY_ESCAPE, 0, 0);
            screen.keyReleased(InputConstants.KEY_ESCAPE, 0, 0);
        } else if (ctx.dpadMove.getAction().isJustPressed()) {
            if (ctx.dpadMove.getAction().get2DValue().y > 0) {
                screen.keyPressed(InputConstants.KEY_UP, 0, 0);
                screen.keyReleased(InputConstants.KEY_UP, 0, 0);
            } else if (ctx.dpadMove.getAction().get2DValue().x < 0) {
                screen.keyPressed(InputConstants.KEY_LEFT, 0, 0);
                screen.keyReleased(InputConstants.KEY_LEFT, 0, 0);
            } else if (ctx.dpadMove.getAction().get2DValue().y < 0) {
                screen.keyPressed(InputConstants.KEY_DOWN, 0, 0);
                screen.keyReleased(InputConstants.KEY_DOWN, 0, 0);
            } else if (ctx.dpadMove.getAction().get2DValue().x > 0) {
                screen.keyPressed(InputConstants.KEY_RIGHT, 0, 0);
                screen.keyReleased(InputConstants.KEY_RIGHT, 0, 0);
            }
        }

        if (ctx.joystickMove.getAction().isJustPressed()) {
            if (ctx.joystickMove.getAction().get2DValue().y < 0) {
                screen.keyPressed(InputConstants.KEY_UP, 0, 0);
                screen.keyReleased(InputConstants.KEY_UP, 0, 0);
            } else if (ctx.joystickMove.getAction().get2DValue().x < 0) {
                screen.keyPressed(InputConstants.KEY_LEFT, 0, 0);
                screen.keyReleased(InputConstants.KEY_LEFT, 0, 0);
            } else if (ctx.joystickMove.getAction().get2DValue().y > 0) {
                screen.keyPressed(InputConstants.KEY_DOWN, 0, 0);
                screen.keyReleased(InputConstants.KEY_DOWN, 0, 0);
            } else if (ctx.joystickMove.getAction().get2DValue().x > 0) {
                screen.keyPressed(InputConstants.KEY_RIGHT, 0, 0);
                screen.keyReleased(InputConstants.KEY_RIGHT, 0, 0);
            }
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

    float getAxis1(ControllerSignedFloat controllerAxis) {
        Float v = getAxis0(controllerAxis);
        if (v == null) return 0f;

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) {
            return v;
        }

        return 0;
    }

    private @Nullable Float getAxis0(ControllerSignedFloat controllerAxis) {
        @MagicConstant(valuesFromClass = SDL_GameControllerAxis.class) int axis = controllerAxis.sdlAxis();
        if (axis == SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_INVALID) return null;
        float v = SDL_GameControllerGetAxis(sdlController, axis) / 32767f;

        float deadZone = Config.get().axisDeadZone;
        int signum = v > 0 ? 1 : -1;
        v = Math.abs(v);
        if (v < deadZone) {
            v = Math.max(0, (v - deadZone) / (1 - deadZone)) * signum;
        } else {
            v *= signum;
        }

        if (v == 0) return 0f;

        return v;
    }

    private float getOldAxis(ControllerSignedFloat controllerAxis) {
        return oldAxes[controllerAxis.sdlAxis()];
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

    private void unsetController() {
        if (0 != this.controller.deviceIndex()) return;

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

    public boolean isButtonPressed(ControllerBoolean button) {
        return button.isPressed();
    }

    public boolean isButtonJustPressed(ControllerBoolean button) {
        return button.isJustPressed();
    }

    public boolean isButtonJustReleased(ControllerBoolean button) {
        return button.isJustReleased();
    }

    public Vector2f getJoystick(ControllerVec2 joystick) {
        return joystick.get(this.tmp);
    }

    public float getTrigger(ControllerUnsignedFloat trigger) {
        return trigger.getValue();
    }

    boolean isButtonPressed0(ControllerBoolean button) {
        int idx = button.sdlButton();
        boolean pressed = SDL_GameControllerGetButton(sdlController, idx) == SDL_PRESSED;

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) return pressed;

        return false;
    }

    public boolean isConnected() {
        return controller != null && SDL_GameControllerGetAttached(controller.sdlController());
    }

    public boolean isAvailable() {
        return isConnected() && ControllerX.get().getInputType() == InputType.CONTROLLER;
    }

    public boolean shouldClick(Minecraft mc, KeyMapping mapping) {
        if (ControllerContext.get() instanceof InGameControllerContext context) {
            ControllerAction<?> action = getAction(mc, mapping, context);
            if (action != null) {
                return action.isJustPressed();
            }
        }
        return false;
    }

    public boolean shouldRelease(Minecraft mc, KeyMapping mapping) {
        if (ControllerContext.get() instanceof InGameControllerContext context) {
            ControllerAction<?> action = getAction(mc, mapping, context);
            if (action != null) {
                return action.isJustReleased();
            }
        }
        return false;
    }

    public boolean isDown(Minecraft mc, KeyMapping mapping) {
        if (ControllerContext.get() instanceof InGameControllerContext context) {
            ControllerAction<?> action = getAction(mc, mapping, context);
            if (action != null)
                return action.isPressed();
        }
        return false;
    }

    public static @Nullable ControllerAction<?> getAction(Minecraft mc, KeyMapping mapping, InGameControllerContext context) {
        if (mapping == mc.options.keyPickItem) return context.pickItem.getAction();
        if (mapping == mc.options.keyDrop) return context.drop.getAction();
        if (mapping == mc.options.keyPlayerList) return context.playerList.getAction();
        if (mapping == mc.options.keyChat) return context.chat.getAction();
        if (mapping == mc.options.keyInventory) return context.inventory.getAction();
        if (mapping == mc.options.keyShift) return context.sneak.getAction();
        if (mapping == mc.options.keySwapOffhand) return context.swapHands.getAction();
        if (mapping == mc.options.keySprint) return context.run.getAction();
        if (mapping == mc.options.keyUse) return context.use.getAction();
        if (mapping == mc.options.keyAttack) return context.attack.getAction();

        // Modded mappings
        ControllerMapping<?> controllerMapping = context.getMappings().get(mapping);
        if (ControllerInput.moddedMappingsLoaded && controllerMapping != null)
            return controllerMapping.getAction();

        return null;
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

    public boolean isTriggerJustPressed(ControllerSignedFloat axis) {
        return getAxis1(axis) > 0 && getOldAxis(axis) == 0;
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

    public void interceptInputOnce(InterceptCallback callback) {
        interceptCallback = callback;
        interceptInvalidation = new CountInvalidation(1);
    }

    @FunctionalInterface
    public interface InterceptCallback {
        void onIntercept(EventObject<?, ?> type);
    }

    public static class EventType<T> {
        public static final EventType<ControllerSignedFloat> AXIS = new EventType<>(ControllerSignedFloat.class);
        public static final EventType<ControllerBoolean> BUTTON = new EventType<>(ControllerBoolean.class);
        public static final EventType<ControllerVec2> JOYSTICK = new EventType<>(ControllerVec2.class);
        public static final EventType<ControllerUnsignedFloat> TRIGGER = new EventType<>(ControllerUnsignedFloat.class);

        private final Class<T> type;

        private EventType(Class<T> type) {
            this.type = type;
        }

        @ApiStatus.Internal
        @SuppressWarnings("unchecked")
        public static <T extends ControllerInterDynamic<?>> EventType<T> get(Class<?> aClass) {
            if (aClass == ControllerAction.Button.class) return (EventType<T>) BUTTON;
            if (aClass == ControllerAction.Axis.class) return (EventType<T>) AXIS;

            throw new IllegalArgumentException("Invalid type: " + aClass);
        }

        public Class<T> getType() {
            return type;
        }
    }

    public record EventObject<V, T extends Enum<T> & ControllerInterDynamic<V>>(EventType<? extends T> type, T mapping,
                                                                                V value) {

        public static EventObject<Boolean, ControllerBoolean> of(ControllerBoolean controllerButton, boolean value) {
                return new EventObject<>(EventType.BUTTON, controllerButton, value);
            }

            public static EventObject<Float, ControllerSignedFloat> of(ControllerSignedFloat controllerAxis, float value) {
                return new EventObject<>(EventType.AXIS, controllerAxis, value);
            }

            public static EventObject<Vector2f, ControllerVec2> of(ControllerVec2 controllerJoystick, Vector2f value) {
                return new EventObject<>(EventType.JOYSTICK, controllerJoystick, value);
            }

            public static EventObject<Float, ControllerUnsignedFloat> of(ControllerUnsignedFloat controllerTrigger, float value) {
                return new EventObject<>(EventType.TRIGGER, controllerTrigger, value);
            }
        }
}
