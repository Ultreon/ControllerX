package io.github.ultreon.controllerx.input;

import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerButton;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.Icon;
import io.github.ultreon.controllerx.util.InputDefinition;
import org.intellij.lang.annotations.MagicConstant;

public enum ControllerButton implements InputDefinition<Boolean> {
    A,
    B,
    X,
    Y,
    Back,
    Start,
    Guide,
    LeftStickClick,
    RightStickClick,
    DPadUp,
    DPadDOwn,
    DPadLeft,
    RPadRight,
    LeftShoulder,
    RightShoulder,
    LeftStickX,
    LeftStickY,
    RightStickX,
    RightStickY,
    LeftTrigger,
    RightTrigger,
    Misc1,
    Paddle1,
    Paddle2,
    Paddle3,
    Paddle4,
    Touchpad,
    Max,
    Invalid,
    Unknown;

    public static ControllerButton fromSDL(int value) {
        return switch (value) {
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_A -> A;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_B -> B;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_X -> X;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_Y -> Y;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_BACK -> Back;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_START -> Start;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_GUIDE -> Guide;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSTICK -> LeftStickClick;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSTICK -> RightStickClick;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_UP -> DPadUp;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_DOWN -> DPadDOwn;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_LEFT -> DPadLeft;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_RIGHT -> RPadRight;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSHOULDER -> LeftShoulder;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSHOULDER -> RightShoulder;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MISC1 -> Misc1;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE1 -> Paddle1;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE2 -> Paddle2;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE3 -> Paddle3;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE4 -> Paddle4;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_TOUCHPAD -> Touchpad;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX -> Max;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_INVALID -> Invalid;
            default -> Unknown;
        };
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
            case LeftStickClick -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSTICK;
            case RightStickClick -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSTICK;
            case DPadUp -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_UP;
            case DPadDOwn -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_DOWN;
            case DPadLeft -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_LEFT;
            case RPadRight -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_RIGHT;
            case LeftShoulder -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSHOULDER;
            case RightShoulder -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSHOULDER;
            case Misc1 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MISC1;
            case Paddle1 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE1;
            case Paddle2 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE2;
            case Paddle3 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE3;
            case Paddle4 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE4;
            case Touchpad -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_TOUCHPAD;
            case Max -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX;
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
            case Back, Max, Invalid, Unknown, Misc1, Paddle1, Paddle2, Paddle3, Paddle4 -> Icon.AnyButton;
            case Start -> Icon.XboxMenu;
            case Guide -> Icon.XboxGuide;
            case LeftStickClick -> Icon.LeftJoyStickPress;
            case RightStickClick -> Icon.RightJoyStickPress;
            case DPadUp -> Icon.DpadUp;
            case DPadDOwn -> Icon.DpadDown;
            case DPadLeft -> Icon.DpadLeft;
            case RPadRight -> Icon.DpadRight;
            case LeftShoulder -> Icon.LeftShoulder;
            case RightShoulder -> Icon.RightShoulder;
            case LeftStickX -> Icon.LeftJoyStickX;
            case LeftStickY -> Icon.LeftJoyStickY;
            case RightStickX -> Icon.RightJoyStickX;
            case RightStickY -> Icon.RightJoyStickY;
            case LeftTrigger -> Icon.LeftTrigger;
            case RightTrigger -> Icon.RightTrigger;
            case Touchpad -> Icon.PS4TouchPad;
        };
    }

    @Override
    public Boolean getValue() {
        return switch (this) {
            case A, B, X, Y, Back, Start, Guide, DPadLeft, RPadRight, DPadUp, DPadDOwn, LeftStickClick, RightStickClick,
                 LeftShoulder, RightShoulder, Misc1, Paddle1, Paddle2, Paddle3, Paddle4, Touchpad ->
                    ControllerX.get().controllerInput.isButtonPressed(this);
            case LeftStickX, LeftStickY, RightStickX, RightStickY, LeftTrigger, RightTrigger ->
                    ControllerX.get().controllerInput.getAxis(ControllerAxis.fromButton(this)) != 0;
            default -> false;
        };
    }

    public boolean isAxis() {
        return this == ControllerButton.LeftStickX
                || this == ControllerButton.LeftStickY
                || this == ControllerButton.RightStickX
                || this == ControllerButton.RightStickY
                || this == ControllerButton.LeftTrigger
                || this == ControllerButton.RightTrigger;
    }
}
