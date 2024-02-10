package io.github.ultreon.controllerx.mixin.forge;

import io.github.ultreon.controllerx.ControllerX;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private Object controllerX$oldMouseX;
    @Unique
    private Object controllerX$oldMouseY;

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void onRender(Args args) {
        if (ControllerX.get().controllerInput.isVirtualKeyboardOpen()) {
            args.set(2, Integer.MIN_VALUE);
            args.set(3, Integer.MIN_VALUE);
        }/* else if (!Objects.equals(args.get(1), controllerX$oldMouseX) || !Objects.equals(args.get(2), controllerX$oldMouseY)) {
            controllerX$oldMouseX = args.get(1);
            controllerX$oldMouseY = args.get(2);

            ControllerX.get().setInputType(InputType.KEYBOARD_AND_MOUSE);
        }*/
    }
}
