package io.github.ultreon.controllerx.forge;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

public class ControllerXImpl {
    public static double getEntityReach(Player player) {
        return player.getAttributeValue(ForgeMod.ENTITY_REACH.get());
    }

    public static double getBlockReach(Player player) {
        return player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
    }
}
