package fr.neyuux.uhc.events;

import fr.neyuux.uhc.PlayerUHC;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEliminationEvent extends Event {
    final PlayerUHC playerUHC;
    final PlayerUHC killer;
    Location stuffLocation;
    String deathmessage;

    public PlayerEliminationEvent(PlayerUHC playerUHC, PlayerUHC killer, Location stuffLocation, String deathmessage) {
        this.playerUHC = playerUHC;
        this.killer = killer;
        this.stuffLocation = stuffLocation;
        this.deathmessage = deathmessage;
    }

    public PlayerUHC getPlayerUHC() {
        return playerUHC;
    }

    public PlayerUHC getKiller() {
        return killer;
    }

    public Location getStuffLocation()  {
        return stuffLocation;
    }

    public void setStuffLocation(Location stuffLocation) {
        this.stuffLocation = stuffLocation;
    }

    public String getDeathMessage()  {
        return deathmessage;
    }

    public void setDeathMessage(String deathMessage) {
        this.deathmessage = deathMessage;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
