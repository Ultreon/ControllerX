package io.github.ultreon.controllerx.input;

import io.github.libsdl4j.api.SdlSubSystemConst;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerButton;

public enum ControllerButton {
    A, B, X, Y, BACK, START, GUIDE, LEFT_STICK, RIGHT_STICK, DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT, LEFT_SHOULDER, RIGHT_SHOULDER, MISC1, PADDLE1, PADDLE2, PADDLE3, PADDLE4, TOUCHPAD, MAX, INVALID, UNKNOWN;

    public static ControllerButton fromSDL(int value) {
        return switch (value) {
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_A -> A;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_B -> B;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_X -> X;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_Y -> Y;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_BACK -> BACK;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_START -> START;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_GUIDE -> GUIDE;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSTICK -> LEFT_STICK;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSTICK -> RIGHT_STICK;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_UP -> DPAD_UP;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_DOWN -> DPAD_DOWN;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_LEFT -> DPAD_LEFT;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_RIGHT -> DPAD_RIGHT;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSHOULDER -> LEFT_SHOULDER;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSHOULDER -> RIGHT_SHOULDER;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MISC1 -> MISC1;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE1 -> PADDLE1;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE2 -> PADDLE2;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE3 -> PADDLE3;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE4 -> PADDLE4;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_TOUCHPAD -> TOUCHPAD;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX -> MAX;
            case SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_INVALID -> INVALID;
            default -> UNKNOWN;
        };
    }

    public int sdlButton() {
        return switch (this) {
            case A -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_A;
            case B -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_B;
            case X -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_X;
            case Y -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_Y;
            case BACK -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_BACK;
            case START -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_START;
            case GUIDE -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_GUIDE;
            case LEFT_STICK -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSTICK;
            case RIGHT_STICK -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSTICK;
            case DPAD_UP -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_UP;
            case DPAD_DOWN -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_DOWN;
            case DPAD_LEFT -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_LEFT;
            case DPAD_RIGHT -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_RIGHT;
            case LEFT_SHOULDER -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_LEFTSHOULDER;
            case RIGHT_SHOULDER -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_RIGHTSHOULDER;
            case MISC1 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MISC1;
            case PADDLE1 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE1;
            case PADDLE2 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE2;
            case PADDLE3 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE3;
            case PADDLE4 -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_PADDLE4;
            case TOUCHPAD -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_TOUCHPAD;
            case MAX -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX;
            case INVALID, UNKNOWN -> SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_INVALID;
        };
    }
}
