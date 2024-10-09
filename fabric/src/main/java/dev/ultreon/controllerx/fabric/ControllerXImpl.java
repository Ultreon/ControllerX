package dev.ultreon.controllerx.fabric;

import net.minecraft.world.entity.player.Player;

public class ControllerXImpl {
    public static double getEntityReach(Player player) {
        return player.isCreative() ? 5.0D : 4.5D;
    }

    public static double getBlockReach(Player player) {
        return player.isCreative() ? 5.0D : 4.5D;
    }
}
