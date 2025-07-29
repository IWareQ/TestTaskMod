package me.iwareq.testtask.common.tile;

import ic2.core.upgrade.UpgradableProperty;
import lombok.Getter;
import me.iwareq.testtask.common.config.ModConfig;
import me.iwareq.testtask.common.container.ContainerOilFactory;
import me.iwareq.testtask.common.gui.GuiOilFactory;
import me.iwareq.testtask.tweaker.recipe.OilFactoryRecipe;
import me.iwareq.testtask.tweaker.recipe.OilFactoryRecipeManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author IWareQ
 */
@Getter
public class TileOilFactory extends TileRecipeMachine<OilFactoryRecipe> implements IFluidHandler {
    private final FluidTank inputFluidTank;
    private final FluidTank outputFluidTank;

    public TileOilFactory() {
        super(ModConfig.oilFactoryEnergyStorage, 3);
        this.inputFluidTank = new FluidTank(ModConfig.oilFactoryInputTankCapacity);
        this.outputFluidTank = new FluidTank(ModConfig.oilFactoryOutputTankCapacity);
    }

    @Override
    public void operate(OilFactoryRecipe recipe) {
        this.inputFluidTank.drain(recipe.getInputFluid().amount, true);
        this.outputFluidTank.fill(recipe.getOutputFluid(), true);
    }

    @Override
    public OilFactoryRecipe getRecipe() {
        if (this.inputFluidTank.getFluidAmount() == 0) {
            return null;
        }

        OilFactoryRecipe matchingRecipe = OilFactoryRecipeManager.INSTANCE.findMatchingRecipe(this.inputFluidTank);
        if (matchingRecipe == null) {
            return null;
        }

        FluidStack outputFluid = this.outputFluidTank.getFluid();
        return outputFluid == null ||
               (
                       matchingRecipe.getOutputFluid().isFluidEqual(outputFluid) &&
                       this.outputFluidTank.getFluidAmount() + matchingRecipe.getOutputFluid().amount <= this.outputFluidTank.getCapacity()
               ) ? matchingRecipe : null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.inputFluidTank.readFromNBT(nbttagcompound.getCompoundTag("inputFluidTank"));
        this.outputFluidTank.readFromNBT(nbttagcompound.getCompoundTag("outputFluidTank"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setTag("inputFluidTank", this.inputFluidTank.writeToNBT(new NBTTagCompound()));
        nbttagcompound.setTag("outputFluidTank", this.outputFluidTank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return this.canFill(from, resource.getFluid()) ? this.inputFluidTank.fill(resource, doFill) : 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return resource != null && resource.isFluidEqual(this.outputFluidTank.getFluid()) ? (!this.canDrain(from, resource.getFluid()) ? null : this.outputFluidTank.drain(resource.amount, doDrain)) : null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return this.outputFluidTank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection forgeDirection, Fluid fluid) {
        FluidStack fluidStack = this.inputFluidTank.getFluid();
        return fluidStack == null || fluidStack.isFluidEqual(new FluidStack(fluid, 1));
    }

    @Override
    public boolean canDrain(ForgeDirection forgeDirection, Fluid fluid) {
        FluidStack fluidStack = this.outputFluidTank.getFluid();
        return fluidStack != null && fluidStack.isFluidEqual(new FluidStack(fluid, 1));
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{this.inputFluidTank.getInfo(), this.outputFluidTank.getInfo()};
    }

    @Override
    public String getInventoryName() {
        return "container.oilFactory";
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(
                UpgradableProperty.Processing,
                UpgradableProperty.Transformer,
                UpgradableProperty.EnergyStorage,
                UpgradableProperty.FluidConsuming,
                UpgradableProperty.FluidProducing
        );
    }

    @Override
    public ContainerOilFactory getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerOilFactory(entityPlayer, this);
    }

    @Override
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean b) {
        return new GuiOilFactory(getGuiContainer(entityPlayer), this);
    }
}
