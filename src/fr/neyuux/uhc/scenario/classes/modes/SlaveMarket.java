package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PluginReloadEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.util.ScoreboardSign;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFirework;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SlaveMarket extends Scenario implements Listener {
    public SlaveMarket() {
        super(Scenarios.SLAVE_MARKET, new ItemStack(Material.CARROT_STICK));
    }

    public static int diamonds = 64;
    public static int nOwners = 3;
    public static boolean randomChoiceOwners = false;

    public static List<PlayerUHC> owners = new ArrayList<>();
    public static List<PlayerUHC> candidates = new ArrayList<>();
    public static final Inventory candidsInv = Bukkit.createInventory(null, 54, "§8Liste des §lCandidats");
    public static boolean canStart = false;
    public static final int[] timers = new int[]{0 ,0};
    public static PlayerUHC bestBidder;
    public static int bid = 0;
    public static HashMap<PlayerUHC, Integer> ownersDiamond = new HashMap<>();

    @Override
    protected void activate() {
        GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(1, false));
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        for (PlayerUHC pu : owners)
            if (pu.getPlayer().isOnline() && ownersDiamond.get(pu) != 0)
                InventoryManager.give(pu.getPlayer().getPlayer(), null, new ItemStack(Material.DIAMOND, ownersDiamond.get(pu)));
    }

    @Override
    public boolean checkStart() {
        return GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("SlaveMarket");
    }


    @EventHandler
    public void onRel(PluginReloadEvent ev) {
        owners.clear();
        ownersDiamond.clear();
        candidates.clear();
        candidsInv.clear();
        canStart = false;
    }



    public static void auction() {
        for (PlayerUHC playerUHC : UHC.getInstance().players) {
            if (UHC.getInstance().boards.containsKey(playerUHC)) {
                UHC.getInstance().boards.get(playerUHC).destroy();
                UHC.getInstance().boards.remove(playerUHC);
            }
            ScoreboardSign ss = new ScoreboardSign(playerUHC.getPlayer().getPlayer(), Scenarios.SLAVE_MARKET.getDisplayName());
            ss.create();
            ss.setLine(13, "§0");
            int l = 0;
            for (UHCTeam t : UHC.getInstance().getUHCTeamManager().getTeams()) {
                if (l == 13) break;
                Player owner = null;
                for (PlayerUHC pu : t.getPlayers()) if (owners.contains(pu)) owner = pu.getPlayer().getPlayer();
                ss.setLine(l, t.getTeam().getDisplayName() + " §7: §f§l" + t.getPlayers().size() + " §ejoueurs / §f§l" + owner.getInventory().getItem(4).getAmount() + " §bdiamants");
                l++;
            }
            UHC.getInstance().boards.put(playerUHC, ss);
        }

        final List<PlayerUHC> players = new ArrayList<>();
        for (PlayerUHC pu : UHC.getInstance().players) if (!pu.isSpec() && pu.getPlayer().isOnline() && !owners.contains(pu)) players.add(pu);

        final PlayerUHC[] playerUHC = {null};
        final Player[] player = {null};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timers[0] == -5 && player[0] != null && playerUHC[0] != null) {
                    UHCTeam t;
                    if (bestBidder != null) t = bestBidder.getTeam();
                    else t = UHC.getInstance().getUHCTeamManager().getTeams().get(new Random().nextInt(UHC.getInstance().getUHCTeamManager().getTeams().size()));
                    Player owner = t.getListPlayers().get(0).getPlayer().getPlayer();
                    t.add(player[0]);
                    owner.getInventory().getItem(owner.getInventory().first(Material.DIAMOND)).setAmount(owner.getInventory().getItem(owner.getInventory().first(Material.DIAMOND)).getAmount() - bid);
                    //tp player
                    Firework fw = (Firework) player[0].getWorld().spawnEntity(player[0].getLocation(), EntityType.FIREWORK);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    fwm.setPower(5);
                    fwm.clearEffects();
                    fwm.addEffect(FireworkEffect.builder().withColor(t.getPrefix().color.getDyecolor().getColor()).with(FireworkEffect.Type.STAR).build());
                    fw.setFireworkMeta(fwm);
                    ((CraftFirework)fw).getHandle().expectedLifespan = 1;
                    Bukkit.broadcastMessage(UHC.getPrefix() + Scenarios.SLAVE_MARKET.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " " + t.getListPlayers().get(t.getListPlayers().size() - 1).getPlayer().getPlayer().getDisplayName() + " §ea acheté §f" + player[0].getDisplayName() + " §epour §b§l" + bid + " §bdiamants !");
                }
                for (PlayerUHC puhc : UHC.getInstance().players) {
                    int l = 0;
                    if (UHC.getInstance().boards.containsKey(puhc))
                        for (UHCTeam t : UHC.getInstance().getUHCTeamManager().getTeams()) {
                            Player owner = null;
                            for (PlayerUHC pu : t.getPlayers()) if (owners.contains(pu)) owner = pu.getPlayer().getPlayer();
                            UHC.getInstance().boards.get(puhc).setLine(l, t.getTeam().getDisplayName() + " §7: §f§l" + t.getPlayers().size() + " §ejoueurs / §f§l" + owner.getInventory().getItem(owner.getInventory().first(Material.DIAMOND)).getAmount() + " §bdiamants");
                            l++;
                        }
                }
                if (timers[0] == -6) {
                    if (players.isEmpty()) {
                        cancel();
                        canStart = true;
                        for (PlayerUHC puhc : owners)
                            if (puhc.getPlayer().getPlayer().getInventory().contains(Material.DIAMOND))
                                ownersDiamond.put(puhc, puhc.getPlayer().getPlayer().getInventory().getItem(puhc.getPlayer().getPlayer().getInventory().first(Material.DIAMOND)).getAmount());
                            else ownersDiamond.put(puhc, 0);
                        InventoryManager.setAllPlayersLevels(0, 0);
                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage(UHC.getPrefix() + "§aVous pouvez démarrer la partie avec la commande : §l/uhc start§a .");
                        return;
                    }
                    playerUHC[0] = players.remove(0);
                    player[0] = playerUHC[0].getPlayer().getPlayer();
                    //tp player
                    player[0].getInventory().clear();
                    for (Map.Entry<PlayerUHC, ScoreboardSign> en : UHC.getInstance().boards.entrySet())
                        en.getValue().setLine(14, "§f" + player[0].getName() + " §eest mis aux enchères !");
                    timers[0] = 55;
                }
                if (bestBidder != null) UHC.sendInfiniteActionBarForAllPlayers(bestBidder.getTeam().getPrefix().color.getColor() + "§l" + bestBidder.getPlayer().getName() + " §7- §b" + bid + " diamants");
                InventoryManager.setAllPlayersLevels(timers[0] + 6, (timers[0] + 6) / 60f);
                timers[0]--;
            }
        }.runTaskTimer(UHC.getInstance(), 0 ,20);
    }

}
