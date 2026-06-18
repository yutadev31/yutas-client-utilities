package com.yutadev31.yutasClientUtilities.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class YutasClientUtilitiesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("yutas-client-utilities.json");
    private static YutasClientUtilitiesConfig instance;

    private String coordinateCopyFormat = "{x}, {y}, {z}";

    private YutasClientUtilitiesConfig() {
    }

    public static void load() {
        if (Files.notExists(CONFIG_PATH)) {
            instance = new YutasClientUtilitiesConfig();
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            YutasClientUtilitiesConfig loaded = GSON.fromJson(reader, YutasClientUtilitiesConfig.class);
            instance = loaded != null ? loaded : new YutasClientUtilitiesConfig();
        } catch (Exception e) {
            if (!(e instanceof JsonParseException)) {
                e.printStackTrace();
            }
            instance = new YutasClientUtilitiesConfig();
            save();
        }
    }

    public static YutasClientUtilitiesConfig get() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void save() {
        YutasClientUtilitiesConfig config = get();
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCoordinateCopyFormat() {
        return coordinateCopyFormat;
    }

    public void setCoordinateCopyFormat(String format) {
        this.coordinateCopyFormat = format == null || format.isBlank() ? "{x} {y} {z}" : format;
    }
}
