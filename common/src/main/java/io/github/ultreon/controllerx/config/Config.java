package io.github.ultreon.controllerx.config;

import com.ultreon.commons.collection.map.OrderedMap;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.config.entries.*;
import io.github.ultreon.controllerx.config.gui.BindingsConfigScreen;
import io.github.ultreon.controllerx.config.gui.ConfigEntry;
import io.github.ultreon.controllerx.input.dyn.ControllerInterDynamic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private static final Map<ControllerContext, Config> CONFIGS = new OrderedMap<>();

    private final Map<ControllerMapping<?>, ConfigEntry<?>> mappings = new HashMap<>();
    private final Map<String, ConfigEntry<?>> entryMap = new HashMap<>();
    private final List<ConfigEntry<?>> entries = new ArrayList<>();
    private final ResourceLocation key;
    private final ControllerContext context;
    private final Path file;

    public Config(ResourceLocation key, ControllerContext context) {
        this.key = key;
        this.context = context;

        Path bindingsDir = Paths.get(ControllerX.BINDINGS_DIRECTORY);
        if (key.getNamespace().equals(ControllerX.MOD_ID))
            file = bindingsDir.resolve(key.getPath() + ".txt");
        else
            file = bindingsDir.resolve(key.getNamespace() + "/" + key.getPath() + ".txt");

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

    public <T extends Enum<T> & ControllerInterDynamic<?>> ConfigEntry<T> add(String key, ControllerMapping<T> defaultValue, Component description) {
        ConfigEntry<T> entry = new ControllerBindingEntry<>(key, defaultValue, defaultValue, description).comment(description.getString());
        entryMap.put(entry.getKey(), entry);
        entries.add(entry);

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

                ConfigEntry<?> entry = entryMap.get(entryArr[0]);
                entry.readAndSet(entryArr[1]);
            }
        } catch (FileNotFoundException ignored) {

        } catch (Exception e) {
            ControllerX.LOGGER.error("Failed to load config", e);
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (ConfigEntry<?> e : entryMap.values()) {
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
        } catch (Exception e) {
            ControllerX.LOGGER.error("Failed to save config", e);
        }
    }

    public ConfigEntry<?>[] values() {
        return entries.toArray(new ConfigEntry[0]);
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
