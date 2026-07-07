package dev.ultreon.controllerx.gui.screen;

import dev.ultreon.controllerx.*;
import dev.ultreon.controllerx.api.VirtualKeyboardEditCallback;
import dev.ultreon.controllerx.api.input.keyboard.keyboard.KeyboardLayout;
import dev.ultreon.controllerx.gui.widget.Keycap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TextInputScreen extends Screen {
    private final KeyboardLayout layout;
    private final VirtualKeyboard virtualKeyboard;
    private String input;
    private boolean shift;
    private boolean caps;
    private VirtualKeyboardSubmitCallback submitCallback = () -> {};
    private VirtualKeyboardEditCallback editCallback = s -> {};
    private final List<Keycap> buttons = new ArrayList<>();

    public TextInputScreen(VirtualKeyboard virtualKeyboard) {
        super(Component.literal("Text Input"));
        this.virtualKeyboard = virtualKeyboard;

        minecraft = Minecraft.getInstance();
        font = Minecraft.getInstance().font;

        layout = ControllerX.get().input.getLayout();
   }

    public void setSubmitCallback(VirtualKeyboardSubmitCallback callback) {
        submitCallback = callback;
    }

    public void setEditCallback(VirtualKeyboardEditCallback callback) {
        editCallback = callback;
    }

    public void setResizeSupported(boolean resizeSupported) {
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
        init();
    }

    @Override
    protected void init() {
        setInput(ControllerX.get().input.getVirtualKeyboardValue());

        reloadButtons();

        super.init();
    }

    private void reloadButtons() {
        removeButtons();

        addButtons();
    }

    private void addButtons() {
        char[][] layoutLayout = layout.getLayout(shift || caps);
        for (int rowIdx = 0, layoutLayoutLength = layoutLayout.length; rowIdx < layoutLayoutLength; rowIdx++) {
            char[] row = layoutLayout[rowIdx];

            int keyboardWidth = 0;
            for (char c : row) {
                Keycap.Key key = Keycap.Key.byChar(c);
                if (key == null) continue;

                keyboardWidth += key.width() + 2;
            }
            addButton(keyboardWidth - 2, row, rowIdx);
        }
    }

    private void addButton(int keyboardWidth, char[] row, int rowIdx) {
        int x = width / 2 - keyboardWidth / 2;
        for (char c : row) {
            Keycap.Key key = Keycap.Key.byChar(c);
            if (key == null) continue;

            addButton(c, x, rowIdx, key);

            x += key.width() + 2;
        }
    }

    private void removeButtons() {
        for (Keycap button : buttons) {
            removeWidget(button);
        }

        buttons.clear();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics gfx) {

    }

    private void addButton(char c, int x, int rowIdx, Keycap.Key key) {
        Keycap imageButton = addRenderableWidget(new Keycap(x, rowIdx * 18 + height - 101 - getYOffset(), key, button -> {
            switch (key) {
                case CAPS_LOCK -> caps = !caps;
                case LEFT_SHIFT, RIGHT_SHIFT -> shift = !shift;
                case ENTER -> submit();
                case BACKSPACE -> backspace();
                case SPACE -> setInput(getInput() + " ");
                default -> {
                    if (key.toString().length() == 1) {
                        setInput(getInput() + c);
                    }
                }
            }
        }));

        buttons.add(imageButton);
    }

    private int getYOffset() {
        if (minecraft != null) {
            return minecraft.screen instanceof ChatScreen ? 32 : 0;
        }

        return 0;
    }

    private void submit() {
        virtualKeyboard.close();
        submitCallback.onSubmit();
    }

    private void backspace() {
        if (!getInput().isEmpty()) {
            setInput(getInput().substring(0, getInput().length() - 1));
        }
    }

    public void close() {

    }

    @Override
    protected boolean shouldNarrateNavigation() {
        return false;
    }

    @Override
    public void onClose() {
        virtualKeyboard.close();

        submitCallback = () -> {};
    }

    public String getInput() {
        return input;
    }

    private void setInput(String input) {
        this.input = input;
        editCallback.onInput(input);
    }
}
