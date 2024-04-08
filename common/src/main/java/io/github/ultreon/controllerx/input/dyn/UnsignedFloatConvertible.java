package io.github.ultreon.controllerx.input.dyn;

import com.ultreon.commons.collection.Pair;
import io.github.ultreon.controllerx.input.ControllerUnsignedFloat;

public interface UnsignedFloatConvertible<T> {
    Pair<ControllerUnsignedFloat, Float> asUnsignedFloat(T value);
}
