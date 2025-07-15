package me.iwareq.testtask.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author IWareQ
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewMaterializerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final File configFile = new File(Launch.minecraftHome, "config/new_materializer_config.json");

    private static NewMaterializerConfig instance;

    private int capacity;
    private int extraAmountPerTick;

    public static NewMaterializerConfig getInstance() {
        if (instance == null) {
            synchronized (NewMaterializerConfig.class) {
                if (instance == null) {
                    instance = load();
                }
            }
        }

        return instance;
    }

    private static NewMaterializerConfig load() {
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, NewMaterializerConfig.class);
        } catch (Exception ignored) {}

        NewMaterializerConfig defaultConfig = new NewMaterializerConfig();
        defaultConfig.capacity = 100000;
        defaultConfig.extraAmountPerTick = 500;
        save(defaultConfig);
        return defaultConfig;
    }

    private static void save(NewMaterializerConfig config) {
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }

            String json = GSON.toJson(config);
            Files.write(configFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
            System.out.println("[NewMaterializerConfig] Создан новый конфиг по умолчанию.");
        } catch (Exception e) {
            System.err.println("[NewMaterializerConfig] Не удалось сохранить конфиг: " + e.getMessage());
        }
    }
}
