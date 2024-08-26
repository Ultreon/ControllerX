package io.github.ultreon.controllerx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class GameApi {
    public static int getChatYOffset() {
        return Minecraft.getInstance().gui.getChat().getLineHeight();
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
