package dev.ultreon.controllerx.fabric;

import dev.ultreon.controllerx.ControllerX;
import net.minecraft.client.player.LocalPlayer;

public class ControllerXImpl extends ControllerX {
    @Override
    public double getEntityReach(LocalPlayer player) {
        return player.isCreative() ? 5.0D : 4.5D;
    }

    @Override
    public double getBlockReach(LocalPlayer player) {
        return player.isCreative() ? 5.0D : 4.5D;
    }
}
