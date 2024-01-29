package io.github.ultreon.controllerx;

import java.io.IOException;
import java.nio.file.Files;

public class Config {
    private static Config instance;
    public float axisDeadZone = 0.3f;

    public void save() {
        String json = ControllerX.GSON.toJson(this, Config.class);

        try {
            Files.write(ModPaths.CONFIG, json.getBytes());
        } catch (IOException e) {
            ControllerX.LOGGER.error("Failed to save config", e);
        }
    }

    public static Config get() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        if (Files.exists(ModPaths.CONFIG)) {
            try {
                instance = ControllerX.GSON.fromJson(Files.readString(ModPaths.CONFIG), Config.class);
            } catch (IOException e) {
                ControllerX.LOGGER.error("Failed to load config", e);
                instance = new Config();
            }
        } else {
            instance = new Config();
            instance.save();
        }
    }
}
