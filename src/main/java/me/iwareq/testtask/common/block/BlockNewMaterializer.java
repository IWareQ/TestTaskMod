package me.iwareq.testtask.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import me.iwareq.testtask.Tags;
import me.iwareq.testtask.TestTaskMod;
import me.iwareq.testtask.common.tile.TileNewMaterializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author IWareQ
 */
public class BlockNewMaterializer extends BlockContainer {
    public BlockNewMaterializer() {
        super(Material.iron);
        setBlockName("newMaterializer");
        setBlockTextureName(Tags.MOD_ID + ":newMaterializer");
        setCreativeTab(TestTaskMod.CREATIVE_TAB);

        GameRegistry.registerTileEntity(TileNewMaterializer.class, Tags.MOD_ID + ":newMaterializer");
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (worldIn.isRemote) {
            return false;
        }

        TileEntity tile = worldIn.getTileEntity(x, y, z);
        if (tile instanceof TileNewMaterializer) {
            player.openGui(TestTaskMod.instance, 0, worldIn, x, y, z);
            return true;
        }

        return false;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if (worldIn.isRemote) {
            return;
        }

        List<ItemStack> drops = new ArrayList<>();

        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null) {
                    drops.add(stack.copy());
                }
            }
        }

        for (ItemStack drop : drops) {
            worldIn.spawnEntityInWorld(new EntityItem(worldIn, x + 0.5, y + 0.5, z + 0.5, drop));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileNewMaterializer();
    }
}
