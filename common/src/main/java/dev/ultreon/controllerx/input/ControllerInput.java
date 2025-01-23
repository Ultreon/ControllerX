package dev.ultreon.controllerx.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.ultreon.mods.lib.client.gui.widget.BaseWidget;
import dev.architectury.impl.ScreenAccessImpl;
import dev.ultreon.controllerx.api.Icon;
import dev.ultreon.controllerx.api.VirtualKeyboardEditCallback;
import dev.ultreon.controllerx.api.input.*;
import dev.ultreon.controllerx.config.gui.tabs.Tabs;
import dev.ultreon.controllerx.impl.contexts.ChatControllerContext;
import dev.ultreon.controllerx.impl.contexts.InGameControllerContext;
import dev.ultreon.controllerx.impl.contexts.MenuControllerContext;
import io.github.libsdl4j.api.gamecontroller.SDL_GameController;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerAxis;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerButton;
import dev.ultreon.controllerx.*;
import dev.ultreon.controllerx.impl.ControllerAction;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.impl.ControllerMapping;
import dev.ultreon.controllerx.gui.ControllerInputHandler;
import dev.ultreon.controllerx.gui.ControllerToast;
import dev.ultreon.controllerx.gui.widget.ItemSlot;
import dev.ultreon.controllerx.injection.CreativeModeInventoryScreenInjection;
import dev.ultreon.controllerx.api.input.keyboard.keyboard.KeyboardLayout;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
public class ControllerInput extends Input implements IControllerInput {
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
    private IInterceptInvalidation IInterceptInvalidation = new IInterceptInvalidation() {
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
            handleScreen(mc.player, mod.virtualKeyboard.getScreen());
            return;
        }

        if (screenWasOpen) {
            screenWasOpen = false;
            return;
        }

