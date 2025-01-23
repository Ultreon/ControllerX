package dev.ultreon.controllerx.api;

import java.util.List;
import java.util.ServiceLoader;

class ModGetter {
    private static IControllerX controllerX;

    static IControllerX getControllerX() {
        if (controllerX != null) return controllerX;

        List<ServiceLoader.Provider<IControllerX>> list = ServiceLoader.load(IControllerX.class).stream().toList();
        if (list.isEmpty()) throw new IllegalStateException("No implementation of IControllerX found!");
        if (list.size() > 1) throw new IllegalStateException("Multiple implementations of IControllerX found!");
        return controllerX = list.get(0).get();
    }
}
