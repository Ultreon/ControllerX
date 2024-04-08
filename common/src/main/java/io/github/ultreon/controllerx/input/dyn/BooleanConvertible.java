package io.github.ultreon.controllerx.input.dyn;

import com.ultreon.commons.collection.Pair;
import io.github.ultreon.controllerx.input.ControllerBoolean;

public interface BooleanConvertible<T> {
    Pair<ControllerBoolean, Boolean> asBoolean(T value);
}
