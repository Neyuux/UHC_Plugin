package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class AloneTogether extends Scenario implements Listener {
    public AloneTogether() {
        super(Scenarios.ALONE_TOGETHER, new ItemStack(Material.PUMPKIN));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        UHC.getInstance().getUHCTeamManager().getTeams().forEach(t -> t.getPlayers().forEach(p -> t.getPlayers().forEach(p2 -> p.getPlayer().getPlayer().hidePlayer(p2.getPlayer().getPlayer()))));
    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent ev) {
        if (!UHC.getInstance().isState(Gstate.PLAYING)) return;

        Player player = ev.getPlayer();

        if (UHC.getInstance().getPlayerUHC(player.getUniqueId()).getTeam() != null)
            for (PlayerUHC p : UHC.getInstance().getPlayerUHC(player.getUniqueId()).getTeam().getAlivePlayers()) {
                p.getPlayer().getPlayer().hidePlayer(player);
                player.hidePlayer(p.getPlayer().getPlayer());
            }
        player.showPlayer(player);
    }

}
