package fr.neyuux.uhc.events;

import fr.neyuux.uhc.PlayerUHC;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerReviveEvent extends Event implements Cancellable {
    final PlayerUHC playerUHC;

    private boolean isCancelled = false;


    public PlayerReviveEvent(PlayerUHC playerUHC) {
        this.playerUHC = playerUHC;
    }

    public PlayerUHC getPlayerUHC() {
        return playerUHC;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
