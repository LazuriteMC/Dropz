package dev.lazurite.dropz.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

@Environment(EnvType.CLIENT)
public class VersionChecker implements Runnable {
    private static final String header = "{\"translate\": \"version_checker.header\", \"clickEvent\": {\"action\": \"open_url\", \"value\": \"%s\"}, \"color\": \"#616ad6\"}";
    private static final String message = "{\"translate\": \"version_checker.message\", \"clickEvent\": {\"action\": \"open_url\", \"value\": \"%s\"}, \"color\": \"white\"}";

    private final String modid;
    private final String version;
    private final String url;
    private String latestVersion;

    public VersionChecker(String modid, String version, String url) {
        this.modid = modid;
        this.version = version;
        this.url = url;
    }

    @Override
    public void run() {
        InputStream in;

        try {
            in = new URL("https://raw.githubusercontent.com/LazuriteMC/lazuritemc.github.io/master/versions.json").openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JsonObject version_info = ((JsonObject) new JsonParser().parse(new InputStreamReader(in))).getAsJsonObject(modid);
        latestVersion = version_info.get("version").getAsString();
    }

    public void sendPlayerMessage() {
        if (!isLatestVersion()) {
            PlayerEntity player = MinecraftClient.getInstance().player;

            if (player != null) {
                player.sendMessage(Text.Serializer.fromJson(String.format(header, url)).append(Text.Serializer.fromJson(String.format(message, url))), false);
            }
        }
    }

    public boolean isLatestVersion() {
        return latestVersion.equals(version);
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public static VersionChecker getVersion(String modid, String version, String url) {
        VersionChecker out = new VersionChecker(modid, version, url);
        Thread versionCheckThread = new Thread(out, "Version Check");
        versionCheckThread.start();
        return out;
    }
}