package io.github.ultreon.controllerx.config.gui;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.config.Config;
import io.github.ultreon.controllerx.text.Texts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigList extends ContainerObjectSelectionList<ConfigList.ListEntry> {
    private final List<ListEntry> entries = new ArrayList<>();
    private final Config config;

    public ConfigList(Minecraft minecraft, int width, int height, int i, int i1, Config config) {
        super(minecraft, width, height, i, i1, 28);
        this.config = config;
        this.centerListVertically = false;
    }

    public void addEntries(ConfigEntry<?>[] options) {
        for (ConfigEntry<?> option : options) {
            ListEntry of = ListEntry.of(this, config, this.getRowWidth(), option);
            this.entries.add(of);
            this.addEntry(of);
        }
    }

    @Override
    protected void clearEntries() {
        super.clearEntries();
        this.entries.clear();
    }

    public int getRowWidth() {
        return this.width - 4;
    }

    protected int getScrollbarPosition() {
        return this.width - 5;
    }

    public void save() {
        for (ListEntry entry : this.entries) {
            entry.configEntry.setFromWidget(entry.widget);
        }
        config.save();
    }

    protected static class ListEntry extends Entry<ListEntry> {
        private final ConfigList list;
        final ConfigEntry<?> configEntry;
        final AbstractWidget widget;
        final Button resetBtn;

        private ListEntry(ConfigList list, ConfigEntry<?> configEntry, AbstractWidget widget) {
            this.list = list;
            this.configEntry = configEntry;
            this.widget = widget;

            this.resetBtn = new ImageButton(0, 0, 20, 20, 0, 0, 20, ControllerX.res("textures/gui/reset.png"), 20, 40, button -> configEntry.reset(), Texts.GUI_RESET);
        }

        public static ListEntry of(ConfigList list, Config config, int rowWidth, ConfigEntry<?> entry) {
            return new ListEntry(list, entry, entry.createButton(config, rowWidth - 160, 0, 150));
        }

        public void render(@NotNull GuiGraphics gfx, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean selected, float partialTicks) {
            if (this.list.isMouseOver(mouseX, mouseY) && this.isMouseOver(mouseX, mouseY)) {
                gfx.fill(x - 4, y, x + rowWidth, y + rowHeight, 0x40ffffff);
            }

            Minecraft mc = Minecraft.getInstance();
            gfx.drawString(mc.font, this.configEntry.getDescription(), 2 + x, y + rowHeight / 2 - mc.font.lineHeight / 2, 0xffffffff, true);

            this.widget.setX(x + rowWidth - this.widget.getWidth() - 2 - 22);
            this.widget.setY(y + 2);
            this.widget.render(gfx, mouseX, mouseY, partialTicks);

            this.resetBtn.setX(x + rowWidth - this.resetBtn.getWidth() - 2);
            this.resetBtn.setY(y + 2);
            this.resetBtn.render(gfx, mouseX, mouseY, partialTicks);
        }

        @NotNull
        public List<? extends GuiEventListener> children() {
            return List.of(this.widget, this.resetBtn);
        }

        @NotNull
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.widget, this.resetBtn);
        }
    }
}
