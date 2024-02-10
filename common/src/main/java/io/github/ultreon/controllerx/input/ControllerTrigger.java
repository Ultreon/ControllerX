package io.github.ultreon.controllerx.input;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.Icon;
import io.github.ultreon.controllerx.util.InputDefinition;

public enum ControllerTrigger implements InputDefinition<Float> {
    Left(ControllerAxis.LeftTrigger),
    Right(ControllerAxis.RightTrigger);

    private final ControllerAxis axis;
    private final ControllerX controllerX = ControllerX.get();

    ControllerTrigger(ControllerAxis axis) {
        this.axis = axis;
    }

    @Override
    public Icon getIcon() {
        return switch (this) {
            case Left -> Icon.LeftTrigger;
            case Right -> Icon.RightTrigger;
        };
    }

    @Override
    public Float getValue() {
        return controllerX.controllerInput.getAxis(axis);
    }

    public ControllerAxis getAxis() {
        return axis;
    }

    public boolean isPressed() {
        return controllerX.controllerInput.isAxisPressed(axis);
    }
}
