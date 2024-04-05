package io.github.ultreon.controllerx.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.ultreon.controllerx.config.Config;
import io.github.ultreon.controllerx.config.gui.ConfigEntry;
import io.github.ultreon.controllerx.util.InputDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public final class ControllerMapping<T extends InputDefinition<?>> {
    final ControllerAction<T> action;
    private final String id;
    private final Side side;
    private final Component name;
    private boolean visible ;
    private final Predicate<Minecraft> condition;
    private final Event<VisibilityEvent> event = EventFactory.createEventResult();

    public ControllerMapping(ControllerAction<T> action,
                             Side side, Component name, boolean visible, String id, Predicate<Minecraft> condition) {
        this.action = action;
        this.side = side;
        this.name = name;
        this.visible = visible;
        this.id = id;
        this.condition = condition;
    }

    public ControllerMapping(ControllerAction<T> action, Side side, Component name, boolean visible, String id) {
        this(action, side, name, visible, id, (minecraft) -> true);
    }

    public ControllerMapping(ControllerAction<T> action, Side side, Component name, String id, Predicate<Minecraft> condition) {
        this(action, side, name, true, id, condition);
    }

    public ControllerMapping(ControllerAction<T> action, Side side, Component name, String id) {
        this(action, side, name, true, id);
    }

    public boolean isVisible() {
        EventResult eventResult = this.event.invoker().onVisible(visible);
        if (eventResult.isPresent()) return eventResult.value();
        return visible && condition.test(Minecraft.getInstance());
    }

    public boolean isEnabled() {
        return condition.test(Minecraft.getInstance());
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "ControllerMapping[" +
                "action=" + action + ", " +
                "side=" + side + ", " +
                "name=" + name + ']';
    }

    public @Nullable ControllerAction<T> getAction() {
        if (!isEnabled()) return action.nulled();
        return action;
    }

    public Side getSide() {
        return side;
    }

    public Component getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ControllerMapping<?>) obj;
        return Objects.equals(this.action, that.action) &&
                Objects.equals(this.side, that.side) &&
                Objects.equals(this.name, that.name) &&
                this.visible == that.visible;
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, side, name, visible);
    }

    public String getId() {
        return id;
    }

    public ConfigEntry<?> createEntry(Config config) {
        ResourceLocation id = config.getContext().id;
        return action.createEntry(config, id.getNamespace() + ".action." + id.getPath().replace('/', '.') + "." + this.id, name);
    }

    public enum Side {
        LEFT,
        RIGHT
    }

    @FunctionalInterface
    public interface VisibilityEvent {
        EventResult onVisible(boolean visible);
    }
}
