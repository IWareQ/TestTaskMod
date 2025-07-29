package me.iwareq.testtask.common.gui;

import codechicken.lib.gui.GuiDraw;
import com.enderio.core.client.render.RenderUtil;
import ic2.core.GuiIC2;
import ic2.core.util.GuiTooltipHelper;
import me.iwareq.testtask.Tags;
import me.iwareq.testtask.common.container.ContainerOilFactory;
import me.iwareq.testtask.common.tile.TileOilFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author IWareQ
 */
public class GuiOilFactory extends GuiIC2 {
    private static final ResourceLocation BG = new ResourceLocation(Tags.MOD_ID, "textures/gui/container/oil_factory.png");

    private final TileOilFactory tile;

    public GuiOilFactory(ContainerOilFactory container, TileOilFactory tile) {
        super(container, 176, 166);
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        GuiDraw.drawTexturedModalRect(this.xoffset + 79, this.yoffset + 40, 176, 0, (int) (17 * tile.getGuiProgress()), 7);

        RenderUtil.renderGuiTank(tile.getInputFluidTank(), this.xoffset + 42, this.yoffset + 21, 1, 30, 46);
        RenderUtil.renderGuiTank(tile.getOutputFluidTank(), this.xoffset + 103, this.yoffset + 21, 1, 30, 46);

        drawTankTooltip(tile.getInputFluidTank().getFluid(), 42, 21, mouseX, mouseY);
        drawTankTooltip(tile.getOutputFluidTank().getFluid(), 103, 21, mouseX, mouseY);
    }

    private void drawTankTooltip(FluidStack fluid, int x, int y, int mouseX, int mouseY) {
        String tooltip = fluid != null
                ? fluid.getLocalizedName() + ": " + fluid.amount + " / " + tile.getInputFluidTank().getCapacity() + " mB"
                : StatCollector.translateToLocal("testtask.gui.empty");
        GuiTooltipHelper.drawAreaTooltip(
                mouseX, mouseY, tooltip,
                this.xoffset + x, this.yoffset + y,
                this.xoffset + x + 30, this.yoffset + y + 46
        );
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
