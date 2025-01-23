package dev.ultreon.controllerx.api.input.dyn;

import com.ultreon.commons.collection.Pair;
import dev.ultreon.controllerx.api.input.ControllerBoolean;

public interface BooleanConvertible<T> {
    Pair<ControllerBoolean, Boolean> asBoolean(T value);
}
