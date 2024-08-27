package io.github.ultreon.controllerx.input;

import com.ultreon.commons.collection.Pair;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerButton;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.Icon;
import io.github.ultreon.controllerx.input.dyn.ControllerInterDynamic;
import org.intellij.lang.annotations.MagicConstant;
import org.joml.Vector2f;

public enum ControllerBoolean implements ControllerInterDynamic<Boolean> {
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
        return this.value;
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
        this.lastValue = this.value;
        this.value = switch (this) {
            case A -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.A);
            case B -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.B);
            case X -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.X);
            case Y -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.Y);
            case Back -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.Back);
            case Start -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.Start);
            case Guide -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.Guide);
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
            case Touchpad -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.Touchpad);
            case DpadLeft -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadLeft);
            case DpadRight -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadRight);
            case DpadUp -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadUp);
            case DpadDown -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadDown);
            case AnyDpad -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadUp) || ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadLeft) || ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadRight) || ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadDown);
            case LeftStickClick -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.LeftStickClick);
            case RightStickClick -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.RightStickClick);
            case LeftShoulder -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.LeftShoulder);
            case RightShoulder -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.RightShoulder);
            case LeftTrigger -> ControllerX.get().input.getTrigger(ControllerUnsignedFloat.LeftTrigger) != 0;
            case RightTrigger -> ControllerX.get().input.getTrigger(ControllerUnsignedFloat.RightTrigger) != 0;
            case AnyTrigger -> ControllerX.get().input.getTrigger(ControllerUnsignedFloat.LeftTrigger) != 0 || ControllerX.get().input.getTrigger(ControllerUnsignedFloat.RightTrigger) != 0;
            case DpadX -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadLeft) || ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadRight);
            case DpadY -> ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadUp) || ControllerX.get().input.isButtonPressed0(ControllerBoolean.DpadDown);
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

    public @MagicConstant(valuesFromClass = SDL_GameControllerButton.class) int sdlButton() {
        return switch (this) {
            case A -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_A;
            case B -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_B;
            case X -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_X;
            case Y -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_Y;
            case Back -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_BACK;
            case Start -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_START;
            case Guide -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_GUIDE;
            case Touchpad -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_TOUCHPAD;
            case DpadLeft -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_LEFT;
            case DpadRight -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_RIGHT;
            case DpadUp -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_UP;
            case DpadDown -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_DOWN;
            case LeftStickClick -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSTICK;
            case RightStickClick -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSTICK;
            case LeftShoulder -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSHOULDER;
            case RightShoulder -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSHOULDER;
            default -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_INVALID;
        };
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
}
