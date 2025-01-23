package dev.ultreon.controllerx.api.input.dyn;

import com.ultreon.commons.collection.Pair;
import dev.ultreon.controllerx.api.input.ControllerUnsignedFloat;

public interface UnsignedFloatConvertible<T> {
    Pair<ControllerUnsignedFloat, Float> asUnsignedFloat(T value);
}
