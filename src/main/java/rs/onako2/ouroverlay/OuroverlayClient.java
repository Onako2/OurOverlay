package rs.onako2.ouroverlay;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.google.gson.Gson;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import rs.onako2.ouroverlay.json.ConfigJson;
import rs.onako2.ouroverlay.json.WebEntry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class OuroverlayClient implements ClientModInitializer {

    public static final String PATH = "ouroverlay/";
    public static final File SETTINGS_FILE = new File(PATH + "settings.json");
    public static final File CURRENT_URL = new File(PATH + "currenturl");
    public static final Gson gson = new Gson();
    public static final Map<String, String> webEntries = new HashMap<>();
    public static MCEFBrowser browser;
    public static boolean isVisible = true;
    public static List<WebEntry> webEntryList;
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
        webEntryList = Arrays.asList(settings.entries);
        webEntries.clear();
        webEntryList.forEach(webEntry -> {
            webEntries.put(webEntry.name, webEntry.url);
        });
        url = webEntryList.getFirst().url;
        try {
            if (CURRENT_URL.createNewFile()) {
                Files.writeString(CURRENT_URL.toPath(), url);
            }
            url = Files.readString(CURRENT_URL.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInitializeClient() {

        File base = new File(PATH);

        base.mkdirs();

        try {
            if (SETTINGS_FILE.createNewFile()) {
                WebEntry[] webEntries = new WebEntry[]{
                        new WebEntry("Standard", "http://overlay.united-island.de:3000/")
                };
                ConfigJson defaultSettings = new ConfigJson(1, 1, webEntries);

                Files.writeString(SETTINGS_FILE.toPath(), gson.toJson(defaultSettings, ConfigJson.class), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager
                            .literal("toggleoverlay")
                            .executes(context -> {
                                isVisible = !isVisible;
                                return 0;
                            }));
            dispatcher.register(
                    ClientCommandManager
                            .literal("reloadoverlay")
                            .executes(context -> {
                                reload();
                                browser.loadURL(url);
                                browser.setZoomLevel(OuroverlayClient.getZoom());
                                return 0;
                            }));
            dispatcher.register(
                    ClientCommandManager.literal("setoverlay")
                            .then(argument("urlname", StringArgumentType.string())
                                    .executes(context -> {
                                        String urlNameArg = context.getArgument("urlname", String.class);
                                        String urlArg = OuroverlayClient.webEntries.get(urlNameArg);
                                        if (urlArg == null) {
                                            throw new IllegalArgumentException("No such URL entry exists for the given name: " + urlNameArg);
                                        }
                                        try {
                                            Files.writeString(OuroverlayClient.CURRENT_URL.toPath(), urlArg);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        browser.loadURL(urlArg);
                                        return 0;
                                    })
                            )
            );
        });

        if (!MCEF.isInitialized()) {
            MCEF.initialize();
        }

        reload();
    }
}
