package me.iwareq.testtask;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.extern.log4j.Log4j2;
import me.iwareq.testtask.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * @author IWareQ
 */
@Log4j2
@Mod(modid = Tags.MOD_ID, name = "TestTaskMod", version = Tags.VERSION, dependencies =
        "required-after:EnderIO@[1.7.10-2.3.0.430_beta,);" +
        "required-after:Botania@[r1.8-249,);" +
        "required-after:Avaritia@[1.13,);" +
        "required-after:AWWayofTime@[1.7.10-1.3.3-17,)"
)
public class TestTaskMod {
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(CreativeTabs.getNextID(), Tags.MOD_ID) {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Items.emerald;
        }
    };

    @SidedProxy(
            clientSide = "me.iwareq.testtask.proxy.ClientProxy",
            serverSide = "me.iwareq.testtask.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.Instance(Tags.MOD_ID)
    public static TestTaskMod instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
