package dev.ultreon.controllerx.config.gui;

import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.config.Config;
import dev.ultreon.controllerx.config.entries.ControllerBindingEntry;
import dev.ultreon.controllerx.text.Texts;
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

public class BindingsList extends ContainerObjectSelectionList<BindingsList.ListEntry> {
    private final List<ListEntry> entries = new ArrayList<>();
    private final Config config;

    public BindingsList(Minecraft minecraft, int width, int height, int y0, int y1, Config config) {
        super(minecraft, width, height, y0, y1, 28);
        this.config = config;
        this.centerListVertically = false;
        this.setRenderTopAndBottom(false);
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

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected static class ListEntry extends Entry<ListEntry> {
        private final BindingsList list;
        final ControllerBindingEntry<?> configEntry;
        final Button resetBtn;
        AbstractWidget widget;

        private ListEntry(BindingsList list, Config config, ConfigEntry<?> configEntry, int rowWidth) {
            this.list = list;
            this.configEntry = (ControllerBindingEntry<?>) configEntry;
            this.widget = configEntry.createButton(config, rowWidth - 160, 0, 150);

            this.resetBtn = new ImageButton(0, 0, 20, 20, 0, 0, 20, ControllerX.res("textures/gui/reset.png"), 20, 40, button -> {
                configEntry.reset();
                widget = configEntry.createButton(config, list.getRowWidth() - 160, 0, 150);
            }, Texts.GUI_RESET);
        }

        public static ListEntry of(BindingsList list, Config config, int rowWidth, ConfigEntry<?> entry) {
            return new ListEntry(list, config, entry, rowWidth);
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
