package io.github.ultreon.controllerx.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.CompoundEventResult;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.event.ItemSlotGuiEvent;
import io.github.ultreon.controllerx.input.InputType;
import io.github.ultreon.controllerx.mixin.accessors.AbstractContainerScreenAccessor;
import io.github.ultreon.controllerx.mixin.accessors.KeyMappingAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class ItemSlot extends AbstractWidget {
    private final AbstractContainerScreen<?> screen;
    private final Slot slot;

    protected ItemSlot(int x, int y, int width, int height, AbstractContainerScreen<?> screen, Slot slot) {
        super(x, y, width, height, Component.empty());
        this.screen = screen;
        this.slot = slot;
    }

    public static ItemSlot getSlot(AbstractContainerScreen<?> screen, Slot slot) {
        int x = slot.x + ((AbstractContainerScreenAccessor) screen).getLeftPos();
        int y = slot.y + ((AbstractContainerScreenAccessor) screen).getTopPos();

        CompoundEventResult<ItemSlot> eventResult = ItemSlotGuiEvent.EVENT.invoker().onSlot(x, y, screen, slot);
        if (eventResult.isPresent()) return eventResult.object();
        return new ItemSlot(x, y, 16, 16, screen, slot);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        if (ControllerX.get().getInputType() != InputType.CONTROLLER) return;
        if (!this.isFocused()) return;

        gfx.fill(getX(), getY(), getX() + width, getY() + height, 0xff00ff00);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean isHovered() {
        return false;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return false;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        // Item slots don't have a message
    }

    public boolean pickUpOrPlace() {
        boolean flag = screen.mouseClicked(getX() + 8, getY() + 8, InputConstants.MOUSE_BUTTON_LEFT);
        flag |= screen.mouseReleased(getX() + 8, getY() + 8, InputConstants.MOUSE_BUTTON_LEFT);
        return flag;
    }

    public boolean splitOrPutSingle() {
        boolean flag = screen.mouseClicked(getX() + 8, getY() + 8, InputConstants.MOUSE_BUTTON_RIGHT);
        flag |= screen.mouseReleased(getX() + 8, getY() + 8, InputConstants.MOUSE_BUTTON_RIGHT);
        return flag;
    }

    public void drop() {
        screen.keyPressed(getX() + 8, getY() + 8, ((KeyMappingAccessor)Minecraft.getInstance().options.keyDrop).getKey().getValue());
        screen.keyReleased(getX() + 8, getY() + 8, ((KeyMappingAccessor)Minecraft.getInstance().options.keyDrop).getKey().getValue());
    }

    public Slot getSlot() {
        return slot;
    }
}
