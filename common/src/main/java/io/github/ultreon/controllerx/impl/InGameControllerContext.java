package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.GameApi;
import io.github.ultreon.controllerx.api.ControllerActions;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.api.ControllerMapping.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InGameControllerContext extends ControllerContext {
    public static final InGameControllerContext INSTANCE = new InGameControllerContext();

    public final ControllerMapping<?> jump;
    public final ControllerMapping<?> run;
    public final ControllerMapping<?> sneak;
    public final ControllerMapping<?> use;
    public final ControllerMapping<?> inventory;
    public final ControllerMapping<?> swapHands;
    public final ControllerMapping<?> movePlayer;
    public final ControllerMapping<?> lookPlayer;
    public final ControllerMapping<?> gameMenu;

    protected InGameControllerContext() {
        super();

        this.jump = mappings.register(new ControllerMapping<>(ControllerActions.A, Side.LEFT, Component.translatable("controllerx.action.inGame.jump")));
        this.run = mappings.register(new ControllerMapping<>(ControllerActions.PRESS_LEFT_STICK, Side.RIGHT, Component.translatable("controllerx.action.inGame.run")));
        this.sneak = mappings.register(new ControllerMapping<>(ControllerActions.PRESS_RIGHT_STICK, Side.RIGHT, Component.translatable("controllerx.action.inGame.sneak")));
        this.use = mappings.register(new ControllerMapping<>(ControllerActions.LEFT_TRIGGER, Side.LEFT, Component.translatable("controllerx.action.inGame.use")));
        this.inventory = mappings.register(new ControllerMapping<>(ControllerActions.Y, Side.RIGHT, Component.translatable("controllerx.action.inGame.inventory")));
        this.swapHands = mappings.register(new ControllerMapping<>(ControllerActions.X, Side.RIGHT, Component.translatable("controllerx.action.inGame.swapHands")));
        this.movePlayer = mappings.register(new ControllerMapping<>(ControllerActions.MOVE_LEFT_STICK, Side.LEFT, Component.translatable("controllerx.action.inGame.movePlayer")));
        this.lookPlayer = mappings.register(new ControllerMapping<>(ControllerActions.MOVE_RIGHT_STICK, Side.LEFT, Component.translatable("controllerx.action.inGame.lookPlayer")));
        this.gameMenu = mappings.register(new ControllerMapping<>(ControllerActions.START, Side.RIGHT, Component.translatable("controllerx.action.inGame.gameMenu")));
    }

    @Override
    public int getYOffset() {
        int chatYOffset = GameApi.getChatYOffset();
        if (chatYOffset > 9) {
            System.out.println("chatYOffset = " + chatYOffset);
            return Math.min((int) (double) Minecraft.getInstance().options.chatHeightFocused().get(), chatYOffset);
        }

        return super.getYOffset();
    }

    public @NotNull LocalPlayer player() {
        return Objects.requireNonNull(Minecraft.getInstance().player);
    }

    public @NotNull ClientLevel level() {
        return Objects.requireNonNull(Minecraft.getInstance().level);
    }
}
