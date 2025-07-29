package me.iwareq.testtask.common.tile;

import ic2.core.IHasGui;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import lombok.Getter;
import me.iwareq.testtask.common.config.ModConfig;
import me.iwareq.testtask.common.container.ContainerChipFabric;
import me.iwareq.testtask.common.gui.GuiChipFabric;
import me.iwareq.testtask.common.tile.invslot.InvSlotConsumableChipFabric;
import me.iwareq.testtask.common.tile.invslot.InvSlotLens;
import me.iwareq.testtask.tweaker.recipe.ChipFabricRecipe;
import me.iwareq.testtask.tweaker.recipe.ChipFabricRecipeManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author IWareQ
 */
@Getter
public class TileChipFabric extends TileEntityElectricMachine implements IHasGui, IUpgradableBlock {
    private final InvSlotConsumableChipFabric inputSlots;
    private final InvSlotLens lensSlot;
    private final InvSlotOutput outputSlot;
    private final InvSlotUpgrade upgradeSlots;

    private int progress = 0;
    private float guiProgress = 0;

    public TileChipFabric() {
        super(ModConfig.chipFabricEnergyStorage, 1, -1);
        this.inputSlots = new InvSlotConsumableChipFabric(this, ChipFabricRecipeManager.INSTANCE);
        this.lensSlot = new InvSlotLens(this, ChipFabricRecipeManager.INSTANCE.getRecipes0());
        this.outputSlot = new InvSlotOutput(this, "output", -1, 1);
        this.upgradeSlots = new InvSlotUpgrade(this, "upgrades", -1, 5);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();

        // Колесо изобретать не буду, код с меха из IC2 адаптированный под мои рецепты
        boolean needsInvUpdate = false;

        ChipFabricRecipe recipe = this.getRecipe();
        if (recipe != null) {
            double operationTime = recipe.getOperationTime() * this.upgradeSlots.processTimeMultiplier + this.upgradeSlots.extraProcessTime;
            double energyPerTick = recipe.getEnergyPerTick() * this.upgradeSlots.energyDemandMultiplier + this.upgradeSlots.extraEnergyDemand;

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

        for (int i = 0; i < this.upgradeSlots.size(); ++i) {
            ItemStack stack = this.upgradeSlots.get(i);
            if (stack != null && stack.getItem() instanceof IUpgradeItem && ((IUpgradeItem) stack.getItem()).onTick(stack, this)) {
                needsInvUpdate = true;
            }
        }

        double operationTime = recipe == null ? 1 : (recipe.getOperationTime() * this.upgradeSlots.processTimeMultiplier + this.upgradeSlots.extraProcessTime);
        this.guiProgress = (float) this.progress / (float) operationTime;

        if (needsInvUpdate) {
            super.markDirty();
        }
    }

    public ChipFabricRecipe getRecipe() {
        if (this.inputSlots.isEmpty() || this.lensSlot.isEmpty()) {
            return null;
        }

        ChipFabricRecipe matchingRecipe = ChipFabricRecipeManager.INSTANCE.findMatchingRecipe(this.inputSlots, this.lensSlot);
        if (matchingRecipe == null) {
            return null;
        }

        return this.outputSlot.canAdd(matchingRecipe.getOutputStack()) ? matchingRecipe : null;
    }

    private void operate(ChipFabricRecipe recipe) {
        this.inputSlots.consume(recipe);
        this.outputSlot.add(recipe.getOutputStack());
    }

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
    public String getInventoryName() {
        return "container.chipFabric";
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
                UpgradableProperty.ItemConsuming,
                UpgradableProperty.ItemProducing
        );
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {}

    @Override
    public ContainerChipFabric getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerChipFabric(entityPlayer, this);
    }

    @Override
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean b) {
        return new GuiChipFabric(getGuiContainer(entityPlayer), this);
    }
}
