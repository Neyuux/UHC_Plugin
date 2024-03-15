package fr.neyuux.uhc.listeners;

import fr.neyuux.uhc.*;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.events.GameStartEvent;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.modes.SlaveMarket;
import fr.neyuux.uhc.tasks.UHCStart;
import fr.neyuux.uhc.teams.TeamPrefix;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.teams.UHCTeamColors;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Random;

import static fr.neyuux.uhc.GameConfig.ConfigurableParams.TEAMTYPE;

public class PreGameListener implements Listener {

    private final UHC main;
    public PreGameListener(UHC main) {
        this.main = main;
    }

    public static BukkitTask waitTask;


    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        Player player = ev.getPlayer();
        PlayerUHC playerUHC = main.getPlayerUHC(player.getUniqueId());

        playerUHC.getAttachment().setPermission("uhc.host", false);

        if (main.isState(Gstate.PLAYING) || main.isState(Gstate.FINISHED)) return;

        if (playerUHC.getTeam() != null) playerUHC.getTeam().leave(playerUHC);
        main.spectators.remove(player);
        main.players.remove(playerUHC);

        int onlines = Bukkit.getOnlinePlayers().size() - 1;
        int maxonlines = (int)GameConfig.ConfigurableParams.SLOTS.getValue();
        String quitmessage = "§a" + onlines;
        if (onlines >= (maxonlines / 4)) quitmessage = "§2" + onlines;
        if (onlines >= (maxonlines / 3)) quitmessage = "§e" + onlines;
        if (onlines >= (maxonlines / 2)) quitmessage = "§6" + onlines;
        if (onlines >= (maxonlines / 1.3)) quitmessage = "§c" + onlines;
        if (onlines >= maxonlines) quitmessage = "§4" + onlines;
        ev.setQuitMessage("§8[§c§l-§r§8] §e§o" + player.getName() + " §8(" + quitmessage + "§8/§c§l" + maxonlines + "§8)");
        main.players.forEach(pu -> main.boards.get(pu).setLine(4, "§6§lSlots §6: §f" + main.players.size() + "§6/§e" + maxonlines));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        if (main.isState(Gstate.PLAYING) || main.isState(Gstate.FINISHED)) return;
        Player player = ev.getPlayer();
        PlayerUHC pt = null;
        for (PlayerUHC pu : main.players) if (pu.getPlayer().getUniqueId().equals(player.getUniqueId())) pt = pu;
        if (pt == null) main.players.add(new PlayerUHC(player, main));
        pt = main.getPlayerUHC(player.getUniqueId());
        PlayerUHC playerUHC = pt;
        playerUHC.setPlayer(player);

        if (playerUHC.isHost())
            if (playerUHC.getTeam() == null) {
                player.setDisplayName(TeamPrefix.getHostPrefix() + player.getName() + "§r");
                main.getUHCTeamManager().getScoreboard().getTeam("Host").addEntry(player.getName());
            } else player.setDisplayName(TeamPrefix.getHostPrefix() + playerUHC.getTeam().getTeam().getPrefix() + player.getName() + playerUHC.getTeam().getTeam().getSuffix());
        else {
            player.setDisplayName(player.getName());
            main.getUHCTeamManager().getScoreboard().getTeam("Joueur").addEntry(player.getName());
        }
        player.setGameMode(GameMode.ADVENTURE);
        player.setPlayerListName(player.getDisplayName());
        player.teleport(main.world.getPlatformLoc());
        main.setLobbyScoreboard(player);

        InventoryManager.giveWaitInventory(player);
        UHC.setPlayerTabList(player, UHC.getPrefixWithoutArrow() + "\n" + "§fBienvenue sur la map de §c§lManon" + "\n" + "§bDev by §d§lManon" + "\n", "\n" + "§fMerci à moi même.");

