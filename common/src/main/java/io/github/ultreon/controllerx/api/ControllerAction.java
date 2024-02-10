package io.github.ultreon.controllerx.api;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.input.ControllerAxis;
import io.github.ultreon.controllerx.input.ControllerButton;
import io.github.ultreon.controllerx.input.ControllerJoystick;
import io.github.ultreon.controllerx.input.ControllerTrigger;
import io.github.ultreon.controllerx.util.InputDefinition;
import org.joml.Vector2f;

public sealed interface ControllerAction<T extends InputDefinition<?>> {
    /**
     * Get the mapping of the action
     *
     * @return the mapping
     */
    T getMapping();

    /**
     * Set the mapping of the action
     *
     * @param mapping the mapping
     */
    void setMapping(T mapping);

    /**
     * Check if the action is pressed
     *
     * @return true if the action is pressed, false otherwise.
     */
    boolean isPressed();

    /**
     * Get the value of the action
     *
     * @return a value between 0..1
     */
    float getValue();

    /**
     * Get the axis value of the action
     *
     * @return a value between -1..1
     */
    float getAxisValue();

    /**
     * Get the 2D value of the action
     *
     * @return a 2D vector with values between -1..1
     */
    default Vector2f get2DValue() {
        return new Vector2f(0, 0);
    }

    default boolean isJustPressed() {
        return false;
    }

    final class Button implements ControllerAction<ControllerButton> {
        private ControllerButton button;

        public Button(ControllerButton button) {
            this.button = button;
        }

        public boolean isPressed() {
            return ControllerX.get().controllerInput.isButtonPressed(button);
        }

        @Override
        public float getValue() {
            return isPressed() ? 1 : 0;
        }

        @Override
        public float getAxisValue() {
            return switch (button) {
                case DPAD_LEFT, DPAD_DOWN -> -1;
                case DPAD_RIGHT, DPAD_UP -> 1;
                default -> 0;
            };
        }

        @Override
        public Vector2f get2DValue() {
            return switch (button) {
                case DPAD_LEFT, DPAD_RIGHT -> new Vector2f(getAxisValue(), 0);
                case DPAD_UP, DPAD_DOWN -> new Vector2f(0, getAxisValue());
                default -> new Vector2f(0, 0);
            };
        }

        @Override
        public ControllerButton getMapping() {
            return button;
        }

        @Override
        public void setMapping(ControllerButton mapping) {
            this.button = mapping;
        }

        @Override
        public boolean isJustPressed() {
            return ControllerX.get().controllerInput.isButtonJustPressed(button);
        }
    }

    final class Axis implements ControllerAction<ControllerAxis> {
        private ControllerAxis axis;

        public Axis(ControllerAxis axis) {
            this.axis = axis;
        }

        @Override
        public ControllerAxis getMapping() {
            return axis;
        }

        @Override
        public void setMapping(ControllerAxis mapping) {
            this.axis = mapping;
        }

        public float getValue() {
            return ControllerX.get().controllerInput.getAxis(axis);
        }

        @Override
        public float getAxisValue() {
            return axis.getValue();
        }

        @Override
        public Vector2f get2DValue() {
            return ControllerX.get().controllerInput.tryGetAxis(axis);
        }

        public boolean isPressed() {
            return ControllerX.get().controllerInput.isAxisPressed(axis);
        }
    }

    final class Joystick implements ControllerAction<ControllerJoystick> {
        private ControllerJoystick joystick;

        public Joystick(ControllerJoystick joystick) {
            this.joystick = joystick;
        }

        @Override
        public ControllerJoystick getMapping() {
            return joystick;
        }

        @Override
        public void setMapping(ControllerJoystick mapping) {
            this.joystick = mapping;
        }

        public boolean isPressed() {
            return joystick.getJoystickLength() > 0;
        }

        @Override
        public float getValue() {
            return joystick.getJoystickLength();
        }

        @Override
        public float getAxisValue() {
            return joystick.getAxisY();
        }

        public Vector2f get2DValue() {
            return ControllerX.get().controllerInput.getJoystick(joystick);
        }
    }

    final class Trigger implements ControllerAction<ControllerTrigger> {
        private ControllerTrigger trigger;

        public Trigger(ControllerTrigger trigger) {
            this.trigger = trigger;
        }

        @Override
        public ControllerTrigger getMapping() {
            return trigger;
        }

        @Override
        public void setMapping(ControllerTrigger mapping) {
            this.trigger = mapping;
        }

        public float getValue() {
            return ControllerX.get().controllerInput.getTrigger(trigger);
        }

        public boolean isPressed() {
            return trigger.isPressed();
        }

        @Override
        public boolean isJustPressed() {
            return trigger.isJustPressed();
        }

        @Override
        public float getAxisValue() {
            return getValue() * 2 - 1;
        }
    }
}
