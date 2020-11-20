package dev.lazurite.dropz.util;

import dev.lazurite.dropz.server.ServerInitializer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class VersionChecker implements Runnable {
    private static String latestVersion;

    @Override
    public void run() {
        InputStream in = null;

        try {
            in = new URL("https://raw.githubusercontent.com/LazuriteMC/Dropz/main/gradle.properties").openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> lines = null;
        try {
            if (in != null) {
                lines = IOUtils.readLines(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }

        for (String line : lines) {
            if (line.contains("mod_version")) {
                latestVersion = line.substring(line.replaceAll("\\s+", "").indexOf('='));
                break;
            }
        }

        System.out.println("Latest mod version is " + latestVersion);
    }

    public void sendChatMessage() {

    }

    public boolean isLatestVersion() {
        return latestVersion.equals(ServerInitializer.MODID);
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}