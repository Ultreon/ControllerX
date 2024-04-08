package io.github.ultreon.controllerx.mixin.forge;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.gui.widget.ItemSlot;
import io.github.ultreon.controllerx.input.InputType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("UnstableApiUsage")
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V")
    )
    private void onRenderScreen(Screen instance, GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        if (ControllerX.get().getInputType() != InputType.CONTROLLER
                || !(instance instanceof AbstractContainerScreen<?> containerScreen)
                || !(containerScreen.getFocused() instanceof ItemSlot itemSlot)) {
            if (ControllerX.get().input.isVirtualKeyboardOpen())
                ForgeHooksClient.drawScreen(instance, gfx, Integer.MIN_VALUE, Integer.MIN_VALUE, partialTick);
            else
                ForgeHooksClient.drawScreen(instance, gfx, mouseX, mouseY, partialTick);
            return;
        }

        ForgeHooksClient.drawScreen(instance, gfx, itemSlot.getX() + 8, itemSlot.getY() + 8, partialTick);
    }
}
