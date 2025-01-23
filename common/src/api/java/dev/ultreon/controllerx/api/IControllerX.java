package dev.ultreon.controllerx.api;

import dev.ultreon.controllerx.api.config.IConfig;
import dev.ultreon.controllerx.api.input.IControllerInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public interface IControllerX {
    String MOD_ID = "controllerx";

    static IControllerX get() {
        return ModGetter.getControllerX();
    }

    static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    double getEntityReach(LocalPlayer player);
    double getBlockReach(LocalPlayer player);

    IControllerMappings createMappings();

    IControllerInput getInput();

    IConfig createConfig(ResourceLocation id, ControllerContext controllerContext);

    @ApiStatus.Internal
    ICxInternals getInternals();
}
