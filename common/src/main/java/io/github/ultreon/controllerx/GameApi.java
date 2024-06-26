package io.github.ultreon.controllerx;

import io.github.ultreon.controllerx.mixin.accessors.ChatComponentAccessor;
import io.github.ultreon.controllerx.mixin.accessors.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;

public class GameApi {
    public static boolean startAttack() {
        return ((MinecraftAccessor) Minecraft.getInstance()).invokeStartAttack();
    }

    public static void continueAttack(boolean bl3) {
        Minecraft instance = Minecraft.getInstance();
        ((MinecraftAccessor) instance).invokeContinueAttack(instance.screen == null && !bl3);
    }

    public static int getChatYOffset() {
        return ((ChatComponentAccessor) Minecraft.getInstance().gui.getChat()).invokeGetLineHeight();
    }

    public static void useItem() {
        ((MinecraftAccessor) Minecraft.getInstance()).invokeStartUseItem();
    }

    public static void startUseItem() {
        Minecraft instance = Minecraft.getInstance();
        MultiPlayerGameMode gameMode = instance.gameMode;
        LocalPlayer player = instance.player;

        if (player != null && gameMode != null && !player.isUsingItem()) {
            ((MinecraftAccessor) instance).invokeStartUseItem();
        }
    }

    public static void stopUseItem() {
        Minecraft instance = Minecraft.getInstance();
        MultiPlayerGameMode gameMode = instance.gameMode;
        LocalPlayer player = instance.player;

        if (player != null && gameMode != null && player.isUsingItem()) {
            gameMode.releaseUsingItem(player);
        }
    }

    public static int getRightClickDelay() {
        return ((MinecraftAccessor) Minecraft.getInstance()).getRightClickDelay();
    }

    public static void scrollHotbar(int delta) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        int newSelected = player.getInventory().selected + delta;
        if (newSelected < 0) newSelected = 8;
        else if (newSelected > 8) newSelected = 0;

        player.getInventory().selected = newSelected;
    }
}
