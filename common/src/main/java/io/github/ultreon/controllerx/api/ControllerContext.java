package io.github.ultreon.controllerx.api;

import com.ultreon.libs.collections.v0.maps.OrderedHashMap;
import com.ultreon.mods.lib.world.Crosshair;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Predicate;

public abstract class ControllerContext {
    private static final Map<Predicate<Minecraft>, ControllerContext> REGISTRY = new OrderedHashMap<>();
    private static volatile boolean frozen = false;
    public final ControllerMappings mappings = new ControllerMappings();

    protected ControllerContext() {
    }

    public static void register(ControllerContext context, Predicate<Minecraft> predicate) {
        if (frozen)
            throw new IllegalStateException("Context registration is frozen.");

        REGISTRY.put(predicate, context);
    }

    @ApiStatus.Internal
    public static void freeze() {
        frozen = true;

        REGISTRY.put(ControllerContext::isInGameTargetingBlock, BlockTargetControllerContext.INSTANCE);
        REGISTRY.put(ControllerContext::isInGameTargetingEntity, EntityTargetControllerContext.INSTANCE);
        REGISTRY.put(ControllerContext::isInGame, InGameControllerContext.INSTANCE);
        REGISTRY.put(ControllerContext::isInCloseableMenu, CloseableMenuControllerContext.INSTANCE);
        REGISTRY.put(ControllerContext::isInMenu, MenuControllerContext.INSTANCE);
        REGISTRY.put(Predicate.isEqual(Minecraft.getInstance()), new ControllerContext() {

        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private static boolean isInGameTargetingEntity(Minecraft minecraft) {
        Crosshair crosshair = Crosshair.get();
        if (isInGame(minecraft) && crosshair != null) {
            double entityReach = ControllerX.getEntityReach(minecraft.player);

            if (entityReach <= 0) return false;
            if (crosshair.entity(entityReach) == null) return false;
            return crosshair.entity(entityReach) instanceof LivingEntity;
        }

        return false;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static boolean isInGameTargetingBlock(Minecraft minecraft) {
        if (isInGame(minecraft)) {
            return Crosshair.get().block(ControllerX.getBlockReach(minecraft.player)) != null;
        }
        return false;
    }

    public static boolean isInMenu(Minecraft minecraft) {
        return minecraft.screen != null;
    }

    public static boolean isInCloseableMenu(Minecraft minecraft) {
        return minecraft.screen != null && minecraft.screen.shouldCloseOnEsc();
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
}
