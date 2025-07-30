package me.iwareq.testtask.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import ic2.core.IHasGui;
import me.iwareq.testtask.common.container.ContainerNewMaterializer;
import me.iwareq.testtask.common.gui.GuiNewMaterializer;
import me.iwareq.testtask.common.tile.TileNewMaterializer;
import me.iwareq.testtask.nei.NEIConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author IWareQ
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent ignoredEvent) {
        super.init(ignoredEvent);
        NEIConfig.registerCatalysts();
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
