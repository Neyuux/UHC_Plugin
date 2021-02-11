package fr.neyuux.uhc.util;

import fr.neyuux.uhc.Index;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.logging.Level;

public class PlayerSkin {

    String uuid;
    String name;
    String value;
    String signatur;

    public PlayerSkin(String uuid) {
        this.uuid = uuid;
        load();
    }

    @SuppressWarnings("resource")
    private void load() {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");
            uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            uc.addRequestProperty("Pragma", "no-cache");

            // Parse it
            String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray properties = (JSONArray) ((JSONObject) obj).get("properties");
            for (Object o : properties) {
                try {
                    JSONObject property = (JSONObject) o;
                    String name = (String) property.get("name");
                    String value = (String) property.get("value");
                    String signature = property.containsKey("signature") ? (String) property.get("signature") : null;

                    this.name = name;
                    this.value = value;
                    this.signatur = signature;

                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to apply auth property", e);
                    Bukkit.broadcastMessage(Index.getInstance().getPrefix() + "�cErreur lors du chargement d'un skin.");
                }
            }
            uc.setConnectTimeout(0);
            uc.getInputStream().close();
        } catch (Exception ignored) {}
    }

    public String getSkinValue() {
        return value;
    }

    public String getSkinName() {
        return name;
    }

    public String getSkinSignature() {
        return signatur;
    }

}