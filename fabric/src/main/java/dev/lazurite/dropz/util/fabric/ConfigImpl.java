package dev.lazurite.dropz.util.fabric;

import dev.lazurite.dropz.Dropz;
import dev.lazurite.dropz.util.Config;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * @see Config
 */
public class ConfigImpl {

    /**
     * @see Config#getPath
     */
    public static Path getPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(Dropz.MODID + ".json");
    }

}
