package io.github.ultreon.controllerx.input;

import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerAxis;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.Icon;
import io.github.ultreon.controllerx.util.InputDefinition;
import org.intellij.lang.annotations.MagicConstant;

public enum ControllerAxis implements InputDefinition<Float> {
    LeftStickX,
    LeftStickY,
    RightStickX,
    RightStickY,
    LeftTrigger,
    RightTrigger,
    Axis6,
    Axis7,
    Axis8,
    Axis9,
    Axis10,
    Axis11,
    Axis12,
    Axis13,
    Axis14,
    Axis15,
    Invalid,
    Unknown,
    DpadX,
    DpadY;

    private final ControllerX controllerX = ControllerX.get();;

    public static ControllerAxis fromSDL(int value) {
        return switch (value) {
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTX -> LeftStickX;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTY -> LeftStickY;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTX -> RightStickX;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTY -> RightStickY;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERLEFT -> LeftTrigger;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERRIGHT -> RightTrigger;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_MAX -> Axis15;
            case SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_INVALID -> Invalid;
            default -> Unknown;
        };
    }

    public static ControllerAxis fromButton(ControllerButton controllerButton) {
        return switch (controllerButton) {
            case LeftStickClick -> LeftStickX;
            case RightStickClick -> RightStickX;
            case LeftTrigger -> LeftTrigger;
            case RightTrigger -> RightTrigger;
            case DPadUp, DPadDOwn -> DpadY;
            case DPadLeft, RPadRight -> DpadX;
            default -> Unknown;
        };
    }

    public @MagicConstant(valuesFromClass = SDL_GameControllerAxis.class) int sdlAxis() {
        return switch (this) {
            case LeftStickX -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTX;
            case LeftStickY -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTY;
            case RightStickX -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTX;
            case RightStickY -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTY;
            case LeftTrigger -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERLEFT;
            case RightTrigger -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERRIGHT;
            case Axis15 -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_MAX;
            default -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_INVALID;
        };
    }

    @Override
    public Icon getIcon() {
        return switch (this) {
            case LeftStickX -> Icon.LeftJoyStickX;
            case LeftStickY -> Icon.LeftJoyStickY;
            case RightStickX -> Icon.RightJoyStickX;
            case RightStickY -> Icon.RightJoyStickY;
            case LeftTrigger -> Icon.LeftTrigger;
            case RightTrigger -> Icon.RightTrigger;
            case DpadX -> Icon.DpadLeftRight;
            case DpadY -> Icon.DpadUpDown;
            default -> Icon.AnyJoyStick;
        };
    }

    @Override
    public Float getValue() {
        return controllerX.controllerInput.getAxis(this);
    }
}
