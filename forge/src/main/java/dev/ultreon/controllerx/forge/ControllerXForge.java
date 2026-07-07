package dev.ultreon.controllerx.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ultreon.controllerx.ControllerX;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ControllerX.MOD_ID)
public class ControllerXForge {
    public ControllerXForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ControllerX.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        EnvExecutor.runInEnv(Env.CLIENT, () -> ControllerXForgeClient::init);
    }
}