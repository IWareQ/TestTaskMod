package me.iwareq.testtask.common.gui;

import com.enderio.core.client.render.RenderUtil;
import me.iwareq.testtask.Tags;
import me.iwareq.testtask.common.container.ContainerNewMaterializer;
import me.iwareq.testtask.common.tileentity.TileNewMaterializer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

import static codechicken.lib.render.FontUtils.fontRenderer;

/**
 * @author IWareQ
 */
public class GuiNewMaterializer extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/gui/container/new_materializer.png");

    private final TileNewMaterializer tile;

    public GuiNewMaterializer(ContainerNewMaterializer container, TileNewMaterializer tile) {
        super(container);
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.getTextureManager().bindTexture(TEXTURE);

        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);

        FluidTank tank = tile.getTank();
        RenderUtil.renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), left + 132, top + 21, 1, 30, 46);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format(tile.getInventoryName());
        fontRenderer.drawString(title, (xSize / 2) - (fontRenderer.getStringWidth(title) / 2), 6, 4210752, false);


        FluidTank tank = tile.getTank();
        String fluidInfo = tank.getFluidAmount() + "/" + tank.getCapacity() + " mB";
        fontRenderer.drawString(fluidInfo, (xSize / 2) - (fontRenderer.getStringWidth(fluidInfo) / 2), 56, 4210752, false);

        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }
}
