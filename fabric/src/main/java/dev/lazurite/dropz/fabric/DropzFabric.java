package dev.lazurite.dropz.fabric;

import dev.lazurite.dropz.Dropz;
import net.fabricmc.api.ModInitializer;

public class DropzFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Dropz.initialize();
    }

}
