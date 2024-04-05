package io.github.ultreon.controllerx.config;

import com.ultreon.libs.collections.v0.maps.OrderedHashMap;
import dev.architectury.platform.Platform;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.config.entries.*;
import io.github.ultreon.controllerx.config.gui.BindingsConfigScreen;
import io.github.ultreon.controllerx.config.gui.ConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Config {
    private static final Map<ControllerContext, Config> CONFIGS = new OrderedHashMap<>();

    private final Map<ControllerMapping<?>, ConfigEntry<?>> mappings = new OrderedHashMap<>();
    private final OrderedHashMap<String, ConfigEntry<?>> entries = new OrderedHashMap<>();
    private final ResourceLocation key;
    private final ControllerContext context;
    private final Path file;

    public Config(ResourceLocation key, ControllerContext context) {
        this.key = key;
        this.context = context;

        if (key.getNamespace().equals(ControllerX.MOD_ID))
            file = Platform.getConfigFolder().resolve("controllerx/").resolve(key.getPath() + ".txt");
        else
            file = Platform.getConfigFolder().resolve("controllerx/").resolve(key.getNamespace() + "/" + key.getPath() + ".txt");
        Path parent = file.getParent();
        if (!Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (ControllerMapping<?> mapping : context.mappings.getAllMappings()) {
            ConfigEntry<?> entry = mapping.createEntry(this);
            mappings.put(mapping, entry);
        }
    }

    public ConfigEntry<?> byMapping(ControllerMapping<?> mapping) {
        return this.mappings.get(mapping);
    }

    public static void register(Config config) {
        CONFIGS.put(config.context, config);
    }

    public static Config[] getConfigs() {
        return CONFIGS.values().toArray(new Config[0]);
    }

    public static void saveAll() {
        for (Config config : CONFIGS.values()) {
            config.save();
        }
    }

    public <T extends Enum<T>> ConfigEntry<T> add(String key, T defaultValue, Component description) {
        ConfigEntry<T> entry = new EnumEntry<>(key, defaultValue, description).comment(description.getString());
        entries.put(key, entry);

        return entry;
    }

    public void load() {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String s;
            while ((s = reader.readLine()) != null) {
                if (s.startsWith("#")) {
                    continue;
                }
                String[] entryArr = s.split("=", 2);
                if (entryArr.length <= 1) {
                    continue;
                }

                ConfigEntry<?> entry = entries.get(entryArr[0]);
                entry.readAndSet(entryArr[1]);
            }
        } catch (FileNotFoundException ignored) {

        } catch (Exception e) {
            ControllerX.LOGGER.error("Failed to load config", e);
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (ConfigEntry<?> e : entries.values()) {
                String key = e.getKey();
                String value = e.write();

                String comment = e.getComment();
                if (comment != null && !comment.isBlank()) {
                    writer.write("# ");
                    writer.write(comment.trim().replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " "));
                    writer.newLine();
                }
                writer.write(key);
                writer.write("=");
                writer.write(value);
                writer.newLine();
            }
        } catch (FileNotFoundException ignored) {

        } catch (Exception e) {
            ControllerX.LOGGER.error("Failed to save config", e);
        }
    }

    public ConfigEntry<?>[] values() {
        return entries.values().toArray(new ConfigEntry[0]);
    }

    public AbstractWidget createButton(Config config, int x, int y, int w) {
        return new ConfigButton(x, y, w, config);
    }

    public ControllerContext getContext() {
        return context;
    }

    private static class ConfigButton extends Button {
        public ConfigButton(int x, int y, int w, Config config) {
            super(x, y, w, 20, Component.translatable("controllerx.open_config"), (button) -> new BindingsConfigScreen(Minecraft.getInstance().screen, config).open(), supplier -> Component.empty());
        }
    }

    public Component getTitle() {
        return Component.translatable("controllerx.config." + this.key.toString().replace(":", "."));
    }
}
