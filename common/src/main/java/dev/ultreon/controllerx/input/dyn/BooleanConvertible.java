package dev.ultreon.controllerx.input.dyn;

import com.ultreon.commons.collection.Pair;
import dev.ultreon.controllerx.input.ControllerBoolean;

public interface BooleanConvertible<T> {
    Pair<ControllerBoolean, Boolean> asBoolean(T value);
}
