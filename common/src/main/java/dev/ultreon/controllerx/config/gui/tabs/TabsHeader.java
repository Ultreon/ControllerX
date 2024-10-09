package dev.ultreon.controllerx.config.gui.tabs;

import dev.ultreon.controllerx.ControllerX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TabsHeader extends AbstractWidget {
    private static final ResourceLocation TEXTURE = ControllerX.res("textures/gui/tab_ui.png");
    private final Font font = Minecraft.getInstance().font;
    private final Tabs tabs;
    private double scrollPosition;
    private double tabScroll;

    public TabsHeader(Tabs tabs) {
        super(0, 0, tabs.getWidth(), 20, tabs.getMessage());
        this.tabs = tabs;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int tabWidth = tabs.getTabWidth();
        int tabIndex = tabs.getTabCount() - tabs.getSelectedTab() - 1;

        int expectedX = tabWidth * tabIndex;

        if (tabScroll > expectedX) {
            tabScroll = Math.max(tabScroll + ((expectedX - tabScroll) / 1.0 * partialTick), expectedX);
        } else if (tabScroll < expectedX) {
            tabScroll = Math.min(tabScroll - ((tabScroll - expectedX) / 1.0 * partialTick), expectedX);
        }

        int x = (int) tabScroll;

        guiGraphics.fill(getX(), 0, getWidth(), getY() + getHeight() - 1, 0xff000000);
        guiGraphics.fill(getX(), getY() + getHeight() - 1, getWidth(), getY() + getHeight(), 0x80ffffff);

        for (int i = tabs.getTabCount() - 1; i >= 0; i--) {
            int tabX = 10 + x - (tabWidth * i);
            int tabY = getY() + 1;

            guiGraphics.blitNineSliced(TEXTURE, tabX, tabY, tabWidth, height - 2, 7, 7, 21, 21, 0, tabIndex == i ? 22 : 0);
            Button.renderScrollingString(guiGraphics, font, tabs.getTabAt(i).getMessage(), tabX + 3, tabY + (tabIndex == i ? 7 : 3), tabX + tabWidth - 3, tabY + height - 5, 0xffffffff);
        }
    }

    @Override
    public @NotNull Component getMessage() {
        return tabs.getCurrentTab().getMessage();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        // Tab headers don't have narration
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        double oldScrollPosition = this.scrollPosition;
        this.scrollPosition += delta / 2.0;

        int index = (int) scrollPosition;
        if (index != (int) oldScrollPosition) {
            int tabCount = this.tabs.getTabCount();
            index = Mth.clamp(index, 0, tabCount - 1);
            scrollPosition = Mth.clamp(scrollPosition, 0, tabCount);
            this.tabs.selectTab((index + tabCount) % tabCount);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public void resize(int width) {
        this.setWidth(width);
    }
}
