package io.github.ultreon.controllerx.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.ultreon.controllerx.util.InputDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Predicate;

public final class ControllerMapping<T extends InputDefinition<?>> {
    final ControllerAction<T> action;
    private final Side side;
    private final Component name;
    private boolean visible ;
    private final Predicate<Minecraft> condition;
    private final Event<VisibilityEvent> event = EventFactory.createEventResult();
    private NullAction nullAction;

    public ControllerMapping(ControllerAction<T> action,
                             Side side, Component name, boolean visible, Predicate<Minecraft> condition) {
        this.action = action;
        this.side = side;
        this.name = name;
        this.visible = visible;
        this.condition = condition;
    }

    public ControllerMapping(ControllerAction<T> action, Side side, Component name, boolean visible) {
        this(action, side, name, visible, (minecraft) -> true);
    }

    public ControllerMapping(ControllerAction<T> action, Side side, Component name, Predicate<Minecraft> condition) {
        this(action, side, name, true, condition);
    }

    public ControllerMapping(ControllerAction<T> action, Side side, Component name) {
        this(action, side, name, true);
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

    public ControllerAction<T> getAction() {
        if (!isEnabled()) return nullAction();
        return action;
    }

    private ControllerAction<T> nullAction() {
        if (this.nullAction == null) {
            this.nullAction = new NullAction();
        }

        return this.nullAction;
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

    public enum Side {
        LEFT,
        RIGHT
    }

    @FunctionalInterface
    public interface VisibilityEvent {
        EventResult onVisible(boolean visible);
    }

    final class NullAction implements ControllerAction<T> {

        @Override
        public T getMapping() {
            return ControllerMapping.this.action.getMapping();
        }

        @Override
        public boolean isPressed() {
            return false;
        }

        @Override
        public float getValue() {
            return 0;
        }

        @Override
        public float getAxisValue() {
            return 0;
        }

        @Override
        public void setMapping(T mapping) {
            ControllerMapping.this.action.setMapping(mapping);
        }
    }
}
