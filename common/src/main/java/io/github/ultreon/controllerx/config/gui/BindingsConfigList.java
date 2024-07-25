package io.github.ultreon.controllerx.config.gui;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.config.Config;
import io.github.ultreon.controllerx.text.Texts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BindingsConfigList extends ContainerObjectSelectionList<BindingsConfigList.ListEntry> {
    private final List<ListEntry> entries = new ArrayList<>();
    private final Config config;

    public BindingsConfigList(Minecraft minecraft, int width, int height, int x, int i1, Config config) {
        super(minecraft, width, height, i1, 28);
        this.setX(x);
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
        private final BindingsConfigList list;
        final ConfigEntry<?> configEntry;
        final Button resetBtn;
        AbstractWidget widget;

        private ListEntry(BindingsConfigList list, Config config, ConfigEntry<?> configEntry, int rowWidth) {
            this.list = list;
            this.configEntry = configEntry;
            this.widget = configEntry.createButton(config, rowWidth - 160, 0, 150);

            this.resetBtn = new ImageButton(0, 0, 20, 20, new WidgetSprites(ControllerX.res("textures/gui/reset.png"), ControllerX.res("textures/gui/reset_disabled.png"), ControllerX.res("textures/gui/reset_highlighted.png")), button -> {
                configEntry.reset();
                widget = configEntry.createButton(config, list.getRowWidth() - 160, 0, 150);
            }, Texts.GUI_RESET);
        }

        public static ListEntry of(BindingsConfigList list, Config config, int rowWidth, ConfigEntry<?> entry) {
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
