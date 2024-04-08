package io.github.ultreon.controllerx.input.dyn;

import io.github.ultreon.controllerx.input.ControllerSignedFloat;
import io.github.ultreon.controllerx.input.ControllerUnsignedFloat;

public interface FloatRepresentable {
    ControllerSignedFloat asSignedFloat();
    ControllerUnsignedFloat asUnsignedFloat();
}
