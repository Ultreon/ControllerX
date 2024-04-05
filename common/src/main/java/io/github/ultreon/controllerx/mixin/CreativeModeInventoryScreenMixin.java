package io.github.ultreon.controllerx.mixin;

import io.github.ultreon.controllerx.injection.CreativeModeInventoryScreenInjection;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin implements CreativeModeInventoryScreenInjection {
    @Shadow protected abstract void selectTab(CreativeModeTab tab);

    @Shadow private static CreativeModeTab selectedTab;

    @Override
    public void controllerX$prevPage() {
        var tabs = controllerX$getCreativeModeTabs();
        int i = tabs.indexOf(selectedTab);
        while (i > 0) {
            CreativeModeTab tab = tabs.get(--i);
            if (!tab.shouldDisplay())
                continue;

            selectTab(tab);
            break;
        }
    }

    @Override
    public void controllerX$nextPage() {
        var tabs = controllerX$getCreativeModeTabs();
        int i = tabs.indexOf(selectedTab);
        while (i < tabs.size() - 1) {
            CreativeModeTab tab = tabs.get(++i);
            if (!tab.shouldDisplay())
                continue;

            selectTab(tab);
            break;
        }
    }

    @Unique
    private static @NotNull List<CreativeModeTab> controllerX$getCreativeModeTabs() {
        return CreativeModeTabs.allTabs();
    }

    @Override
    public CreativeModeTab controllerX$getPrevPage() {
        var tabs = controllerX$getCreativeModeTabs();
        int i = tabs.indexOf(selectedTab);
        return i > 0 ? tabs.get(i - 1) : null;
    }

    @Override
    public CreativeModeTab controllerX$getNextPage() {
        var tabs = controllerX$getCreativeModeTabs();
        int i = tabs.indexOf(selectedTab);
        return i < tabs.size() - 1 ? tabs.get(i + 1) : null;
    }
}
