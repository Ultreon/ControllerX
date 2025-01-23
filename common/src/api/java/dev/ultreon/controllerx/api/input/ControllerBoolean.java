package dev.ultreon.controllerx.api.input;

import com.ultreon.commons.collection.Pair;
import dev.ultreon.controllerx.api.IControllerX;
import dev.ultreon.controllerx.api.Icon;
import dev.ultreon.controllerx.api.input.dyn.IControllerInterDynamic;
import org.apache.commons.lang3.EnumUtils;
import org.joml.Vector2f;

public enum ControllerBoolean implements IControllerInterDynamic<Boolean> {
    AnyButton,
    A,
    B,
    X,
    Y,
    Back,
    Start,
    Guide,
    AnyJoyStick,
    LeftStickAny,
    RightStickAny,
    LeftStickUsed,
    RightStickUsed,
    LeftStickX,
    LeftStickY,
    RightStickX,
    RightStickY,
    LeftStickLeft,
    LeftStickRight,
    LeftStickUp,
    LeftStickDown,
    RightStickLeft,
    RightStickRight,
    RightStickUp,
    RightStickDown,
    Touchpad,
    AnyDpad,
    DpadX,
    DpadY,
    DpadLeft,
    DpadRight,
    DpadUp,
    DpadDown,
    DpadUsed,
    LeftStickClick,
    RightStickClick,
    LeftShoulder,
    RightShoulder,
    AnyTrigger,
    LeftTrigger,
    RightTrigger,
    Unknown;

    private boolean lastValue = false;
    private boolean value = false;

    public boolean getValue() {
        return value;
    }

    public boolean getPrevValue() {
        return lastValue;
    }

    @Override
    public ControllerSignedFloat asSignedFloat() {
        return switch (this) {
            case LeftStickX -> ControllerSignedFloat.LeftStickX;
            case LeftStickY -> ControllerSignedFloat.LeftStickY;
            case RightStickX -> ControllerSignedFloat.RightStickX;
            case RightStickY -> ControllerSignedFloat.RightStickY;
            case LeftStickUsed -> ControllerSignedFloat.LeftStickMagnitude;
            case RightStickUsed -> ControllerSignedFloat.RightStickMagnitude;
            case DpadX -> ControllerSignedFloat.DpadX;
            case DpadY -> ControllerSignedFloat.DpadY;
            case DpadUsed -> ControllerSignedFloat.DpadMagnitude;
            default -> ControllerSignedFloat.Unknown;
        };
    }

    @Override
    public ControllerUnsignedFloat asUnsignedFloat() {
        return switch (this) {
            case LeftStickX -> ControllerUnsignedFloat.LeftStickX;
            case LeftStickY -> ControllerUnsignedFloat.LeftStickY;
            case RightStickX -> ControllerUnsignedFloat.RightStickX;
            case RightStickY -> ControllerUnsignedFloat.RightStickY;
            case LeftStickUsed -> ControllerUnsignedFloat.LeftStickMagnitude;
            case RightStickUsed -> ControllerUnsignedFloat.RightStickMagnitude;
            case DpadX -> ControllerUnsignedFloat.DpadX;
            case DpadY -> ControllerUnsignedFloat.DpadY;
            case DpadUsed -> ControllerUnsignedFloat.DpadMagnitude;
            default -> ControllerUnsignedFloat.Unknown;
        };
    }

    @Override
    public ControllerVec2 asVec2() {
        return switch (this) {
            case LeftTrigger, RightTrigger -> ControllerVec2.Triggers;
            case LeftStickX, LeftStickY, LeftStickUsed -> ControllerVec2.LeftStick;
            case RightStickX, RightStickY, RightStickUsed -> ControllerVec2.RightStick;
            case DpadX, DpadY, DpadUsed -> ControllerVec2.Dpad;
            default -> ControllerVec2.Unknown;
        };
    }

    @Override
    public Pair<ControllerSignedFloat, Float> asSignedFloat(Boolean value) {
        return switch (this) {
            case LeftStickX -> new Pair<>(ControllerSignedFloat.LeftStickX, value ? 1f : -1f);
            case LeftStickY -> new Pair<>(ControllerSignedFloat.LeftStickY, value ? 1f : -1f);
            case RightStickX -> new Pair<>(ControllerSignedFloat.RightStickX, value ? 1f : -1f);
            case RightStickY -> new Pair<>(ControllerSignedFloat.RightStickY, value ? 1f : -1f);
            case LeftStickUsed -> new Pair<>(ControllerSignedFloat.LeftStickMagnitude, value ? 1f : -1f);
            case RightStickUsed -> new Pair<>(ControllerSignedFloat.RightStickMagnitude, value ? 1f : -1f);
            case DpadX -> new Pair<>(ControllerSignedFloat.DpadX, value ? 1f : -1f);
            case DpadY -> new Pair<>(ControllerSignedFloat.DpadY, value ? 1f : -1f);
            case DpadUsed -> new Pair<>(ControllerSignedFloat.DpadMagnitude, value ? 1f : -1f);
            default -> new Pair<>(ControllerSignedFloat.Unknown, 0f);
        };
    }

