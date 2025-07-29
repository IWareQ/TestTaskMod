package me.iwareq.testtask.common.tile;

import ic2.core.block.invslot.InvSlotOutput;
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

import java.util.EnumSet;
import java.util.Set;

/**
 * @author IWareQ
 */
@Getter
public class TileChipFabric extends TileRecipeMachine<ChipFabricRecipe> {
    private final InvSlotConsumableChipFabric inputSlots;
    private final InvSlotLens lensSlot;
    private final InvSlotOutput outputSlot;

    public TileChipFabric() {
        super(ModConfig.chipFabricEnergyStorage, 5);
        this.inputSlots = new InvSlotConsumableChipFabric(this, ChipFabricRecipeManager.INSTANCE);
        this.lensSlot = new InvSlotLens(this, ChipFabricRecipeManager.INSTANCE.getRecipes0());
        this.outputSlot = new InvSlotOutput(this, "output", -1, 1);
    }

    @Override
    public void operate(ChipFabricRecipe recipe) {
        this.inputSlots.consume(recipe);
        this.outputSlot.add(recipe.getOutputStack());
    }

    @Override
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

    @Override
    public String getInventoryName() {
        return "container.chipFabric";
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
    public ContainerChipFabric getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerChipFabric(entityPlayer, this);
    }

    @Override
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean b) {
        return new GuiChipFabric(getGuiContainer(entityPlayer), this);
    }
}
