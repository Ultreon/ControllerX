package dev.ultreon.controllerx;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyboardHelper {
    public static boolean isAltDown() {
        return GLFW.glfwGetKey(getWindowHandle(), GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(getWindowHandle(), GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
    }

    public static boolean isCtrlDown() {
        return GLFW.glfwGetKey(getWindowHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(getWindowHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }

    public static boolean isShiftDown() {
        return GLFW.glfwGetKey(getWindowHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(getWindowHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    private static long getWindowHandle() {
        return Minecraft.getInstance().getWindow().getWindow();
    }
}
