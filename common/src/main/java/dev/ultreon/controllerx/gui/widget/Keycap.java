package dev.ultreon.controllerx.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ultreon.controllerx.ControllerX;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class Keycap extends Button {
    private static final ResourceLocation TEXTURE = ControllerX.res("textures/gui/keycap.png");
    private static final ResourceLocation TEXTURE_SEL = ControllerX.res("textures/gui/keycap_sel.png");
    private boolean selected;

    public Keycap(int x, int y, Key key, Button.OnPress onPress) {
        super(x, y, key.width(), key.height(), Component.literal(key.toString()), onPress, supplier -> Component.empty());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        guiGraphics.blitNineSliced(TEXTURE, getX(), getY(), getWidth(), getHeight(), 4, 4, 16, 16, 0, getTextureY());
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int color = active ? 0xffffff : 0xa0a0a0;
        renderString(guiGraphics, minecraft.font, color | Mth.ceil(alpha * 255.0F) << 24);
    }

    public enum Key {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
        COMMA, PERIOD, MINUS, EQUALS, COLON, SEMI, TILDE, QUESTION, EXCLAMATION, AT, HASH, UNDERSCORE,
        QUOTE, APOSTROPHE, BACKSLASH, PIPE, EURO, POUND, YEN, TIMES, DIVIDE,
        AMPER, DOLLAR, PERCENT, CIRCUMFLEX, CARET, LEFT_ARROW, RIGHT_ARROW, UP_ARROW, DOWN_ARROW,
        F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12,
        F13, F14, F15, F16, F17, F18, F19, F20, F21, F22, F23, F24,
        KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7, KEY_8, KEY_9, KEY_0,
        LEFT_PAREN, RIGHT_PAREN, LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE,
        LEFT_SHIFT, RIGHT_SHIFT, LEFT_CONTROL, RIGHT_CONTROL, LEFT_ALT, RIGHT_ALT, LEFT_META, RIGHT_META,
        SPACE, BACKSPACE, CAPS_LOCK, ESCAPE, NUM_LOCK, SCROLL_LOCK, TAB, ENTER;

        public static Key byChar(char c) {
            switch (c) {
                case 'a' -> { return A; }
                case 'b' -> { return B; }
                case 'c' -> { return C; }
                case 'd' -> { return D; }
                case 'e' -> { return E; }
                case 'f' -> { return F; }
                case 'g' -> { return G; }
                case 'h' -> { return H; }
                case 'i' -> { return I; }
                case 'j' -> { return J; }
                case 'k' -> { return K; }
                case 'l' -> { return L; }
                case 'm' -> { return M; }
                case 'n' -> { return N; }
                case 'o' -> { return O; }
                case 'p' -> { return P; }
                case 'q' -> { return Q; }
                case 'r' -> { return R; }
                case 's' -> { return S; }
                case 't' -> { return T; }
                case 'u' -> { return U; }
                case 'v' -> { return V; }
                case 'w' -> { return W; }
                case 'x' -> { return X; }
                case 'y' -> { return Y; }
                case 'z' -> { return Z; }
                case ',' -> { return COMMA; }
                case '.' -> { return PERIOD; }
                case '-' -> { return MINUS; }
                case '=' -> { return EQUALS; }
                case ':' -> { return COLON; }
                case ';' -> { return SEMI; }
                case '~' -> { return TILDE; }
                case '?' -> { return QUESTION; }
                case '!' -> { return EXCLAMATION; }
                case '@' -> { return AT; }
                case '#' -> { return HASH; }
                case '_' -> { return UNDERSCORE; }
                case '\'' -> { return APOSTROPHE; }
                case '\\' -> { return BACKSLASH; }
                case '|' -> { return PIPE; }
                case '€' -> { return EURO; }
                case '£' -> { return POUND; }
                case '¥' -> { return YEN; }
                case '×' -> { return TIMES; }
                case '÷' -> { return DIVIDE; }
                case '&' -> { return AMPER; }
                case '$' -> { return DOLLAR; }
                case '%' -> { return PERCENT; }
                case '^' -> { return CIRCUMFLEX; }
                case '1' -> { return KEY_1; }
                case '2' -> { return KEY_2; }
                case '3' -> { return KEY_3; }
                case '4' -> { return KEY_4; }
                case '5' -> { return KEY_5; }
                case '6' -> { return KEY_6; }
                case '7' -> { return KEY_7; }
                case '8' -> { return KEY_8; }
                case '9' -> { return KEY_9; }
                case '0' -> { return KEY_0; }
                case '(' -> { return LEFT_PAREN; }
                case ')' -> { return RIGHT_PAREN; }
                case '[' -> { return LEFT_BRACKET; }
                case ']' -> { return RIGHT_BRACKET; }
                case '{' -> { return LEFT_BRACE; }
                case '}' -> { return RIGHT_BRACE; }
                case ' ' -> { return SPACE; }
                case '\b' -> { return BACKSPACE; }
                case '\t' -> { return TAB; }
                case '\n', '\r' -> { return ENTER; }

                // Lock keys
                case '\f' -> { return CAPS_LOCK; }
                case 0x0002 -> { return SCROLL_LOCK; }
                case 0x0001 -> { return NUM_LOCK; }

                // Navigation keys
                case 0x0003 -> { return LEFT_ARROW; }
                case 0x0004 -> { return RIGHT_ARROW; }
                case 0x0005 -> { return UP_ARROW; }
                case 0x0006 -> { return DOWN_ARROW; }

                // Modifier keys
                case 0x001b -> { return ESCAPE; }
                case 0xff1b -> { return LEFT_CONTROL; }
                case 0xff1d -> { return LEFT_SHIFT; }
                case 0xff1c -> { return LEFT_ALT; }
                case 0xff1e -> { return LEFT_META; }
                case 0xff1f -> { return RIGHT_CONTROL; }
                case 0xff20 -> { return RIGHT_SHIFT; }
                case 0xff21 -> { return RIGHT_ALT; }
                case 0xff22 -> { return RIGHT_META; }

                // Function keys
                case 0xff23 -> { return F1; }
                case 0xff24 -> { return F2; }
                case 0xff25 -> { return F3; }
                case 0xff26 -> { return F4; }
                case 0xff27 -> { return F5; }
                case 0xff28 -> { return F6; }
                case 0xff29 -> { return F7; }
                case 0xff2a -> { return F8; }
                case 0xff2b -> { return F9; }
                case 0xff2c -> { return F10; }
                case 0xff2d -> { return F11; }
                case 0xff2e -> { return F12; }
                case 0xff2f -> { return F13; }
                case 0xff30 -> { return F14; }
                case 0xff31 -> { return F15; }
                case 0xff32 -> { return F16; }
                case 0xff33 -> { return F17; }
                case 0xff34 -> { return F18; }
                case 0xff35 -> { return F19; }
                case 0xff36 -> { return F20; }
                case 0xff37 -> { return F21; }
                case 0xff38 -> { return F22; }
                case 0xff39 -> { return F23; }
                case 0xff3a -> { return F24; }
                default -> { return null; }
            }
        }

        @Override
        public String toString() {
            return switch (this) {
                case LEFT_SHIFT, RIGHT_SHIFT -> Util.getPlatform() == Util.OS.OSX ? "⇧" : "Shift";
                case LEFT_CONTROL, RIGHT_CONTROL -> Util.getPlatform() == Util.OS.OSX ? "⌃" : "Ctrl";
                case LEFT_ALT, RIGHT_ALT -> Util.getPlatform() == Util.OS.OSX ? "⌥" : "Alt";
                case LEFT_META, RIGHT_META -> switch (Util.getPlatform()) {
                    case WINDOWS -> "Win";
                    case OSX -> "⌘";
                    default -> "Meta";
                };
                case LEFT_ARROW -> "←";
                case RIGHT_ARROW -> "→";
                case UP_ARROW -> "↑";
                case DOWN_ARROW -> "↓";
                case LEFT_PAREN -> "(";
                case RIGHT_PAREN -> ")";
                case LEFT_BRACKET -> "[";
                case RIGHT_BRACKET -> "]";
                case LEFT_BRACE -> "{";
                case RIGHT_BRACE -> "}";
                case COMMA -> ",";
                case PERIOD -> ".";
                case MINUS -> "-";
                case EQUALS -> "=";
                case COLON -> ":";
                case SEMI -> ";";
                case TILDE -> "~";
                case EXCLAMATION -> "!";
                case AMPER -> "&";
                case DOLLAR -> "$";
                case PERCENT -> "%";
                case CIRCUMFLEX -> "^";
                case CARET -> "^";
                case KEY_1 -> "1";
                case KEY_2 -> "2";
                case KEY_3 -> "3";
                case KEY_4 -> "4";
                case KEY_5 -> "5";
                case KEY_6 -> "6";
                case KEY_7 -> "7";
                case KEY_8 -> "8";
                case KEY_9 -> "9";
                case KEY_0 -> "0";
                case SPACE -> "                    ";
                case BACKSPACE -> "⌫";
                case TAB -> Util.getPlatform() == Util.OS.OSX ? "⇥" : "Tab";
                case CAPS_LOCK -> Util.getPlatform() == Util.OS.OSX ? "⇪" : "Caps";
                case ENTER -> Util.getPlatform() == Util.OS.OSX ? "↩" : "Enter";
                case AT -> "@";
                case HASH -> "#";
                case UNDERSCORE -> "_";
                case QUOTE -> "'";
                case APOSTROPHE -> "'";
                case BACKSLASH -> "\\";
                case PIPE -> "|";
                case EURO -> "€";
                case POUND -> "£";
                case YEN -> "¥";
                case TIMES -> "*";
                case DIVIDE -> "/";
                case F1 -> "F1";
                case F2 -> "F2";
                case F3 -> "F3";
                case F4 -> "F4";
                case F5 -> "F5";
                case F6 -> "F6";
                case F7 -> "F7";
                case F8 -> "F8";
                case F9 -> "F9";
                case F10 -> "F10";
                case F11 -> "F11";
                case F12 -> "F12";
                case F13 -> "F13";
                case F14 -> "F14";
                case F15 -> "F15";
                case F16 -> "F16";
                case F17 -> "F17";
                case F18 -> "F18";
                case F19 -> "F19";
                case F20 -> "F20";
                case F21 -> "F21";
                case F22 -> "F22";
                case F23 -> "F23";
                case F24 -> "F24";
                case ESCAPE -> Util.getPlatform() == Util.OS.OSX ? "esc" : "Esc";
                case NUM_LOCK -> "Num";
                case QUESTION -> "?";
                case SCROLL_LOCK -> "ScrLck";
                default -> super.toString();
            };
        }

        public int width() {
            int w = Minecraft.getInstance().font.width(toString()) + 4;
            return Math.max(w, 16);
        }

        public int height() {
            return 16;
        }
    }

    private int getTextureY() {
        return (isHoveredOrFocused() ? 0 : 1) * 16;
    }
}
