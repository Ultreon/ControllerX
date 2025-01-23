package dev.ultreon.controllerx.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.gui.widget.ItemSlot;
import dev.ultreon.controllerx.api.input.InputType;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V")
    )
    private void onRenderScreen(Screen instance, GuiGraphics gfx, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        if (Util.getPlatform() == Util.OS.OSX && !ControllerX.get().skippedWarning) {
            instance.renderWithTooltip(gfx, mouseX, mouseY, partialTick);
            return;
        }
        if (ControllerX.get().getInputType() != InputType.CONTROLLER
            || !(instance instanceof AbstractContainerScreen<?> containerScreen)
            || !(containerScreen.getFocused() instanceof ItemSlot itemSlot)) {
            if (ControllerX.get().input.isVirtualKeyboardOpen())
                original.call(instance, gfx, Integer.MIN_VALUE, Integer.MIN_VALUE, partialTick);
            else
                original.call(instance, gfx, mouseX, mouseY, partialTick);
            return;
        }

        original.call(instance, gfx, itemSlot.getX() + 8, itemSlot.getY() + 8, partialTick);
    }
}
