package dev.ultreon.controllerx.api.input;

import dev.ultreon.controllerx.api.IControllerAction;
import dev.ultreon.controllerx.api.IControllerX;
import dev.ultreon.controllerx.api.VirtualKeyboardEditCallback;
import dev.ultreon.controllerx.api.input.dyn.IControllerInterDynamic;
import dev.ultreon.controllerx.api.input.keyboard.keyboard.KeyboardLayout;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

public interface IControllerInput {
    void closeVirtualKeyboard();

    void openVirtualKeyboard(VirtualKeyboardEditCallback callback);

    void openVirtualKeyboard(@NotNull String value, VirtualKeyboardEditCallback callback);

    boolean isVirtualKeyboardOpen();

    boolean isJoystickRight();

    boolean isJoystickDown();

    boolean isJoystickLeft();

    boolean isJoystickUp();

    boolean isYAxis();

    boolean isXAxis();

    float getAxis1(ControllerSignedFloat controllerSignedFloat);

    boolean isButtonPressed0(ControllerBoolean controllerBoolean);

    @Nullable IController getController();

    boolean isButtonPressed(ControllerBoolean button);

    boolean isButtonJustPressed(ControllerBoolean button);

    boolean isButtonJustReleased(ControllerBoolean button);

    Vector2f getJoystick(ControllerVec2 joystick);

    float getTrigger(ControllerUnsignedFloat controllerUnsignedFloat);

    boolean isConnected();

    boolean isAvailable();

    IControllerAction<?> actionFromKeyMap(Minecraft mc, KeyMapping mapping);

    boolean shouldRelease(Minecraft mc, KeyMapping mapping);

    boolean isDown(Minecraft mc, KeyMapping mapping);

    IControllerX getMod();

    void setLayout(KeyboardLayout layout);

    KeyboardLayout getLayout();

    boolean hasAnyInput();

    @FunctionalInterface
    interface InterceptCallback {
        void onIntercept(EventObject<?, ?> type);
    }

    record EventObject<V, T extends Enum<T> & IControllerInterDynamic<V>>(EventType<? extends T> type, T mapping,
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

    class EventType<T> {
        public static final EventType<ControllerSignedFloat> AXIS = new EventType<>(ControllerSignedFloat.class);
        public static final EventType<ControllerBoolean> BUTTON = new EventType<>(ControllerBoolean.class);
        public static final EventType<ControllerVec2> JOYSTICK = new EventType<>(ControllerVec2.class);
        public static final EventType<ControllerUnsignedFloat> TRIGGER = new EventType<>(ControllerUnsignedFloat.class);

        private final Class<T> type;

        private EventType(Class<T> type) {
            this.type = type;
        }

        public Class<T> getType() {
            return type;
        }
    }
}
