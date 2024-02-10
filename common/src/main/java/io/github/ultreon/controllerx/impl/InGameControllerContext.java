package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.GameApi;
import io.github.ultreon.controllerx.api.ControllerActions;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.api.ControllerMapping.Side;
import io.github.ultreon.controllerx.input.ControllerButton;
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
    public final ControllerMapping<?> pickItem;
    public final ControllerMapping<?> drop;
    public final ControllerMapping<?> playerList;
    public final ControllerMapping<?> chat;
    public final ControllerMapping<ControllerButton> itemLeft;
    public final ControllerMapping<ControllerButton> itemRight;

    protected InGameControllerContext() {
        super();

        this.jump = mappings.register(new ControllerMapping<>(ControllerActions.A, Side.LEFT, Component.translatable("controllerx.action.inGame.jump")));
        this.run = mappings.register(new ControllerMapping<>(ControllerActions.PRESS_LEFT_STICK, Side.RIGHT, Component.translatable("controllerx.action.inGame.run")));
        this.sneak = mappings.register(new ControllerMapping<>(ControllerActions.PRESS_RIGHT_STICK, Side.RIGHT, Component.translatable("controllerx.action.inGame.sneak")));
        this.use = mappings.register(new ControllerMapping<>(ControllerActions.LEFT_TRIGGER, Side.LEFT, Component.translatable("controllerx.action.inGame.use")));
        this.inventory = mappings.register(new ControllerMapping<>(ControllerActions.Y, Side.RIGHT, Component.translatable("controllerx.action.inGame.inventory")));
        this.swapHands = mappings.register(new ControllerMapping<>(ControllerActions.X, Side.RIGHT, Component.translatable("controllerx.action.inGame.swapHands")));
        this.lookPlayer = mappings.register(new ControllerMapping<>(ControllerActions.MOVE_RIGHT_STICK, Side.LEFT, Component.translatable("controllerx.action.inGame.lookPlayer")));
        this.movePlayer = mappings.register(new ControllerMapping<>(ControllerActions.MOVE_LEFT_STICK, Side.LEFT, Component.translatable("controllerx.action.inGame.movePlayer")));
        this.gameMenu = mappings.register(new ControllerMapping<>(ControllerActions.BACK, Side.RIGHT, Component.translatable("controllerx.action.inGame.gameMenu")));
        this.pickItem = mappings.register(new ControllerMapping<>(ControllerActions.DPAD_UP, Side.LEFT, Component.translatable("controllerx.action.inGame.pickItem"), false));
        this.drop = mappings.register(new ControllerMapping<>(ControllerActions.DPAD_DOWN, Side.LEFT, Component.translatable("controllerx.action.inGame.drop")));
        this.playerList = mappings.register(new ControllerMapping<>(ControllerActions.DPAD_LEFT, Side.LEFT, Component.translatable("controllerx.action.inGame.playerList"), false));
        this.chat = mappings.register(new ControllerMapping<>(ControllerActions.DPAD_RIGHT, Side.LEFT, Component.translatable("controllerx.action.inGame.chat"), false));
        this.itemLeft = mappings.register(new ControllerMapping<>(ControllerActions.LEFT_SHOULDER, Side.LEFT, Component.translatable("controllerx.action.inGame.itemLeft"), false));
        this.itemRight = mappings.register(new ControllerMapping<>(ControllerActions.RIGHT_SHOULDER, Side.LEFT, Component.translatable("controllerx.action.inGame.itemRight"), false));
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
