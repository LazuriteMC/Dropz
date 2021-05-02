package dev.lazurite.dropz.config;

import dev.lazurite.dropz.util.YeetType;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Settings(onlyAnnotated = true)
public final class Config {
    private static final Config instance = new Config();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("dropz.json");

    @Setting public YeetType yeetMultiplier;
    @Setting public boolean merge;

    private Config() {
        /* Defaults */
        yeetMultiplier = YeetType.MEDIUM;
        merge = true;
    }

    public static Config getInstance() {
        return instance;
    }

    public void load() {
        if (Files.exists(PATH)) {
            try {
                FiberSerialization.deserialize(
                        ConfigTree.builder().applyFromPojo(instance, AnnotatedSettings.builder().build()).build(),
                        Files.newInputStream(PATH),
                        new JanksonValueSerializer(false)
                );
            } catch (IOException | FiberException e) {
                e.printStackTrace();
            }
        } else {
            this.save();
        }
    }

    public void save() {
        try {
            FiberSerialization.serialize(
                    ConfigTree.builder().applyFromPojo(instance, AnnotatedSettings.builder().build()).build(),
                    Files.newOutputStream(PATH),
                    new JanksonValueSerializer(false)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}