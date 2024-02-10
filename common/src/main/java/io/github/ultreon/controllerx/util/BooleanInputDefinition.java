package io.github.ultreon.controllerx.util;

public interface BooleanInputDefinition extends InputDefinition<Boolean> {
    @Override
    @Deprecated
    default Boolean getValue() {
        return getBooleanValue();
    }

    boolean getBooleanValue();
}
