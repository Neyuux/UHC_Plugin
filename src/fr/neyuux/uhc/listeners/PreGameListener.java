package fr.neyuux.uhc.listeners;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.tasks.UHCStart;
import fr.neyuux.uhc.teams.TeamPrefix;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;

import static fr.neyuux.uhc.config.GameConfig.ConfigurableParams.TEAMTYPE;

public class PreGameListener implements Listener {

    private final Index main;
    public PreGameListener(Index main) {
        this.main = main;
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        Player player = ev.getPlayer();
        PlayerUHC playerUHC = main.getPlayerUHC(player);

        if (main.permissions.get(player.getName()) != null) {
            player.removeAttachment(main.permissions.get(player.getName()));
            main.permissions.remove(player.getName());
        }

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
        main.players.forEach(pu -> main.boards.get(pu).setLine(4, "§6§lSlots §6: §f" + Bukkit.getServer().getOnlinePlayers().size() + "§6/§e" + GameConfig.ConfigurableParams.SLOTS.getValue()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();
        PlayerUHC pt = null;
        for (PlayerUHC pu : main.players) if (pu.getPlayer().getUniqueId().equals(player.getUniqueId())) pt = pu;
        if (pt == null) main.players.add(new PlayerUHC(player, main));
        pt = main.getPlayerUHC(player);
        PlayerUHC playerUHC = pt;

        if (main.isState(Gstate.PLAYING) || main.isState(Gstate.FINISHED)) return;

        if (playerUHC.isHost())
            if (playerUHC.getTeam() == null) player.setDisplayName(TeamPrefix.getHostPrefix() + player.getName() + "§r");
            else player.setDisplayName(TeamPrefix.getHostPrefix() + playerUHC.getTeam().getTeam().getPrefix() + player.getName() + playerUHC.getTeam().getTeam().getSuffix());
        else player.setDisplayName(player.getName());
        player.setPlayerListName(player.getDisplayName());
        player.teleport(new Location(Bukkit.getWorld("Core"), -565, 23.2, 850));
        main.setLobbyScoreboard(player);

        InventoryManager.giveWaitInventory(playerUHC);
        Index.setPlayerTabList(player, main.getPrefixWithoutArrow() + "\n" + "§fBienvenue sur la map de §c§lNeyuux_" + "\n", "\n" + "§fMerci à moi même.");

        int onlines = Bukkit.getOnlinePlayers().size();
        int maxonlines = (int) GameConfig.ConfigurableParams.SLOTS.getValue();
        String joinmessage = "§a" + onlines;
        if (onlines >= (maxonlines / 4)) joinmessage = "§3" + onlines;
        if (onlines >= (maxonlines / 3)) joinmessage = "§2" + onlines;
        if (onlines >= (maxonlines / 2)) joinmessage = "§e" + onlines;
        if (onlines >= (maxonlines / 1.3)) joinmessage = "§c" + onlines;
        if (onlines >= maxonlines) joinmessage = "§4" + onlines;
        ev.setJoinMessage("§8[§a§l+§r§8] §e§o" + player.getName() + " §8(" + joinmessage + "§8/§c§l" + maxonlines + "§8)");
        main.players.forEach(pu -> main.boards.get(pu).setLine(4, "§6§lSlots §6: §f" + Bukkit.getServer().getOnlinePlayers().size() + "§6/§e" + GameConfig.ConfigurableParams.SLOTS.getValue()));
    }


    @EventHandler
    public void onChangeTeamItem(PlayerInteractEvent ev) {
        Player player = ev.getPlayer();
        ItemStack current = player.getItemInHand();

        if (current.getType().equals(Material.BANNER) && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && current.getItemMeta().getDisplayName().equals(GameConfig.getChooseTeamBanner().getItemMeta().getDisplayName()))
            player.openInventory(getChangeTeamInv(player, 1));
    }

    @EventHandler
    public void onChangeTeamInv(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        PlayerUHC playerUHC = main.getPlayerUHC(player);
        ItemStack current = ev.getCurrentItem();
        Inventory inv = ev.getInventory();

        if (current == null) return;

        if (inv.getName().startsWith("§e§lChoix d'équipe ")) {
            ev.setCancelled(true);

            if (current.getType().equals(Material.BANNER)) {
                if (current.getItemMeta().getDisplayName().equals("§fRéinitialiser votre Équipe")) {
                    if (playerUHC.getTeam() != null) {
                        Index.playPositiveSound(player);
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
                    Index.playPositiveSound(player);
                    t.add(player);
                    player.closeInventory();
                    BannerMeta bm = (BannerMeta)player.getItemInHand().getItemMeta();
                    bm.setPatterns(((BannerMeta)t.getBanner().getItemMeta()).getPatterns());
                    bm.setBaseColor(((BannerMeta)t.getBanner().getItemMeta()).getBaseColor());
                    player.getItemInHand().setItemMeta(bm);
                }
                main.boards.get(playerUHC).setLine(2, "§e§lÉquipe §e: " +(playerUHC.getTeam() != null ? playerUHC.getTeam().getTeam().getDisplayName() : "§cAucune"));
            }
        }
    }

    @EventHandler
    public void onCommandScenarioInv(InventoryClickEvent ev) {
        if (ev.getInventory().getName().equals("§6Liste des §lScénarios activés"))
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



    private Inventory getChangeTeamInv(Player player, int page) {
        int maxpages = BigDecimal.valueOf((double) main.getUHCTeamManager().getTeams().size() / 28.0).setScale(0, RoundingMode.UP).toBigInteger().intValue();
        Inventory inv = Bukkit.createInventory(null, Index.adaptInvSizeForInt(main.getUHCTeamManager().getTeams().size(), 18), "§e§lChoix d'équipe " + " §8["+page+"/" + maxpages + "]");
        GameConfig.setInvCoin(inv, (short)4);
        if (page < maxpages)
            inv.setItem(44, GameConfig.getNextPaper());
        if (page > 1)
            inv.setItem(inv.getSize() - 18, GameConfig.getPreviousPaper());
        inv.setItem(inv.getSize() - 5, new ItemsStack(Material.BANNER, (short)15, "§fRéinitialiser votre Équipe", "", "§b>>Clique").toItemStackwithItemFlag(ItemFlag.HIDE_POTION_EFFECTS));

        int ord = 28 * (page - 1);
        for (int i = 10; i < inv.getSize() - 10; i++) {
            if (ord >= main.getUHCTeamManager().getTeams().size()) continue;
            UHCTeam t = main.getUHCTeamManager().getTeams().toArray(new UHCTeam[0])[ord];
            if (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35)
                while (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35) i++;
            ItemsStack it = new ItemsStack(t.getBanner());
            it.setName(t.getTeam().getDisplayName());
            it.setLore("§eJoueurs : ");
            for (int p = 0; p < GameConfig.getTeamTypeInt((String)TEAMTYPE.getValue()); p++)
                if (t.getPlayers().size() - 1 >= p)
                    it.addLore(t.getPrefix().color.getColor() + " - " + t.getListPlayers().get(p).getPlayer().getPlayer().getPlayerListName());
                else it.addLore(t.getPrefix().color.getColor() + " - ");
            it.addLore("", "§b>>Cliquez rejoindre cette équipe.");
            inv.setItem(i, it.toItemStack());
            ord++;
        }
        return inv;
    }

}
