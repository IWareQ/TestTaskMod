package me.iwareq.testtask.tweaker.recipe;

import lombok.Getter;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author IWareQ
 */
@Getter
public class OilFactoryRecipe extends MachineRecipe {
    private final FluidStack inputFluid;
    private final FluidStack outputFluid;

    public OilFactoryRecipe(FluidStack inputFluid, FluidStack outputFluid, int operationTime, double energyPerTick) {
        super(operationTime, energyPerTick);
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
    }

    public FluidStack getInputFluid() {
        return this.inputFluid.copy();
    }

    public FluidStack getOutputFluid() {
        return this.outputFluid.copy();
    }
}
