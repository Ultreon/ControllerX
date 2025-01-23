package dev.ultreon.controllerx.impl.contexts;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.GameApi;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.api.IControllerMapping;
import dev.ultreon.controllerx.impl.ControllerAction;
import dev.ultreon.controllerx.impl.ControllerMapping;
import dev.ultreon.controllerx.api.input.ControllerBoolean;
import dev.ultreon.controllerx.input.ControllerInput;
import dev.ultreon.controllerx.api.input.ControllerVec2;
import net.minecraft.client.KeyMapping;
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

import java.util.*;
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

    private final BiMap<KeyMapping, ControllerMapping<?>> moddedKeyMappings = HashBiMap.create();

    protected InGameControllerContext(ResourceLocation id) {
        super(id);

        jump = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.A), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.jump"), "jump", (mc) -> {
            if (mc.player == null) return false;
            MobEffectInstance effect = mc.player.getEffect(MobEffects.JUMP);
            if (effect == null) return true;

            // Due to integer overflow the jump boost will not work at 128 and beyond.
            // See https://minecraft.wiki/w/Jump_Boost#Notes for more info
            // !TODO This will be removed in 1.20.5
            return effect.getAmplifier() < 128;
        }));
        run = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.LeftStickClick), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.sprint"), "sprint", (mc) -> checkPlayer(mc, player -> player.canSprint() && player.getFoodData().getFoodLevel() > 3)));
        sneak = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.RightStickClick), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.sneak"), "sneak"));
        use = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.LeftTrigger), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.useItem"), "use_item", (mc) -> checkPlayer(mc, player -> !player.getMainHandItem().isEmpty())));
        inventory = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.Y), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.openInventory"), "open_inventory"));
        swapHands = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.X), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.swapHands"), "swap_hands", (mc) -> checkPlayer(mc, player -> !(player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()))));
        lookPlayer = mappings.register(new ControllerMapping<>(new ControllerAction.Joystick(ControllerVec2.RightStick), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.lookPlayer"), "look_player"));
        movePlayer = mappings.register(new ControllerMapping<>(new ControllerAction.Joystick(ControllerVec2.LeftStick), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.movePlayer"), "move_player"));
        gameMenu = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.Start), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.openGameMenu"), "open_game_menu"));
        pickItem = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.DpadUp), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.pickItem"), false, "pick_item", (mc) -> checkPlayer(mc, (player) -> player.getMainHandItem().isEmpty() && (mc.crosshairPickEntity != null || player.pick(player.getPickRadius(), Minecraft.getInstance().getDeltaFrameTime(), false).getType() != HitResult.Type.MISS))));
        drop = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.DpadDown), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.dropItem"), "drop_item", (mc) -> checkPlayer(mc, (player) -> !player.getMainHandItem().isEmpty())));
        playerList = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.DpadLeft), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.showPlayerList"), false, "show_player_list"));
        chat = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.DpadRight), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.openChat"), false, "open_chat"));
        itemLeft = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.LeftShoulder), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.selectLeft"), false, "select_left"));
        itemRight = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.RightShoulder), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.inGame.selectRight"), false, "select_right"));
        attack = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.RightTrigger), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.attack"), "attack", (mc) -> isTargetingBlock(mc) || isTargetingEntity(mc)));

        final Set<KeyMapping> illegalKeyMappings = Set.of(
                Minecraft.getInstance().options.keyLeft,
                Minecraft.getInstance().options.keyRight,
                Minecraft.getInstance().options.keyUp,
                Minecraft.getInstance().options.keyDown,
                Minecraft.getInstance().options.keyJump,
                Minecraft.getInstance().options.keySprint,
                Minecraft.getInstance().options.keyUse,
                Minecraft.getInstance().options.keyAttack,
                Minecraft.getInstance().options.keySwapOffhand,
                Minecraft.getInstance().options.keyDrop,
                Minecraft.getInstance().options.keyChat,
                Minecraft.getInstance().options.keyPlayerList,
                Minecraft.getInstance().options.keyPickItem,
                Minecraft.getInstance().options.keyInventory
        );

        for (KeyMapping keyMapping : KeyMapping.ALL.values()) {
            if (ControllerInput.getAction(Minecraft.getInstance(), keyMapping, this) != null) continue;
            if (illegalKeyMappings.contains(keyMapping)) continue;
            moddedKeyMappings.put(keyMapping, mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.Unknown), IControllerMapping.Side.RIGHT, Component.translatable(keyMapping.getName()), false, "minecraft." + keyMapping.getName(), (mc) -> isTargetingBlock(mc) || isTargetingEntity(mc))));
        }

        ControllerInput.moddedMappingsLoaded = true;
    }

    public Map<KeyMapping, ControllerMapping<?>> getKeyToController() {
        return Collections.unmodifiableMap(moddedKeyMappings);
    }

    public Map<ControllerMapping<?>, KeyMapping> getControllerToKey() {
        return Collections.unmodifiableMap(moddedKeyMappings.inverse());
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

    @Override
    public void releaseAll() {
        super.releaseAll();

        for (KeyMapping mapping : moddedKeyMappings.keySet()) {
            mapping.setDown(false);
        }
    }

    public @NotNull LocalPlayer player() {
        return Objects.requireNonNull(Minecraft.getInstance().player);
    }

    public @NotNull ClientLevel level() {
        return Objects.requireNonNull(Minecraft.getInstance().level);
    }

    public boolean isModded(ControllerMapping<?> mapping) {
        return moddedKeyMappings.containsValue(mapping);
    }
}
