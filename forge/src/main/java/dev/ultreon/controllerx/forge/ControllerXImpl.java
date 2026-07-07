package dev.ultreon.controllerx.forge;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.gui.screen.ControllerXConfigScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModLoadingContext;

public class ControllerXImpl extends ControllerX {
    public ControllerXImpl() {

    }

    @Override
    public double getEntityReach(LocalPlayer player) {
        return player.getAttributeValue(ForgeMod.ENTITY_REACH.get());
    }

    @Override
    public double getBlockReach(LocalPlayer player) {
        return player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
    }
}
