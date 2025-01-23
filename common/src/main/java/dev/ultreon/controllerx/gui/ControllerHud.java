package dev.ultreon.controllerx.gui;

import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.api.IControllerMapping;
import dev.ultreon.controllerx.input.ControllerInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ControllerHud {
    private final ControllerInput input = ControllerX.get().input;

    public void render(GuiGraphics gfx, float ignoredPartialTicks) {
        ControllerContext ctx = ControllerContext.get();

        if (ctx == null) return;
        if (!input.isAvailable()) return;

        Iterable<IControllerMapping<?>> mappings = ctx.mappings.getAllMappings();

        if (!ctx.shouldShowHUD()) return;

        int leftY = 20 + ctx.getYOffset();
        int rightY = 20 + ctx.getYOffset();

        for (IControllerMapping<?> mapping : mappings) {
            if (!mapping.isVisible()) continue;

            IControllerMapping.Side side = mapping.getSide();
            int x = side == IControllerMapping.Side.LEFT ? 4 + ctx.getLeftXOffset() : width() - 24 - ctx.getRightXOffset();
            int y = height() - (side == IControllerMapping.Side.LEFT ? leftY : rightY);
            mapping.getAction().getMapping().getIcon().render(gfx, x, y);

            if (side == IControllerMapping.Side.LEFT) {
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
