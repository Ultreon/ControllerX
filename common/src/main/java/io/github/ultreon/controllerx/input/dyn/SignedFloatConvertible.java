package io.github.ultreon.controllerx.input.dyn;

import dev.ultreon.mods.lib.common.tuple.Pair;
import io.github.ultreon.controllerx.input.ControllerSignedFloat;

public interface SignedFloatConvertible<T> {
    Pair<ControllerSignedFloat, Float> asSignedFloat(T value);
}
