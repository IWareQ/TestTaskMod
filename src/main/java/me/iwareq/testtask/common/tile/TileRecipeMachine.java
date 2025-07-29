package me.iwareq.testtask.common.tile;

import ic2.core.IHasGui;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import lombok.Getter;
import me.iwareq.testtask.tweaker.recipe.MachineRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

/**
 * @author IWareQ
 */
@Getter
public abstract class TileRecipeMachine<T extends MachineRecipe> extends TileEntityElectricMachine implements IHasGui, IUpgradableBlock {
    private final InvSlotUpgrade upgradesSlot;

    private int progress = 0;
    private float guiProgress = 0;

    public TileRecipeMachine(int maxEnergy, int upgradesCount) {
        super(maxEnergy, 1, -1);
        this.upgradesSlot = new InvSlotUpgrade(this, "upgrades", -1, upgradesCount);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();

        // Колесо изобретать не буду, код с меха из IC2 адаптированный под мои рецепты
        boolean needsInvUpdate = false;

        T recipe = this.getRecipe();
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

    public abstract T getRecipe();

    public abstract void operate(T recipe);

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.progress = nbttagcompound.getInteger("progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("progress", this.progress);
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
    public void onGuiClosed(EntityPlayer entityPlayer) {}

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
    public double getEnergy() {
        return this.energy;
    }
}
