package io.github.ultreon.controllerx.util;

public interface FloatInputDefinition extends InputDefinition<Float> {
    @Override
    @Deprecated
    default Float getValue() {
        return getFloatValue();
    }

    float getFloatValue();
}
