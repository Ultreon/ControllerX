package io.github.ultreon.controllerx.input.dyn;

import dev.ultreon.mods.lib.common.tuple.Pair;
import io.github.ultreon.controllerx.input.ControllerBoolean;

public interface BooleanConvertible<T> {
    Pair<ControllerBoolean, Boolean> asBoolean(T value);
}
