package fr.neyuux.uhc.events;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEliminationEvent extends Event {
    final PlayerUHC playerUHC;
    final PlayerUHC killer;
    public final Index main;
    public PlayerEliminationEvent(PlayerUHC playerUHC, PlayerUHC killer, Index main) {
        this.playerUHC = playerUHC;
        this.main = main;
        this.killer = killer;
    }

    public PlayerUHC getPlayerUHC() {
        return playerUHC;
    }

    public PlayerUHC getKiller() {
        return killer;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
