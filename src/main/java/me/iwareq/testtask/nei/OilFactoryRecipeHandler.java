package me.iwareq.testtask.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.FluidUtil;
import crazypants.enderio.fluid.Fluids;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import me.iwareq.testtask.common.config.ModConfig;
import me.iwareq.testtask.common.gui.GuiOilFactory;
import me.iwareq.testtask.tweaker.recipe.OilFactoryRecipe;
import me.iwareq.testtask.tweaker.recipe.OilFactoryRecipeManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

/**
 * @author IWareQ
 */
public class OilFactoryRecipeHandler extends TemplateRecipeHandler {
    private static final int GUI_WIDTH = 101;

    private static final int TANK_WIDTH = 30;
    private static final int TANK_HEIGHT = 46;

    private final List<OilFactoryRecipe> recipes;

    public OilFactoryRecipeHandler() {
        this.recipes = OilFactoryRecipeManager.INSTANCE.getRecipes();
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("liquid")) {
            loadCraftingRecipes((FluidStack) results[0]);
        } else if (outputId.equals(getOverlayIdentifier())) {
            for (OilFactoryRecipe recipe : this.recipes) {
                this.arecipes.add(new Cached(recipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        FluidStack fluid = FluidUtil.getFluidFromItem(result);
        if (fluid != null) {
            this.loadCraftingRecipes(fluid);
        }
    }

    private void loadCraftingRecipes(FluidStack result) {
        if (result == null) {
            return;
        }

        for (OilFactoryRecipe recipe : this.recipes) {
            FluidStack output = recipe.getOutputFluid();
            if (output.isFluidEqual(result)) {
                this.arecipes.add(new Cached(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("liquid")) {
            this.loadUsageRecipes((FluidStack) ingredients[0]);
        } else {
            super.loadUsageRecipes(inputId, ingredients);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        FluidStack fluid = FluidUtil.getFluidFromItem(ingredient);
        if (fluid != null) {
            this.loadUsageRecipes(fluid);
        }
    }

    private void loadUsageRecipes(FluidStack ingredient) {
        if (ingredient == null) {
            return;
        }

        for (OilFactoryRecipe recipe : this.recipes) {
            if (recipe.getInputFluid().isFluidEqual(ingredient)) {
                this.arecipes.add(new Cached(recipe));
            }
        }
    }

    @Override
    public void drawBackground(int recipeId) {
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(x(0), 0, 0, 0, GUI_WIDTH, 56);
    }

    @Override
    public void drawExtras(int recipeId) {
        drawProgressBar(x(42), 24, 101, 0, 17, 7, 50, 0);

        Cached cachedRecipe = (Cached) arecipes.get(recipeId);
        FluidStack inputFluid = cachedRecipe.getInputFluid();
        RenderUtil.renderGuiTank(inputFluid, ModConfig.oilFactoryInputTankCapacity, inputFluid.amount, x(5), 5, 1, TANK_WIDTH, TANK_HEIGHT);

        FluidStack outputFluid = cachedRecipe.getOutputFluid();
        RenderUtil.renderGuiTank(outputFluid, ModConfig.oilFactoryOutputTankCapacity, outputFluid.amount, x(66), 5, 1, TANK_WIDTH, TANK_HEIGHT);
    }

    @Override
    public List<String> handleTooltip(GuiRecipe<?> gui, List<String> currentTip, int recipeId) {
        Cached cachedRecipe = (Cached) arecipes.get(recipeId);
        if (this.isHover(gui, recipeId, x(5), 5, TANK_WIDTH, TANK_HEIGHT)) {
            FluidStack inputFluid = cachedRecipe.getInputFluid();
            currentTip.add(inputFluid.getLocalizedName());
            currentTip.add(EnumChatFormatting.GRAY.toString() + inputFluid.amount + " " + Fluids.MB());
        }

        if (this.isHover(gui, recipeId, x(66), 5, TANK_WIDTH, TANK_HEIGHT)) {
            FluidStack outputFluid = cachedRecipe.getOutputFluid();
            currentTip.add(outputFluid.getLocalizedName());
            currentTip.add(EnumChatFormatting.GRAY.toString() + outputFluid.amount + " " + Fluids.MB());
        }

        return super.handleTooltip(gui, currentTip, recipeId);
    }

    private boolean isHover(GuiRecipe<?> gui, int recipeId, int x, int y, int w, int h) {
        Point mouse = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipeId);
        int relX = mouse.x - gui.guiLeft - offset.x;
        int relY = mouse.y - gui.guiTop - offset.y;
        return relX >= x && relY >= y && relX <= x + w && relY <= y + h;
    }

    @Override
    public String getGuiTexture() {
        return "testtaskmod:textures/gui/nei/oil_factory.png";
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("testtask.nei.oilFactory");
    }

    @Override
    public String getOverlayIdentifier() {
        return "oilfactory";
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiOilFactory.class;
    }

    private static int x(int x) {
        return ((166 - GUI_WIDTH) / 2) + x;
    }

    @AllArgsConstructor
    public class Cached extends CachedRecipe {
        @Delegate
        private final OilFactoryRecipe recipe;

        @Override
        public PositionedStack getResult() {
            return null;
        }
    }
}
