package dev.ultreon.controllerx.input;

import io.github.libsdl4j.api.gamecontroller.SDL_GameController;

public record Controller(SDL_GameController sdlController, int deviceIndex, short productId, short vendorId,
                         String name, String mapping) {
}
