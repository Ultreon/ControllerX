package io.github.ultreon.controllerx.gui;

import io.github.ultreon.controllerx.Config;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.KeyMappingIcon;
import io.github.ultreon.controllerx.input.InputType;
import io.github.ultreon.controllerx.mixin.accessors.KeyMappingAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class KeyboardHud {
    private static final List<KeyMapping> VISIBLE_MAPPINGS = new ArrayList<>();

    public static void addMapping(KeyMapping mapping) {
        VISIBLE_MAPPINGS.add(mapping);
    }

    public void render(GuiGraphics gfx, DeltaTracker ignoredPartialTicks) {
        if (ControllerX.get().getInputType() != InputType.KEYBOARD_AND_MOUSE) return;
        if (!Config.get().enableKeyboardHud) return;

        int leftY = 20;
        int rightY = 20;
        
        enum Side {
            LEFT,
            RIGHT
        }
        
        var side = Side.LEFT;

        for (KeyMapping mapping : VISIBLE_MAPPINGS) {
            KeyMappingIcon icon = KeyMappingIcon.byKey(((KeyMappingAccessor) mapping).getKey());
            if (icon == null || mapping.isUnbound()) continue;

            int x = side == Side.LEFT ? 4 : width() - 12 - icon.width;
            int y = height() - (side == Side.LEFT ? leftY : rightY);

            icon.render(gfx, x, y);

            MutableComponent name = Component.translatable(mapping.getName());
            if (side == Side.LEFT) {
                gfx.drawString(Minecraft.getInstance().font, name, 12 + icon.width, height() - leftY - 3 + icon.height / 2, 0xFFFFFF);

                leftY += 4 + icon.height;
                side = Side.RIGHT;
            } else {
                int textRightX = width() - 20 - icon.width - Minecraft.getInstance().font.width(name);
                gfx.drawString(Minecraft.getInstance().font, name, textRightX, height() - rightY - 3 + icon.height / 2, 0xFFFFFF);

                rightY += 4 + icon.height;
                side = Side.LEFT;
            }
        }
    }

    private int width() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    private int height() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }
}
