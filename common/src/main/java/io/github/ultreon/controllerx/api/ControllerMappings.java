package io.github.ultreon.controllerx.api;

import com.google.common.base.Preconditions;
import io.github.ultreon.controllerx.input.ControllerAxis;
import io.github.ultreon.controllerx.input.ControllerButton;
import io.github.ultreon.controllerx.input.ControllerJoystick;
import io.github.ultreon.controllerx.input.ControllerTrigger;

import java.util.ArrayList;
import java.util.List;

public final class ControllerMappings {
    private final List<ControllerMapping<ControllerButton>> buttonMappings = new ArrayList<>();
    private final List<ControllerMapping<ControllerAxis>> axisMappings = new ArrayList<>();
    private final List<ControllerMapping<ControllerJoystick>> joystickMappings = new ArrayList<>();
    private final List<ControllerMapping<ControllerTrigger>> triggerMappings = new ArrayList<>();

    public List<ControllerMapping<ControllerButton>> getButtonMappings() {
        return buttonMappings;
    }

    public List<ControllerMapping<ControllerAxis>> getAxisMappings() {
        return axisMappings;
    }

    public List<ControllerMapping<ControllerJoystick>> getJoystickMappings() {
        return joystickMappings;
    }

    public List<ControllerMapping<ControllerTrigger>> getTriggerMappings() {
        return triggerMappings;
    }

    public List<ControllerMapping<?>> getLeftSideMappings() {
        List<ControllerMapping<?>> mappings = new ArrayList<>();
        mappings.addAll(this.getButtonMappings());
        mappings.addAll(this.getAxisMappings());
        mappings.addAll(this.getTriggerMappings());
        mappings.addAll(this.getJoystickMappings());
        return mappings.stream().filter(mapping -> mapping.side() == ControllerMapping.Side.LEFT).toList();
    }

    public List<ControllerMapping<?>> getRightSideMappings() {
        List<ControllerMapping<?>> mappings = new ArrayList<>();
        mappings.addAll(this.getButtonMappings());
        mappings.addAll(this.getAxisMappings());
        mappings.addAll(this.getTriggerMappings());
        mappings.addAll(this.getJoystickMappings());
        return mappings.stream().filter(mapping -> mapping.side() == ControllerMapping.Side.RIGHT).toList();
    }

    @SuppressWarnings("unchecked")
    public <T extends ControllerMapping<?>> T register(T mapping) {
        Preconditions.checkNotNull(mapping, "mapping cannot be null");

        if (mapping.action() instanceof ControllerAction.Button) {
            this.buttonMappings.add((ControllerMapping<ControllerButton>) mapping);
        } else if (mapping.action() instanceof ControllerAction.Axis) {
            this.axisMappings.add((ControllerMapping<ControllerAxis>) mapping);
        } else if (mapping.action() instanceof ControllerAction.Joystick) {
            this.joystickMappings.add((ControllerMapping<ControllerJoystick>) mapping);
        } else if (mapping.action() instanceof ControllerAction.Trigger) {
            this.triggerMappings.add((ControllerMapping<ControllerTrigger>) mapping);
        } else {
            throw new IllegalArgumentException("Unsupported controller action: " + mapping.action().getClass().getName());
        }

        return mapping;
    }

    public List<ControllerMapping<?>> getAllMappings() {
        List<ControllerMapping<?>> mappings = new ArrayList<>();
        mappings.addAll(this.getButtonMappings());
        mappings.addAll(this.getAxisMappings());
        mappings.addAll(this.getTriggerMappings());
        mappings.addAll(this.getJoystickMappings());
        return mappings;
    }
}
