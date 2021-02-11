package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.events.SlaveMarketCandidateEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.tasks.UHCStart;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class SlaveMarket extends Scenario implements Listener {
    private Index main;
    public SlaveMarket() {
        super(Scenarios.SLAVE_MARKET, new ItemStack(Material.CARROT_STICK));
    }

    public static int diamonds = 64;
    public static int nOwners = 3;
    public static boolean randomChoiceOwners = false;

    public static List<PlayerUHC> owners = new ArrayList<>();
    public static List<PlayerUHC> candidates = new ArrayList<>();
    private static final Inventory candidsInv = Bukkit.createInventory(null, 54, "§8Liste des §lCandidats");

    @Override
    protected void activate() {
        GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(1, false));
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("SlaveMarket");
    }


    @EventHandler
    public void onCandidate(SlaveMarketCandidateEvent ev) {
        candidates.add(ev.getPlayerUHC());
        for (HumanEntity hplayer : candidsInv.getViewers())
            hplayer.getOpenInventory().getTopInventory().addItem(new ItemsStack(Material.SKULL_ITEM, (short) 3, ev.getPlayerUHC().getPlayer().getPlayer().getDisplayName(), "§7Ce joueur ce propose pour être acheteur.", "", "§b>>Cliquer pour accepter").toItemStackwithSkullMeta(ev.getPlayerUHC().getPlayer().getPlayer().getName()));
        this.main = ev.main;
    }

    @EventHandler
    public void onInvCandid(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        ItemStack current = ev.getCurrentItem();

        if (current == null) return;

        if (ev.getInventory().getName().equals(candidsInv.getName())) {
            ev.setCancelled(true);
            if (current.getType().equals(Material.SKULL_ITEM)) {
                Player p = Bukkit.getPlayer(((SkullMeta)current.getItemMeta()).getOwner());
                p.sendMessage(Index.getStaticPrefix() + "§aVous avez été accepté comme acheteur.");
                Index.playPositiveSound(p);
                for (HumanEntity hplayer : candidsInv.getViewers())
                    hplayer.getOpenInventory().getTopInventory().remove(current);
                ev.getInventory().remove(current);
                player.sendMessage(main.getPrefix() + "§aVous avez accepté §b" + p.getName() + " §acomme acheteur.");
                Index.playPositiveSound(player);
                owners.add(main.getPlayerUHC(p));
                candidates.remove(main.getPlayerUHC(p));
                if (owners.size() == nOwners) {
                    UHCStart.waitTask.cancel();
                    UHCStart.waitTask = null;
                }
            }
        }
    }


    public static Inventory getCandidInv() {
        Inventory inv = candidsInv;
        for (PlayerUHC puhc : SlaveMarket.candidates)
            inv.setItem(inv.firstEmpty(), new ItemsStack(Material.SKULL_ITEM, (short) 3, puhc.getPlayer().getPlayer().getDisplayName(), "§7Ce joueur ce propose pour être acheteur.", "", "§b>>Cliquer pour accepter").toItemStackwithSkullMeta(puhc.getPlayer().getPlayer().getName()));
        return inv;
    }
}
