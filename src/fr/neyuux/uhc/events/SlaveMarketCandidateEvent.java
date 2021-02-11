package fr.neyuux.uhc.events;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SlaveMarketCandidateEvent extends Event {

    final PlayerUHC playerUHC;
    public final Index main;
    public SlaveMarketCandidateEvent(PlayerUHC playerUHC, Index main) {
        this.playerUHC = playerUHC;
        this.main = main;
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
}
