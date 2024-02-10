package io.github.ultreon.controllerx.input;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.Icon;
import io.github.ultreon.controllerx.util.InputDefinition;
import org.joml.Vector2f;

public enum ControllerJoystick implements InputDefinition<Vector2f> {
    Left(ControllerAxis.LeftStickX, ControllerAxis.LeftStickY),
    Right(ControllerAxis.RightStickX, ControllerAxis.RightStickY),
    Dpad(ControllerAxis.DpadX, ControllerAxis.DpadY);

    public final ControllerAxis xAxis;
    public final ControllerAxis yAxis;

    ControllerJoystick(ControllerAxis xAxis, ControllerAxis yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    @Override
    public Icon getIcon() {
        return switch (this) {
            case Left -> Icon.LeftJoyStickMove;
            case Right -> Icon.RightJoyStickMove;
            case Dpad -> Icon.Dpad;
        };
    }

    public float getAxisX() {
        return xAxis.getValue();
    }

    public float getAxisY() {
        return yAxis.getValue();
    }

    @Override
    public Vector2f getValue() {
        return new Vector2f(getAxisX(), getAxisY());
    }

    public int sdlAxisX() {
        return xAxis.sdlAxis();
    }

    public int sdlAxisY() {
        return yAxis.sdlAxis();
    }

    public ControllerAxis getXAxis() {
        return xAxis;
    }

    public ControllerAxis getYAxis() {
        return yAxis;
    }

    public float getJoystickLength() {
        return (float) Math.sqrt(getAxisX() * getAxisX() + getAxisY() * getAxisY());
    }

    public float getJoystickAngle() {
        return (float) Math.atan2(getAxisY(), getAxisX());
    }

    public boolean isPressed() {
        ControllerInput input = ControllerX.get().controllerInput;
        return switch (this) {
            case Left -> input.isButtonPressed(ControllerButton.LEFT_STICK);
            case Right -> input.isButtonPressed(ControllerButton.RIGHT_STICK);
            case Dpad -> input.isButtonPressed(ControllerButton.DPAD_UP) ||
                    input.isButtonPressed(ControllerButton.DPAD_DOWN) ||
                    input.isButtonPressed(ControllerButton.DPAD_LEFT) ||
                    input.isButtonPressed(ControllerButton.DPAD_RIGHT);
        };
    }
}
