package io.github.ultreon.controllerx.input;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public class ControllerEvent {
    public static final Event<AxisEvent> CONTROLLER_AXIS = EventFactory.createLoop();
    public static final Event<ButtonEvent> CONTROLLER_BUTTON = EventFactory.createLoop();
    public static final Event<ConnectionEvent> CONTROLLER_CONNECTED = EventFactory.createLoop();
    public static final Event<ConnectionEvent> CONTROLLER_DISCONNECTED = EventFactory.createLoop();

    @FunctionalInterface
    public interface AxisEvent {
        void onAxis(ControllerAxis controllerAxis, float value);
    }

    @FunctionalInterface
    public interface ButtonEvent {
        void onButton(ControllerButton controllerButton, boolean pressed);
    }

    @FunctionalInterface
    public interface ConnectionEvent {
        void onConnectionStatus(Controller controller);
    }
}
