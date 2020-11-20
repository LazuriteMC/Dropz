package dev.lazurite.dropz.client;

import dev.lazurite.dropz.client.render.PhysicsItemRenderer;
import dev.lazurite.dropz.util.VersionChecker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientInitializer implements ClientModInitializer {
    private static final VersionChecker versionChecker = new VersionChecker();

    @Override
    public void onInitializeClient() {
        Thread versionCheckThread = new Thread(versionChecker, "Version Check");
        versionCheckThread.start();

        PhysicsItemRenderer.register();
    }

    public static VersionChecker getVersionChecker() {
        return versionChecker;
    }
}
