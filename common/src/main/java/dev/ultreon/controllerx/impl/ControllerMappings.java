package dev.ultreon.controllerx.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import dev.ultreon.controllerx.api.IControllerMapping;
import dev.ultreon.controllerx.api.IControllerMappings;
import dev.ultreon.controllerx.api.input.ControllerSignedFloat;
import dev.ultreon.controllerx.api.input.ControllerBoolean;
import dev.ultreon.controllerx.api.input.ControllerVec2;
import dev.ultreon.controllerx.api.input.ControllerUnsignedFloat;

import java.util.ArrayList;
import java.util.List;

public final class ControllerMappings implements IControllerMappings {
    private final List<ControllerMapping<ControllerBoolean>> buttonMappings = new ArrayList<>();
    private final List<ControllerMapping<ControllerSignedFloat>> axisMappings = new ArrayList<>();
    private final List<ControllerMapping<ControllerVec2>> joystickMappings = new ArrayList<>();
    private final List<ControllerMapping<ControllerUnsignedFloat>> triggerMappings = new ArrayList<>();

    public List<ControllerMapping<ControllerBoolean>> getButtonMappings() {
        return buttonMappings;
    }

    public List<ControllerMapping<ControllerSignedFloat>> getAxisMappings() {
        return axisMappings;
    }

    public List<ControllerMapping<ControllerVec2>> getJoystickMappings() {
        return joystickMappings;
    }

    public List<ControllerMapping<ControllerUnsignedFloat>> getTriggerMappings() {
        return triggerMappings;
    }

    public List<ControllerMapping<?>> getLeftSideMappings() {
        List<ControllerMapping<?>> mappings = new ArrayList<>();
        mappings.addAll(getButtonMappings());
        mappings.addAll(getAxisMappings());
        mappings.addAll(getTriggerMappings());
        mappings.addAll(getJoystickMappings());
        return mappings.stream().filter(mapping -> mapping.getSide() == IControllerMapping.Side.LEFT).toList();
    }

    public List<ControllerMapping<?>> getRightSideMappings() {
        List<ControllerMapping<?>> mappings = new ArrayList<>();
        mappings.addAll(getButtonMappings());
        mappings.addAll(getAxisMappings());
        mappings.addAll(getTriggerMappings());
        mappings.addAll(getJoystickMappings());
        return mappings.stream().filter(mapping -> mapping.getSide() == IControllerMapping.Side.RIGHT).toList();
    }

    @SuppressWarnings("unchecked")
    public <T extends IControllerMapping<?>> T register(T mapping) {
        Preconditions.checkNotNull(mapping, "mapping cannot be null");

        if (mapping.getActionDirect() instanceof ControllerAction.Button) {
            buttonMappings.add((ControllerMapping<ControllerBoolean>) mapping);
        } else if (mapping.getActionDirect() instanceof ControllerAction.Axis) {
            axisMappings.add((ControllerMapping<ControllerSignedFloat>) mapping);
        } else if (mapping.getActionDirect() instanceof ControllerAction.Joystick) {
            joystickMappings.add((ControllerMapping<ControllerVec2>) mapping);
        } else if (mapping.getActionDirect() instanceof ControllerAction.Trigger) {
            triggerMappings.add((ControllerMapping<ControllerUnsignedFloat>) mapping);
        } else {
            throw new IllegalArgumentException("Unsupported controller action: " + mapping.getActionDirect().getClass().getName());
        }

        return mapping;
    }

    public Iterable<IControllerMapping<?>> getAllMappings() {
        return Iterables.concat(getButtonMappings(), getAxisMappings(), getTriggerMappings(), getJoystickMappings());
    }

    public void releaseAll() {

    }
}
