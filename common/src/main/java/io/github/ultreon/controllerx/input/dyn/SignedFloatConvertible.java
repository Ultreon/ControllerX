package io.github.ultreon.controllerx.input.dyn;

import com.ultreon.commons.collection.Pair;
import io.github.ultreon.controllerx.input.ControllerSignedFloat;

public interface SignedFloatConvertible<T> {
    Pair<ControllerSignedFloat, Float> asSignedFloat(T value);
}
