package dev.ultreon.controllerx.input;

public class CountInvalidationI implements IInterceptInvalidation {
    private int count;

    public CountInvalidationI(int count) {
        this.count = count;
    }

    @Override
    public void onIntercept(ControllerInput.InterceptCallback callback) {
        count--;
    }

    @Override
    public boolean isStillValid() {
        return count > 0;
    }
}
