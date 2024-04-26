package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.GameApi;
import io.github.ultreon.controllerx.api.ControllerAction;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.api.ControllerMapping.Side;
import io.github.ultreon.controllerx.input.ControllerBoolean;
import io.github.ultreon.controllerx.input.ControllerVec2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public class InGameControllerContext extends ControllerContext {
    public static final InGameControllerContext INSTANCE = new InGameControllerContext(ControllerX.res("in_game"));

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

    protected InGameControllerContext(ResourceLocation id) {
        super(id);

        this.jump = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.A), Side.LEFT, Component.translatable("controllerx.action.inGame.jump"), "jump", (mc) -> {
            if (mc.player == null) return false;
            MobEffectInstance effect = mc.player.getEffect(MobEffects.JUMP);
            if (effect == null) return true;

            // Due to integer overflow the jump boost will not work at 128 and beyond.
            // See https://minecraft.wiki/w/Jump_Boost#Notes for more info
            // !TODO This will be removed in 1.20.5
            return effect.getAmplifier() < 128;
        }));
        this.run = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.LeftStickClick), Side.RIGHT, Component.translatable("controllerx.action.inGame.sprint"), "sprint", (mc) -> checkPlayer(mc, player -> player.canSprint() && player.getFoodData().getFoodLevel() > 3)));
        this.sneak = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.RightStickClick), Side.RIGHT, Component.translatable("controllerx.action.inGame.sneak"), "sneak"));
        this.use = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.LeftTrigger), Side.LEFT, Component.translatable("controllerx.action.inGame.useItem"), "use_item", (mc) -> checkPlayer(mc, player -> !player.getMainHandItem().isEmpty())));
        this.inventory = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.Y), Side.RIGHT, Component.translatable("controllerx.action.inGame.openInventory"), "open_inventory"));
        this.swapHands = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.X), Side.RIGHT, Component.translatable("controllerx.action.inGame.swapHands"), "swap_hands", (mc) -> checkPlayer(mc, player -> !(player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()))));
        this.lookPlayer = mappings.register(new ControllerMapping<>(new ControllerAction.Joystick(ControllerVec2.RightStick), Side.LEFT, Component.translatable("controllerx.action.inGame.lookPlayer"), "look_player"));
        this.movePlayer = mappings.register(new ControllerMapping<>(new ControllerAction.Joystick(ControllerVec2.LeftStick), Side.LEFT, Component.translatable("controllerx.action.inGame.movePlayer"), "move_player"));
        this.gameMenu = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.Back), Side.RIGHT, Component.translatable("controllerx.action.inGame.openGameMenu"), "open_game_menu"));
        this.pickItem = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.DpadUp), Side.LEFT, Component.translatable("controllerx.action.inGame.pickItem"), false, "pick_item", (mc) -> checkPlayer(mc, (player) -> player.getMainHandItem().isEmpty() && (mc.crosshairPickEntity != null || player.pick(player.getPickRadius(), Minecraft.getInstance().getDeltaFrameTime(), false).getType() != HitResult.Type.MISS))));
        this.drop = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.DpadDown), Side.LEFT, Component.translatable("controllerx.action.inGame.dropItem"), "drop_item", (mc) -> checkPlayer(mc, (player) -> !player.getMainHandItem().isEmpty())));
        this.playerList = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.DpadLeft), Side.LEFT, Component.translatable("controllerx.action.inGame.showPlayerList"), false, "show_player_list"));
        this.chat = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.DpadRight), Side.LEFT, Component.translatable("controllerx.action.inGame.openChat"), false, "open_chat"));
        this.itemLeft = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.LeftShoulder), Side.LEFT, Component.translatable("controllerx.action.inGame.selectLeft"), false, "select_left"));
        this.itemRight = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.RightShoulder), Side.LEFT, Component.translatable("controllerx.action.inGame.selectRight"), false, "select_right"));

        this.attack = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.RightTrigger), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.attack"), "attack", ControllerContext::isTargetingEntity));
        this.destroyBlock = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.RightTrigger), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.destroyBlock"), "destroy_block", ControllerContext::isTargetingBlock));
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