        leftStick.set(getJoystick(ControllerVec2.LeftStick));
        rightStick.set(getJoystick(ControllerVec2.RightStick));
        if ((ControllerContext.get()) instanceof InGameControllerContext context) {
            LocalPlayer player = context.player();

            leftImpulse = -context.movePlayer.getAction().get2DValue().x;
            forwardImpulse = -context.movePlayer.getAction().get2DValue().y;

            player.setXRot(Mth.clamp((float) (player.getXRot() + context.lookPlayer.getAction().get2DValue().y * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10), -90, 90));
            player.setYRot((float) (player.getYRot() + context.lookPlayer.getAction().get2DValue().x * mc.options.sensitivity().get() * mc.getDeltaFrameTime() * 10));

            jumping = context.jump.getAction().isPressed();
            shiftKeyDown = context.sneak.getAction().isPressed();

            if (context.itemLeft.getAction().isJustPressed()) GameApi.scrollHotbar(-1);
            if (context.itemRight.getAction().isJustPressed()) GameApi.scrollHotbar(1);

            for (KeyMapping keyMapping : mc.options.keyMappings) {
                ControllerAction<?> shouldClick = actionFromKeyMap(mc, keyMapping);
                if (shouldClick == null) continue;

                if (shouldClick.isPressed()) {
                    keyMapping.setDown(true);

                    if (keyMapping.key.getType() == InputConstants.Type.KEYSYM ||
                            keyMapping.key.getType() == InputConstants.Type.MOUSE && shouldClick.isJustPressed()) {
                        keyMapping.clickCount++;
                    }
                }

                if (shouldRelease(mc, keyMapping)) {
                    keyMapping.setDown(false);
                    keyMapping.clickCount = 0;
                }
            }

            if (context.gameMenu.getAction().isJustPressed()) {
                mc.setScreen(new PauseScreen(true));
            }
        } else {
            leftStick.set(0, 0);
            rightStick.set(0, 0);
            forwardImpulse = 0;
            leftImpulse = 0;
            jumping = false;
            shiftKeyDown = false;
        }
        LocalPlayer player = mc.player;
        if (player != null) {
            float sneakingSpeed = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(player), 0.0F, 1.0F);
            if (player.isMovingSlowly()) {
                forwardImpulse *= sneakingSpeed;
                leftImpulse *= sneakingSpeed;
            }
        }
    }

    private boolean pollEvents() {
        SDL_GameControllerUpdate();

        if (ControllerX.get().input != null) {
            ControllerBoolean.pollAll();
        }

        if (sdlController == null) {
            setController(0);
            if (sdlController == null)
                return true;
        }

        if (!SDL_GameControllerGetAttached(sdlController)) {
            unsetController();
            return true;
        }

        for (@MagicConstant(valuesFromClass = SDL_GameControllerButton.class) int idx = SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_A; idx < SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX; idx++) {
            boolean pressed = SDL_GameControllerGetButton(sdlController, idx) == SDL_PRESSED;
            pressedButtons.set(idx, pressed);

            if (pressed) {
                ControllerX.get().setInputType(InputType.CONTROLLER);
            }
        }

        for (int i = 0; i < ControllerSignedFloat.values().length; i++) {
            ControllerSignedFloat axis = ControllerSignedFloat.values()[i];
            Float axisValue = getAxis0(axis);
            if (axisValue == null) axisValue = 0.0F;
            oldAxes[i] = axes[i];
            axes[i] = axisValue;

            if (axisValue != 0)
                ControllerX.get().setInputType(InputType.CONTROLLER);
        }
        return false;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void handleScreen(LocalPlayer player, Screen screen) {
        ControllerContext context = ControllerContext.get();

        if (IInterceptInvalidation.isStillValid()) {
            boolean input = false;
            for (ControllerVec2 joystick : ControllerVec2.values()) {
                if (joystick.asBoolean().isJustPressed()) {
                    interceptCallback.onIntercept(new EventObject<>(EventType.JOYSTICK, joystick, joystick.get(tmp)));
                    input = true;
                }
            }

            for (ControllerSignedFloat axis : ControllerSignedFloat.values()) {
                if (axis.asBoolean().isJustPressed()) {
                    interceptCallback.onIntercept(new EventObject<>(EventType.AXIS, axis, axis.getValue()));
                    input = true;
                }
            }

            for (ControllerUnsignedFloat axis : ControllerUnsignedFloat.values()) {
                if (axis.asBoolean().isJustPressed()) {
                    interceptCallback.onIntercept(new EventObject<>(EventType.TRIGGER, axis, axis.getValue()));
                    input = true;
                }
            }

            for (ControllerBoolean button : ControllerBoolean.values()) {
                if (button.isJustPressed()) {
                    interceptCallback.onIntercept(new EventObject<>(EventType.BUTTON, button, true));
                    input = true;
                } else if (button.isJustReleased()) {
                    interceptCallback.onIntercept(new EventObject<>(EventType.BUTTON, button, false));
                }
            }

            if (input) {
                IInterceptInvalidation.onIntercept(interceptCallback);
            }

            return;
        }

        if (context instanceof ChatControllerContext ctx) handleChat(screen, ctx);
        if (!(context instanceof MenuControllerContext ctx)) return;

        if (ctx.closeInventory.getAction().isJustPressed()) {
            player.closeContainer();
            return;
        }

        if (isVirtualKeyboardOpen() && isButtonJustPressed(ControllerBoolean.B))
            closeVirtualKeyboard();
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

        if (screen.getFocused() instanceof Tabs tabs) {
            if (ctx.prevPage.getAction().isJustPressed()) {
                tabs.previousTab();
            } else if (ctx.nextPage.getAction().isJustPressed()) {
                tabs.nextTab();
            }
        }

        if (ctx.activate.getAction().isJustPressed()) {
            if (screen.getFocused() instanceof EditBox editBox && !(screen instanceof ChatScreen)) {
                screen.setFocused(true);
                screen.setFocused(editBox);
                openVirtualKeyboard(editBox.getValue(), input -> {
                    if (input == null) {
                        throw new IllegalArgumentException("Input cannot be null");
                    }

                    editBox.setValue(input);
                });
            } else if (screen.getFocused() instanceof BaseWidget baseWidget) {
                baseWidget.leftClick();
            } else {
                press(screen, InputConstants.KEY_RETURN);
                release(screen, InputConstants.KEY_RETURN);
            }
        }

        float axisValue = ctx.scrollY.getAction().getAxisValue();
        if (axisValue != 0) {
            axisValue = -axisValue;
            GuiEventListener focused = screen.getFocused();
            if (focused instanceof Tabs widget) {
                screen.mouseScrolled(widget.getX(), widget.getY() + 21, axisValue);
            } else if (focused instanceof LayoutElement widget) {
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
            press(screen, InputConstants.KEY_ESCAPE);
            release(screen, InputConstants.KEY_ESCAPE);
        } else if (ctx.dpadMove.getAction().isJustPressed()) {
            Minecraft.getInstance().setLastInputType(net.minecraft.client.InputType.KEYBOARD_ARROW);
            if (ctx.dpadMove.getAction().get2DValue().y > 0) {
                press(screen, InputConstants.KEY_UP);
            } else if (ctx.dpadMove.getAction().get2DValue().x < 0) {
                press(screen, InputConstants.KEY_LEFT);
            } else if (ctx.dpadMove.getAction().get2DValue().y < 0) {
                press(screen, InputConstants.KEY_DOWN);
            } else if (ctx.dpadMove.getAction().get2DValue().x > 0) {
                press(screen, InputConstants.KEY_RIGHT);
            }
        } else if (ctx.dpadMove.getAction().isJustReleased()) {
            Minecraft.getInstance().setLastInputType(net.minecraft.client.InputType.KEYBOARD_ARROW);
            if (ctx.dpadMove.getAction().get2DValue().y > 0) {
                release(screen, InputConstants.KEY_UP);
            } else if (ctx.dpadMove.getAction().get2DValue().x < 0) {
                release(screen, InputConstants.KEY_LEFT);
            } else if (ctx.dpadMove.getAction().get2DValue().y < 0) {
                release(screen, InputConstants.KEY_DOWN);
            } else if (ctx.dpadMove.getAction().get2DValue().x > 0) {
                release(screen, InputConstants.KEY_RIGHT);
            }
        }

        if (ctx.joystickMove.getAction().isJustPressed()) {
            Minecraft.getInstance().setLastInputType(net.minecraft.client.InputType.KEYBOARD_ARROW);
            if (ctx.joystickMove.getAction().get2DValue().y < 0) {
                press(screen, InputConstants.KEY_UP);
            } else if (ctx.joystickMove.getAction().get2DValue().x < 0) {
                press(screen, InputConstants.KEY_LEFT);
            } else if (ctx.joystickMove.getAction().get2DValue().y > 0) {
                press(screen, InputConstants.KEY_DOWN);
            } else if (ctx.joystickMove.getAction().get2DValue().x > 0) {
                press(screen, InputConstants.KEY_RIGHT);
            }
        }

        if (ctx.joystickMove.getAction().isJustReleased()) {
            Minecraft.getInstance().setLastInputType(net.minecraft.client.InputType.KEYBOARD_ARROW);
            if (ctx.joystickMove.getAction().get2DValue().y < 0) {
                release(screen, InputConstants.KEY_UP);
            } else if (ctx.joystickMove.getAction().get2DValue().x < 0) {
                release(screen, InputConstants.KEY_LEFT);
            } else if (ctx.joystickMove.getAction().get2DValue().y > 0) {
                release(screen, InputConstants.KEY_DOWN);
            } else if (ctx.joystickMove.getAction().get2DValue().x > 0) {
                release(screen, InputConstants.KEY_RIGHT);
            }
        }
    }

    private static boolean release(Screen screen, int esc) {
        return screen.keyReleased(esc, 0, 0);
    }

    private static boolean press(Screen screen, int esc) {
        return screen.keyPressed(esc, 0, 0);
    }

    private void handleChat(Screen screen, ChatControllerContext chatContext) {
        if (!(screen instanceof ChatScreen)) return;

        EditBox val = screen.children().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast).findAny().orElse(null);
        if (chatContext.openKeyboard.getAction().isJustPressed()) {
            if (val != null) {
                openVirtualKeyboard(val.getValue(), input -> {
                    if (input == null) {
                        throw new IllegalArgumentException("Input cannot be null");
                    }

                    val.setValue(input);
                }, () -> {
                    assert Minecraft.getInstance().screen != null;
                    press(Minecraft.getInstance().screen, InputConstants.KEY_RETURN);
                });
            } else {
                ControllerX.LOGGER.warn("Chat screen does not contain any edit boxes.");
            }
        } else if (chatContext.send.getAction().isJustPressed()) {
            press(screen, InputConstants.KEY_RETURN);
            release(screen, InputConstants.KEY_RETURN);
        } else if (chatContext.close.getAction().isJustPressed()) {
            press(screen, InputConstants.KEY_RETURN);
            release(screen, InputConstants.KEY_RETURN);
        }
    }

    public void updateScreen(Screen screen) {
        screenWasOpen = screen != null;

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

    @Override
    public void closeVirtualKeyboard() {
        virtualKeyboardValue = "";
        virtualKeyboardOpen = false;
        ControllerX.get().virtualKeyboard.close();
    }

    @Override
    public void openVirtualKeyboard(VirtualKeyboardEditCallback callback) {
        openVirtualKeyboard("", callback);
    }

    @Override
    public void openVirtualKeyboard(@NotNull String value, VirtualKeyboardEditCallback callback) {
        if (!Config.get().enableVirtualKeyboard) return;

        virtualKeyboardValue = value;
        virtualKeyboardOpen = true;

        ControllerX.get().virtualKeyboard.open(callback, () -> callback.onInput(mod.virtualKeyboard.getScreen().getInput()));
    }

    public void openVirtualKeyboard(@NotNull String value, VirtualKeyboardEditCallback callback, VirtualKeyboardSubmitCallback submitCallback) {
        virtualKeyboardValue = value;
        virtualKeyboardOpen = true;

        ControllerX.get().virtualKeyboard.open(callback, submitCallback);
    }

    public @NotNull String getVirtualKeyboardValue() {
        return virtualKeyboardValue;
    }

    @Override
    public boolean isVirtualKeyboardOpen() {
        return virtualKeyboardOpen;
    }

    @Override
    public boolean isJoystickRight() {
        return leftStick.x > 0 && isXAxis();
    }

    @Override
    public boolean isJoystickDown() {
        return leftStick.y > 0 && isYAxis();
    }

    @Override
    public boolean isJoystickLeft() {
        return leftStick.x < 0 && isXAxis();
    }

    @Override
    public boolean isJoystickUp() {
        return leftStick.y < 0 && isYAxis();
    }

    @Override
    public boolean isYAxis() {
        return Math.abs(leftStick.x) <= Math.abs(leftStick.y);
    }

    @Override
    public boolean isXAxis() {
        return Math.abs(leftStick.x) > Math.abs(leftStick.y);
    }

    @Override
    public float getAxis1(ControllerSignedFloat controllerAxis) {
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
        sdlController = SDL_GameControllerOpen(deviceIndex);
        if (sdlController == null) return;

        short productId = SDL_GameControllerGetProduct(sdlController);
        short vendorId = SDL_GameControllerGetVendor(sdlController);
        String name = SDL_GameControllerName(sdlController);
        String mapping = SDL_GameControllerMapping(sdlController);

        controller = new Controller(sdlController, deviceIndex, productId, vendorId, name, mapping);

        ControllerEvent.CONTROLLER_CONNECTED.invoker().onConnectionStatus(controller);
        Minecraft.getInstance().getToasts().addToast(new ControllerToast(Icon.AnyJoyStick, Component.translatable("controllerx.toast.controller_connected.title"), Component.translatable("controllerx.toast.controller_connected.description", name)).hideIn(Duration.ofSeconds(5)));

        ControllerX.LOGGER.info("Controller {} connected", name);
    }

    private void unsetController() {
        if (0 != controller.deviceIndex()) return;

        Minecraft.getInstance().getToasts().addToast(new ControllerToast(Icon.AnyJoyStick, Component.translatable("controllerx.toast.controller_disconnected.title"), Component.translatable("controllerx.toast.controller_disconnected.description", controller.name())).hideIn(Duration.ofSeconds(5)));

        sdlController = null;
        controller = null;

        ControllerEvent.CONTROLLER_DISCONNECTED.invoker().onConnectionStatus(controller);
        ControllerX.get().forceSetInputType(InputType.KEYBOARD_AND_MOUSE, 10);

        ControllerX.LOGGER.info("Controller disconnected");
    }

    @Override
    public @Nullable Controller getController() {
        return controller;
    }

    public @Nullable SDL_GameController getSDLController() {
        return sdlController;
    }

    @Override
    public boolean isButtonPressed(ControllerBoolean button) {
        return button.isPressed();
    }

    @Override
    public boolean isButtonJustPressed(ControllerBoolean button) {
        return button.isJustPressed();
    }

    @Override
    public boolean isButtonJustReleased(ControllerBoolean button) {
        return button.isJustReleased();
    }

    @Override
    public Vector2f getJoystick(ControllerVec2 joystick) {
        return joystick.get(tmp);
    }

    @Override
    public float getTrigger(ControllerUnsignedFloat trigger) {
        return trigger.getValue();
    }

    @Override
    public boolean isButtonPressed0(ControllerBoolean button) {
        int idx = button.sdlButton();
        boolean pressed = SDL_GameControllerGetButton(sdlController, idx) == SDL_PRESSED;

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) return pressed;

        return false;
    }

    @Override
    public boolean isConnected() {
        return controller != null && SDL_GameControllerGetAttached(controller.sdlController());
    }

    @Override
    public boolean isAvailable() {
        return isConnected() && ControllerX.get().getInputType() == InputType.CONTROLLER;
    }

    @Override
    public ControllerAction<?> actionFromKeyMap(Minecraft mc, KeyMapping mapping) {
        if (ControllerContext.get() instanceof InGameControllerContext context) {
            return getAction(mc, mapping, context);
        }
        return null;
    }

    @Override
    public boolean shouldRelease(Minecraft mc, KeyMapping mapping) {
        if (ControllerContext.get() instanceof InGameControllerContext context) {
            ControllerAction<?> action = getAction(mc, mapping, context);
            if (action != null) {
                return action.isJustReleased();
            }
        }
        return false;
    }

    @Override
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
        ControllerMapping<?> controllerMapping = context.getKeyToController().get(mapping);
        if (ControllerInput.moddedMappingsLoaded && controllerMapping != null)
            return controllerMapping.getAction();

        return null;
    }

    @Override
    public ControllerX getMod() {
        return mod;
    }

    @Override
    public void setLayout(KeyboardLayout layout) {
        this.layout = layout;
    }
    
    @Override
    public KeyboardLayout getLayout() {
        return layout;
    }

    public void handleVirtualKeyboardClosed(String value) {
        virtualKeyboardValue = value;
        virtualKeyboardOpen = false;
    }

    public boolean isTriggerJustPressed(ControllerSignedFloat axis) {
        return getAxis1(axis) > 0 && getOldAxis(axis) == 0;
    }

    @Override
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
        IInterceptInvalidation = new CountInvalidationI(1);
    }

    public void onKeyPress(InputConstants.Key key, boolean held) {
        // TODO: Implement
    }

}
