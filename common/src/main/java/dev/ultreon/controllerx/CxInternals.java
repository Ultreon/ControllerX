package dev.ultreon.controllerx;

import com.ultreon.mods.lib.world.Crosshair;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.api.IControllerX;
import dev.ultreon.controllerx.api.ICxInternals;
import dev.ultreon.controllerx.gui.widget.ItemSlot;
import dev.ultreon.controllerx.impl.contexts.ChatControllerContext;
import dev.ultreon.controllerx.impl.contexts.InGameControllerContext;
import dev.ultreon.controllerx.impl.contexts.MenuControllerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

class CxInternals implements ICxInternals {
    @Override
    public void finalRegistration() {
        ControllerContext.register(ChatControllerContext.INSTANCE, CxInternals::isChatting);
        ControllerContext.register(InGameControllerContext.INSTANCE, CxInternals::isInGame);
        ControllerContext.register(MenuControllerContext.INSTANCE, CxInternals::isInMenu);
        ControllerContext.register(new EmptyContext(), Predicate.isEqual(Minecraft.getInstance()));
    }

    private static boolean isInCreativeMenu(Minecraft minecraft) {
        return minecraft.screen instanceof CreativeModeInventoryScreen;
    }

    public static boolean isChatting(Minecraft minecraft) {
        return minecraft.player != null && minecraft.level != null && minecraft.screen instanceof ChatScreen;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static boolean isTargetingEntity(Minecraft minecraft) {
        Crosshair crosshair = Crosshair.get();
        if (crosshair == null) return false;
        double entityReach = IControllerX.get().getEntityReach(minecraft.player);
        if (entityReach <= 0) return false;
        return crosshair.entity(entityReach) instanceof LivingEntity;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static boolean isTargetingBlock(Minecraft minecraft) {
        Crosshair crosshair = Crosshair.get();
        if (crosshair == null) return false;
        double blockReach = IControllerX.get().getBlockReach(minecraft.player);
        if (blockReach <= 0) return false;
        return crosshair.block(blockReach) != null && minecraft.player.getAbilities().mayBuild;
    }

    public static boolean isInMenu(Minecraft minecraft) {
        return minecraft.screen != null;
    }

    public static boolean isInCloseableMenu(Minecraft minecraft) {
        return minecraft.screen != null && minecraft.screen.shouldCloseOnEsc();
    }

    public static boolean isInMenuSelectedItemSlot(Minecraft minecraft) {
        return minecraft.screen != null && minecraft.screen.shouldCloseOnEsc() && minecraft.screen.getFocused() instanceof ItemSlot;
    }

    public static boolean isInGame(Minecraft minecraft) {
        return minecraft.player != null && minecraft.screen == null;
    }

    private static class EmptyContext extends ControllerContext {
        public EmptyContext() {
            super(new ResourceLocation(ControllerX.MOD_ID, "default"));
        }
    }

}
