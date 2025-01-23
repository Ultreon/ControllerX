package dev.ultreon.controllerx.api.input;

import net.minecraft.world.phys.Vec2;

public interface IControllerBackend {
    float getFloat(ControllerSignedFloat axis);
    boolean getButton(ControllerBoolean button);
    Vec2 getVec2(ControllerVec2 joystick);
}
