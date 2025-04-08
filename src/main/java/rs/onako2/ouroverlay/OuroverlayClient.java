package rs.onako2.ouroverlay;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.google.gson.Gson;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import rs.onako2.ouroverlay.json.ConfigJson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class OuroverlayClient implements ClientModInitializer {

    public static final String PATH = "ouroverlay/";
    public static final File SETTINGS_FILE = new File(PATH + "settings.json");
    public static final Gson gson = new Gson();
    public static MCEFBrowser browser;
    public static boolean isVisible = true;
    private static String url;
    private static int version;
    private static double zoom;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        OuroverlayClient.url = url;
    }

    public static int getVersion() {
        return version;
    }

    public static void setVersion(int version) {
        OuroverlayClient.version = version;
    }

    public static double getZoom() {
        return zoom;
    }

    public static void setZoom(double zoom) {
        OuroverlayClient.zoom = zoom;
    }

    public static void reload() {
        String settingsString;
        try {
            settingsString = Files.readString(SETTINGS_FILE.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ConfigJson settings = gson.fromJson(settingsString, ConfigJson.class);

        version = settings.version;
        zoom = settings.zoom;
        url = settings.url;
    }

    @Override
    public void onInitializeClient() {

        File base = new File(PATH);

        base.mkdirs();

        try {
            if (SETTINGS_FILE.createNewFile()) {

                ConfigJson defaultSettings = new ConfigJson(0, 1, "http://overlay.united-island.de:3000/");

                Files.writeString(SETTINGS_FILE.toPath(), gson.toJson(defaultSettings, ConfigJson.class), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("toggleoverlay").executes(context -> {
                        isVisible = !isVisible;
                        return 0;
                    }));
            dispatcher.register(
                    ClientCommandManager.literal("reloadoverlay").executes(context -> {
                        reload();
                        browser.loadURL(url);
                        browser.setZoomLevel(OuroverlayClient.getZoom());
                        return 0;
                    }));
        });

        if (!MCEF.isInitialized()) {
            MCEF.initialize();
        }

        reload();
    }
}
