package dev.ultreon.controllerx.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.controllerx.ControllerX;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ControllerX.MOD_ID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> MENU_TICK = REGISTER.register("gui.controllerx.menu.tick", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation("gui.controllerx.menu.tick"), 10));

    public static void register() {
        REGISTER.register();
    }
}
