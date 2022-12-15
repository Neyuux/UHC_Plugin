package fr.neyuux.uhc.listeners;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.GameEndEvent;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.Anonymous;
import fr.neyuux.uhc.tasks.UHCRunnable;
import fr.neyuux.uhc.util.Loot;
import fr.neyuux.uhc.util.LootItem;
import fr.neyuux.uhc.util.ScoreboardSign;
import fr.neyuux.uhc.util.VarsLoot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerListener implements Listener {

    private final UHC main;
    public PlayerListener(UHC main) {
        this.main = main;
        if (main.mode.equals(UHC.Modes.LG)) canChat = false;
    }

    public static boolean canChat = true;


    @EventHandler
    public void onLeave(PlayerQuitEvent ev) {
        Player player = ev.getPlayer();
        PlayerUHC playerUHC = main.getPlayerUHC(player);

        if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING)) return;

        if (playerUHC.getTeam() != null)
            playerUHC.getTeam().leaveInGame(playerUHC);
        HashMap<Integer, ItemStack> lastArmor = new HashMap<>();
        int ii;
        for (ItemStack it : player.getInventory().getArmorContents()) {
            if (it == null)continue;
            int slot = 0;
            ii = 0;
            for (ItemStack iit : player.getInventory().getArmorContents()) {
                if (iit != null && iit.equals(it)) slot = ii;
                ii++;
            }
            lastArmor.put(slot, it);
        }
        playerUHC.setLastInv(player.getInventory().getContents());
        playerUHC.setLastArmor(lastArmor);
        playerUHC.setLastLocation(player.getLocation());
        playerUHC.health = player.getHealth();
        playerUHC.foodLevel = player.getFoodLevel();
        playerUHC.maxHealth = player.getMaxHealth();

        main.boards.remove(playerUHC);

        int onlines = Bukkit.getOnlinePlayers().size() - 1;
        int maxonlines = (int) GameConfig.ConfigurableParams.SLOTS.getValue();
        String joinmessage = "§a" + onlines;
        if (onlines >= (maxonlines / 4)) joinmessage = "§3" + onlines;
        if (onlines >= (maxonlines / 3)) joinmessage = "§2" + onlines;
        if (onlines >= (maxonlines / 2)) joinmessage = "§e" + onlines;
        if (onlines >= (maxonlines / 1.3)) joinmessage = "§c" + onlines;
        if (onlines >= maxonlines) joinmessage = "§4" + onlines;
        String name = player.getName();
        if (Scenarios.ANONYMOUS.isActivated()) name = "§k" + Anonymous.usedName;
        ev.setQuitMessage("§8[§c§l-§r§8] §e§o" + name + " §8(" + joinmessage + "§8/§c§l" + maxonlines + "§8)");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING)) return;
        Player player = ev.getPlayer();
        PlayerUHC pt = null;
        for (PlayerUHC pu : main.players) if (pu.getPlayer().getUniqueId().equals(player.getUniqueId())) pt = pu;
        if (pt == null) main.players.add(new PlayerUHC(player, main));
        pt = main.getPlayerUHC(player);
        PlayerUHC playerUHC = pt;
        playerUHC.setPlayer(player);

        if (!main.getAlivePlayers().contains(playerUHC)) {
            main.spectators.add(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(new Location(Bukkit.getWorld(main.world.getSeed() + ""), 0, 100, 0));
            InventoryManager.clearInventory(player);
            playerUHC.heal();
        }
        if (main.isState(Gstate.PLAYING)) main.setGameScoreboard(player);
        else if (main.isState(Gstate.FINISHED)) main.setKillsScoreboard(player);
        UHC.setPlayerTabList(player, UHC.getPrefixWithoutArrow() + "\n" + "§fBienvenue sur la map de §c§lNeyuux_" + "\n", "\n" + "§fMerci à moi même.");
        if (playerUHC.getTeam() != null && playerUHC.isAlive())
            playerUHC.getTeam().reconnect(player);
        if (playerUHC.isSpec()) player.setDisplayName("§8[§7Spectateur§8] §7" + player.getName() + "§r");
        player.setPlayerListName(player.getDisplayName());

        playerUHC.setInvulnerable(true);
        player.sendMessage(UHC.getPrefix() + "§6Vous êtes invincible pendant 5 secondes.");
        new BukkitRunnable() {
            int i = 5;
            public void run() {
                if (i == 0) {
                    playerUHC.setInvulnerable(false);
                    cancel();
                }
                i--;
            }
        }.runTaskTimer(main, 0, 20);

        player.setMaxHealth(playerUHC.maxHealth);
        player.setHealth(playerUHC.health);
        player.setFoodLevel(playerUHC.foodLevel);

        int onlines = Bukkit.getOnlinePlayers().size();
        int maxonlines = (int) GameConfig.ConfigurableParams.SLOTS.getValue();
        String joinmessage = "§a" + onlines;
        if (onlines >= (maxonlines / 4)) joinmessage = "§3" + onlines;
        if (onlines >= (maxonlines / 3)) joinmessage = "§2" + onlines;
        if (onlines >= (maxonlines / 2)) joinmessage = "§e" + onlines;
        if (onlines >= (maxonlines / 1.3)) joinmessage = "§c" + onlines;
        if (onlines >= maxonlines) joinmessage = "§4" + onlines;
        String name = player.getName();
        if (Scenarios.ANONYMOUS.isActivated()) name = "§k" + Anonymous.usedName;
        ev.setJoinMessage("§8[§a§l+§r§8] §e§o" + name + " §8(" + joinmessage + "§8/§c§l" + maxonlines + "§8)");
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e){
        ItemStack result = e.getRecipe().getResult();
        Player player = (Player)e.getWhoClicked();
        if (result == null || result.getType() == null) return;

        if (e.getCurrentItem().isSimilar(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1))) {
            e.setCancelled(true);
            player.sendMessage(UHC.getPrefix() + "§cCe craft est interdit !");
            UHC.playNegativeSound(player);
        }
    }

    @EventHandler
    public void onDoubleArrowCraft(PrepareItemCraftEvent ev) {
        if ((boolean)GameConfig.ConfigurableParams.DOUBLE_ARROW.getValue()) {
            ItemStack result = ev.getRecipe().getResult();
            if (result == null || result.getType() != Material.ARROW) return;
            ev.getInventory().setResult(new ItemStack(result.getType(), result.getAmount() * 2));
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent ev) {
        Player player = ev.getPlayer();
        PlayerUHC playerUHC = main.getPlayerUHC(player);
        Entity e = ev.getRightClicked();

        if (e.getType().equals(EntityType.HORSE) && !(boolean)GameConfig.ConfigurableParams.HORSE.getValue()) {
            ev.setCancelled(true);
            player.sendMessage(UHC.getPrefix() + "§cLes chevaux sont désactivés !");
            UHC.playNegativeSound(player);
        }
        else if (e.getType().equals(EntityType.PLAYER) && playerUHC.isSpec())
            player.openInventory(main.getPlayerUHC((Player) e).getSpecInfosInventory(Collections.emptyList()));
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e){
        ItemStack item = e.getItem();
        Player p = e.getPlayer();
        if(item == null) return;

        if(item.getType() == Material.MILK_BUCKET && !(boolean)GameConfig.ConfigurableParams.MILK.getValue()){
            e.setCancelled(true);
            p.sendMessage(UHC.getPrefix() + "§cIl est interdit de boire un seau de lait !");
            UHC.playNegativeSound(p);
            return;
        }

        if(item.getType() == Material.GOLDEN_APPLE){
            p.removePotionEffect(PotionEffectType.ABSORPTION);

            if(item.equals(UHC.getGoldenHead(item.getAmount()))) { // GOLDEN HEAD
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1, true, true));
            }

            if (!(boolean)GameConfig.ConfigurableParams.ABSORPTION.getValue()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                        p.removePotionEffect(PotionEffectType.ABSORPTION);
                        main.setHealth(p);
                    }, (long) 0.01);
            }
            main.setHealth(p);
        }
    }

    @EventHandler
    public void onPlayerFood(FoodLevelChangeEvent e) {
        try {
            if (e.getEntity().getGameMode().equals(GameMode.SURVIVAL) && main.isState(Gstate.PLAYING) && main.getPlayerUHC((Player) e.getEntity()).isAlive()) {
                PlayerUHC pu = main.getPlayerUHC((Player) e.getEntity());
                pu.foodLevel = e.getFoodLevel();
            }
        } catch (Exception ignored){}
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerHealth(EntityRegainHealthEvent e) {
        if (e.getEntityType().equals(EntityType.PLAYER) && main.isState(Gstate.PLAYING))
            main.getPlayerUHC((Player)e.getEntity()).health = ((Player) e.getEntity()).getHealth() + e.getAmount();
    }

    @EventHandler
    public void killScoreboardLineUpdater(PlayerEliminationEvent ev) {
        if (!GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA")) {
            for (Map.Entry<PlayerUHC, ScoreboardSign> en : main.boards.entrySet()) if (en.getKey().getTeam() != null)
                en.getValue().setLine(4, "§c§lKills §c: §l" + en.getKey().getKills() + " §4("+en.getKey().getTeam().getAlivePlayersKills()+")");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if(item == null) return;
        boolean nerf = e.getAction() == Action.RIGHT_CLICK_BLOCK && ((item.getType() == Material.LAVA_BUCKET && main.getPlayerUHC(p).havePlayerAround(10)) ||
                (item.getType() == Material.FLINT_AND_STEEL && e.getClickedBlock().getType() != Material.OBSIDIAN));

        if(nerf && UHCRunnable.pvpTimer != -1 && !(boolean)GameConfig.ConfigurableParams.I_PVP.getValue()) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            p.updateInventory();
        }

        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(p.getWorld().getEnvironment().equals(World.Environment.NORMAL))
            if (!(boolean) GameConfig.ConfigurableParams.LAVA.getValue() && item.getType() == Material.LAVA_BUCKET) {
                e.setCancelled(true);
                e.setUseInteractedBlock(Event.Result.DENY);
                p.updateInventory();
            } else if (!(boolean)GameConfig.ConfigurableParams.FLINT_AND_STEEL.getValue() && item.getType() == Material.FLINT_AND_STEEL && e.getClickedBlock().getType() != Material.OBSIDIAN) {
                e.setCancelled(true);
                e.setUseInteractedBlock(Event.Result.DENY);
                p.updateInventory();
            }

        if(p.getWorld().getEnvironment().equals(World.Environment.NETHER) || p.getWorld().getEnvironment().equals(World.Environment.THE_END))
            if (!(boolean)GameConfig.ConfigurableParams.FLINT_AND_STEEL.getValue() && item.getType() == Material.FLINT_AND_STEEL && e.getClickedBlock().getType() != Material.OBSIDIAN) {
                e.setCancelled(true);
                e.setUseInteractedBlock(Event.Result.DENY);
                p.updateInventory();
            } else if (!(boolean)GameConfig.ConfigurableParams.BED$NOT_OVERWORLD.getValue() && item.getType() == Material.BED) {
                e.setCancelled(true);
                e.setUseInteractedBlock(Event.Result.DENY);
                p.updateInventory();
            } else if (!(boolean)GameConfig.ConfigurableParams.LAVA$NOT_OVERWORLD.getValue() && item.getType() == Material.LAVA_BUCKET) {
                e.setCancelled(true);
                e.setUseInteractedBlock(Event.Result.DENY);
                p.updateInventory();
            }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        Material mat = b.getType();

        HashMap<Material, Loot> loots = VarsLoot.getBlocksLoots();

        if (loots.containsKey(mat)) {
            Loot loot = loots.get(mat);

            World w = b.getLocation().getWorld();

            if (loot.getExp() != 0) {
                ExperienceOrb orb = w.spawn(b.getLocation(), ExperienceOrb.class);
                orb.setExperience((int) (event.getExpToDrop() == 0 ? loot.getExp() : loot.getExp() * event.getExpToDrop()));
            }

            for (LootItem item : loot.getLoots()) {
                ItemStack is = item.getLootItem();
                if (is != null) {

                    if (mat == Material.LOG || mat == Material.LOG_2) {
                        int amount = is.getAmount();
                        byte data = (byte) (b.getData() & 0x3);
                        is = new ItemStack(mat, amount, data);
                    }
                    w.dropItemNaturally(b.getLocation().add(0.5, 0.5, 0.5), is);
                }
            }
            event.setExpToDrop(0);
            event.setCancelled(true);
            b.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onInvSpecInfoClick(InventoryClickEvent ev) {
        if (ev.getInventory().getName().startsWith("§7Stuff §6"))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        if(e.getTo().getWorld().getEnvironment() == World.Environment.NETHER && !(boolean)GameConfig.ConfigurableParams.NETHER.getValue()) {
            e.setCancelled(true);
            player.sendMessage(UHC.getPrefix() + "§cLe Nether est désactivé.");
            UHC.playNegativeSound(player);
        } else if(e.getTo().getWorld().getEnvironment() == World.Environment.THE_END && !(boolean)GameConfig.ConfigurableParams.END.getValue()) {
            e.setCancelled(true);
            player.sendMessage(UHC.getPrefix() + "§cL'End est désactivé.");
            UHC.playNegativeSound(player);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        Player player = ev.getPlayer();
        PlayerUHC playerUHC = main.getPlayerUHC(player);
        String msg = ev.getMessage();
        String format = null;

        if (msg == null) return;

        if (main.isState(Gstate.PLAYING) || main.isState(Gstate.FINISHED)) {
            if (!canChat && !playerUHC.isHost()) {
                player.sendMessage(UHC.getPrefix() + "§cLe chat est désactivé !");
                UHC.playNegativeSound(player);
                ev.setCancelled(true);
            } else {
                if (playerUHC.getTeam() == null) {
                    if (playerUHC.isSpec() || !playerUHC.isAlive()) {
                        ev.setCancelled(true);
                        for (PlayerUHC pu : main.players)
                            if (pu.isSpec() && pu.getPlayer().isOnline())
                                pu.getPlayer().getPlayer().sendMessage(player.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §7" + msg);
                    } else format = player.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §f" + msg;
                } else
                    if (msg.startsWith("!"))
                        format = "§8[§7Global§8] " + player.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §f" + msg.substring(1);
                    else {
                        format = "§8[" + playerUHC.getTeam().getPrefix().color.getColor() + "Team" + "§8] " + player.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §f" + msg;
                        ev.setCancelled(true);
                        playerUHC.getTeam().sendMessage(format);
                    }
            }
        } else {
            if (!canChat && !playerUHC.isHost()) {
                player.sendMessage(UHC.getPrefix() + "§cLe chat est désactivé !");
                UHC.playNegativeSound(player);
                ev.setCancelled(true);
            } else format = player.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §f" + msg;
        }

        if (!ev.isCancelled()) ev.setFormat(format);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        List<PlayerUHC> playings = new ArrayList<>();
        for (PlayerUHC pu : main.players) if (!pu.isSpec()) playings.add(pu);

        if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING))
            if (playings.size() >= (int) GameConfig.ConfigurableParams.SLOTS.getValue()) if (!p.isOp()) {
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(UHC.getPrefixWithoutArrow() + "\n" + "§cLe serveur est plein ... \n" + "§7Slots : §8[§e" + playings.size() + "§7/§c" + GameConfig.ConfigurableParams.SLOTS.getValue() + "§8]");
                return;
            }
        playings.clear();
        for (PlayerUHC pu : main.players) if (pu.isAlive()) playings.add(pu);

        if (main.isState(Gstate.PLAYING) && !(boolean)GameConfig.ConfigurableParams.SPECTATORS.getValue() && !playings.contains(main.getPlayerUHC(p))) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(UHC.getPrefixWithoutArrow() + "\n" + " §cLes spectateurs ne sont pas autorisés !");
            return;
        }

        if (main.hasWhitelist && !main.getWhitelist().contains(p)) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(UHC.getPrefixWithoutArrow() + "\n" + "§cVous n'êtes pas whitelisté !");
            return;
        }

        e.setResult(PlayerLoginEvent.Result.ALLOWED);
        e.allow();
    }

}
