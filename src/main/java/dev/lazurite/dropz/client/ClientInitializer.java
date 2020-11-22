package dev.lazurite.dropz.client;

import dev.lazurite.dropz.client.render.PhysicsItemRenderer;
import dev.lazurite.dropz.server.ServerInitializer;
import dev.lazurite.dropz.util.VersionChecker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientInitializer implements ClientModInitializer {
    private static VersionChecker versionChecker;

    @Override
    public void onInitializeClient() {
        PhysicsItemRenderer.register();
        versionChecker = VersionChecker.getVersion(ServerInitializer.MODID, ServerInitializer.VERSION, ServerInitializer.URL);
    }

    public static VersionChecker getVersionChecker() {
        return versionChecker;
    }
}
