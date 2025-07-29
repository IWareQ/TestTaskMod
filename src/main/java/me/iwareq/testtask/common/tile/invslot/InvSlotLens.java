package me.iwareq.testtask.common.tile.invslot;

import ic2.core.block.invslot.InvSlot;
import me.iwareq.testtask.common.tile.TileChipFabric;
import me.iwareq.testtask.tweaker.recipe.ChipFabricRecipe;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author IWareQ
 */
public class InvSlotLens extends InvSlot {
    private final List<ChipFabricRecipe> recipes;

    public InvSlotLens(TileChipFabric tile, List<ChipFabricRecipe> recipes) {
        super(tile, "lens", -1, Access.I, 1, InvSide.ANY);
        this.setStackSizeLimit(1);
        this.recipes = recipes;
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        return itemStack != null && this.recipes.stream().anyMatch(recipe -> recipe.getLensStack().isItemEqual(itemStack));
    }

    public boolean matches(ItemStack itemStack) {
        ItemStack lens = this.get();
        return lens != null && itemStack != null && lens.isItemEqual(itemStack);
    }
}
