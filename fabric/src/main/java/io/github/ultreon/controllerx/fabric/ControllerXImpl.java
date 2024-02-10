package io.github.ultreon.controllerx.fabric;

import net.minecraft.world.entity.player.Player;

public class ControllerXImpl {
    public static double getEntityReach(Player player) {
        return player.isCreative() ? 6.0D : 3.0D;
    }

    public static double getBlockReach(Player player) {
        return player.isCreative() ? 6.0D : 3.0D;
    }
}
