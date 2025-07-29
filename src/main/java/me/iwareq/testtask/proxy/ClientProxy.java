package me.iwareq.testtask.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import me.iwareq.testtask.nei.NEIConfig;

/**
 * @author IWareQ
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent ignoredEvent) {
        super.init(ignoredEvent);
        NEIConfig.registerCatalysts();
    }
}
