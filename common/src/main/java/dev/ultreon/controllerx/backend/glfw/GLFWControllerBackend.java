package dev.ultreon.controllerx.backend.glfw;

import dev.ultreon.controllerx.Config;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.api.input.*;
import dev.ultreon.controllerx.input.ControllerInput;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.util.BitSet;

public class GLFWControllerBackend implements IControllerBackend {
    private GLFWController connected = null;
    private ControllerInput input;

    private final BitSet pressedButtons = new BitSet(GLFW.GLFW_GAMEPAD_BUTTON_LAST);
    private GLFWGamepadState state;

    public int glfwAxis(ControllerSignedFloat axis) {
        return switch (axis) {
            case LeftTrigger -> GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
            case RightTrigger -> GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;
            case LeftStickX -> GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
            case LeftStickY -> GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
            case RightStickX -> GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
            case RightStickY -> GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;
            default -> -1;
        };
    }

    public int glfwButton(ControllerBoolean button) {
        return switch (button) {
            case A -> GLFW.GLFW_GAMEPAD_BUTTON_A;
            case B -> GLFW.GLFW_GAMEPAD_BUTTON_B;
            case X -> GLFW.GLFW_GAMEPAD_BUTTON_X;
            case Y -> GLFW.GLFW_GAMEPAD_BUTTON_Y;
            case Back -> GLFW.GLFW_GAMEPAD_BUTTON_BACK;
            case Start -> GLFW.GLFW_GAMEPAD_BUTTON_START;
            case Guide -> GLFW.GLFW_GAMEPAD_BUTTON_GUIDE;
            case DpadLeft -> GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
            case DpadRight -> GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT;
            case DpadUp -> GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP;
            case DpadDown -> GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN;
            case LeftStickClick -> GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB;
            case RightStickClick -> GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB;
            case LeftShoulder -> GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER;
            case RightShoulder -> GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER;
            default -> -1;
        };
    }

    @Override
    public void update() {
        if (input == null) {
            input = ControllerX.get().input;
            if (input == null) return;
        }

        if (ControllerX.get().input != null) {
            ControllerBoolean.pollAll();
        }

        if (connected == null) {
            input.setController(0);
            if (connected == null)
                return;
        }

        if (!GLFW.glfwJoystickPresent(connected.glfwController()) && GLFW.glfwJoystickIsGamepad(connected.glfwController())) {
            input.unsetController();
            return;
        }

        if (state == null) {
            state = GLFWGamepadState.create();
        }
        GLFW.glfwGetGamepadState(connected.glfwController(), state);
        for (int idx = GLFW.GLFW_GAMEPAD_BUTTON_A; idx < GLFW.GLFW_GAMEPAD_BUTTON_LAST; idx++) {
            boolean pressed = state.buttons(idx) == GLFW.GLFW_PRESS;
            pressedButtons.set(idx, pressed);

            if (pressed) {
                ControllerX.get().setInputType(InputType.CONTROLLER);
            }
        }
    }

    @Override
    public Float getAxis(ControllerSignedFloat controllerAxis) {
        int axis = glfwAxis(controllerAxis);
        if (axis == -1) return null;
        if (state == null) return null;
        float v = state.axes(axis);

        if (controllerAxis == ControllerSignedFloat.LeftTrigger || controllerAxis == ControllerSignedFloat.RightTrigger) {
            v = (v + 1) / 2;
        }

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
        int idx = glfwButton(button);
        if (idx == -1) return false;
        if (state == null) return false;
        boolean pressed = state.buttons(idx) == GLFW.GLFW_PRESS;

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) return pressed;

        return false;
    }

    @Override
    public boolean isConnected() {
        return GLFW.glfwJoystickPresent(connected.glfwController()) && GLFW.glfwJoystickIsGamepad(connected.glfwController());
    }

    @Override
    public IController getController(int deviceIndex) {
        String name = GLFW.glfwGetGamepadName(deviceIndex);

        GLFWController controller = new GLFWController(deviceIndex, deviceIndex, name);
        connected = controller;
        return controller;
    }

    @Override
    public boolean isAnyButtonPressed() {
        return !pressedButtons.isEmpty();
    }

    @Override
    public void init() {
        // No-op
    }

    @Override
    public void quit() {
        // No-op
    }
}
