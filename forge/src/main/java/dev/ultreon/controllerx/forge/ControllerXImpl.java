package dev.ultreon.controllerx.forge;

import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.gui.screen.ControllerXConfigScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModLoadingContext;

public class ControllerXImpl extends ControllerX {
    public ControllerXImpl() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ControllerXConfigScreen(screen))
        );
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
