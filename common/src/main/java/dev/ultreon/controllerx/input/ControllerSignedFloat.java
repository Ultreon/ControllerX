package dev.ultreon.controllerx.input;

import com.ultreon.commons.collection.Pair;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerAxis;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.Icon;
import dev.ultreon.controllerx.input.dyn.ControllerInterDynamic;
import org.apache.commons.lang3.EnumUtils;
import org.intellij.lang.annotations.MagicConstant;
import org.joml.Vector2f;

public enum ControllerSignedFloat implements ControllerInterDynamic<Float> {
    TriggerMagnitude,
    LeftStickMagnitude,
    RightStickMagnitude,
    DpadMagnitude,
    LeftTrigger,
    RightTrigger,
    Triggers,
    LeftStickX,
    LeftStickY,
    RightStickX,
    RightStickY,
    DpadX,
    DpadY,
    Unknown;

    public float getValue() {
        return switch (this) {
            case LeftTrigger -> ControllerX.get().input.getAxis1(ControllerSignedFloat.LeftTrigger) * 2 - 1;
            case RightTrigger -> ControllerX.get().input.getAxis1(ControllerSignedFloat.RightTrigger) * 2 - 1;
            case Triggers -> ControllerX.get().input.getAxis1(ControllerSignedFloat.LeftTrigger) - ControllerX.get().input.getAxis1(ControllerSignedFloat.RightTrigger);
            case LeftStickX -> ControllerX.get().input.getAxis1(ControllerSignedFloat.LeftStickX);
            case LeftStickY -> ControllerX.get().input.getAxis1(ControllerSignedFloat.LeftStickY);
            case RightStickX -> ControllerX.get().input.getAxis1(ControllerSignedFloat.RightStickX);
            case RightStickY -> ControllerX.get().input.getAxis1(ControllerSignedFloat.RightStickY);
            case DpadX -> of(ControllerBoolean.DpadLeft, ControllerBoolean.DpadRight);
            case DpadY -> of(ControllerBoolean.DpadDown, ControllerBoolean.DpadUp);
            case LeftStickMagnitude -> ControllerVec2.LeftStick.getMagnitude() * 2 - 1;
            case RightStickMagnitude -> ControllerVec2.RightStick.getMagnitude() * 2 - 1;
            case DpadMagnitude -> ControllerVec2.Dpad.getMagnitude() * 2 - 1;
            case TriggerMagnitude -> ControllerVec2.Triggers.getMagnitude() * 2 - 1;
            default -> 0;
        };
    }

    private float of(ControllerBoolean a, ControllerBoolean b) {
        float v = 0;
        if (a.getValue()) v--;
        if (b.getValue()) v++;
        return v;
    }

    @Override
    public ControllerBoolean asBoolean() {
        return switch (this) {
            case LeftTrigger -> ControllerBoolean.LeftTrigger;
            case RightTrigger -> ControllerBoolean.RightTrigger;
            case Triggers, TriggerMagnitude -> ControllerBoolean.AnyTrigger;
            case LeftStickX -> ControllerBoolean.LeftStickLeft;
            case LeftStickY -> ControllerBoolean.LeftStickUp;
            case RightStickX -> ControllerBoolean.RightStickLeft;
            case RightStickY -> ControllerBoolean.RightStickUp;
            case DpadX -> ControllerBoolean.DpadX;
            case DpadY -> ControllerBoolean.DpadY;
            case LeftStickMagnitude -> ControllerBoolean.LeftStickUsed;
            case RightStickMagnitude -> ControllerBoolean.RightStickUsed;
            case DpadMagnitude -> ControllerBoolean.DpadUsed;
            default -> ControllerBoolean.Unknown;
        };
    }

