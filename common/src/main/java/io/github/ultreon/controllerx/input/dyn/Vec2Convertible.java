package io.github.ultreon.controllerx.input.dyn;

import dev.ultreon.mods.lib.common.tuple.Pair;
import io.github.ultreon.controllerx.input.ControllerVec2;
import org.joml.Vector2f;

public interface Vec2Convertible<T> {
    default Pair<ControllerVec2, Vector2f> asVec2(T value) {
        return this.asVec2(value, new Vector2f());
    }

    Pair<ControllerVec2, Vector2f> asVec2(T value, Vector2f result);
}
