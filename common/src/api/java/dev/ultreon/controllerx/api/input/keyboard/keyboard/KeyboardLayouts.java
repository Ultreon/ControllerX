package dev.ultreon.controllerx.api.input.keyboard.keyboard;

public class KeyboardLayouts {
    public static final KeyboardLayout QWERTY = new KeyboardLayout(
            new char[][]{
                    {'`', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '=', '\b'},
                    {'\t', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']', '\\'},
                    {'\f', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'', '\n'},
                    {0xff1d, 'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/'},
                    {0xff1b, 0xff1c, 0xff1e, ' ', 0xff1f, 0xff21, 0xff22}
            },
            new char[][]{
                    {'~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '\b'},
                    {'\t', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', '{', '}', '|'},
                    {'\f', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ':', '"', '\n'},
                    {0xff1d, 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '<', '>', '?'},
                    {0xff1b, 0xff1c, 0xff1e, ' ', 0xff1f, 0xff21, 0xff22}
            }
    );

    public static final KeyboardLayout AZERTY = new KeyboardLayout(
            new char[][]{
                    {'`', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '=', '\b'},
                    {'\t', 'a', 'z', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']', '\\'},
                    {'\f', 'q', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'', '\n'},
                    {0xff1d, 'w', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/'},
                    {0xff1b, 0xff1c, 0xff1e, ' ', 0xff1f, 0xff21, 0xff22}
            },
            new char[][]{
                    {'~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '\b'},
                    {'\t', 'A', 'Z', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', '{', '}', '|'},
                    {'\f', 'Q', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ':', '"', '\n'},
                    {0xff1d, 'W', 'X', 'C', 'V', 'B', 'N', 'M', '<', '>', '?'},
                    {0xff1b, 0xff1c, 0xff1e, ' ', 0xff1f, 0xff21, 0xff22}
            }
    );

    public static final KeyboardLayout QWERTZ = new KeyboardLayout(
            new char[][]{
                    {'`', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '=', '\b'},
                    {'\t', 'q', 'w', 'e', 'r', 't', 'z', 'u', 'i', 'o', 'p', '[', ']', '\\'},
                    {'\f', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'', '\n'},
                    {0xff1d, 'y', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/'},
                    {0xff1b, 0xff1c, 0xff1e, ' ', 0xff1f, 0xff21, 0xff22}
            },
            new char[][]{
                    {'~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '\b'},
                    {'\t', 'Q', 'W', 'E', 'R', 'T', 'Z', 'U', 'I', 'O', 'P', '{', '}', '|'},
                    {'\f', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ':', '"', '\n'},
                    {0xff1d, 'Y', 'X', 'C', 'V', 'B', 'N', 'M', '<', '>', '?'},
                    {0xff1b, 0xff1c, 0xff1e, ' ', 0xff1f, 0xff21, 0xff22}
            }
    );
}
