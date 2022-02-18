package dev.lazurite.dropz.util.forge;

import dev.lazurite.dropz.Dropz;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;

public class ConfigImpl {
    public static Path getPath() {
        return FMLLoader.getGamePath().resolve("config").resolve(Dropz.MODID + ".json");
    }
}