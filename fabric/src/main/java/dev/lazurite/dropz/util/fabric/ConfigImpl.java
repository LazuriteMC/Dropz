package dev.lazurite.dropz.util.fabric;

import dev.lazurite.dropz.Dropz;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ConfigImpl {
    public static Path getPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(Dropz.MODID + ".json");
    }
}
