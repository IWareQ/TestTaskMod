package me.iwareq.testtask.common;

import cpw.mods.fml.common.registry.GameRegistry;
import me.iwareq.testtask.common.block.BlockChipFabric;
import me.iwareq.testtask.common.block.BlockNewMaterializer;
import me.iwareq.testtask.common.block.BlockOilFactory;

/**
 * @author IWareQ
 */
public interface ModBlocks {
    BlockChipFabric chipFabric = new BlockChipFabric();
    BlockNewMaterializer newMaterializer = new BlockNewMaterializer();
    BlockOilFactory oilFactory = new BlockOilFactory();

    static void register() {
        GameRegistry.registerBlock(chipFabric, "chipFabric");
        GameRegistry.registerBlock(newMaterializer, "newMaterializer");
        GameRegistry.registerBlock(oilFactory, "oilFactory");
    }
}
