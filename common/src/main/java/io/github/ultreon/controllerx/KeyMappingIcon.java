package io.github.ultreon.controllerx;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public enum KeyMappingIcon {
    ESC(0, 0),
    F1(16, 0),
    F2(32, 0),
    F3(48, 0),
    F4(64, 0),
    F5(80, 0),
    F6(96, 0),
    F7(112, 0),
    F8(128, 0),
    F9(144, 0),
    F10(160, 0),
    F11(176, 0),
    F12(192, 0),
    TILDE(208, 0),
    EXCLAMATION(224, 0),
    AT(240, 0),
    HASH(256, 0),
    KEY_1(0, 16),
    KEY_2(16, 16),
    KEY_3(32, 16),
    KEY_4(48, 16),
    KEY_5(64, 16),
    KEY_6(80, 16),
    KEY_7(96, 16),
    KEY_8(112, 16),
    KEY_9(128, 16),
    KEY_0(144, 16),
    MINUS(160, 16),
    PLUS(176, 16),
    EQUALS(192, 16),
    UNDERSCORE(208, 16),
    BROKEN_BAR(224, 16),
    BACKSPACE(240, 16, 32, 16),
    Q(0, 32),
    W(16, 32),
    E(32, 32),
    R(48, 32),
    T(64, 32),
    Y(80, 32),
    U(96, 32),
    I(112, 32),
    O(128, 32),
    P(144, 32),
    LEFT_BRACKET(160, 32),
    RIGHT_BRACKET(176, 32),
    LEFT_CURLY(192, 32),
    RIGHT_CURLY(208, 32),
    BACKSLASH(224, 32),
    ENTER(240, 32, 32, 32),
    A(0, 48),
    S(16, 48),
    D(32, 48),
    F(48, 48),
    G(64, 48),
    H(80, 48),
    J(96, 48),
    K(112, 48),
    L(128, 48),
    QUOTE(144, 48),
    DOUBLE_QUOTE(160, 48),
    COLON(176, 48),
    SEMICOLON(192, 48),
    ASTERISK(208, 48),
    SPACE_SMALL(0, 64),
    WINDOWS(16, 64),
    Z(32, 64),
    X(48, 64),
    C(64, 64),
    V(80, 64),
    B(96, 64),
    N(112, 64),
    M(128, 64),
    LESS(144, 64),
    GREATER(160, 64),
    QUESTION(176, 64),
    SLASH(192, 64),
    UP(208, 64),
    RIGHT(224, 64),
    DOWN(240, 64),
    LEFT(256, 64),
    ALT(0, 80, 32, 16),
    TAB(32, 80, 32, 16),
    DELETE(64, 80, 32, 16),
    END(96, 80, 32, 16),
    NUM_LOCK(128, 80, 32, 16),
    PERIOD(160, 80),
    DOLLAR(176, 80),
    PERCENT(192, 80),
    CIRCUMFLEX(208, 80),
    CENT(224, 80),
    LEFT_PARENTHESIS(240, 80),
    RIGHT_PARENTHESIS(256, 80),
    CTRL(0, 96, 32, 16),
    CAPS(32, 96, 32, 16),
    HOME(64, 96, 32, 16),
    PAGE_UP(96, 96, 32, 16),
    PAGE_DOWN(128, 96, 32, 16),
    COMMA(160, 96),
    ENLARGE(176, 96),
    EMPTY(192, 96),
    RECORD(208, 96),
    SPACE_BIG(224, 96, 48, 16),
    SHIFT(0, 112, 32, 16),
    INSERT(32, 112, 32, 16),
    PRINT(64, 112, 32, 16),
    SCROLL_LOCK(96, 112, 32, 16),
    PAUSE_BREAK(128, 112, 32, 16),
    PLAY(160, 112),
    PAUSE(176, 112),
    STOP(192, 112),
    FAST_BACKWARD(208, 112),
    FAST_FORWARD(224, 112),
    PREVIOUS(240, 112),
    NEXT(256, 112),
    MOUSE(0, 128),
    MOUSE_LEFT(16, -1),
    MOUSE_RIGHT(32, -1),
    MOUSE_MIDDLE(48, -1),
    MOUSE_SCROLL_UP(64, -1),
    MOUSE_SCROLL_DOWN(80, -1),
    MOUSE_SCROLL(96, -1),
    POWER;

    private static final ResourceLocation TEXTURE = ControllerX.res("textures/gui/icons.png");
    public final int u;
    public final int v;
    public final int width;
    public final int height;

    KeyMappingIcon() {
        this(0, 0);
    }

    KeyMappingIcon(int u, int v) {
        this(u, v, 16, 16);
    }

    KeyMappingIcon(int u, int v, int width, int height) {
        this.u = u + 272;
        this.v = v + 128;
        this.width = width;
        this.height = height;
    }

    public void render(GuiGraphics gfx, int x, int y) {
        if (this == POWER) {
            gfx.blit(TEXTURE, x, y, 16, 16, 240, 336, 16, 16, 544, 384);
            return;
        }

        if (v == 127) {
            gfx.blit(TEXTURE, x, y, 16, 16, u - 272 + 128, 48, width, height, 544, 384);
            return;
        }

        gfx.blit(TEXTURE, x, y, width, height, u, v, width, height, 544, 384);
    }

    public static KeyMappingIcon byKey(InputConstants.Key key) {
        if (key.getType() == InputConstants.Type.MOUSE) {
            return switch (key.getValue()) {
                case GLFW.GLFW_MOUSE_BUTTON_1 -> MOUSE_LEFT;
                case GLFW.GLFW_MOUSE_BUTTON_2 -> MOUSE_RIGHT;
                case GLFW.GLFW_MOUSE_BUTTON_3 -> MOUSE_MIDDLE;
                default -> MOUSE;
            };
        }
        if (key.getType() != InputConstants.Type.KEYSYM) {
            return null;
        }

        return switch (key.getValue()) {
            case GLFW.GLFW_KEY_ESCAPE -> ESC;
            case GLFW.GLFW_KEY_F1 -> F1;
            case GLFW.GLFW_KEY_F2 -> F2;
            case GLFW.GLFW_KEY_F3 -> F3;
            case GLFW.GLFW_KEY_F4 -> F4;
            case GLFW.GLFW_KEY_F5 -> F5;
            case GLFW.GLFW_KEY_F6 -> F6;
            case GLFW.GLFW_KEY_F7 -> F7;
            case GLFW.GLFW_KEY_F8 -> F8;
            case GLFW.GLFW_KEY_F9 -> F9;
            case GLFW.GLFW_KEY_F10 -> F10;
            case GLFW.GLFW_KEY_F11 -> F11;
            case GLFW.GLFW_KEY_F12 -> F12;
            case GLFW.GLFW_KEY_GRAVE_ACCENT -> TILDE;
            case GLFW.GLFW_KEY_1 -> KEY_1;
            case GLFW.GLFW_KEY_2 -> KEY_2;
            case GLFW.GLFW_KEY_3 -> KEY_3;
            case GLFW.GLFW_KEY_4 -> KEY_4;
            case GLFW.GLFW_KEY_5 -> KEY_5;
            case GLFW.GLFW_KEY_6 -> KEY_6;
            case GLFW.GLFW_KEY_7 -> KEY_7;
            case GLFW.GLFW_KEY_8 -> KEY_8;
            case GLFW.GLFW_KEY_9 -> KEY_9;
            case GLFW.GLFW_KEY_0 -> KEY_0;
            case GLFW.GLFW_KEY_MINUS -> MINUS;
            case GLFW.GLFW_KEY_EQUAL -> EQUALS;
            case GLFW.GLFW_KEY_BACKSPACE -> BACKSPACE;
            case GLFW.GLFW_KEY_TAB -> TAB;
            case GLFW.GLFW_KEY_INSERT -> INSERT;
            case GLFW.GLFW_KEY_DELETE -> DELETE;
            case GLFW.GLFW_KEY_RIGHT -> RIGHT;
            case GLFW.GLFW_KEY_LEFT -> LEFT;
            case GLFW.GLFW_KEY_DOWN -> DOWN;
            case GLFW.GLFW_KEY_UP -> UP;
            case GLFW.GLFW_KEY_PAGE_UP -> PAGE_UP;
            case GLFW.GLFW_KEY_PAGE_DOWN -> PAGE_DOWN;
            case GLFW.GLFW_KEY_HOME -> HOME;
            case GLFW.GLFW_KEY_END -> END;
            case GLFW.GLFW_KEY_CAPS_LOCK -> CAPS;
            case GLFW.GLFW_KEY_SCROLL_LOCK -> SCROLL_LOCK;
            case GLFW.GLFW_KEY_NUM_LOCK -> NUM_LOCK;
            case GLFW.GLFW_KEY_PRINT_SCREEN -> PRINT;
            case GLFW.GLFW_KEY_PAUSE -> PAUSE;
            case GLFW.GLFW_KEY_BACKSLASH -> BACKSLASH;
            case GLFW.GLFW_KEY_LEFT_BRACKET -> LEFT_BRACKET;
            case GLFW.GLFW_KEY_RIGHT_BRACKET -> RIGHT_BRACKET;
            case GLFW.GLFW_KEY_SEMICOLON -> SEMICOLON;
            case GLFW.GLFW_KEY_COMMA -> COMMA;
            case GLFW.GLFW_KEY_PERIOD -> PERIOD;
            case GLFW.GLFW_KEY_SLASH -> SLASH;
            case GLFW.GLFW_KEY_SPACE -> SPACE_SMALL;
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> SHIFT;
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> CTRL;
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> ALT;
            case GLFW.GLFW_KEY_LEFT_SUPER, GLFW.GLFW_KEY_RIGHT_SUPER -> WINDOWS;
            case GLFW.GLFW_KEY_A -> A;
            case GLFW.GLFW_KEY_B -> B;
            case GLFW.GLFW_KEY_C -> C;
            case GLFW.GLFW_KEY_D -> D;
            case GLFW.GLFW_KEY_E -> E;
            case GLFW.GLFW_KEY_F -> F;
            case GLFW.GLFW_KEY_G -> G;
            case GLFW.GLFW_KEY_H -> H;
            case GLFW.GLFW_KEY_I -> I;
            case GLFW.GLFW_KEY_J -> J;
            case GLFW.GLFW_KEY_K -> K;
            case GLFW.GLFW_KEY_L -> L;
            case GLFW.GLFW_KEY_M -> M;
            case GLFW.GLFW_KEY_N -> N;
            case GLFW.GLFW_KEY_O -> O;
            case GLFW.GLFW_KEY_P -> P;
            case GLFW.GLFW_KEY_Q -> Q;
            case GLFW.GLFW_KEY_R -> R;
            case GLFW.GLFW_KEY_S -> S;
            case GLFW.GLFW_KEY_T -> T;
            case GLFW.GLFW_KEY_U -> U;
            case GLFW.GLFW_KEY_V -> V;
            case GLFW.GLFW_KEY_W -> W;
            case GLFW.GLFW_KEY_X -> X;
            case GLFW.GLFW_KEY_Y -> Y;
            case GLFW.GLFW_KEY_Z -> Z;
            case GLFW.GLFW_KEY_ENTER -> ENTER;
            default -> EMPTY;
        };
    }
}
