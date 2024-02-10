package io.github.ultreon.controllerx;

import io.github.ultreon.controllerx.mixin.accessors.ChatComponentAccessor;
import io.github.ultreon.controllerx.mixin.accessors.MinecraftAccessor;
import net.minecraft.client.Minecraft;

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

    public static void startUseItem() {
        ((MinecraftAccessor) Minecraft.getInstance()).invokeStartUseItem();
    }

    public static int getRightClickDelay() {
        return ((MinecraftAccessor) Minecraft.getInstance()).getRightClickDelay();
    }
}