        int onlines = main.players.size();
        int maxonlines = (int) GameConfig.ConfigurableParams.SLOTS.getValue();
        String joinmessage = "§a" + onlines;
        if (onlines >= (maxonlines / 4)) joinmessage = "§3" + onlines;
        if (onlines >= (maxonlines / 3)) joinmessage = "§2" + onlines;
        if (onlines >= (maxonlines / 2)) joinmessage = "§e" + onlines;
        if (onlines >= (maxonlines / 1.3)) joinmessage = "§c" + onlines;
        if (onlines >= maxonlines) joinmessage = "§4" + onlines;
        ev.setJoinMessage("§8[§a§l+§r§8] §e§o" + player.getName() + " §8(" + joinmessage + "§8/§c§l" + maxonlines + "§8)");
        main.players.forEach(pu -> main.boards.get(pu).setLine(4, "§6§lSlots §6: §f" + main.players.size() + "§6/§e" + maxonlines));
    }


    @EventHandler
    public void onChangeTeamItem(PlayerInteractEvent ev) {
        Player player = ev.getPlayer();
        ItemStack current = player.getItemInHand();

        if (current == null) return;

        if (current.getType().equals(Material.BANNER) && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && current.getItemMeta().getDisplayName().equals(GameConfig.getChooseTeamBanner(main.getPlayerUHC(player.getUniqueId())).getItemMeta().getDisplayName()))
            player.openInventory(getChangeTeamInv(1));
    }

    @EventHandler
    public void onSpecItem(PlayerInteractEvent ev) {
        Player player = ev.getPlayer();
        ItemStack current = ev.getItem();
        Action action = ev.getAction();

        if (current == null || action == null) return;

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.PHYSICAL))
            if (current.equals(UHC.getSpecTear())) {
                main.spectators.add(player);
                player.setGameMode(GameMode.SPECTATOR);
                player.setDisplayName("§8[§7Spectateur§8] §7" + player.getName());
                player.setPlayerListName(player.getDisplayName());
                InventoryManager.clearInventory(player);

                player.sendMessage(UHC.getPrefix() + "§6Vous avez établi votre mode de jeu en spectateur.");
                player.sendMessage(UHC.getPrefix() + "§7Pour revenir au mode non-spectateur, utilisez la commande §6§l/uhc spec off§7.");
                main.players.forEach(pu -> main.boards.get(pu).setLine(4, "§6§lSlots §6: §f" + main.players.size() + "§6/§e" + GameConfig.ConfigurableParams.SLOTS.getValue()));
            }
    }

    @EventHandler
    public void onChangeTeamInv(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        PlayerUHC playerUHC = main.getPlayerUHC(player.getUniqueId());
        ItemStack current = ev.getCurrentItem();
        Inventory inv = ev.getInventory();

        if (current == null) return;

        if (inv.getName().startsWith("§e§lChoix d'équipe ")) {
            ev.setCancelled(true);

            if (current.getType().equals(Material.BANNER)) {
                if (current.getItemMeta().getDisplayName().equals("§fRéinitialiser votre Équipe")) {
                    if (playerUHC.getTeam() != null) {
                        UHC.playPositiveSound(player);
                        playerUHC.getTeam().leave(playerUHC);
                        player.closeInventory();
                        BannerMeta bm = (BannerMeta)player.getItemInHand().getItemMeta();
                        bm.setPatterns(Collections.emptyList());
                        bm.setBaseColor(DyeColor.WHITE);
                        player.getItemInHand().setItemMeta(bm);
                    }
                } else {
                    UHCTeam t = main.getUHCTeamManager().getTeamByDisplayName(current.getItemMeta().getDisplayName());
                    if (playerUHC.getTeam() != null && playerUHC.getTeam().equals(t)) return;

                    if (playerUHC.getTeam() != null) playerUHC.getTeam().leave(playerUHC);
                    UHC.playPositiveSound(player);
                    t.add(player);
                    player.closeInventory();
                    BannerMeta bm = (BannerMeta)player.getItemInHand().getItemMeta();
                    bm.setPatterns(((BannerMeta)t.getBanner().getItemMeta()).getPatterns());
                    bm.setBaseColor(((BannerMeta)t.getBanner().getItemMeta()).getBaseColor());
                    player.getItemInHand().setItemMeta(bm);
                }
                main.boards.get(playerUHC).setLine(2, "§e§lÉquipe §e: " +(playerUHC.getTeam() != null ? playerUHC.getTeam().getTeam().getDisplayName() : "§cAucune"));

            } else if (current.equals(GameConfig.getPreviousPaper()))
                player.openInventory(getChangeTeamInv((Integer.parseInt(String.valueOf(inv.getName().charAt(23)))) - 1));
            else if (current.equals(GameConfig.getNextPaper()))
                player.openInventory(getChangeTeamInv((Integer.parseInt(String.valueOf(inv.getName().charAt(23)))) + 1));
        }
    }

    @EventHandler
    public void onCommandInv(InventoryClickEvent ev) {
        if (ev.getInventory().getName().equals("§6Liste des §lScénarios activés") || ev.getInventory().getName().equals("§dInventaire de §lDépart§8 /inv"))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onStartGameConfigItem(InventoryClickEvent ev) {
        if (ev.getInventory().getName().equals("§c§lConfiguration") && ev.getCurrentItem() != null && ev.getCurrentItem().getType().equals(Material.STAINED_CLAY)) {
            ev.getWhoClicked().closeInventory();
            if (main.uhcStart != null) main.uhcStart.cancelStart();
            else {
                main.setState(Gstate.STARTING);
                new UHCStart(main).runTaskTimer(main, 0, 20);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent ev) {
        if (!main.isState(Gstate.PLAYING) && !main.isState(Gstate.FINISHED))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent ev) {
        if (!main.isState(Gstate.PLAYING) && !main.isState(Gstate.FINISHED))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent ev) {
        if (!main.isState(Gstate.PLAYING) && !main.isState(Gstate.FINISHED) && !ev.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent ev) {
        if (!main.isState(Gstate.PLAYING) && !main.isState(Gstate.FINISHED) && !ev.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent ev) {
        if (!main.isState(Gstate.PLAYING) && !main.isState(Gstate.FINISHED))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onStartSlaveMarket(GameStartEvent ev) {
        if (Scenarios.SLAVE_MARKET.isActivated() && !SlaveMarket.canStart) {
            final int[] id = {0};

            if (SlaveMarket.nOwners == 1) SlaveMarket.nOwners = 2;
            if (SlaveMarket.nOwners > UHCTeamColors.values().length) SlaveMarket.nOwners = UHCTeamColors.values().length;
            if (!SlaveMarket.randomChoiceOwners) {
                Bukkit.broadcastMessage(UHC.getPrefix() + "§6Veuillez choisir les acheteurs de la partie. Pour ce faire, les joueurs voulant l'être doivent effectuer la commande §b§l/uhc sm candidate §6et les hosts doivent accepter les joueurs.");
                for (PlayerUHC playerUHC : main.players)
                    if (playerUHC.isHost())
                        playerUHC.getPlayer().getPlayer().sendMessage(UHC.getPrefix() + "§6Pour ouvrir l'inventaire des joueurs candidats, effectuez la commande §b§l/uhc sm view§6.");
                final int[] waitTicks = {6000};
                waitTask = new BukkitRunnable() {
                    public void run() {
                        InventoryManager.setAllPlayersLevels((short) (Math.floorDiv(waitTicks[0], 20) + 1), waitTicks[0] / 6000f);
                        if (waitTicks[0] == 0) {
                            cancel();
                            main.uhcStart = null;
                            waitTask = null;
                            Bukkit.broadcastMessage(UHC.getPrefix() + "§cVous avez mit trop de temps à choisir les acheteurs.");
                            main.rel();
                        }
                        waitTicks[0]--;
                    }
                }.runTaskTimer(main, 0L, 1L);
            } else {
                int max = 40;
                while (SlaveMarket.owners.size() != SlaveMarket.nOwners && max > 0) {
                    PlayerUHC playerUHC = main.players.get(new Random().nextInt(main.players.size()));
                    if (SlaveMarket.owners.contains(playerUHC)) {
                        max--;
                        continue;
                    }
                    SlaveMarket.owners.add(playerUHC);
                }
            }

            new BukkitRunnable() {
                final int[] i = {0, 0};
                public void run() {
                    if (SlaveMarket.owners.size() == SlaveMarket.nOwners) {
                        if (i[1] == 0) {
                            Bukkit.broadcastMessage(UHC.getPrefix() + "§aIl est temps d'annoncer les acheteurs de la partie.");
                            InventoryManager.setAllPlayersLevels(0, 0f);
                            i[1] = 1;
                            main.getUHCTeamManager().clearTeams();
                            for (int i = 1; i <= SlaveMarket.nOwners; i++)
                                main.getUHCTeamManager().createTeam();
                            BukkitRunnable ownersPresentation = new BukkitRunnable() {
                                public void run() {
                                    if (id[0] == 0) {
                                        cancel();
                                        ev.setCancelled(true);
                                        return;
                                    }
                                    UHCTeam t = main.getUHCTeamManager().getTeams().get(i[0]);
                                    PlayerUHC owner = SlaveMarket.owners.get(i[0]);
                                    Player p = owner.getPlayer().getPlayer();

                                    Bukkit.broadcastMessage(UHC.getPrefix() + t.getPrefix().color.getColor() + "L'acheteur de l'équipe " + t.getTeam().getDisplayName() + " sera §f" + p.getName() + t.getPrefix().color.getColor() +".");
                                    for (Player player : Bukkit.getOnlinePlayers())
                                        player.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 8, 1);
                                    t.add(p);
                                    //tp p
                                    InventoryManager.give(p, 4, new ItemsStack(Material.DIAMOND, SlaveMarket.diamonds, "§b§lDiamants").toItemStack());
                                    p.getInventory().remove(Material.REDSTONE_COMPARATOR);
                                    p.getInventory().remove(Material.GHAST_TEAR);
                                    if (!main.getPlayerUHC(p.getUniqueId()).isHost()) p.setDisplayName(p.getDisplayName().substring(0, 2) + "§l" + p.getDisplayName().substring(2));
                                    else p.setDisplayName(p.getDisplayName().substring(0, TeamPrefix.getHostPrefix().length() + 2) + "§l" + p.getDisplayName().substring(TeamPrefix.getHostPrefix().length() + 2));
                                    p.setPlayerListName(p.getDisplayName());

                                    i[0]++;
                                    if (i[0] == main.getUHCTeamManager().getTeams().size()) {
                                        id[0] = 0;
                                        i[1] = 2;
                                        cancel();
                                        ev.setCancelled(true);
                                        SlaveMarket.canStart = true;
                                        SlaveMarket.auction();
                                    }
                                }
                            };
                            ownersPresentation.runTaskTimer(main, 40L, 60L);
                            id[0] = ownersPresentation.getTaskId();
                        } else if (i[1] == 2) {
                            cancel();
                            ev.setCancelled(true);
                        }
                    } else
                    if (waitTask == null) {
                        cancel();
                        ev.setCancelled(true);
                    }
                }
            }.runTaskTimer(main, 0L, 20L);
        }
    }
    @EventHandler
    public void onInvCandid(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        ItemStack current = ev.getCurrentItem();

        if (current == null) return;

        if (ev.getInventory().getName().equals(SlaveMarket.candidsInv.getName())) {
            ev.setCancelled(true);
            if (current.getType().equals(Material.SKULL_ITEM)) {
                Player p = Bukkit.getPlayer(((SkullMeta)current.getItemMeta()).getOwner());
                PlayerUHC puhc = UHC.getInstance().getPlayerUHC(p.getUniqueId());
                p.sendMessage(UHC.getPrefix() + "§aVous avez été accepté comme acheteur.");
                UHC.playPositiveSound(p);
                SlaveMarket.candidsInv.remove(current);
                ev.getInventory().remove(current);
                player.sendMessage(UHC.getPrefix() + "§aVous avez accepté §b" + p.getName() + " §acomme acheteur.");
                UHC.playPositiveSound(player);
                SlaveMarket.owners.add(puhc);
                SlaveMarket.candidates.remove(puhc);
                if (SlaveMarket.owners.size() == SlaveMarket.nOwners) {
                    PreGameListener.waitTask.cancel();
                    PreGameListener.waitTask = null;
                }
            }
        } else {
            if (current.getType() == Material.DIAMOND && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && current.getItemMeta().getDisplayName().equals("§b§lDiamants"))
                ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropDiamsSlaveMarket(PlayerDropItemEvent ev) {
        ItemStack current = ev.getItemDrop().getItemStack();
        if (current.getType() == Material.DIAMOND && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && current.getItemMeta().getDisplayName().equals("§b§lDiamants"))
            ev.setCancelled(true);
    }



    private Inventory getChangeTeamInv(int page) {
        int maxpages = BigDecimal.valueOf((double) main.getUHCTeamManager().getTeams().size() / 21.0).setScale(0, RoundingMode.UP).toBigInteger().intValue();
        Inventory inv = Bukkit.createInventory(null, UHC.adaptInvSizeForInt(main.getUHCTeamManager().getTeams().size(), 18), "§e§lChoix d'équipe " + " §8["+page+"/" + maxpages + "]");
        GameConfig.setInvCoin(inv, (short)4);
        if (page < maxpages)
            inv.setItem(44, GameConfig.getNextPaper());
        if (page > 1)
            inv.setItem(inv.getSize() - 9, GameConfig.getPreviousPaper());
        inv.setItem(inv.getSize() - 5, new ItemsStack(Material.BANNER, (short)15, "§fRéinitialiser votre Équipe", "", "§b>>Clique").toItemStackwithItemFlag(ItemFlag.HIDE_POTION_EFFECTS));

        int ord = 21 * (page - 1);
        for (int i = 10; i < inv.getSize() - 10; i++) {
            if (ord >= main.getUHCTeamManager().getTeams().size()) continue;
            UHCTeam t = main.getUHCTeamManager().getTeams().toArray(new UHCTeam[0])[ord];
            if (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35)
                while (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35) i++;
            ItemsStack it = new ItemsStack(t.getBanner());
            it.setName(t.getTeam().getDisplayName());
            it.setLore("§eJoueurs : ");
            for (int p = 0; p < (Scenarios.SKY_DEFENDER.isActivated() ? 10 : GameConfig.getTeamTypeInt((String)TEAMTYPE.getValue())); p++)
                if (t.getPlayers().size() - 1 >= p)
                    it.addLore(t.getPrefix().color.getColor() + " - " + t.getPlayers().get(p).getPlayer().getPlayer().getPlayerListName());
                else it.addLore(t.getPrefix().color.getColor() + " - ");
            it.addLore("", "§b>>Cliquez rejoindre cette équipe.");
            inv.setItem(i, it.toItemStack());
            ord++;
        }
        return inv;
    }

}
