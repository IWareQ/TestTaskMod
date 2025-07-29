package me.iwareq.testtask.tweaker.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author IWareQ
 */
@Getter
@AllArgsConstructor
public class OilFactoryRecipe {
    private final FluidStack inputFluid;
    private final FluidStack outputFluid;
    private final int operationTime;
    private final double energyPerTick;

    public FluidStack getInputFluid() {
        return this.inputFluid.copy();
    }

    public FluidStack getOutputFluid() {
        return this.outputFluid.copy();
    }
}
