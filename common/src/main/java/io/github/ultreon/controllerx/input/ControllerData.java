package io.github.ultreon.controllerx.input;

import it.unimi.dsi.fastutil.ints.Int2FloatFunction;

import java.util.BitSet;
import java.util.function.IntPredicate;

public class ControllerData {
    public float[] axes;
    public BitSet buttons;
    
    public ControllerData() {
        axes = new float[ControllerSignedFloat.values().length];
        buttons = new BitSet(ControllerBoolean.values().length);
    }
    
    public ControllerData(ControllerData other) {
        axes = new float[ControllerSignedFloat.values().length];
        buttons = new BitSet(ControllerBoolean.values().length);
        
        System.arraycopy(other.axes, 0, axes, 0, axes.length);
        buttons.clear();
        other.buttons.stream().forEachOrdered(buttons::set);
    }

    public ControllerData(float[] axes, BitSet pressedButtons) {

    }

    public boolean isButtonPressed(ControllerBoolean button) {
        return buttons.get(button.ordinal());
    }
    
    public float getAxis(ControllerSignedFloat axis) {
        return axes[axis.ordinal()];
    }

    public void set(Int2FloatFunction axisMapping, IntPredicate buttonMapping) {
        for (ControllerSignedFloat axis : ControllerSignedFloat.values())
            axes[axis.ordinal()] = axisMapping.apply(axis.sdlAxis());
        for (ControllerBoolean button : ControllerBoolean.values())
            buttons.set(button.ordinal(), buttonMapping.test(button.sdlButton()));
    }

    public ControllerData copy() {
        synchronized (this) {
            return new ControllerData(this);
        }
    }
}
