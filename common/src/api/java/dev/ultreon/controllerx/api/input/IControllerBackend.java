package dev.ultreon.controllerx.api.input;

public interface IControllerBackend {
    void update();

    Float getAxis(ControllerSignedFloat axis);
    boolean getButton(ControllerBoolean button);

    boolean isConnected();

    IController getController(int deviceIndex);

    boolean isAnyButtonPressed();

    void init();

    void quit();
}
