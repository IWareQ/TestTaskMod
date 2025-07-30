package me.iwareq.testtask.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import lombok.Getter;
import me.iwareq.testtask.common.gui.GuiChipFabric;
import me.iwareq.testtask.tweaker.recipe.ChipFabricRecipe;
import me.iwareq.testtask.tweaker.recipe.ChipFabricRecipeManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

/**
 * @author IWareQ
 */
public class ChipFabricRecipeHandler extends TemplateRecipeHandler {
    private final List<ChipFabricRecipe> recipes;

    public ChipFabricRecipeHandler() {
        this.recipes = ChipFabricRecipeManager.INSTANCE.getRecipes0();
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            for (ChipFabricRecipe recipe : recipes) {
                arecipes.add(new Cached(recipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result == null) {
            return;
        }

        for (ChipFabricRecipe recipe : recipes) {
            if (recipe.getOutputStack().isItemEqual(result) && ItemStack.areItemStackTagsEqual(recipe.getOutputStack(), result)) {
                arecipes.add(new Cached(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient == null) {
            return;
        }

        for (ChipFabricRecipe recipe : recipes) {
            for (ItemStack input : recipe.getInputStacks()) {
                if (input.isItemEqual(ingredient) && ItemStack.areItemStackTagsEqual(input, ingredient)) {
                    arecipes.add(new Cached(recipe));
                    break;
                }
            }
        }
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(x(0), 0, 0, 0, 148, 80);
    }

    @Override
    public void drawExtras(int recipe) {
        drawProgressBar(x(40), 12, 148, 0, 86, 56, 50, 0);
    }

    @Override
    public String getGuiTexture() {
        return "testtaskmod:textures/gui/nei/chip_fabric.png";
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("testtask.nei.chipFabric");
    }

    @Override
    public String getOverlayIdentifier() {
        return "chipfabric";
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiChipFabric.class;
    }

    private static int x(int x) {
        return ((166 - 148) / 2) + x;
    }

    public class Cached extends CachedRecipe {
        @Getter
        private final List<PositionedStack> ingredients = new ArrayList<>();
        private final PositionedStack lens;
        private final PositionedStack output;

        public Cached(ChipFabricRecipe recipe) {
            this.lens = new PositionedStack(recipe.getLensStack(), x(77), 32);

            ItemStack[] inputs = recipe.getInputStacks();
            for (int i = 0; i < inputs.length; i++) {
                int row = i / 2;
                int col = i % 2;
                ingredients.add(new PositionedStack(inputs[i], x(5 + (col * 18)), 5 + (row * 18)));
            }

            this.output = new PositionedStack(recipe.getOutputStack(), x(127), 32);
        }

        @Override
        public PositionedStack getResult() {
            return output;
        }

        @Override
        public PositionedStack getOtherStack() {
            return lens;
        }
    }
}
