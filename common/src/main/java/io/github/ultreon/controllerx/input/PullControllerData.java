package io.github.ultreon.controllerx.input;

import it.unimi.dsi.fastutil.ints.Int2FloatFunction;

import java.util.BitSet;
import java.util.function.IntPredicate;

public class PullControllerData {
    private ControllerData front;
    private final ControllerData back;

    public PullControllerData() {
        front = new ControllerData();
        back = new ControllerData();
    }

    public PullControllerData(float[] axes, BitSet pressedButtons) {
        front = new ControllerData();
        back = new ControllerData(axes, pressedButtons);
    }

    public void pull() {
        front = back.copy();
    }
    
    public ControllerData getData() {
        return front;
    }
    
    public ControllerData getBackData() {
        return back;
    }
    
    public void setBackData(Int2FloatFunction axisMapping, IntPredicate buttonMapping) {
        back.set(axisMapping, buttonMapping);
    }
}
