package dev.ultreon.controllerx.api.input.dyn;

import dev.ultreon.controllerx.api.input.ControllerBoolean;
import dev.ultreon.controllerx.api.input.ControllerSignedFloat;
import dev.ultreon.controllerx.api.input.ControllerUnsignedFloat;
import dev.ultreon.controllerx.api.input.ControllerVec2;
import net.minecraft.network.chat.Component;

public interface IControllerInterDynamic<T> extends
        ControllerDynamic, BooleanConvertible<T>, SignedFloatConvertible<T>, UnsignedFloatConvertible<T>, Vec2Convertible<T> {

    Component getDisplayName();

    @SuppressWarnings("unchecked")
    default <V extends IControllerInterDynamic<?>> V as(V mapping) {
        if (mapping instanceof ControllerBoolean) {
            return (V) asBoolean();
        } else if (mapping instanceof ControllerSignedFloat) {
            return (V) asSignedFloat();
        } else if (mapping instanceof ControllerUnsignedFloat) {
            return (V) asUnsignedFloat();
        } else if (mapping instanceof ControllerVec2) {
            return (V) asVec2();
        } else {
            throw new IllegalArgumentException("Cannot convert " + this + " to " + mapping);
        }
    }

    dev.ultreon.controllerx.api.Icon getIcon();

    <E extends Enum<E> & IControllerInterDynamic<E>> E fromName(String text);
}
