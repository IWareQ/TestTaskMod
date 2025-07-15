package me.iwareq.testtask.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author IWareQ
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GaiaKillerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final File configFile = new File(Launch.minecraftHome, "config/gaia_killer_config.json");

    private static GaiaKillerConfig instance;

    private List<GaiaKillerEntry> levels;

    public static GaiaKillerConfig getInstance() {
        if (instance == null) {
            synchronized (GaiaKillerConfig.class) {
                if (instance == null) {
                    instance = load();
                }
            }
        }

        return instance;
    }

    private static GaiaKillerConfig load() {
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, GaiaKillerConfig.class);
        } catch (Exception ignored) {}

        GaiaKillerConfig defaultConfig = new GaiaKillerConfig();
        defaultConfig.levels = new ArrayList<>();

        GaiaKillerEntry level1 = new GaiaKillerEntry();
        level1.killCooldown = 10;
        level1.doubleDropChance = 0.1;
        level1.extraDropChance = 0.1;
        GaiaKillerEntry.ExtraDropEntry drop1 = new GaiaKillerEntry.ExtraDropEntry();
        drop1.item = "minecraft:diamond";
        drop1.count = 1;
        drop1.meta = 0;
        drop1.chance = 0.3;
        level1.extraDrops = Collections.singletonList(drop1);
        defaultConfig.levels.add(level1);

        GaiaKillerEntry level2 = new GaiaKillerEntry();
        level2.killCooldown = 9;
        level2.doubleDropChance = 0.3;
        level2.extraDropChance = 0.3;
        GaiaKillerEntry.ExtraDropEntry drop2 = new GaiaKillerEntry.ExtraDropEntry();
        drop2.item = "minecraft:diamond";
        drop2.count = 1;
        drop2.meta = 0;
        drop2.chance = 0.8;
        level2.extraDrops = Collections.singletonList(drop2);
        defaultConfig.levels.add(level2);

        GaiaKillerEntry level3 = new GaiaKillerEntry();
        level3.killCooldown = 8;
        level3.doubleDropChance = 0.6;
        level3.extraDropChance = 0.5;
        GaiaKillerEntry.ExtraDropEntry drop3 = new GaiaKillerEntry.ExtraDropEntry();
        drop3.item = "minecraft:diamond";
        drop3.count = 1;
        drop3.meta = 0;
        drop3.chance = 0.8;
        level3.extraDrops = Collections.singletonList(drop3);
        defaultConfig.levels.add(level3);

        GaiaKillerEntry level4 = new GaiaKillerEntry();
        level4.killCooldown = 6;
        level4.doubleDropChance = 0.8;
        level4.extraDropChance = 0.75;
        GaiaKillerEntry.ExtraDropEntry drop4 = new GaiaKillerEntry.ExtraDropEntry();
        drop4.item = "minecraft:diamond";
        drop4.count = 1;
        drop4.meta = 0;
        drop4.chance = 0.8;
        level4.extraDrops = Collections.singletonList(drop4);
        defaultConfig.levels.add(level4);

        GaiaKillerEntry level5 = new GaiaKillerEntry();
        level5.killCooldown = 5;
        level5.doubleDropChance = 1.0;
        level5.extraDropChance = 1.0;
        GaiaKillerEntry.ExtraDropEntry drop5 = new GaiaKillerEntry.ExtraDropEntry();
        drop5.item = "minecraft:diamond";
        drop5.count = 1;
        drop5.meta = 0;
        drop5.chance = 0.8;
        level5.extraDrops = Collections.singletonList(drop5);
        defaultConfig.levels.add(level5);

        save(defaultConfig);
        return defaultConfig;
    }

    private static void save(GaiaKillerConfig config) {
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }

            String json = GSON.toJson(config);
            Files.write(configFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
            System.out.println("[GaiaKillerConfig] Создан новый конфиг по умолчанию.");
        } catch (Exception e) {
            System.err.println("[GaiaKillerConfig] Не удалось сохранить конфиг: " + e.getMessage());
        }
    }

    @Getter
    public static class GaiaKillerEntry {
        private int killCooldown;
        private int killsForLevel;
        private double doubleDropChance;
        private double extraDropChance;
        private List<ExtraDropEntry> extraDrops;

        @Getter
        public static class ExtraDropEntry {
            private String item;
            private int count;
            private int meta;
            private double chance;

            public ItemStack toItemStack() {
                String[] parts = item.split(":");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid item entry: " + item);
                }

                Item findedItem = GameRegistry.findItem(parts[0], parts[1]);
                if (findedItem == null) {
                    throw new IllegalStateException("Cannot find item " + item);
                }

                return new ItemStack(findedItem, count, meta);
            }
        }
    }
}