    @Override
    public Pair<ControllerUnsignedFloat, Float> asUnsignedFloat(Boolean value) {
        return switch (this) {
            case LeftStickX -> new Pair<>(ControllerUnsignedFloat.LeftStickX, value ? 1f : 0f);
            case LeftStickY -> new Pair<>(ControllerUnsignedFloat.LeftStickY, value ? 1f : 0f);
            case RightStickX -> new Pair<>(ControllerUnsignedFloat.RightStickX, value ? 1f : 0f);
            case RightStickY -> new Pair<>(ControllerUnsignedFloat.RightStickY, value ? 1f : 0f);
            case LeftStickUsed -> new Pair<>(ControllerUnsignedFloat.LeftStickMagnitude, value ? 1f : 0f);
            case RightStickUsed -> new Pair<>(ControllerUnsignedFloat.RightStickMagnitude, value ? 1f : 0f);
            case DpadX -> new Pair<>(ControllerUnsignedFloat.DpadX, value ? 1f : 0f);
            case DpadY -> new Pair<>(ControllerUnsignedFloat.DpadY, value ? 1f : 0f);
            case DpadUsed -> new Pair<>(ControllerUnsignedFloat.DpadMagnitude, value ? 1f : 0f);
            default -> new Pair<>(ControllerUnsignedFloat.Unknown, 0f);
        };
    }

    @Override
    public Pair<ControllerVec2, Vector2f> asVec2(Boolean value, Vector2f result) {
        return switch (this) {
            case LeftTrigger -> new Pair<>(ControllerVec2.Triggers, value ? result.set(1, 0) : result.set(-1, 0));
            case RightTrigger -> new Pair<>(ControllerVec2.Triggers, value ? result.set(0, 1) : result.set(0, -1));
            case LeftStickX -> new Pair<>(ControllerVec2.LeftStick, value ? result.set(1, 0) : result.set(-1, 0));
            case LeftStickY -> new Pair<>(ControllerVec2.LeftStick, value ? result.set(0, 1) : result.set(0, -1));
            case RightStickX -> new Pair<>(ControllerVec2.RightStick, value ? result.set(1, 0) : result.set(-1, 0));
            case RightStickY -> new Pair<>(ControllerVec2.RightStick, value ? result.set(0, 1) : result.set(0, -1));
            case LeftStickUsed -> new Pair<>(ControllerVec2.LeftStick, value ? result.set(1, 1) : result.set(-1, -1));
            case RightStickUsed -> new Pair<>(ControllerVec2.RightStick, value ? result.set(1, 1) : result.set(-1, -1));
            case DpadX -> new Pair<>(ControllerVec2.Dpad, value ? result.set(1, 0) : result.set(-1, 0));
            case DpadY -> new Pair<>(ControllerVec2.Dpad, value ? result.set(0, 1) : result.set(0, -1));
            case DpadUsed -> new Pair<>(ControllerVec2.Dpad, value ? result.set(1, 1) : result.set(-1, -1));
            default -> new Pair<>(ControllerVec2.Unknown, result.set(0, 0));
        };
    }

    @Override
    public Pair<ControllerBoolean, Boolean> asBoolean(Boolean value) {
        return new Pair<>(this, value);
    }

    @Override
    public ControllerBoolean asBoolean() {
        return this;
    }

    public static void pollAll() {
        for (ControllerBoolean value : values()) {
            value.poll();
        }
    }

