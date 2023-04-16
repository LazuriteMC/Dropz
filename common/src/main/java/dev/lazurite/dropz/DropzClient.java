package dev.lazurite.dropz;

import dev.lazurite.dropz.util.Config;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.toolbox.api.event.ClientEvents;

public class DropzClient {

    public static void initialize() {
        ClientEvents.Tick.END_LEVEL_TICK.register(client -> {
            Config.dropzEnabled = Rayon.serverHasRayon();
        });
    }

}
