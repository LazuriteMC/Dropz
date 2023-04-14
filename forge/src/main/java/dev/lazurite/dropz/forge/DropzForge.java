package dev.lazurite.dropz.forge;

import dev.lazurite.dropz.Dropz;
import dev.lazurite.dropz.DropzClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Dropz.MODID)
public class DropzForge {

    public DropzForge() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        Dropz.initialize();
    }

    @SubscribeEvent
    public void onClientInitialize(FMLClientSetupEvent event) {
        DropzClient.initialize();
    }

}
