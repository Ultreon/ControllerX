package io.github.ultreon.controllerx.util;

import io.github.ultreon.controllerx.Icon;

public interface InputDefinition<T> {
    Icon getIcon();

    T getValue();
}
