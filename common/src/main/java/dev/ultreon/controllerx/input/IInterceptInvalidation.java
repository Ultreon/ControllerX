package dev.ultreon.controllerx.input;

public interface IInterceptInvalidation {
    void onIntercept(ControllerInput.InterceptCallback callback);

    boolean isStillValid();
}
