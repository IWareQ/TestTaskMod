package me.iwareq.testtask.tweaker.zen;

import me.iwareq.testtask.tweaker.recipe.OilFactoryRecipe;
import me.iwareq.testtask.tweaker.recipe.OilFactoryRecipeManager;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.minecraft.MineTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Objects;

/**
 * @author IWareQ
 */
@ZenClass("mods.testtask.OilFactory")
public class ZenOilFactory {
    @ZenMethod
    public static void addRecipe(ILiquidStack input, ILiquidStack output, int operationTime, double energyPerTick) {
        Objects.requireNonNull(input, "Input cannot be null");
        Objects.requireNonNull(output, "Output cannot be null");

        OilFactoryRecipe recipe = new OilFactoryRecipe(MineTweakerMC.getLiquidStack(input), MineTweakerMC.getLiquidStack(output), operationTime, energyPerTick);
        OilFactoryRecipeManager.INSTANCE.addRecipe(recipe);
    }
}
