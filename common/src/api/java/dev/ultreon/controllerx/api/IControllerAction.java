package dev.ultreon.controllerx.api;

import dev.ultreon.controllerx.api.input.ControllerBoolean;
import dev.ultreon.controllerx.api.input.ControllerSignedFloat;
import dev.ultreon.controllerx.api.input.ControllerUnsignedFloat;
import dev.ultreon.controllerx.api.input.ControllerVec2;
import dev.ultreon.controllerx.api.input.dyn.IControllerInterDynamic;
import org.jetbrains.annotations.NotNull;

/// Represents a controller action.
///
/// @param <T> The type of the mapping for this action.
/// @author <a href="https://github.com/XyperCode">XyperCode</a>
public interface IControllerAction<T extends Enum<T> & IControllerInterDynamic<?>> {
    /**
     * Get the nulled version of this action.
     *
     * @return The nulled version of this action.
     */
    @NotNull IControllerAction<T> nulled();

    /// Get the mapping for this action.
    /// This often would be an instance of [ControllerBoolean], [ControllerSignedFloat], [ControllerUnsignedFloat], or [ControllerVec2].
    ///
    /// @return The mapping for this action.
    T getMapping();

    enum Type {
        BOOLEAN,
        SIGNED_FLOAT,
        UNSIGNED_FLOAT,
        VEC2
    }
}
