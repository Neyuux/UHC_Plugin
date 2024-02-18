package fr.neyuux.uhc.util;

import com.mojang.authlib.properties.Property;
import fr.neyuux.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerSkin {

    UUID uuid;
    String name;
    String value;
    String signatur;

    public PlayerSkin(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    @SuppressWarnings("resource")
    private void load() {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            Property textures = getPlayerTextures(player);
            this.name = textures.getName();
            this.value = textures.getValue();
            this.signatur = textures.getSignature();
            return;
        }

        String uuid = this.uuid.toString().replace("-", "");

        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");
            uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            uc.addRequestProperty("Pragma", "no-cache");

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
                    Bukkit.broadcastMessage(UHC.getPrefix() + "§cErreur lors du chargement d'un skin.");
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


    private static Property getPlayerTextures(Player player) {
        return new ArrayList<>(((CraftPlayer) player).getHandle().getProfile().getProperties().get("textures")).get(0);
    }
}