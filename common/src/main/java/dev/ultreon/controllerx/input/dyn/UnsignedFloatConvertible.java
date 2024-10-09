package dev.ultreon.controllerx.input.dyn;

import com.ultreon.commons.collection.Pair;
import dev.ultreon.controllerx.input.ControllerUnsignedFloat;

public interface UnsignedFloatConvertible<T> {
    Pair<ControllerUnsignedFloat, Float> asUnsignedFloat(T value);
}
