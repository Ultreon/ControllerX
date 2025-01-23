package dev.ultreon.controllerx.api.input.dyn;

import com.ultreon.commons.collection.Pair;
import dev.ultreon.controllerx.api.input.ControllerSignedFloat;

public interface SignedFloatConvertible<T> {
    Pair<ControllerSignedFloat, Float> asSignedFloat(T value);
}
