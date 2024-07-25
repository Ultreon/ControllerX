package io.github.ultreon.controllerx.gui;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.input.ControllerInput;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ControllerHud {
    private final ControllerInput input = ControllerX.get().input;

    public void render(GuiGraphics gfx, DeltaTracker ignoredPartialTicks) {
        ControllerContext ctx = ControllerContext.get();

        if (ctx == null) return;
        if (!input.isAvailable()) return;

        Iterable<ControllerMapping<?>> mappings = ctx.mappings.getAllMappings();

        int leftY = 20 + ctx.getYOffset();
        int rightY = 20 + ctx.getYOffset();

        for (ControllerMapping<?> mapping : mappings) {
            if (!mapping.isVisible()) continue;

            ControllerMapping.Side side = mapping.getSide();
            int x = side == ControllerMapping.Side.LEFT ? 4 + ctx.getLeftXOffset() : width() - 24 - ctx.getRightXOffset();
            int y = height() - (side == ControllerMapping.Side.LEFT ? leftY : rightY);
            mapping.getAction().getMapping().getIcon().render(gfx, x, y);

            if (side == ControllerMapping.Side.LEFT) {
                gfx.drawString(Minecraft.getInstance().font, mapping.getName(), 28 + ctx.getLeftXOffset(), height() - leftY + 4, 0xFFFFFF);

                leftY += 20;
            } else {
                int textRightX = width() - 28 - Minecraft.getInstance().font.width(mapping.getName());
                gfx.drawString(Minecraft.getInstance().font, mapping.getName(), textRightX - ctx.getRightXOffset(), height() - rightY + 4, 0xFFFFFF);

                rightY += 20;
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
