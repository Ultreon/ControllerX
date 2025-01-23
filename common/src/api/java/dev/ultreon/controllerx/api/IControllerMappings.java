package dev.ultreon.controllerx.api;

public interface IControllerMappings {

    <T extends IControllerMapping<?>> T register(T mapping);

    Iterable<IControllerMapping<?>> getAllMappings();
}
