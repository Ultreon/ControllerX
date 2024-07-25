package io.github.ultreon.controllerx.input.dyn;

import dev.ultreon.mods.lib.common.tuple.Pair;
import io.github.ultreon.controllerx.input.ControllerUnsignedFloat;

public interface UnsignedFloatConvertible<T> {
    Pair<ControllerUnsignedFloat, Float> asUnsignedFloat(T value);
}
