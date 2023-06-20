package fr.neyuux.uhc.events;

import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamChangeEvent extends Event {
    final PlayerUHC playerUHC;
    final UHCTeam team;

    public TeamChangeEvent(PlayerUHC playerUHC, UHCTeam team) {
        this.playerUHC = playerUHC;
        this.team = team;
    }

    public PlayerUHC getPlayerUHC() {
        return playerUHC;
    }

    public UHCTeam getTeam() {
        return team;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
