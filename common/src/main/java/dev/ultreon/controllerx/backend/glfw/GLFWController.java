package dev.ultreon.controllerx.backend.glfw;

import dev.ultreon.controllerx.api.input.IController;

public record GLFWController(int glfwController, int deviceIndex, String name) implements IController {
}
