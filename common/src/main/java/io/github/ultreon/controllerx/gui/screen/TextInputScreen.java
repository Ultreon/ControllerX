package io.github.ultreon.controllerx.gui.screen;

import dev.ultreon.mods.lib.client.gui.screen.BaseScreen;
import io.github.ultreon.controllerx.*;
import io.github.ultreon.controllerx.input.keyboard.KeyboardLayout;
import net.minecraft.client.DeltaTracker;
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
    private final List<IconButton> buttons = new ArrayList<>();

    public TextInputScreen(VirtualKeyboard virtualKeyboard) {
        super(Component.literal("Text Input"));
        this.virtualKeyboard = virtualKeyboard;

        this.minecraft = Minecraft.getInstance();
        this.font = Minecraft.getInstance().font;

        this.layout = ControllerX.get().input.getLayout();
    }

    public void setSubmitCallback(VirtualKeyboardSubmitCallback callback) {
        this.submitCallback = callback;
    }

    public void setEditCallback(VirtualKeyboardEditCallback callback) {
        this.editCallback = callback;
    }

    public void setResizeSupported(boolean resizeSupported) {
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
        this.init();
    }

    @Override
    protected void init() {
        this.setInput(ControllerX.get().input.getVirtualKeyboardValue());

        for (IconButton button : this.buttons) {
            this.removeWidget(button);
        }

        this.buttons.clear();

        char[][] layoutLayout = layout.getLayout(shift || caps);
        for (int rowIdx = 0, layoutLayoutLength = layoutLayout.length; rowIdx < layoutLayoutLength; rowIdx++) {
            char[] row = layoutLayout[rowIdx];

            int keyboardWidth = row.length * 16;
            if (rowIdx == 0) keyboardWidth += 16;
            if (rowIdx == 1) keyboardWidth += 7;
            if (rowIdx == 2) keyboardWidth += 27;
            if (rowIdx == 3) keyboardWidth += 33;
            if (rowIdx == 4) keyboardWidth += 41;

            int x = this.width / 2 - keyboardWidth / 2;
            for (char c : row) {
                KeyMappingIcon icon = KeyMappingIcon.byChar(c);
                if (icon == null) continue;

                this.addButton(c, x, rowIdx, icon);

                x += icon.width;
            }
        }

        super.init();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    private void addButton(char c, int x, int rowIdx, KeyMappingIcon icon) {
        IconButton imageButton = this.addRenderableWidget(new IconButton(x, rowIdx * 16 + height - 85 - getYOffset(), icon.width, icon.height, icon.u, icon.v, -128, icon.getTexture(), 544, 384, button -> {
            if (c >= 0x20) {
                setInput(getInput() + c);
                return;

            }
            switch (c) {
                case '\n', '\r' -> this.submit();
                case '\b' -> this.backspace();
                case '\t' -> setInput(getInput() + "    ");
                case '\0', '\1', '\3', '\4', '\5', '\6', '\7' -> {
                    // TODO: Add support for other controller input characters
                }
            }
        }));

        this.buttons.add(imageButton);
    }

    private int getYOffset() {
        if (this.minecraft != null) {
            return this.minecraft.screen instanceof ChatScreen ? 32 : 0;
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
        this.virtualKeyboard.close();

        this.submitCallback = () -> {};
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
        this.editCallback.onInput(input);
    }
}
