package io.github.ultreon.controllerx.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconButton extends Button {
    private int u;
    private int v;
    private int yOffset;
    private ResourceLocation texture;
    private int texWidth;
    private int texHeight;

    public IconButton(int x, int y, int width, int height, int u, int v, int yOffset, ResourceLocation texture, int texWidth, int texHeight, Button.OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, (componentSupplier) -> Component.empty());

        this.u = u;
        this.v = v;
        this.yOffset = yOffset;
        this.texture = texture;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(texture, getX(), getY(), yOffset, u, v, width, height, texWidth, texHeight);
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getyOffset() {
        return yOffset;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getTexWidth() {
        return texWidth;
    }

    public int getTexHeight() {
        return texHeight;
    }

    public void setU(int u) {
        this.u = u;
    }

    public void setV(int v) {
        this.v = v;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public void setTexWidth(int texWidth) {
        this.texWidth = texWidth;
    }

    public void setTexHeight(int texHeight) {
        this.texHeight = texHeight;
    }
}
