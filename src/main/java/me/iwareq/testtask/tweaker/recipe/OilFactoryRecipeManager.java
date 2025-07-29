package me.iwareq.testtask.tweaker.recipe;

import lombok.Getter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.ArrayList;
import java.util.List;

/**
 * @author IWareQ
 */
public class OilFactoryRecipeManager {
    public static final OilFactoryRecipeManager INSTANCE = new OilFactoryRecipeManager();

    @Getter
    private final List<OilFactoryRecipe> recipes = new ArrayList<>();

    public void addRecipe(OilFactoryRecipe recipe) {
        recipes.add(recipe);
    }

    public OilFactoryRecipe findMatchingRecipe(FluidTank input) {
        for (OilFactoryRecipe recipe : recipes) {
            FluidStack requiredFluid = recipe.getInputFluid();
            if (requiredFluid.isFluidEqual(input.getFluid()) && input.getFluidAmount() >= requiredFluid.amount) {
                return recipe;
            }
        }

        return null;
    }
}
