package dev.lazurite.dropz.util.forge;

import dev.lazurite.dropz.Dropz;
import dev.lazurite.dropz.util.Config;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;

/**
 * @see Config
 */
public class ConfigImpl {

    /**
     * @see Config#getPath
     */
    public static Path getPath() {
        return FMLLoader.getGamePath().resolve("config").resolve(Dropz.MODID + ".json");
    }

}