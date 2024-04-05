package io.github.ultreon.controllerx.api;

import io.github.ultreon.controllerx.input.ControllerAxis;
import io.github.ultreon.controllerx.input.ControllerButton;
import io.github.ultreon.controllerx.input.ControllerJoystick;
import io.github.ultreon.controllerx.input.ControllerTrigger;

import static io.github.ultreon.controllerx.api.ControllerAction.*;

public class ControllerActions {
    public static final Button A = new Button(ControllerButton.A);
    public static final Button B = new Button(ControllerButton.B);
    public static final Button X = new Button(ControllerButton.X);
    public static final Button Y = new Button(ControllerButton.Y);
    public static final Button DPAD_UP = new Button(ControllerButton.DPadUp);
    public static final Button DPAD_DOWN = new Button(ControllerButton.DPadDOwn);
    public static final Button DPAD_LEFT = new Button(ControllerButton.DPadLeft);
    public static final Button DPAD_RIGHT = new Button(ControllerButton.RPadRight);
    public static final Button LEFT_SHOULDER = new Button(ControllerButton.LeftShoulder);
    public static final Button RIGHT_SHOULDER = new Button(ControllerButton.RightShoulder);
    public static final Button PRESS_LEFT_STICK = new Button(ControllerButton.LeftStickClick);
    public static final Button PRESS_RIGHT_STICK = new Button(ControllerButton.RightStickClick);
    public static final Button START = new Button(ControllerButton.Start);
    public static final Button BACK = new Button(ControllerButton.Back);
    public static final Button GUIDE = new Button(ControllerButton.Guide);
    public static final Trigger LEFT_TRIGGER = new Trigger(ControllerTrigger.Left);
    public static final Trigger RIGHT_TRIGGER = new Trigger(ControllerTrigger.Right);
    public static final Joystick MOVE_LEFT_STICK = new Joystick(ControllerJoystick.Left);
    public static final Joystick MOVE_RIGHT_STICK = new Joystick(ControllerJoystick.Right);
    public static final Axis MOVE_LEFT_STICK_X = new Axis(ControllerAxis.LeftStickX);
    public static final Axis MOVE_LEFT_STICK_Y = new Axis(ControllerAxis.LeftStickY);
    public static final Axis MOVE_RIGHT_STICK_X = new Axis(ControllerAxis.RightStickX);
    public static final Axis MOVE_RIGHT_STICK_Y = new Axis(ControllerAxis.RightStickY);
    public static final Axis MOVE_LEFT_TRIGGER = new Axis(ControllerAxis.LeftTrigger);
    public static final Axis MOVE_RIGHT_TRIGGER = new Axis(ControllerAxis.RightTrigger);
    public static final Button RIGHT_TRIGGER_HOLD = new Button(ControllerButton.RightTrigger);
    public static final Button LEFT_TRIGGER_HOLD = new Button(ControllerButton.LeftTrigger);
}
