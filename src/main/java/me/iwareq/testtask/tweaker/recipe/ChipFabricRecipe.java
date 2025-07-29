package me.iwareq.testtask.tweaker.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;

/**
 * @author IWareQ
 */
@Getter
@AllArgsConstructor
public class ChipFabricRecipe {
    private final ItemStack[] inputStacks;
    private final ItemStack lensStack;
    private final ItemStack outputStack;
    private final int operationTime;
    private final double energyPerTick;

    public ItemStack getOutputStack() {
        return this.outputStack.copy();
    }

    public ItemStack getLensStack() {
        return this.lensStack.copy();
    }
}
