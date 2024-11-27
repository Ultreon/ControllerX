package dev.ultreon.controllerx.input.dyn;

import com.ultreon.commons.collection.Pair;
import dev.ultreon.controllerx.input.ControllerSignedFloat;

public interface SignedFloatConvertible<T> {
    Pair<ControllerSignedFloat, Float> asSignedFloat(T value);
}
