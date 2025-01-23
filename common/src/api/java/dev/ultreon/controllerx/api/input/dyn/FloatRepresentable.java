package dev.ultreon.controllerx.api.input.dyn;

import dev.ultreon.controllerx.api.input.ControllerSignedFloat;
import dev.ultreon.controllerx.api.input.ControllerUnsignedFloat;

public interface FloatRepresentable {
    ControllerSignedFloat asSignedFloat();
    ControllerUnsignedFloat asUnsignedFloat();
}
