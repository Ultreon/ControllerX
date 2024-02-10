package io.github.ultreon.controllerx;

import io.github.ultreon.controllerx.mixin.accessors.ChatComponentAccessor;
import io.github.ultreon.controllerx.mixin.accessors.MinecraftAccessor;
import net.minecraft.client.Minecraft;

public class GameApi {
    public static boolean startAttack() {
        return ((MinecraftAccessor) Minecraft.getInstance()).invokeStartAttack();
    }

    public static int getChatYOffset() {
        return ((ChatComponentAccessor) Minecraft.getInstance().gui.getChat()).invokeGetLineHeight();
    }
}
