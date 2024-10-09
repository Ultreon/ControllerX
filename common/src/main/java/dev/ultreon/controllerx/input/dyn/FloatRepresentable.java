package dev.ultreon.controllerx.input.dyn;

import dev.ultreon.controllerx.input.ControllerSignedFloat;
import dev.ultreon.controllerx.input.ControllerUnsignedFloat;

public interface FloatRepresentable {
    ControllerSignedFloat asSignedFloat();
    ControllerUnsignedFloat asUnsignedFloat();
}
