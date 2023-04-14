package dev.lazurite.dropz.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.dropz.Dropz;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {

    static {
        read();
    }

    public static boolean dropzEnabled = true;
    public static float yeetMultiplier = 1.0f;
    public static boolean doItemCombination = true;
    public static boolean doBuoyancy = true;

    @ExpectPlatform
    public static Path getPath() {
        throw new AssertionError();
    }

    public static void write() {
        var path = getPath();
        var config = new JsonObject();

        config.addProperty("yeetMultiplier", yeetMultiplier);
        config.addProperty("doItemCombination", doItemCombination);
        config.addProperty("doBuoyancy", doBuoyancy);

        try {
            Files.writeString(path, config.toString());
        } catch (IOException e) {
            Dropz.LOGGER.warn("Failed to write config file at: " + path.toAbsolutePath().toString());
        }
    }

    public static void read() {
        var path = getPath();

        if (!Files.exists(path)) {
            write();
            return;
        }

        try {
            var config = JsonParser.parseReader(new InputStreamReader(Files.newInputStream(path))).getAsJsonObject();
            yeetMultiplier = config.get("yeetMultiplier").getAsFloat();
            doItemCombination = config.get("doItemCombination").getAsBoolean();
            doBuoyancy = config.get("doBuoyancy").getAsBoolean();
        } catch (IOException e) {
            Dropz.LOGGER.warn("Failed to read config file at: " + path.toAbsolutePath().toString());
        }
    }

}
