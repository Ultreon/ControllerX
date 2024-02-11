package io.github.ultreon.controllerx.mixin.forge;

import io.github.ultreon.controllerx.ControllerX;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private Object controllerX$oldMouseX;
    @Unique
    private Object controllerX$oldMouseY;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void onRender(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (ControllerX.get().controllerInput.isVirtualKeyboardOpen()) {
            ForgeHooksClient.drawScreen(screen, guiGraphics, Integer.MIN_VALUE, Integer.MIN_VALUE, partialTick);
        } else {
            ForgeHooksClient.drawScreen(screen, guiGraphics, mouseX, mouseY, partialTick);
        }/* else if (!Objects.equals(args.get(1), controllerX$oldMouseX) || !Objects.equals(args.get(2), controllerX$oldMouseY)) {
            controllerX$oldMouseX = args.get(1);
            controllerX$oldMouseY = args.get(2);

            ControllerX.get().setInputType(InputType.KEYBOARD_AND_MOUSE);
        }*/
    }
}
