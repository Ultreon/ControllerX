package dev.ultreon.controllerx.api.input.dyn;

import com.mojang.datafixers.util.Pair;
import dev.ultreon.controllerx.api.input.ControllerUnsignedFloat;

public interface UnsignedFloatConvertible<T> {
    Pair<ControllerUnsignedFloat, Float> asUnsignedFloat(T value);
}
