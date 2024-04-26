package io.github.ultreon.controllerx.config.entries;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.api.ControllerAction;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.config.Config;
import io.github.ultreon.controllerx.config.gui.ConfigEntry;
import io.github.ultreon.controllerx.input.dyn.ControllerInterDynamic;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ControllerBindingEntry<T extends Enum<T> & ControllerInterDynamic<?>> extends ConfigEntry<T> {
    private final Class<T> clazz;
    private final ControllerMapping<T> mapping;

    @SuppressWarnings("unchecked")
    public ControllerBindingEntry(String key, ControllerMapping<T> mapping, ControllerMapping<T> value, Component description) {
        super(key, value.getAction().getDefaultValue(), description);
        this.mapping = mapping;

        ControllerAction<T> action = value.getAction();
        this.clazz = (Class<T>) action.getMapping().getClass();
    }

    @Override
    protected T read(String text) {
        return mapping.getAction().getMapping();
    }

    @Override
    public AbstractWidget createButton(Config options, int x, int y, int width) {
        final ControllerInputButton button = new ControllerInputButton(x, y, width, 20, Component.nullToEmpty("Value"), options.getContext(), mapping);
        button.setAction(mapping.getAction());
        return button;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromWidget(AbstractWidget widget) {
        ControllerInputButton button = (ControllerInputButton) widget;
        ControllerAction<T> value = button.getAction();
        this.set(value.getMapping());
    }

    public class ControllerInputButton extends Button {
        private final ControllerContext context;
        private final ControllerMapping<T> mapping;
        private ControllerAction<T> action;

        public ControllerInputButton(int x, int y, int width, int height, Component message, ControllerContext context, ControllerMapping<T> mapping) {
            super(x, y, width, height, message, (button) -> {}, (button) -> Component.empty());
            this.context = context;
            this.mapping = mapping;
            this.action = mapping.getAction();
        }

        @Override
        public void onPress() {
            ControllerX.get().input.interceptInputOnce((evt) -> {
                if (evt.mapping().getClass() == clazz) {
                    this.action.setMapping(evt.mapping().as(this.action.getMapping()));
                    this.setMessage(Component.nullToEmpty(evt.mapping().name()));
                }
            });
        }

        public ControllerContext getContext() {
            return context;
        }

        public ControllerMapping<T> getMapping() {
            return mapping;
        }

        public ControllerAction<T> getAction() {
            return this.action;
        }

        public void setAction(@NotNull ControllerAction<T> action) {
            this.action = action;
            this.setMessage(Component.nullToEmpty(action.getMapping().name()));
        }

        public void actuallySetAction(@NotNull ControllerAction<T> tControllerMapping) {
            mapping.setAction(tControllerMapping);
        }

        public void reset() {
            this.action = mapping.getDefaultAction();
        }
    }
}
