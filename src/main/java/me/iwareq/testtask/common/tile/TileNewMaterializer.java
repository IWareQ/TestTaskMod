package me.iwareq.testtask.common.tile;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBloodOrb;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import fox.spiteful.avaritia.compat.bloodmagic.ItemOrbArmok;
import lombok.Getter;
import me.iwareq.testtask.common.config.NewMaterializerConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.Arrays;

/**
 * @author IWareQ
 */
public class TileNewMaterializer extends TileEntity implements IInventory, IFluidHandler {
    private static final NewMaterializerConfig CONFIG = NewMaterializerConfig.getInstance();

    private final ItemStack[] inventory = new ItemStack[1];

    @Getter
    private final FluidTank tank = new FluidTank(CONFIG.getCapacity());

    @Override
    public void updateEntity() {
        ItemStack orb = getStackInSlot(0);
        if (orb == null || !(orb.getItem() instanceof IBloodOrb)) {
            return;
        }

        if (isArmokOrbPresent()) {
            if (tank.getFluidAmount() != tank.getCapacity()) {
                tank.fill(new FluidStack(AlchemicalWizardry.lifeEssenceFluid, tank.getCapacity()), true);
            }
        } else if (orb.hasTagCompound()) {
            String ownerName = orb.getTagCompound().getString("ownerName");

            int toExtract = CONFIG.getExtraAmountPerTick();
            if (SoulNetworkHandler.getCurrentEssence(ownerName) >= toExtract && tank.getFluidAmount() + toExtract <= tank.getCapacity()) {
                SoulNetworkHandler.syphonFromNetwork(ownerName, toExtract);
                tank.fill(new FluidStack(AlchemicalWizardry.lifeEssenceFluid, toExtract), true);
            }
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeNBTCustom(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        Arrays.fill(inventory, null);
        NBTTagList items = compound.getTagList("Items", 10);
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound itemTag = items.getCompoundTagAt(i);
            int slot = itemTag.getShort("Slot");
            if (slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }

        tank.readFromNBT(compound.getCompoundTag("Tank"));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setShort("Slot", (short) i);
                inventory[i].writeToNBT(itemTag);
                items.appendTag(itemTag);
            }
        }
        compound.setTag("Items", items);

        writeNBTCustom(compound);
    }

    protected void writeNBTCustom(NBTTagCompound compound) {
        compound.setTag("Tank", tank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        if (slotIn >= 0 && slotIn < inventory.length) {
            return inventory[slotIn];
        }

        return null;

    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemStack = getStackInSlot(index);
        if (itemStack == null) {
            return null;
        }

        if (itemStack.stackSize <= count) {
            this.inventory[index] = null;
        } else {
            ItemStack oldStack = itemStack;
            itemStack = oldStack.splitStack(count);
            if (oldStack.stackSize == 0) {
                this.inventory[index] = null;
            }
        }

        this.markDirty();
        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack item = getStackInSlot(index);
        if (item != null) {
            setInventorySlotContents(index, null);
        }

        return item;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= inventory.length) {
            return;
        }

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        inventory[index] = stack;

        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "container.newMaterializer";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
               && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (isArmokOrbPresent()) {
            if (resource == null || resource.getFluid() != AlchemicalWizardry.lifeEssenceFluid) {
                return null;
            }

            return new FluidStack(resource.getFluid(), resource.amount);
        }

        FluidStack drained = tank.drain(resource.amount, doDrain);
        if (doDrain && drained != null) {
            markDirty();
        }
        return drained;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (isArmokOrbPresent()) {
            return new FluidStack(AlchemicalWizardry.lifeEssenceFluid, maxDrain);
        }

        FluidStack drained = tank.drain(maxDrain, doDrain);
        if (doDrain && drained != null) {
            markDirty();
        }
        return drained;
    }

    protected boolean isArmokOrbPresent() {
        ItemStack orb = inventory[0];
        return orb != null && orb.getItem() instanceof ItemOrbArmok;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{tank.getInfo()};
    }
}
