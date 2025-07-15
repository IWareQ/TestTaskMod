package me.iwareq.testtask.common.block;

import me.iwareq.testtask.TestTaskMod;
import me.iwareq.testtask.common.tileentity.TileNewMaterializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
        setBlockName("new_materializer");
        setBlockTextureName("testtaskmod:new_materializer");
        setCreativeTab(TestTaskMod.CREATIVE_TAB);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        TileEntity tile = worldIn.getTileEntity(x, y, z);
        if (tile instanceof TileNewMaterializer) {
            player.openGui(TestTaskMod.instance, 0, worldIn, x, y, z);
            return true;
        }

        return false;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        List<ItemStack> drops = new ArrayList<>();

        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity instanceof TileNewMaterializer) {
            TileNewMaterializer tileNewMaterializer = (TileNewMaterializer) tileEntity;
            for (int i = 0; i < tileNewMaterializer.getSizeInventory(); i++) {
                ItemStack stack = tileNewMaterializer.getStackInSlot(i);
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