    @Override
    public Pair<ControllerBoolean, Boolean> asBoolean(Float value) {
        return switch (this) {
            case LeftTrigger -> new Pair<>(ControllerBoolean.LeftTrigger, value > 0);
            case RightTrigger -> new Pair<>(ControllerBoolean.RightTrigger, value > 0);
            case LeftStickX -> new Pair<>(ControllerBoolean.LeftStickLeft, value < 0);
            case LeftStickY -> new Pair<>(ControllerBoolean.LeftStickUp, value > 0);
            case RightStickX -> new Pair<>(ControllerBoolean.RightStickLeft, value < 0);
            case RightStickY -> new Pair<>(ControllerBoolean.RightStickUp, value > 0);
            case DpadX -> new Pair<>(ControllerBoolean.DpadX, value > 0);
            case DpadY -> new Pair<>(ControllerBoolean.DpadY, value > 0);
            case LeftStickMagnitude -> new Pair<>(ControllerBoolean.LeftStickUsed, value > 0);
            case RightStickMagnitude -> new Pair<>(ControllerBoolean.RightStickUsed, value > 0);
            case DpadMagnitude -> new Pair<>(ControllerBoolean.DpadUsed, value > 0);
            case TriggerMagnitude -> new Pair<>(ControllerBoolean.AnyTrigger, value > 0);
            default -> new Pair<>(ControllerBoolean.Unknown, false);
        };
    }

    @Override
    public ControllerUnsignedFloat asUnsignedFloat() {
        return switch (this) {
            case LeftStickX -> ControllerUnsignedFloat.LeftStickX;
            case LeftStickY -> ControllerUnsignedFloat.LeftStickY;
            case RightStickX -> ControllerUnsignedFloat.RightStickX;
            case RightStickY -> ControllerUnsignedFloat.RightStickY;
            case LeftStickMagnitude -> ControllerUnsignedFloat.LeftStickMagnitude;
            case RightStickMagnitude -> ControllerUnsignedFloat.RightStickMagnitude;
            case DpadX -> ControllerUnsignedFloat.DpadX;
            case DpadY -> ControllerUnsignedFloat.DpadY;
            case DpadMagnitude -> ControllerUnsignedFloat.DpadMagnitude;
            case TriggerMagnitude -> ControllerUnsignedFloat.TriggerMagnitude;
            default -> ControllerUnsignedFloat.Unknown;
        };
    }

    @Override
    public ControllerSignedFloat asSignedFloat() {
        return this;
    }

    @Override
    public Pair<ControllerUnsignedFloat, Float> asUnsignedFloat(Float value) {
        return new Pair<>(asUnsignedFloat(), value * 2 - 1);
    }

    @Override
    public Pair<ControllerSignedFloat, Float> asSignedFloat(Float value) {
        return new Pair<>(this, value);
    }

    @Override
    public Pair<ControllerVec2, Vector2f> asVec2(Float value, Vector2f result) {
        Pair<ControllerSignedFloat, Float> signedFloat = asSignedFloat(value);
        return signedFloat.first().asVec2(signedFloat.second(), result);
    }

    @Override
    public ControllerVec2 asVec2() {
        return switch (this) {
            case LeftStickX, LeftStickY, LeftStickMagnitude -> ControllerVec2.LeftStick;
            case RightStickX, RightStickY, RightStickMagnitude -> ControllerVec2.RightStick;
            case DpadX, DpadY, DpadMagnitude -> ControllerVec2.Dpad;
            case TriggerMagnitude -> ControllerVec2.Triggers;
            default -> ControllerVec2.Unknown;
        };
    }

    @Override
    public Icon getIcon() {
        return switch (this) {
            case LeftTrigger, TriggerMagnitude -> Icon.LeftTrigger;
            case RightTrigger -> Icon.RightTrigger;
            case LeftStickX -> Icon.LeftJoyStickX;
            case LeftStickY -> Icon.LeftJoyStickY;
            case RightStickX -> Icon.RightJoyStickX;
            case RightStickY -> Icon.RightJoyStickY;
            case LeftStickMagnitude -> Icon.LeftJoyStickMove;
            case RightStickMagnitude -> Icon.RightJoyStickMove;
            case DpadX -> Icon.DpadLeftRight;
            case DpadY -> Icon.DpadUpDown;
            case DpadMagnitude -> Icon.Dpad;
            default -> Icon.AnyJoyStick;
        };
    }

    @Override
    public ControllerSignedFloat fromName(String text) {
        return EnumUtils.getEnum(ControllerSignedFloat.class, text);
    }

    public @MagicConstant(valuesFromClass = SDL_GameControllerAxis.class) int sdlAxis() {
        return switch (this) {
            case LeftTrigger -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERLEFT;
            case RightTrigger -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERRIGHT;
            case LeftStickX -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTX;
            case LeftStickY -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTY;
            case RightStickX -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTX;
            case RightStickY -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTY;
            default -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_INVALID;
        };
    }
}
