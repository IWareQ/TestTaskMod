package me.iwareq.testtask.common.gui;

import codechicken.lib.gui.GuiDraw;
import ic2.core.GuiIC2;
import me.iwareq.testtask.Tags;
import me.iwareq.testtask.common.container.ContainerChipFabric;
import me.iwareq.testtask.common.tile.TileChipFabric;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * @author IWareQ
 */
public class GuiChipFabric extends GuiIC2 {
    private static final ResourceLocation BG = new ResourceLocation(Tags.MOD_ID, "textures/gui/container/chip_fabric.png");

    private final TileChipFabric tile;

    public GuiChipFabric(ContainerChipFabric container, TileChipFabric tile) {
        super(container, 176, 184);
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        GuiDraw.drawTexturedModalRect(this.xoffset + 43, this.yoffset + 25, 0, 184, (int) (86 * tile.getGuiProgress()), 56);
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal(tile.getInventoryName());
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return BG;
    }
}
