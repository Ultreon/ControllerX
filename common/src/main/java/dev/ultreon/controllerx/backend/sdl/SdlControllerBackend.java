package dev.ultreon.controllerx.backend.sdl;

import dev.ultreon.controllerx.Config;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.api.input.*;
import dev.ultreon.controllerx.input.ControllerInput;
import io.github.libsdl4j.api.SdlSubSystemConst;
import io.github.libsdl4j.api.gamecontroller.SDL_GameController;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerAxis;
import io.github.libsdl4j.api.gamecontroller.SDL_GameControllerButton;
import org.intellij.lang.annotations.MagicConstant;

import java.util.BitSet;

import static io.github.libsdl4j.api.Sdl.SDL_Init;
import static io.github.libsdl4j.api.Sdl.SDL_Quit;
import static io.github.libsdl4j.api.event.SdlEventsConst.SDL_PRESSED;
import static io.github.libsdl4j.api.gamecontroller.SdlGamecontroller.*;

public class SdlControllerBackend implements IControllerBackend {
    private SDL_GameController sdlController = null;
    private SdlController connected = null;
    private ControllerInput input;

    private final BitSet pressedButtons = new BitSet(SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX);

    public @MagicConstant(valuesFromClass = SDL_GameControllerAxis.class) int sdlAxis(ControllerSignedFloat axis) {
        return switch (axis) {
            case LeftTrigger -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERLEFT;
            case RightTrigger -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_TRIGGERRIGHT;
            case LeftStickX -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTX;
            case LeftStickY -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_LEFTY;
            case RightStickX -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTX;
            case RightStickY -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_RIGHTY;
            default -> SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_INVALID;
        };
    }

    public @MagicConstant(valuesFromClass = SDL_GameControllerButton.class) int sdlButton(ControllerBoolean button) {
        return switch (button) {
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
    public void update() {
        if (input == null) {
            input = ControllerX.get().input;
            if (input == null) return;
        }
        
        SDL_GameControllerUpdate();

        if (ControllerX.get().input != null) {
            ControllerBoolean.pollAll();
        }

        if (connected == null) {
            input.setController(0);
            if (connected == null)
                return;
        }

        if (!SDL_GameControllerGetAttached(sdlController)) {
            input.unsetController();
            return;
        }

        for (@MagicConstant(valuesFromClass = SDL_GameControllerButton.class) int idx = SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_A; idx < SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_MAX; idx++) {
            boolean pressed = SDL_GameControllerGetButton(connected.sdlController(), idx) == SDL_PRESSED;
            pressedButtons.set(idx, pressed);

            if (pressed) {
                ControllerX.get().setInputType(InputType.CONTROLLER);
            }
        }
    }

    @Override
    public Float getAxis(ControllerSignedFloat controllerAxis) {
        @MagicConstant(valuesFromClass = SDL_GameControllerAxis.class) int axis = sdlAxis(controllerAxis);
        if (axis == SDL_GameControllerAxis.SDL_CONTROLLER_AXIS_INVALID) return null;
        float v = SDL_GameControllerGetAxis(sdlController, axis) / 32767f;

        float deadZone = Config.get().axisDeadZone;
        int sign = v > 0 ? 1 : -1;
        v = Math.abs(v);
        if (v < deadZone) {
            v = Math.max(0, (v - deadZone) / (1 - deadZone)) * sign;
        } else {
            v *= sign;
        }

        if (v == 0) return 0f;

        return v;
    }

    @Override
    public boolean getButton(ControllerBoolean button) {
        int idx = sdlButton(button);
        boolean pressed = SDL_GameControllerGetButton(sdlController, idx) == SDL_PRESSED;

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) return pressed;

        return false;
    }

    @Override
    public boolean isConnected() {
        return SDL_GameControllerGetAttached(sdlController);
    }

    @Override
    public IController getController(int deviceIndex) {
        sdlController = SDL_GameControllerOpen(deviceIndex);
        if (sdlController == null) return null;

        short productId = SDL_GameControllerGetProduct(sdlController);
        short vendorId = SDL_GameControllerGetVendor(sdlController);
        String name = SDL_GameControllerName(sdlController);
        String mapping = SDL_GameControllerMapping(sdlController);

        SdlController controller = new SdlController(sdlController, deviceIndex, productId, vendorId, name, mapping);
        connected = controller;
        return controller;
    }

    @Override
    public boolean isAnyButtonPressed() {
        return !pressedButtons.isEmpty();
    }

    @Override
    public void init() {
        SDL_Init(SdlSubSystemConst.SDL_INIT_EVENTS | SdlSubSystemConst.SDL_INIT_GAMECONTROLLER | SdlSubSystemConst.SDL_INIT_JOYSTICK);
    }

    @Override
    public void quit() {
        SDL_Quit();
    }
}
