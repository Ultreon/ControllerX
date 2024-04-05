package io.github.ultreon.controllerx.forge;

import com.google.common.eventbus.Subscribe;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.impl.ScreenAccessImpl;
import dev.architectury.platform.forge.EventBuses;
import io.github.ultreon.controllerx.ControllerX;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ControllerX.MOD_ID)
public class ControllerXForge {
    private final ControllerX instance;

    public ControllerXForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ControllerX.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        instance = ControllerX.get();
    }

    public ControllerX getInstance() {
        return instance;
    }
}