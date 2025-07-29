package me.iwareq.testtask.common.container;

import ic2.core.ContainerFullInv;
import ic2.core.slot.SlotInvSlot;
import me.iwareq.testtask.common.tile.TileChipFabric;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * @author IWareQ
 */
public class ContainerChipFabric extends ContainerFullInv<TileChipFabric> {
    public ContainerChipFabric(EntityPlayer player, TileChipFabric tile) {
        super(player, tile, 184);

        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlotToContainer(new SlotInvSlot(tile.getInputSlots(), index++, 8 + (j * 18), 18 + (i * 18)));
            }
        }

        this.addSlotToContainer(new SlotInvSlot(tile.getLensSlot(), 0, 80, 45));

        this.addSlotToContainer(new SlotInvSlot(tile.getOutputSlot(), 0, 130, 45));

        for (int i = 0; i < 5; i++) {
            this.addSlotToContainer(new SlotInvSlot(tile.getUpgradeSlots(), i, 152, 9 + (i * 18)));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> fields = super.getNetworkedFields();
        fields.add("progress");
        fields.add("guiProgress");
        return fields;
    }
}
