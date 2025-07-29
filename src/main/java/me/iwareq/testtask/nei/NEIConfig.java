package me.iwareq.testtask.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.*;
import me.iwareq.testtask.Tags;
import me.iwareq.testtask.common.ModBlocks;
import net.minecraft.item.ItemStack;

/**
 * @author IWareQ
 */
public class NEIConfig implements IConfigureNEI {
    @Override
    public void loadConfig() {
        registerHandler(new ChipFabricRecipeHandler());
        registerHandler(new OilFactoryRecipeHandler());
    }

    private void registerHandler(TemplateRecipeHandler recipeHandler) {
        API.registerRecipeHandler(recipeHandler);
        API.registerUsageHandler(recipeHandler);
    }

    @Override
    public String getName() {
        return "TestTask";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }

    public static void registerCatalysts() {
        registerCatalystHandler("chipfabric", new ItemStack(ModBlocks.chipFabric));
        registerCatalystHandler("oilfactory", new ItemStack(ModBlocks.oilFactory));
    }

    private static void registerCatalystHandler(String name, ItemStack visibleItem) {
        HandlerInfo handlerInfo = new HandlerInfo(name, "TestTask", Tags.MOD_ID, true, null);
        handlerInfo.setHandlerDimensions(HandlerInfo.DEFAULT_HEIGHT + 16, HandlerInfo.DEFAULT_WIDTH, 4);
        handlerInfo.setItem(Tags.MOD_ID + ":" + name, null);
        GuiRecipeTab.handlerAdderFromIMC.put(name, handlerInfo);

        RecipeCatalysts.addRecipeCatalyst(name, new CatalystInfo(visibleItem, 0));
    }
}