    private void poll() {
        lastValue = value;
        value = switch (this) {
            case A -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.A);
            case B -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.B);
            case X -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.X);
            case Y -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.Y);
            case Back -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.Back);
            case Start -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.Start);
            case Guide -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.Guide);
            case LeftStickLeft -> ControllerSignedFloat.LeftStickX.getValue() < 0;
            case LeftStickRight -> ControllerSignedFloat.LeftStickX.getValue() > 0;
            case LeftStickUp -> ControllerSignedFloat.LeftStickY.getValue() < 0;
            case LeftStickDown -> ControllerSignedFloat.LeftStickY.getValue() > 0;
            case LeftStickAny -> ControllerSignedFloat.LeftStickY.getValue() != 0 || ControllerSignedFloat.LeftStickX.getValue() != 0;
            case RightStickLeft -> ControllerSignedFloat.RightStickX.getValue() < 0;
            case RightStickRight -> ControllerSignedFloat.RightStickX.getValue() > 0;
            case RightStickUp -> ControllerSignedFloat.RightStickY.getValue() < 0;
            case RightStickDown -> ControllerSignedFloat.RightStickY.getValue() > 0;
            case RightStickAny -> ControllerSignedFloat.RightStickY.getValue() != 0 || ControllerSignedFloat.RightStickX.getValue() != 0;
            case AnyJoyStick -> ControllerSignedFloat.LeftStickY.getValue() != 0 || ControllerSignedFloat.LeftStickX.getValue() != 0 || ControllerUnsignedFloat.RightStickY.getValue() != 0 || ControllerUnsignedFloat.RightStickX.getValue() != 0;
            case LeftStickX -> ControllerSignedFloat.LeftStickX.getValue() != 0;
            case LeftStickY -> ControllerSignedFloat.LeftStickY.getValue() != 0;
            case RightStickX -> ControllerSignedFloat.RightStickX.getValue() != 0;
            case RightStickY -> ControllerSignedFloat.RightStickY.getValue() != 0;
            case LeftStickUsed -> ControllerUnsignedFloat.LeftStickMagnitude.getValue() != 0;
            case RightStickUsed -> ControllerUnsignedFloat.RightStickMagnitude.getValue() != 0;
            case Touchpad -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.Touchpad);
            case DpadLeft -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadLeft);
            case DpadRight -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadRight);
            case DpadUp -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadUp);
            case DpadDown -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadDown);
            case AnyDpad -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadUp) || IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadLeft) || IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadRight) || IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadDown);
            case LeftStickClick -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.LeftStickClick);
            case RightStickClick -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.RightStickClick);
            case LeftShoulder -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.LeftShoulder);
            case RightShoulder -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.RightShoulder);
            case LeftTrigger -> IControllerX.get().getInput().getTrigger(ControllerUnsignedFloat.LeftTrigger) != 0;
            case RightTrigger -> IControllerX.get().getInput().getTrigger(ControllerUnsignedFloat.RightTrigger) != 0;
            case AnyTrigger -> IControllerX.get().getInput().getTrigger(ControllerUnsignedFloat.LeftTrigger) != 0 || IControllerX.get().getInput().getTrigger(ControllerUnsignedFloat.RightTrigger) != 0;
            case DpadX -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadLeft) || IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadRight);
            case DpadY -> IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadUp) || IControllerX.get().getInput().isButtonPressed0(ControllerBoolean.DpadDown);
            case DpadUsed -> ControllerSignedFloat.DpadMagnitude.getValue() != 0;
            default -> false;
        };
    }

    public boolean isPressed() {
        return value;
    }

    public boolean isJustPressed() {
        return value && !lastValue;
    }

    public boolean isJustReleased() {
        return !value && lastValue;
    }

    @Override
    public Icon getIcon() {
        return switch (this) {
            case A -> Icon.ButtonA;
            case B -> Icon.ButtonB;
            case X -> Icon.ButtonX;
            case Y -> Icon.ButtonY;
            case AnyJoyStick -> Icon.AnyJoyStick;
            case LeftStickAny -> Icon.LeftJoyStick;
            case RightStickAny -> Icon.RightJoyStick;
            case LeftStickX -> Icon.LeftJoyStickX;
            case LeftStickY -> Icon.LeftJoyStickY;
            case RightStickX -> Icon.RightJoyStickX;
            case RightStickY -> Icon.RightJoyStickY;
            case LeftStickUsed -> Icon.LeftJoyStickMove;
            case RightStickUsed -> Icon.RightJoyStickMove;
            case DpadLeft -> Icon.DpadLeft;
            case DpadRight -> Icon.DpadRight;
            case DpadUp -> Icon.DpadUp;
            case DpadDown -> Icon.DpadDown;
            case LeftStickClick -> Icon.LeftJoyStickPress;
            case RightStickClick -> Icon.RightJoyStickPress;
            case LeftShoulder -> Icon.LeftShoulder;
            case RightShoulder -> Icon.RightShoulder;
            case LeftTrigger -> Icon.LeftTrigger;
            case RightTrigger -> Icon.RightTrigger;
            case AnyDpad -> Icon.DpadAny;
            case DpadX -> Icon.DpadLeftRight;
            case DpadY -> Icon.DpadUpDown;
            case DpadUsed -> Icon.Dpad;
            case Start -> Icon.XboxGuide;
            case Back -> Icon.XboxMenu;
            case Guide -> Icon.Start;
            case LeftStickDown -> Icon.LeftJoyStickDown;
            case LeftStickUp -> Icon.LeftJoyStickUp;
            case LeftStickLeft -> Icon.LeftJoyStickLeft;
            case LeftStickRight -> Icon.LeftJoyStickRight;
            case RightStickDown -> Icon.RightJoyStickDown;
            case RightStickUp -> Icon.RightJoyStickUp;
            case RightStickLeft -> Icon.RightJoyStickLeft;
            case RightStickRight -> Icon.RightJoyStickRight;
            default -> Icon.AnyButton;
        };
    }

    @Override
    public ControllerBoolean fromName(String text) {
        return EnumUtils.getEnum(ControllerBoolean.class, text);
    }
}
