package me.iwareq.testtask.tweaker.recipe;

import lombok.Getter;
import net.minecraft.item.ItemStack;

/**
 * @author IWareQ
 */
@Getter
public class ChipFabricRecipe extends MachineRecipe {
    private final ItemStack[] inputStacks;
    private final ItemStack lensStack;
    private final ItemStack outputStack;

    public ChipFabricRecipe(ItemStack[] inputStacks, ItemStack lensStack, ItemStack outputStack, int operationTime, double energyPerTick) {
        super(operationTime, energyPerTick);
        this.inputStacks = inputStacks;
        this.lensStack = lensStack;
        this.outputStack = outputStack;
    }

    public ItemStack getOutputStack() {
        return this.outputStack.copy();
    }

    public ItemStack getLensStack() {
        return this.lensStack.copy();
    }
}
