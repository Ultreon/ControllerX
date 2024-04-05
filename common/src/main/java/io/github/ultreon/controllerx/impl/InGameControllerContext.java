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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

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
    public final ControllerMapping<?> itemLeft;
    public final ControllerMapping<?> itemRight;
    public final ControllerMapping<?> attack;
    public final ControllerMapping<?> destroyBlock;

    protected InGameControllerContext() {
        super();

        this.jump = mappings.register(new ControllerMapping<>(ControllerActions.A, Side.LEFT, Component.translatable("controllerx.action.inGame.jump"), (mc) -> {
            if (mc.player == null) return false;
            MobEffectInstance effect = mc.player.getEffect(MobEffects.JUMP);
            if (effect == null) return true;

            // Due to integer overflow the jump boost will not work at 128 and beyond.
            // See https://minecraft.wiki/w/Jump_Boost#Notes for more info
            // !TODO This will be removed in 1.20.5
            return effect.getAmplifier() < 128;
        }));
        this.run = mappings.register(new ControllerMapping<>(ControllerActions.PRESS_LEFT_STICK, Side.RIGHT, Component.translatable("controllerx.action.inGame.run"), (mc) -> checkPlayer(mc, player -> player.canSprint() && player.getFoodData().getFoodLevel() > 3)));
        this.sneak = mappings.register(new ControllerMapping<>(ControllerActions.PRESS_RIGHT_STICK, Side.RIGHT, Component.translatable("controllerx.action.inGame.sneak")));
        this.use = mappings.register(new ControllerMapping<>(ControllerActions.LEFT_TRIGGER, Side.LEFT, Component.translatable("controllerx.action.inGame.use"), (mc) -> checkPlayer(mc, player -> !player.getMainHandItem().isEmpty())));
        this.inventory = mappings.register(new ControllerMapping<>(ControllerActions.Y, Side.RIGHT, Component.translatable("controllerx.action.inGame.inventory")));
        this.swapHands = mappings.register(new ControllerMapping<>(ControllerActions.X, Side.RIGHT, Component.translatable("controllerx.action.inGame.swapHands"), (mc) -> checkPlayer(mc, player -> !(player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()))));
        this.lookPlayer = mappings.register(new ControllerMapping<>(ControllerActions.MOVE_RIGHT_STICK, Side.LEFT, Component.translatable("controllerx.action.inGame.lookPlayer")));
        this.movePlayer = mappings.register(new ControllerMapping<>(ControllerActions.MOVE_LEFT_STICK, Side.LEFT, Component.translatable("controllerx.action.inGame.movePlayer")));
        this.gameMenu = mappings.register(new ControllerMapping<>(ControllerActions.BACK, Side.RIGHT, Component.translatable("controllerx.action.inGame.gameMenu")));
        this.pickItem = mappings.register(new ControllerMapping<>(ControllerActions.DPAD_UP, Side.LEFT, Component.translatable("controllerx.action.inGame.pickItem"), false, (mc) -> checkPlayer(mc, (player) -> player.getMainHandItem().isEmpty() && (mc.crosshairPickEntity != null || player.pick(player.getPickRadius(), Minecraft.getInstance().getDeltaFrameTime(), false).getType() != HitResult.Type.MISS))));
        this.drop = mappings.register(new ControllerMapping<>(ControllerActions.DPAD_DOWN, Side.LEFT, Component.translatable("controllerx.action.inGame.drop"), (mc) -> checkPlayer(mc, (player) -> !player.getMainHandItem().isEmpty())));
        this.playerList = mappings.register(new ControllerMapping<>(ControllerActions.DPAD_LEFT, Side.LEFT, Component.translatable("controllerx.action.inGame.playerList"), false));
        this.chat = mappings.register(new ControllerMapping<>(ControllerActions.DPAD_RIGHT, Side.LEFT, Component.translatable("controllerx.action.inGame.chat"), false));
        this.itemLeft = mappings.register(new ControllerMapping<>(ControllerActions.LEFT_SHOULDER, Side.LEFT, Component.translatable("controllerx.action.inGame.itemLeft"), false));
        this.itemRight = mappings.register(new ControllerMapping<>(ControllerActions.RIGHT_SHOULDER, Side.LEFT, Component.translatable("controllerx.action.inGame.itemRight"), false));

        this.attack = mappings.register(new ControllerMapping<>(ControllerActions.RIGHT_TRIGGER_HOLD, ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.attack"), ControllerContext::isTargetingEntity));
        this.destroyBlock = mappings.register(new ControllerMapping<>(ControllerActions.RIGHT_TRIGGER_HOLD, ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.destroyBlock"), ControllerContext::isTargetingBlock));
    }

    private boolean checkPlayer(Minecraft mc, Predicate<Player> predicate) {
        LocalPlayer player = mc.player;
        if (player == null)
            return false;

        return predicate.test(player);
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
