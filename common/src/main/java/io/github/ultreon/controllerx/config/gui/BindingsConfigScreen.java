package io.github.ultreon.controllerx.config.gui;

import io.github.ultreon.controllerx.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BindingsConfigScreen extends Screen {
    private final Screen back;
    private BindingsConfigList list;
    private Button doneButton;
    private final Config config;

    public BindingsConfigScreen(Screen back, Config config) {
        super(Component.translatable("controllerx.screen.config.bindings.title"));
        this.back = back;
        this.config = config;
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.back);
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();

        this.list = new BindingsConfigList(this.minecraft, this.width, this.height, 32, this.height - 32, config);
        this.list.addEntries(config.values());
        this.addRenderableWidget(this.list);

        this.doneButton = new Button.Builder(CommonComponents.GUI_DONE, button -> {
            this.list.save();
            assert this.minecraft != null;
            this.minecraft.setScreen(this.back);
        }).bounds(this.width / 2 + 5, this.height - 6 - 20, 150, 20).build();
        this.addRenderableWidget(this.doneButton);

        Button cancelButton = new Button.Builder(CommonComponents.GUI_CANCEL, button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(this.back);
        }).bounds(this.width / 2 - 155, this.height - 6 - 20, 150, 20).build();
        this.addRenderableWidget(cancelButton);
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, int i, int j, float f) {
        super.render(gfx, i, j, f);

        gfx.drawCenteredString(this.font, this.getTitle(), this.width / 2, 16 - this.font.lineHeight / 2, 0xffffffff);
    }

    public Screen getBack() {
        return this.back;
    }

    public BindingsConfigList getList() {
        return this.list;
    }

    public Button getDoneButton() {
        return this.doneButton;
    }

    public void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(this);
    }
}
