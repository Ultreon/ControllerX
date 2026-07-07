package dev.ultreon.controllerx;

import java.util.function.Function;

public interface StateInit<T> extends Function<String, T> {
    StateInit<Boolean> BOOLEAN = property -> {
        String value = System.getProperty(property);
        return value == null ? null : Boolean.parseBoolean(value);
    };

    StateInit<Integer> INT = Integer::getInteger;

    StateInit<String> STRING = System::getProperty;
}
