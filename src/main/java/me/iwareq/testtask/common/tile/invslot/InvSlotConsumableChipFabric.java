package me.iwareq.testtask.common.tile.invslot;

import ic2.core.block.invslot.InvSlotConsumable;
import me.iwareq.testtask.common.tile.TileChipFabric;
import me.iwareq.testtask.tweaker.recipe.ChipFabricRecipe;
import me.iwareq.testtask.tweaker.recipe.ChipFabricRecipeManager;
import net.minecraft.item.ItemStack;

/**
 * @author IWareQ
 */
public class InvSlotConsumableChipFabric extends InvSlotConsumable {
    private final ChipFabricRecipeManager recipeManager;

    public InvSlotConsumableChipFabric(TileChipFabric tile, ChipFabricRecipeManager recipeManager) {
        super(tile, "inputs", -1, 8);
        this.recipeManager = recipeManager;
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        return this.recipeManager.getOutputFor(itemStack, true) != null;
    }

    public void consume(ChipFabricRecipe recipe) {
        if (recipe == null) {
            return;
        }

        ItemStack[] requiredStacks = recipe.getInputStacks();
        if (requiredStacks.length != this.size()) {
            return;
        }

        for (ItemStack required : requiredStacks) {
            int requiredCount = required.stackSize;

            for (int i = 0; i < this.size(); i++) {
                ItemStack slotStack = this.get(i);
                if (slotStack == null) continue;

                if (required.isItemEqual(slotStack)) {
                    int toConsume = Math.min(requiredCount, slotStack.stackSize);
                    slotStack.stackSize -= toConsume;
                    requiredCount -= toConsume;

                    if (slotStack.stackSize <= 0) {
                        this.put(i, null);
                    } else {
                        this.put(i, slotStack);
                    }

                    if (requiredCount <= 0) {
                        break;
                    }
                }
            }
        }
    }
}
