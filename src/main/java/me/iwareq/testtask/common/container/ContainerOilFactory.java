package me.iwareq.testtask.common.container;

import ic2.core.ContainerFullInv;
import ic2.core.slot.SlotInvSlot;
import me.iwareq.testtask.common.tile.TileOilFactory;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * @author IWareQ
 */
public class ContainerOilFactory extends ContainerFullInv<TileOilFactory> {
    public ContainerOilFactory(EntityPlayer player, TileOilFactory tile) {
        super(player, tile, 166);

        for (int i = 0; i < 3; i++) {
            this.addSlotToContainer(new SlotInvSlot(tile.getUpgradesSlot(), i, 152, 18 + (i * 18)));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> fields = super.getNetworkedFields();
        fields.add("guiProgress");
        fields.add("inputFluidTank");
        fields.add("outputFluidTank");
        return fields;
    }
}
