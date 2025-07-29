package me.iwareq.testtask.tweaker.zen;

import me.iwareq.testtask.tweaker.recipe.ChipFabricRecipe;
import me.iwareq.testtask.tweaker.recipe.ChipFabricRecipeManager;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author IWareQ
 */
@ZenClass("mods.testtask.ChipFabric")
public class ZenChipFabric {
    @ZenMethod
    public static void addRecipe(IItemStack[] inputs, IItemStack lens, IItemStack output, int operationTime, double energyPerTick) {
        ItemStack[] inputStacks = new ItemStack[inputs.length];
        for (int i = 0; i < inputs.length; ++i) {
            inputStacks[i] = MineTweakerMC.getItemStack(inputs[i]);
        }

        ChipFabricRecipe recipe = new ChipFabricRecipe(inputStacks, MineTweakerMC.getItemStack(lens), MineTweakerMC.getItemStack(output), operationTime, energyPerTick);
        ChipFabricRecipeManager.INSTANCE.addRecipe(recipe);
    }
}
