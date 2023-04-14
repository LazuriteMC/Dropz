package dev.lazurite.dropz.fabric;

import dev.lazurite.dropz.Dropz;
import dev.lazurite.dropz.DropzClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class DropzFabric implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitialize() {
        Dropz.initialize();
    }

    @Override
    public void onInitializeClient() {
        DropzClient.initialize();
    }

}
