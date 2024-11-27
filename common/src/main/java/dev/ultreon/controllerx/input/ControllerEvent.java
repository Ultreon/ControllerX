package dev.ultreon.controllerx.input;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public class ControllerEvent {
    public static final Event<ConnectionEvent> CONTROLLER_CONNECTED = EventFactory.createLoop();
    public static final Event<ConnectionEvent> CONTROLLER_DISCONNECTED = EventFactory.createLoop();

    @FunctionalInterface
    public interface ConnectionEvent {
        void onConnectionStatus(Controller controller);
    }
}
