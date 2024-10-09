package dev.ultreon.controllerx.input;

public class CountInvalidation implements InterceptInvalidation {
    private int count;

    public CountInvalidation(int count) {
        this.count = count;
    }

    @Override
    public void onIntercept(ControllerInput.InterceptCallback callback) {
        this.count--;
    }

    @Override
    public boolean isStillValid() {
        return count > 0;
    }
}
