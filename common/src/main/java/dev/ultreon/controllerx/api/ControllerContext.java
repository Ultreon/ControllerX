package dev.ultreon.controllerx.api;

import com.ultreon.libs.collections.v0.maps.OrderedHashMap;
import com.ultreon.mods.lib.world.Crosshair;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.config.Config;
import dev.ultreon.controllerx.gui.widget.ItemSlot;
import dev.ultreon.controllerx.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class ControllerContext {
    private static final Map<Predicate<Minecraft>, ControllerContext> REGISTRY = new OrderedHashMap<>();
    private static volatile boolean frozen = false;
    public final ControllerMappings mappings = new ControllerMappings();
    private static boolean initialized = false;
    final ResourceLocation id;
    private Config config;

    private static boolean isUsingVirtualKeyboard(Minecraft minecraft) {
        return ControllerX.get().input.isVirtualKeyboardOpen();
    }

    protected ControllerContext(ResourceLocation id) {
        this.id = id;
        if (!initialized) {
            REGISTRY.put(ControllerContext::isUsingVirtualKeyboard, VirtKeyboardControllerContext.INSTANCE);
            initialized = true;
        }
    }

    public static Iterable<Config> createConfigs() {
        List<Config> configs = new ArrayList<>();
        for (Map.Entry<Predicate<Minecraft>, ControllerContext> entry : REGISTRY.entrySet()) {
            configs.add(entry.getValue().createConfig());
        }

        return configs;
    }

    public static Iterable<? extends ControllerContext> getContexts() {
        return REGISTRY.values();
    }

    private Config createConfig() {
        this.config = new Config(this.id, this);
        Config.register(this.config);
        return this.config;
    }

    public String getId() {
        return this.id.toString();
    }

    public static void register(ControllerContext context, Predicate<Minecraft> predicate) {
        if (frozen)
            throw new IllegalStateException("Context registration is frozen.");

        REGISTRY.put(predicate, context);
    }

    @ApiStatus.Internal
    public static void freeze() {
        frozen = true;

        REGISTRY.put(ControllerContext::isChatting, ChatControllerContext.INSTANCE);
        REGISTRY.put(ControllerContext::isInGame, InGameControllerContext.INSTANCE);
        REGISTRY.put(ControllerContext::isInMenu, MenuControllerContext.INSTANCE);
        REGISTRY.put(Predicate.isEqual(Minecraft.getInstance()), new ControllerContext(new ResourceLocation("controllerx", "default")) {

        });
    }

    private static boolean isInCreativeMenu(Minecraft minecraft) {
        return minecraft.screen instanceof CreativeModeInventoryScreen;
    }

    public static boolean isChatting(Minecraft minecraft) {
        return minecraft.player != null && minecraft.level != null && minecraft.screen instanceof ChatScreen;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static boolean isTargetingEntity(Minecraft minecraft) {
        Crosshair crosshair = Crosshair.get();
        if (crosshair == null) return false;
        double entityReach = ControllerX.getEntityReach(minecraft.player);
        if (entityReach <= 0) return false;
        return crosshair.entity(entityReach) instanceof LivingEntity;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static boolean isTargetingBlock(Minecraft minecraft) {
        Crosshair crosshair = Crosshair.get();
        if (crosshair == null) return false;
        double blockReach = ControllerX.getBlockReach(minecraft.player);
        if (blockReach <= 0) return false;
        return crosshair.block(blockReach) != null && minecraft.player.getAbilities().mayBuild;
    }

    public static boolean isInMenu(Minecraft minecraft) {
        return minecraft.screen != null;
    }

    public static boolean isInCloseableMenu(Minecraft minecraft) {
        return minecraft.screen != null && minecraft.screen.shouldCloseOnEsc();
    }

    public static boolean isInMenuSelectedItemSlot(Minecraft minecraft) {
        return minecraft.screen != null && minecraft.screen.shouldCloseOnEsc() && minecraft.screen.getFocused() instanceof ItemSlot;
    }

    public static boolean isInGame(Minecraft minecraft) {
        return minecraft.player != null && minecraft.screen == null;
    }

    public static ControllerContext get() {
        for (Map.Entry<Predicate<Minecraft>, ControllerContext> entry : REGISTRY.entrySet()) {
            if (entry.getKey().test(Minecraft.getInstance())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public int getYOffset() {
        return 0;
    }

    public int getLeftXOffset() {
        return 0;
    }

    public int getRightXOffset() {
        return 0;
    }

    public Config getConfig() {
        return config;
    }

    public Component getName() {
        return config.getTitle();
    }
}
