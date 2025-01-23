package dev.ultreon.controllerx.gui.screen;

import com.ultreon.mods.lib.client.gui.screen.BaseScreen;
import dev.ultreon.controllerx.*;
import dev.ultreon.controllerx.api.VirtualKeyboardEditCallback;
import dev.ultreon.controllerx.api.input.keyboard.keyboard.KeyboardLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TextInputScreen extends BaseScreen {
    private final KeyboardLayout layout;
    private final VirtualKeyboard virtualKeyboard;
    private String input;
    private boolean shift;
    private boolean caps;
    private VirtualKeyboardSubmitCallback submitCallback = () -> {};
    private VirtualKeyboardEditCallback editCallback = s -> {};
    private final List<ImageButton> buttons = new ArrayList<>();

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

        for (ImageButton button : buttons) {
            removeWidget(button);
        }

        buttons.clear();

        char[][] layoutLayout = layout.getLayout(shift || caps);
        for (int rowIdx = 0, layoutLayoutLength = layoutLayout.length; rowIdx < layoutLayoutLength; rowIdx++) {
            char[] row = layoutLayout[rowIdx];

            int keyboardWidth = row.length * 16;
            if (rowIdx == 0) keyboardWidth += 16;
            if (rowIdx == 1) keyboardWidth += 7;
            if (rowIdx == 2) keyboardWidth += 27;
            if (rowIdx == 3) keyboardWidth += 33;
            if (rowIdx == 4) keyboardWidth += 41;

            int x = width / 2 - keyboardWidth / 2;
            for (char c : row) {
                KeyMappingIcon icon = KeyMappingIcon.byChar(c);
                if (icon == null) continue;

                addButton(c, x, rowIdx, icon);

                x += icon.width;
            }
        }

        super.init();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics gfx) {

    }

    private void addButton(char c, int x, int rowIdx, KeyMappingIcon icon) {
        ImageButton imageButton = addRenderableWidget(new ImageButton(x, rowIdx * 16 + height - 85 - getYOffset(), icon.width, icon.height, icon.u, icon.v, -128, icon.getTexture(), 544, 384, button -> {
            if (c >= 0x20) {
                setInput(getInput() + c);
                return;

            }
            switch (c) {
                case '\n', '\r' -> submit();
                case '\b' -> backspace();
                case '\t' -> setInput(getInput() + "    ");
                case '\0', '\1', '\3', '\4', '\5', '\6', '\7' -> {
                    // TODO: Add support for other controller input characters
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

    @Override
    public @Nullable Vec2 getCloseButtonPos() {
        return null;
    }

    public String getInput() {
        return input;
    }

    private void setInput(String input) {
        this.input = input;
        editCallback.onInput(input);
    }
}
