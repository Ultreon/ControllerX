package io.github.ultreon.controllerx.input;

import com.ultreon.commons.collection.Pair;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerAxis;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.Icon;
import io.github.ultreon.controllerx.input.dyn.ControllerInterDynamic;
import org.intellij.lang.annotations.MagicConstant;
import org.joml.Vector2f;

public enum ControllerUnsignedFloat implements ControllerInterDynamic<Float> {
    TriggerMagnitude,
    LeftStickMagnitude,
    RightStickMagnitude,
    DpadMagnitude,
    LeftTrigger,
    RightTrigger,
    LeftStickX,
    LeftStickY,
    RightStickX,
    RightStickY,
    DpadX,
    DpadY,
    Unknown;

    public float getValue() {
        return switch (this) {
            case LeftTrigger -> ControllerX.get().input.getAxis1(ControllerSignedFloat.LeftTrigger);
            case RightTrigger -> ControllerX.get().input.getAxis1(ControllerSignedFloat.RightTrigger);
            case LeftStickX -> (ControllerX.get().input.getAxis1(ControllerSignedFloat.LeftStickX) + 1) / 2;
            case LeftStickY -> (ControllerX.get().input.getAxis1(ControllerSignedFloat.LeftStickY) + 1) / 2;
            case RightStickX -> (ControllerX.get().input.getAxis1(ControllerSignedFloat.RightStickX) + 1) / 2;
            case RightStickY -> (ControllerX.get().input.getAxis1(ControllerSignedFloat.RightStickY) + 1) / 2;
            case DpadX -> (of(ControllerBoolean.DpadLeft, ControllerBoolean.DpadRight) + 1) / 2;
            case DpadY -> (of(ControllerBoolean.DpadUp, ControllerBoolean.DpadDown) + 1) / 2;
            case LeftStickMagnitude -> ControllerVec2.LeftStick.getMagnitude();
            case RightStickMagnitude -> ControllerVec2.RightStick.getMagnitude();
            case DpadMagnitude -> ControllerVec2.Dpad.getMagnitude();
            case TriggerMagnitude -> ControllerVec2.Triggers.getMagnitude();
            default -> 0;
        };
    }

    private float of(ControllerBoolean controllerBoolean, ControllerBoolean controllerBoolean1) {
        if (controllerBoolean.getValue() && controllerBoolean1.getValue()) return 0;
        else if (controllerBoolean.getValue()) return -1;
        else if (controllerBoolean1.getValue()) return 1;
        else return 0;
    }

    public ControllerBoolean asBoolean() {
        return switch (this) {
            case LeftTrigger -> ControllerBoolean.LeftTrigger;
            case RightTrigger -> ControllerBoolean.RightTrigger;
            case LeftStickX -> ControllerBoolean.LeftStickX;
            case LeftStickY -> ControllerBoolean.LeftStickY;
            case RightStickX -> ControllerBoolean.RightStickX;
            case RightStickY -> ControllerBoolean.RightStickY;
            case DpadX -> ControllerBoolean.DpadX;
            case DpadY -> ControllerBoolean.DpadY;
            case LeftStickMagnitude -> ControllerBoolean.LeftStickUsed;
            case RightStickMagnitude -> ControllerBoolean.RightStickUsed;
            case DpadMagnitude -> ControllerBoolean.DpadUsed;
            default -> ControllerBoolean.Unknown;
        };
    }

    @Override
    public ControllerUnsignedFloat asUnsignedFloat() {
        return this;
    }

    @Override
    public ControllerSignedFloat asSignedFloat() {
        return switch (this) {
            case LeftStickX -> ControllerSignedFloat.LeftStickX;
            case LeftStickY -> ControllerSignedFloat.LeftStickY;
            case RightStickX -> ControllerSignedFloat.RightStickX;
            case RightStickY -> ControllerSignedFloat.RightStickY;
            case DpadX -> ControllerSignedFloat.DpadX;
            case DpadY -> ControllerSignedFloat.DpadY;
            case LeftStickMagnitude -> ControllerSignedFloat.LeftStickMagnitude;
            case RightStickMagnitude -> ControllerSignedFloat.RightStickMagnitude;
            case DpadMagnitude -> ControllerSignedFloat.DpadMagnitude;
            case TriggerMagnitude -> ControllerSignedFloat.TriggerMagnitude;
            default -> ControllerSignedFloat.Unknown;
        };
    }

    @Override
    public Pair<ControllerUnsignedFloat, Float> asUnsignedFloat(Float value) {
        return new Pair<>(this, value);
    }

    @Override
    public Pair<ControllerBoolean, Boolean> asBoolean(Float value) {
        return new Pair<>(asBoolean(), value != 0);
    }

    @Override
    public Pair<ControllerSignedFloat, Float> asSignedFloat(Float value) {
        return new Pair<>(asSignedFloat(), value);
    }

    @Override
    public ControllerVec2 asVec2() {
        return switch (this) {
            case LeftStickX, LeftStickY, LeftStickMagnitude -> ControllerVec2.LeftStick;
            case RightStickX, RightStickY, RightStickMagnitude -> ControllerVec2.RightStick;
            case DpadX, DpadY, DpadMagnitude -> ControllerVec2.Dpad;
            default -> ControllerVec2.Unknown;
        };
    }

    @Override
    public Pair<ControllerVec2, Vector2f> asVec2(Float value, Vector2f result) {
        return switch (this) {
            case LeftStickX -> new Pair<>(ControllerVec2.LeftStick, result.set(value, 0));
            case LeftStickY -> new Pair<>(ControllerVec2.LeftStick, result.set(0, value));
            case RightStickX -> new Pair<>(ControllerVec2.RightStick, result.set(value, 0));
            case RightStickY -> new Pair<>(ControllerVec2.RightStick, result.set(0, value));
            case LeftStickMagnitude -> new Pair<>(ControllerVec2.LeftStick, result.set(value, value));
            case RightStickMagnitude -> new Pair<>(ControllerVec2.RightStick, result.set(value, value));
            case DpadX -> new Pair<>(ControllerVec2.Dpad, result.set(value, 0));
            case DpadY -> new Pair<>(ControllerVec2.Dpad, result.set(0, value));
            case DpadMagnitude -> new Pair<>(ControllerVec2.Dpad, result.set(value, value));
            default -> new Pair<>(ControllerVec2.Unknown, result.set(0, 0));
        };
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
}
