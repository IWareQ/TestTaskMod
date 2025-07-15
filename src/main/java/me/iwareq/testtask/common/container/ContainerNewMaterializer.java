package me.iwareq.testtask.common.container;

import WayofTime.alchemicalWizardry.api.items.interfaces.IBloodOrb;
import me.iwareq.testtask.common.tileentity.TileNewMaterializer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author IWareQ
 */
public class ContainerNewMaterializer extends Container {
    private final IInventory tileInventory;

    public ContainerNewMaterializer(InventoryPlayer playerInv, TileNewMaterializer tile) {
        this.tileInventory = tile;

        this.addSlotToContainer(new Slot(tile, 0, 80, 36) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.getItem() instanceof IBloodOrb;
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(index);

        if (slot == null || !slot.getHasStack()) {
            return stack;
        }

        ItemStack stackInSlot = slot.getStack();
        stack = stackInSlot.copy();

        if (index == 0) {
            if (!mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
                return null;
            }
        } else {
            if (stackInSlot.getItem() instanceof IBloodOrb && !mergeItemStack(stackInSlot, 0, 1, false)) {
                return null;
            }
        }

        if (stackInSlot.stackSize == 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }

        if (stackInSlot.stackSize == stack.stackSize) {
            return null;
        }

        slot.onPickupFromSlot(player, stackInSlot);
        return stack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileInventory.isUseableByPlayer(player);
    }
}
