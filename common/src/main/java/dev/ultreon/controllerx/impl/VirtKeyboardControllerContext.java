package dev.ultreon.controllerx.impl;

import dev.ultreon.controllerx.ControllerX;
import net.minecraft.resources.ResourceLocation;

public class VirtKeyboardControllerContext extends MenuControllerContext {
    public static final VirtKeyboardControllerContext INSTANCE = new VirtKeyboardControllerContext(ControllerX.res("virtual_keyboard"));

    public VirtKeyboardControllerContext(ResourceLocation id) {
        super(id);
    }
}
