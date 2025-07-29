package me.iwareq.testtask.common.tile;

import ic2.core.IHasGui;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import lombok.Getter;
import me.iwareq.testtask.common.config.ModConfig;
import me.iwareq.testtask.common.container.ContainerOilFactory;
import me.iwareq.testtask.common.gui.GuiOilFactory;
import me.iwareq.testtask.tweaker.recipe.OilFactoryRecipe;
import me.iwareq.testtask.tweaker.recipe.OilFactoryRecipeManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author IWareQ
 */
@Getter
public class TileOilFactory extends TileEntityElectricMachine implements IHasGui, IUpgradableBlock, IFluidHandler {
    private final FluidTank inputFluidTank;
    private final FluidTank outputFluidTank;
    private final InvSlotUpgrade upgradesSlot;

    private int progress = 0;
    private float guiProgress = 0;

    public TileOilFactory() {
        super(ModConfig.oilFactoryEnergyStorage, 1, -1);
        this.inputFluidTank = new FluidTank(ModConfig.oilFactoryInputTankCapacity);
        this.outputFluidTank = new FluidTank(ModConfig.oilFactoryOutputTankCapacity);
        this.upgradesSlot = new InvSlotUpgrade(this, "upgrades", -1, 5);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();

        // Колесо изобретать не буду, код с меха из IC2 адаптированный под мои рецепты
        boolean needsInvUpdate = false;

        OilFactoryRecipe recipe = this.getRecipe();
        if (recipe != null) {
            double operationTime = recipe.getOperationTime() * this.upgradesSlot.processTimeMultiplier + this.upgradesSlot.extraProcessTime;
            double energyPerTick = recipe.getEnergyPerTick() * this.upgradesSlot.energyDemandMultiplier + this.upgradesSlot.extraEnergyDemand;

            if (this.energy >= energyPerTick) {
                this.setActive(true);
                this.energy -= energyPerTick;
                ++this.progress;

                if (this.progress >= operationTime) {
                    this.operate(recipe);
                    needsInvUpdate = true;
                    this.progress = 0;
                }
            } else {
                this.setActive(false);
            }
        } else {
            this.progress = 0;
            this.setActive(false);
        }

        for (int i = 0; i < this.upgradesSlot.size(); ++i) {
            ItemStack stack = this.upgradesSlot.get(i);
            if (stack != null && stack.getItem() instanceof IUpgradeItem && ((IUpgradeItem) stack.getItem()).onTick(stack, this)) {
                needsInvUpdate = true;
            }
        }

        double operationTime = recipe == null ? 1 : (recipe.getOperationTime() * this.upgradesSlot.processTimeMultiplier + this.upgradesSlot.extraProcessTime);
        this.guiProgress = (float) this.progress / (float) operationTime;

        if (needsInvUpdate) {
            super.markDirty();
        }
    }

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

    private void operate(OilFactoryRecipe recipe) {
        this.inputFluidTank.drain(recipe.getInputFluid().amount, true);
        this.outputFluidTank.fill(recipe.getOutputFluid(), true);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.progress = nbttagcompound.getInteger("progress");
        this.inputFluidTank.readFromNBT(nbttagcompound.getCompoundTag("inputFluidTank"));
        this.outputFluidTank.readFromNBT(nbttagcompound.getCompoundTag("outputFluidTank"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("progress", this.progress);
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
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public String getInventoryName() {
        return "container.oilFactory";
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public boolean useEnergy(double amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
            return true;
        } else {
            return false;
        }
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
    public void onGuiClosed(EntityPlayer entityPlayer) {}

    @Override
    public ContainerOilFactory getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerOilFactory(entityPlayer, this);
    }

    @Override
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean b) {
        return new GuiOilFactory(getGuiContainer(entityPlayer), this);
    }
}
