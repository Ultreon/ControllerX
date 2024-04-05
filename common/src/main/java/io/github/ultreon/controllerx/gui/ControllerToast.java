//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.ultreon.controllerx.gui;

import io.github.ultreon.controllerx.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

@Environment(EnvType.CLIENT)
public class ControllerToast implements Toast {
    public static final int PROGRESS_BAR_WIDTH = 154;
    public static final int PROGRESS_BAR_HEIGHT = 1;
    public static final int PROGRESS_BAR_X = 3;
    public static final int PROGRESS_BAR_Y = 28;
    private final Icon icon;
    private final Component title;
    @Nullable
    private final Component message;
    private Toast.Visibility visibility;
    private Instant hideAt;

    public ControllerToast(Icon icon, Component title, @Nullable Component message) {
        this.visibility = Visibility.SHOW;
        this.icon = icon;
        this.title = title;
        this.message = message;
    }

    public Toast.@NotNull Visibility render(GuiGraphics gfx, @NotNull ToastComponent toastComponent, long timeSinceLastVisible) {
        gfx.blit(TEXTURE, 0, 0, 0, 96, this.width(), this.height());
        this.icon.render(gfx, 6, 6);
        if (this.message == null) {
            gfx.drawString(toastComponent.getMinecraft().font, this.title, 30, 12, -11534256, false);
        } else {
            gfx.drawString(toastComponent.getMinecraft().font, this.title, 30, 7, -11534256, false);
            gfx.drawString(toastComponent.getMinecraft().font, this.message, 30, 18, -16777216, false);
        }

        if (hideAt != null && Instant.now().isAfter(hideAt)) {
            this.visibility = Visibility.HIDE;
        }

        return this.visibility;
    }

    public void hide() {
        this.visibility = Visibility.HIDE;
    }

    public ControllerToast hideIn(Duration hideTime) {
        this.hideAt(Instant.now().plus(hideTime));
        return this;
    }

    public ControllerToast hideAt(Instant instant) {
        this.hideAt = instant;
        return this;
    }

    public Instant getHideAt() {
        return hideAt;
    }
}
