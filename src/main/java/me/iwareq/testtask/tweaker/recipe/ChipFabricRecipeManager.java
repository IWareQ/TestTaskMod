package me.iwareq.testtask.tweaker.recipe;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.core.block.invslot.InvSlot;
import me.iwareq.testtask.common.tile.invslot.InvSlotConsumableChipFabric;
import me.iwareq.testtask.common.tile.invslot.InvSlotLens;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author IWareQ
 */
public class ChipFabricRecipeManager implements IMachineRecipeManager {
    public static final ChipFabricRecipeManager INSTANCE = new ChipFabricRecipeManager();

    private final List<ChipFabricRecipe> recipes = new ArrayList<>();

    public void addRecipe(ChipFabricRecipe recipe) {
        recipes.add(recipe);
    }

    @Override
    public void addRecipe(IRecipeInput input, NBTTagCompound metadata, ItemStack... output) {}

    public ChipFabricRecipe findMatchingRecipe(InvSlotConsumableChipFabric input, InvSlotLens lens) {
        for (ChipFabricRecipe recipe : recipes) {
            if (lens.matches(recipe.getLensStack()) && matchesInputs(recipe, input)) {
                return recipe;
            }
        }

        return null;
    }

    private boolean matchesInputs(ChipFabricRecipe recipe, InvSlotConsumableChipFabric input) {
        List<ItemStack> available = getContents(input);
        for (ItemStack required : recipe.getInputStacks()) {
            boolean matched = false;
            for (ItemStack availableStack : available) {
                if (availableStack != null && required != null && required.isItemEqual(availableStack) && availableStack.stackSize >= required.stackSize) {
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return false;
            }
        }

        return true;
    }

    private List<ItemStack> getContents(InvSlot invSlot) {
        List<ItemStack> result = new ArrayList<>();
        for (int i = 0; i < invSlot.size(); i++) {
            ItemStack stack = invSlot.get(i);
            if (stack != null) {
                result.add(stack.copy());
            }
        }

        return result;
    }

    @Override
    public RecipeOutput getOutputFor(ItemStack input, boolean adjustInput) {
        for (ChipFabricRecipe recipe : recipes) {
            for (ItemStack stack : recipe.getInputStacks()) {
                if (stack != null && input != null && input.isItemEqual(stack)) {
                    return new RecipeOutput(null, recipe.getOutputStack());
                }
            }
        }

        return null;
    }

    public List<ChipFabricRecipe> getRecipes0() {
        return recipes;
    }

    @Override
    public Map<IRecipeInput, RecipeOutput> getRecipes() {
        return new HashMap<>();
    }
}
