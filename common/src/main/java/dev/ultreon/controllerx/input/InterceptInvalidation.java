package dev.ultreon.controllerx.input;

public interface InterceptInvalidation {
    void onIntercept(ControllerInput.InterceptCallback callback);

    boolean isStillValid();
}
