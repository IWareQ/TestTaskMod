package me.iwareq.testtask.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IHasGui;
import me.iwareq.testtask.TestTaskMod;
import me.iwareq.testtask.common.ModBlocks;
import me.iwareq.testtask.common.config.ModConfig;
import me.iwareq.testtask.common.container.ContainerNewMaterializer;
import me.iwareq.testtask.common.gui.GuiNewMaterializer;
import me.iwareq.testtask.common.item.GaiaKiller;
import me.iwareq.testtask.common.tile.TileNewMaterializer;
import me.iwareq.testtask.tweaker.zen.ZenChipFabric;
import me.iwareq.testtask.tweaker.zen.ZenOilFactory;
import minetweaker.MineTweakerAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

/**
 * @author IWareQ
 */
public class CommonProxy implements IGuiHandler {
    public void preInit(FMLPreInitializationEvent event) {
        ModConfig.load(new Configuration(event.getSuggestedConfigurationFile()));

        GameRegistry.registerItem(new GaiaKiller(), "gaiaKiller");

        ModBlocks.register();
    }

    public void init(FMLInitializationEvent ignoredEvent) {
        MineTweakerAPI.registerClass(ZenChipFabric.class);
        MineTweakerAPI.registerClass(ZenOilFactory.class);

        NetworkRegistry.INSTANCE.registerGuiHandler(TestTaskMod.instance, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileNewMaterializer) {
            return new ContainerNewMaterializer(player.inventory, (TileNewMaterializer) tile);
        } else if (tile instanceof IHasGui) {
            return ((IHasGui) tile).getGuiContainer(player);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileNewMaterializer) {
            return new GuiNewMaterializer(
                    new ContainerNewMaterializer(player.inventory, (TileNewMaterializer) tile),
                    (TileNewMaterializer) tile
            );
        } else if (tile instanceof IHasGui) {
            return ((IHasGui) tile).getGui(player, false);
        }

        return null;
    }
}
