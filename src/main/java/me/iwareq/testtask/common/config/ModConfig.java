package me.iwareq.testtask.common.config;

import net.minecraftforge.common.config.Configuration;

/**
 * @author IWareQ
 */
public class ModConfig {
    public static int chipFabricEnergyStorage;

    public static int oilFactoryInputTankCapacity;
    public static int oilFactoryOutputTankCapacity;
    public static int oilFactoryEnergyStorage;

    public static void load(Configuration configuration) {
        configuration.load();

        chipFabricEnergyStorage = configuration.getInt("energyStorage", "chipFabric", 100000, 1000, Integer.MAX_VALUE, "Буфер энергии");

        oilFactoryInputTankCapacity = configuration.getInt("inputTankCapacity", "oilFactory", 10000, 1000, Integer.MAX_VALUE, "Ёмкость входного бака");
        oilFactoryOutputTankCapacity = configuration.getInt("outputTankCapacity", "oilFactory", 10000, 1000, Integer.MAX_VALUE, "Ёмкость выходного бака");
        oilFactoryEnergyStorage = configuration.getInt("energyStorage", "oilFactory", 100000, 1000, Integer.MAX_VALUE, "Буфер энергии");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
