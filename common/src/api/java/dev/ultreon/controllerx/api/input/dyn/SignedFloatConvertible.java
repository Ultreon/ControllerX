package dev.ultreon.controllerx.api.input.dyn;

import com.mojang.datafixers.util.Pair;
import dev.ultreon.controllerx.api.input.ControllerSignedFloat;

public interface SignedFloatConvertible<T> {
    Pair<ControllerSignedFloat, Float> asSignedFloat(T value);
}
