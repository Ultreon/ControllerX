package dev.ultreon.controllerx.api;

import dev.ultreon.controllerx.api.config.IConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class ControllerContext {
    private static final Map<Predicate<Minecraft>, ControllerContext> REGISTRY = new LinkedHashMap<>();
    private static volatile boolean frozen = false;
    public final IControllerMappings mappings = IControllerX.get().createMappings();
    final ResourceLocation id;
    private IConfig config;

    protected ControllerContext(ResourceLocation id) {
        this.id = id;
    }

    public static Iterable<IConfig> createConfigs() {
        List<IConfig> configs = new ArrayList<>();
        for (Map.Entry<Predicate<Minecraft>, ControllerContext> entry : REGISTRY.entrySet()) {
            configs.add(entry.getValue().createConfig());
        }

        return configs;
    }

    public static Iterable<? extends ControllerContext> getContexts() {
        synchronized (REGISTRY) {
            return REGISTRY.values();
        }
    }

    private IConfig createConfig() {
        config = IControllerX.get().createConfig(id, this);
        return config;
    }

    public String getId() {
        return id.toString();
    }

    public static void register(ControllerContext context, Predicate<Minecraft> predicate) {
        synchronized (REGISTRY) {
            if (frozen)
                throw new IllegalStateException("Context registration is frozen.");

            REGISTRY.put(predicate, context);
        }
    }

    @ApiStatus.Internal
    public static void freeze() {
        synchronized (REGISTRY) {
            IControllerX.get().getInternals().finalRegistration();
            frozen = true;
        }
    }

    public static boolean isTargetingEntity(Minecraft minecraft) {
        Crosshair crosshair = Crosshair.get();
        if (crosshair == null) return false;
        double entityReach = IControllerX.get().getEntityReach(minecraft.player);
        if (entityReach <= 0) return false;
        return crosshair.entity(entityReach) instanceof LivingEntity;
    }

    public static boolean isTargetingBlock(Minecraft minecraft) {
        Crosshair crosshair = Crosshair.get();
        if (crosshair == null) return false;
        double blockReach = IControllerX.get().getBlockReach(minecraft.player);
        if (blockReach <= 0) return false;
        if (minecraft.player != null) {
            return crosshair.block(blockReach) != null && minecraft.player.getAbilities().mayBuild;
        }

        return false;
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

    public IConfig getConfig() {
        return config;
    }

    public Component getName() {
        return config.getTitle();
    }

    public boolean shouldShowHUD() {
        return true;
    }

    public void releaseAll() {

    }

    public ResourceLocation getLocation() {
        return id;
    }
}
