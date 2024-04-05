package io.github.ultreon.controllerx.api;

import com.google.common.base.Preconditions;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.config.Config;
import io.github.ultreon.controllerx.config.gui.ConfigEntry;
import io.github.ultreon.controllerx.input.ControllerAxis;
import io.github.ultreon.controllerx.input.ControllerButton;
import io.github.ultreon.controllerx.input.ControllerJoystick;
import io.github.ultreon.controllerx.input.ControllerTrigger;
import io.github.ultreon.controllerx.util.InputDefinition;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector2f;

public sealed abstract class ControllerAction<T extends InputDefinition<?>> permits ControllerAction.Axis, ControllerAction.Button, ControllerAction.Joystick, ControllerAction.Nulled, ControllerAction.Trigger {
    private final T defaultValue;
    private ControllerAction<T> nullAction;
    private ConfigEntry<T> entry;

    protected ControllerAction(@UnknownNullability T value) {
        if (this.getClass() != Nulled.class) {
            Preconditions.checkNotNull(value, "Controller action value shouldn't be null.");
        }
        this.defaultValue = value;
    }

    /**
     * Get the mapping of the action
     *
     * @return the mapping
     */
    public T getMapping() {
        return entry == null ? defaultValue : entry.get();
    }


    /**
     * Set the mapping of the action
     *
     * @param mapping the mapping
     */
    public void setMapping(T mapping) {
        if (entry == null) return;
        this.entry.set(mapping);
    }

    /**
     * Check if the action is pressed
     *
     * @return true if the action is pressed, false otherwise.
     */
    public abstract boolean isPressed();

    /**
     * Get the value of the action
     *
     * @return a value between 0..1
     */
    public abstract float getValue();

    /**
     * Get the axis value of the action
     *
     * @return a value between -1..1
     */
    public abstract float getAxisValue();

    /**
     * Get the 2D value of the action
     *
     * @return a 2D vector with values between -1..1
     */
    public Vector2f get2DValue() {
        return new Vector2f(0, 0);
    }

    public boolean isJustPressed() {
        return false;
    }

    protected abstract ConfigEntry<T> createEntry0(Config config, String id, Component description);

    public ConfigEntry<T> createEntry(Config config, String id, Component description) {
        return this.entry = createEntry0(config, id, description);
    }

    public @Nullable ControllerAction<T> nulled() {
        if (this.nullAction == null) {
            this.nullAction = new Nulled();
        }

        return this.nullAction;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public static final class Button extends ControllerAction<ControllerButton> {
        public Button(ControllerButton value) {
            super(value);
        }

        public boolean isPressed() {
            return ControllerX.get().controllerInput.isButtonPressed(getMapping());
        }

        @Override
        public float getValue() {
            return isPressed() ? 1 : 0;
        }

        @Override
        public float getAxisValue() {
            return switch (getMapping()) {
                case DPadLeft, DPadDOwn -> -1;
                case RPadRight, DPadUp -> 1;
                default -> 0;
            };
        }

        @Override
        public Vector2f get2DValue() {
            return switch (getMapping()) {
                case DPadLeft, RPadRight -> new Vector2f(getAxisValue(), 0);
                case DPadUp, DPadDOwn -> new Vector2f(0, getAxisValue());
                default -> new Vector2f(0, 0);
            };
        }

        @Override
        public boolean isJustPressed() {
            return ControllerX.get().controllerInput.isButtonJustPressed(getMapping());
        }

        @Override
        protected ConfigEntry<ControllerButton> createEntry0(Config config, String id, Component description) {
            return config.add(id, getMapping(), description);
        }
    }

    public static final class Axis extends ControllerAction<ControllerAxis> {
        public Axis(ControllerAxis value) {
            super(value);
        }

        public float getValue() {
            return ControllerX.get().controllerInput.getAxis(getMapping());
        }

        @Override
        public float getAxisValue() {
            return getMapping().getValue();
        }

        @Override
        public Vector2f get2DValue() {
            return ControllerX.get().controllerInput.tryGetAxis(getMapping());
        }

        @Override
        protected ConfigEntry<ControllerAxis> createEntry0(Config config, String id, Component description) {
            return config.add(id, getMapping(), description);
        }

        public boolean isPressed() {
            return ControllerX.get().controllerInput.isAxisPressed(getMapping());
        }
    }

    public static final class Joystick extends ControllerAction<ControllerJoystick> {
        public Joystick(ControllerJoystick value) {
            super(value);
        }

        public boolean isPressed() {
            return getMapping().getJoystickLength() > 0;
        }

        @Override
        public float getValue() {
            return getMapping().getJoystickLength();
        }

        @Override
        public float getAxisValue() {
            return getMapping().getAxisY();
        }

        public Vector2f get2DValue() {
            return ControllerX.get().controllerInput.getJoystick(getMapping());
        }

        @Override
        protected ConfigEntry<ControllerJoystick> createEntry0(Config config, String id, Component description) {
            return config.add(id, getMapping(), description);
        }
    }

    public static final class Trigger extends ControllerAction<ControllerTrigger> {
        public Trigger(ControllerTrigger trigger) {
            super(trigger);
        }

        public float getValue() {
            return ControllerX.get().controllerInput.getTrigger(getMapping());
        }

        public boolean isPressed() {
            return getMapping().isPressed();
        }

        @Override
        public boolean isJustPressed() {
            return getMapping().isJustPressed();
        }

        @Override
        protected ConfigEntry<ControllerTrigger> createEntry0(Config config, String id, Component description) {
            return config.add(id, getMapping(), description);
        }

        @Override
        public float getAxisValue() {
            return getValue() * 2 - 1;
        }
    }

    final class Nulled extends ControllerAction<T> {
        private Nulled() {
            super(null);
        }

        @Override
        public T getMapping() {
            return ControllerAction.this.getMapping();
        }

        @Override
        public void setMapping(T mapping) {
            ControllerAction.this.setMapping(mapping);
        }

        @Override
        public boolean isPressed() {
            return false;
        }

        @Override
        public float getValue() {
            return 0;
        }

        @Override
        public float getAxisValue() {
            return 0;
        }

        @Override
        protected ConfigEntry<T> createEntry0(Config config, String id, Component description) {
            return ControllerAction.this.createEntry0(config, id, description);
        }
    }
}
