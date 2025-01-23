package dev.ultreon.controllerx.backend.sdl;

import dev.ultreon.controllerx.api.input.IController;
import io.github.libsdl4j.api.gamecontroller.SDL_GameController;

public record SdlController(SDL_GameController sdlController, int deviceIndex, short productId, short vendorId,
                            String name, String mapping) implements IController {
}
