package io.github.ultreon.controllerx.config.gui;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import io.github.ultreon.controllerx.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigEntry<T> {
    private final String key;
    private final Component description;
    private final T defaultValue;
    private T value;
    private String comment;

    public ConfigEntry(String key, T value, Component description) {
        this.key = transform(key);
        this.defaultValue = value;
        this.value = value;
        this.description = description;
    }

    private String transform(String key) {
        String[] split = key.split("\\.");
        for (int i = 0; i < split.length; i++) {
            split[i] = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, split[i]);
        }

        return String.join(".", split);
    }

    public T get() {
        return this.value;
    }

    public void set(@NotNull T value) {
        Preconditions.checkNotNull(value, "Entry value shouldn't be null.");
        this.value = value;
    }

    public ConfigEntry<T> comment(String comment) {
        this.comment = comment;
        return this;
    }

    protected abstract T read(String text);

    public void readAndSet(String text) {
        try {
            this.value = this.read(text);
        } catch (Exception ignored) {

        }
    }

    public String getComment() {
        return this.comment;
    }

    public String getKey() {
        return this.key;
    }

    public String write() {
        return this.value.toString();
    }

    public Component getDescription() {
        return this.description;
    }

    public AbstractWidget createButton(Config options, int x, int y, int width) {
        return new AbstractWidget(x, y, width, 20, this.getDescription()) {
            @Override
            public void renderWidget(@NotNull GuiGraphics gfx, int i, int j, float f) {
                gfx.drawCenteredString(Minecraft.getInstance().font, ConfigEntry.this.getDescription(), this.getX() + this.width / 2, this.getY() + (this.height / 2 - 5), 0xffffffff);
            }

            @Override
            protected void updateWidgetNarration(@NotNull NarrationElementOutput ignored) {

            }
        };
    }

    public abstract void setFromWidget(AbstractWidget widget);

    public void reset() {
        this.value = this.defaultValue;
    }

    public T getDefault() {
        return defaultValue;
    }
}
