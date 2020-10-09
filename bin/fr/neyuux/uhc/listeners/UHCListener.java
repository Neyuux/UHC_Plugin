package fr.neyuux.uhc.listeners;

import fr.neyuux.uhc.Index;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UHCListener implements Listener {

    private Index main;
    public UHCListener(Index main) {
        this.main = main;
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        Player player = ev.getPlayer();
        player.removeAttachment(main.permissions.get(player.getName()));
        main.permissions.remove(player.getName());
    }

}
