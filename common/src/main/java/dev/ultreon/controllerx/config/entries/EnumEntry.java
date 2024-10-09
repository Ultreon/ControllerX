package dev.ultreon.controllerx.config.entries;

import dev.ultreon.controllerx.config.Config;
import dev.ultreon.controllerx.config.gui.ConfigEntry;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public class EnumEntry<T extends Enum<T>> extends ConfigEntry<T> {
    private final Class<T> clazz;

    @SuppressWarnings("unchecked")
    public EnumEntry(String key, T value, Component description) {
        super(key, value, description);

        this.clazz = (Class<T>) value.getClass();
    }

    @Override
    protected T read(String text) {
        return Enum.valueOf(clazz, text);
    }

    @Override
    public AbstractWidget createButton(Config options, int x, int y, int width) {
        CycleButton<T> cycleButton = CycleButton.<T>builder(enumValue -> Component.nullToEmpty(enumValue.name())).withValues(clazz.getEnumConstants()).withInitialValue(this.get()).displayOnlyValue().create(x, y, width, 20, Component.nullToEmpty("Value"), (cycler, enumValue) -> {
            cycler.setValue(enumValue);
            cycler.setMessage(Component.nullToEmpty(enumValue.name()));
        });
        cycleButton.setValue(this.get());
        return cycleButton;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromWidget(AbstractWidget widget) {
        CycleButton<T> cycleButton = (CycleButton<T>) widget;
        T value = cycleButton.getValue();
        this.set(value);
    }
}
