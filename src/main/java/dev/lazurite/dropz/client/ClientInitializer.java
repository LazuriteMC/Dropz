package dev.lazurite.dropz.client;

import dev.lazurite.dropz.client.render.PhysicsItemRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PhysicsItemRenderer.register();
    }
}
