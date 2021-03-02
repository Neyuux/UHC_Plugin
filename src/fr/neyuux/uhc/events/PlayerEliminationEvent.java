package fr.neyuux.uhc.events;

import fr.neyuux.uhc.PlayerUHC;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEliminationEvent extends Event {
    final PlayerUHC playerUHC;
    final PlayerUHC killer;
    Location stuffLocation;

    public PlayerEliminationEvent(PlayerUHC playerUHC, PlayerUHC killer, Location stuffLocation) {
        this.playerUHC = playerUHC;
        this.killer = killer;
        this.stuffLocation = stuffLocation;
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

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
