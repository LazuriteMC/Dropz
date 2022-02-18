package dev.lazurite.dropz.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Settings(onlyAnnotated = true)
public final class Config {
    private static final Config instance = new Config();

    @Setting public float yeetMultiplier;
    @Setting public boolean doItemCombination;
    @Setting public boolean doBuoyancy;

    private Config() {
        this.yeetMultiplier = 1.0f;
        this.doItemCombination = true;
        this.doBuoyancy = true;
    }

    public static Config getInstance() {
        return instance;
    }

    @ExpectPlatform
    public static Path getPath() {
        throw new AssertionError();
    }

    public void load() {
        final var path = getPath();

        if (Files.exists(path)) {
            try {
                FiberSerialization.deserialize(
                        ConfigTree.builder().applyFromPojo(instance, AnnotatedSettings.builder().build()).build(),
                        Files.newInputStream(path),
                        new JanksonValueSerializer(false)
                );
            } catch (IOException | FiberException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FiberSerialization.serialize(
                        ConfigTree.builder().applyFromPojo(instance, AnnotatedSettings.builder().build()).build(),
                        Files.newOutputStream(path),
                        new JanksonValueSerializer(false)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
