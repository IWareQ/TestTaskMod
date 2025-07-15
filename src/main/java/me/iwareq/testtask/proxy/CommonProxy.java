package me.iwareq.testtask.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import me.iwareq.testtask.Tags;
import me.iwareq.testtask.TestTaskMod;
import me.iwareq.testtask.common.block.BlockNewMaterializer;
import me.iwareq.testtask.common.container.ContainerNewMaterializer;
import me.iwareq.testtask.common.gui.GuiNewMaterializer;
import me.iwareq.testtask.common.item.GaiaKiller;
import me.iwareq.testtask.common.tileentity.TileNewMaterializer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author IWareQ
 */
public class CommonProxy implements IGuiHandler {
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerItem(new GaiaKiller(), "GaiaKiller");

        GameRegistry.registerBlock(new BlockNewMaterializer(), "new_materializer");

        GameRegistry.registerTileEntity(TileNewMaterializer.class, Tags.MOD_ID + ":new_materializer");
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(TestTaskMod.instance, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileNewMaterializer) {
            return new ContainerNewMaterializer(player.inventory, (TileNewMaterializer) tile);
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
        }

        return null;
    }
}
