
package io.github.ultreon.controllerx.config.gui;

import io.github.ultreon.controllerx.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ConfigsList extends ContainerObjectSelectionList<ConfigsList.ListEntry> {
    public ConfigsList(Minecraft minecraft, int width, int height, int i, int i1) {
        super(minecraft, width, height, i, i1, 28);
        this.centerListVertically = false;
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ConfigsScreen(mc.screen));
    }

    public void addEntries(Config[] configs) {
        for (Config config : configs) {
            ListEntry of = ListEntry.of(this, config, this.getRowWidth(), config);
            this.addEntry(of);
        }
    }

    public int getRowWidth() {
        return this.width - 4;
    }

    protected int getScrollbarPosition() {
        return this.width - 5;
    }

    public void save() {
        Config.saveAll();
    }

    protected static class ListEntry extends Entry<ListEntry> {
        private final ConfigsList list;
        final Config configEntry;
        final AbstractWidget widget;

        private ListEntry(ConfigsList list, Config configEntry, AbstractWidget widget) {
            this.list = list;
            this.configEntry = configEntry;
            this.widget = widget;
        }

        public static ListEntry of(ConfigsList list, Config config, int rowWidth, Config rightOption) {
            return new ListEntry(list, rightOption, rightOption.createButton(config, rowWidth - 160, 0, 150));
        }

        public void render(@NotNull GuiGraphics gfx, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean selected, float partialTicks) {
            if (this.list.isMouseOver(mouseX, mouseY) && this.isMouseOver(mouseX, mouseY)) {
                gfx.fill(x - 4, y, x + rowWidth, y + rowHeight, 0x40ffffff);
            }

            Minecraft mc = Minecraft.getInstance();
            gfx.drawString(mc.font, this.configEntry.getTitle(), 2 + x, y + rowHeight / 2 - mc.font.lineHeight / 2, 0xffffffff, true);

            this.widget.setY(y + 2);
            this.widget.render(gfx, mouseX, mouseY, partialTicks);
        }

        @NotNull
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(this.widget);
        }

        @NotNull
        public List<? extends NarratableEntry> narratables() {
            return Collections.singletonList(this.widget);
        }
    }
}
