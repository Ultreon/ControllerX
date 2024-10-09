package dev.ultreon.controllerx.input.dyn;

import dev.ultreon.controllerx.Icon;
import dev.ultreon.controllerx.input.ControllerBoolean;
import dev.ultreon.controllerx.input.ControllerSignedFloat;
import dev.ultreon.controllerx.input.ControllerUnsignedFloat;
import dev.ultreon.controllerx.input.ControllerVec2;

public interface ControllerInterDynamic<T> extends
        ControllerDynamic, BooleanConvertible<T>, SignedFloatConvertible<T>, UnsignedFloatConvertible<T>, Vec2Convertible<T> {

    @SuppressWarnings("unchecked")
    default <V extends ControllerInterDynamic<?>> V as(V mapping) {
        if (mapping instanceof ControllerBoolean) {
            return (V) this.asBoolean();
        } else if (mapping instanceof ControllerSignedFloat) {
            return (V) this.asSignedFloat();
        } else if (mapping instanceof ControllerUnsignedFloat) {
            return (V) this.asUnsignedFloat();
        } else if (mapping instanceof ControllerVec2) {
            return (V) this.asVec2();
        } else {
            throw new IllegalArgumentException("Cannot convert " + this + " to " + mapping);
        }
    }

    Icon getIcon();

    <T extends Enum<T> & ControllerInterDynamic<T>> T fromName(String text);
}
