package io.github.ultreon.controllerx.input;

import com.ultreon.commons.collection.Pair;
import io.github.ultreon.controllerx.Icon;
import io.github.ultreon.controllerx.input.dyn.ControllerInterDynamic;
import org.joml.Vector2f;

public enum ControllerVec2 implements ControllerInterDynamic<Vector2f> {
    LeftStick,
    RightStick,
    Dpad,
    Triggers,
    Unknown;

    private static final Vector2f VEC = new Vector2f();

    public float getX() {
        return switch (this) {
            case LeftStick -> ControllerSignedFloat.LeftStickX.getValue();
            case RightStick -> ControllerSignedFloat.RightStickX.getValue();
            case Dpad -> ControllerSignedFloat.DpadX.getValue();
            case Triggers -> ControllerSignedFloat.LeftTrigger.getValue();
            default -> 0f;
        };
    }

    public float getY() {
        return switch (this) {
            case LeftStick -> ControllerSignedFloat.LeftStickY.getValue();
            case RightStick -> ControllerSignedFloat.RightStickY.getValue();
            case Dpad -> ControllerSignedFloat.DpadY.getValue();
            case Triggers -> ControllerSignedFloat.RightTrigger.getValue();
            default -> 0f;
        };
    }

    @Deprecated
    public Vector2f get() {
        return new Vector2f(getX(), getY());
    }

    public Vector2f get(Vector2f out) {
        return out.set(getX(), getY());
    }

    /**
     * A value between 0 and 1 that represents the magnitude of the vector
     *
     * @return the magnitude
     */
    public float getMagnitude() {
        return get(VEC).length();
    }

    @Override
    public ControllerBoolean asBoolean() {
        return switch (this) {
            case LeftStick -> ControllerBoolean.LeftStickAny;
            case RightStick -> ControllerBoolean.RightStickAny;
            case Dpad -> ControllerBoolean.AnyDpad;
            case Triggers -> ControllerBoolean.AnyTrigger;
            default -> ControllerBoolean.Unknown;
        };
    }

    @Override
    public ControllerSignedFloat asSignedFloat() {
        return switch (this) {
            case LeftStick -> ControllerSignedFloat.LeftStickMagnitude;
            case RightStick -> ControllerSignedFloat.RightStickMagnitude;
            case Dpad -> ControllerSignedFloat.DpadMagnitude;
            case Triggers -> ControllerSignedFloat.TriggerMagnitude;
            default -> ControllerSignedFloat.Unknown;
        };
    }

    @Override
    public ControllerUnsignedFloat asUnsignedFloat() {
        return switch (this) {
            case LeftStick -> ControllerUnsignedFloat.LeftStickMagnitude;
            case RightStick -> ControllerUnsignedFloat.RightStickMagnitude;
            case Dpad -> ControllerUnsignedFloat.DpadMagnitude;
            case Triggers -> ControllerUnsignedFloat.TriggerMagnitude;
            default -> ControllerUnsignedFloat.Unknown;
        };
    }

    @Override
    public Pair<ControllerSignedFloat, Float> asSignedFloat(Vector2f value) {
        return new Pair<>(asSignedFloat(), value.x != 0 || value.y != 0 ? 1f : 0f);
    }

    @Override
    public Pair<ControllerUnsignedFloat, Float> asUnsignedFloat(Vector2f value) {
        return new Pair<>(asUnsignedFloat(), value.x != 0 || value.y != 0 ? 1f : 0f);
    }

    @Override
    public Pair<ControllerVec2, Vector2f> asVec2(Vector2f value, Vector2f result) {
        return new Pair<>(asVec2(), result.set(getX(), getY()));
    }

    @Override
    public ControllerVec2 asVec2() {
        return this;
    }

    @Override
    public Pair<ControllerBoolean, Boolean> asBoolean(Vector2f value) {
        return new Pair<>(asBoolean(), value.x != 0 || value.y != 0);
    }



    @Override
    public Icon getIcon() {
        return switch (this) {
            case LeftStick -> Icon.LeftJoyStickMove;
            case RightStick -> Icon.RightJoyStickMove;
            case Dpad -> Icon.DpadAny;
            case Triggers -> Icon.LeftTrigger;
            default -> Icon.AnyJoyStick;
        };
    }
}
