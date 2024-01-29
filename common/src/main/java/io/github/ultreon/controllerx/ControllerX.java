package io.github.ultreon.controllerx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import io.github.libsdl4j.api.SdlSubSystemConst;
import io.github.libsdl4j.api.event.SDL_Event;
import io.github.ultreon.controllerx.input.ControllerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.libsdl4j.api.Sdl.SDL_Init;
import static io.github.libsdl4j.api.Sdl.SDL_Quit;

public class ControllerX {
    public static final String MOD_ID = "controllerx";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Logger LOGGER = LoggerFactory.getLogger("ControllerX");
    public static final byte MAX_CONTROLLERS = 1;
    private static ControllerInput input;

    public static void init() {
        SDL_Init(SdlSubSystemConst.SDL_INIT_EVENTS | SdlSubSystemConst.SDL_INIT_GAMECONTROLLER | SdlSubSystemConst.SDL_INIT_JOYSTICK);
        ClientLifecycleEvent.CLIENT_STOPPING.register(instance -> SDL_Quit());
        input = new ControllerInput();

        LOGGER.info("ControllerX initialized");
    }

    public static ControllerInput getInput() {
        return input;
    }
}
