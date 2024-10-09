package dev.ultreon.controllerx.injection;

import net.minecraft.world.item.CreativeModeTab;

public interface CreativeModeInventoryScreenInjection {
    void controllerX$prevPage();

    void controllerX$nextPage();

    CreativeModeTab controllerX$getPrevPage();

    CreativeModeTab controllerX$getNextPage();
}
