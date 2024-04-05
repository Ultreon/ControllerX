package io.github.ultreon.controllerx.debug;

import dev.architectury.platform.Platform;
import io.github.ultreon.controllerx.ControllerX;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class DebugLog {
    public static final Marker DEBUG = MarkerFactory.getMarker("DEBUG");
    
    public static void log(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            ControllerX.LOGGER.info(DEBUG, message);
        }
    }
}
