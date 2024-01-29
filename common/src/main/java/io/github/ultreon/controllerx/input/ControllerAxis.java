package io.github.ultreon.controllerx.input;

import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerAxis;
import io.github.libsdl4j.api.gamecontroller.SdlGamecontroller;

public enum ControllerAxis {
    LEFT_STICK_X, LEFT_STICK_Y, RIGHT_STICK_X, RIGHT_STICK_Y, LEFT_TRIGGER, RIGHT_TRIGGER, AXIS_6, AXIS_7, AXIS_8, AXIS_9, AXIS_10, AXIS_11, AXIS_12, AXIS_13, AXIS_14, AXIS_15, INVALID, UNKNOWN;

    public static ControllerAxis fromSDL(int value) {
        return switch (value) {
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTX -> LEFT_STICK_X;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTY -> LEFT_STICK_Y;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTX -> RIGHT_STICK_X;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTY -> RIGHT_STICK_Y;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERLEFT -> LEFT_TRIGGER;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERRIGHT -> RIGHT_TRIGGER;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_MAX -> AXIS_15;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_INVALID -> INVALID;
            default -> UNKNOWN;
        };
    }

    public int sdlAxis() {
        return switch (this) {
            case LEFT_STICK_X -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTX;
            case LEFT_STICK_Y -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTY;
            case RIGHT_STICK_X -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTX;
            case RIGHT_STICK_Y -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTY;
            case LEFT_TRIGGER -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERLEFT;
            case RIGHT_TRIGGER -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERRIGHT;
            case AXIS_15 -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_MAX;
            default -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_INVALID;
        };
    }
}
