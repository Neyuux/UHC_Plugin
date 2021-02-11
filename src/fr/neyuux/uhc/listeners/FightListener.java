package fr.neyuux.uhc.listeners;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.tasks.UHCStop;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.teams.UHCTeamManager;
import fr.neyuux.uhc.util.ScoreboardSign;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class FightListener implements Listener {

    private final Index main;
    public FightListener (Index main) {
        this.main = main;
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent ev) {
        Entity e = ev.getEntity();
        double fdamage = ev.getFinalDamage();

        if (e.getType().equals(EntityType.PLAYER) && main.isState(Gstate.PLAYING)) {
            Player player = (Player)e;
            PlayerUHC playerUHC = main.getPlayerUHC(player);

            if (playerUHC.isInvulnerable()) ev.setCancelled(true);
            if (ev.isCancelled()) return;

            playerUHC.health = player.getHealth() + playerUHC.absorption - fdamage;
            playerUHC.absorption = (float) (((CraftPlayer) player).getHandle().getAbsorptionHearts() - fdamage);
            if (playerUHC.health < 0) playerUHC.health = 0;
            if (playerUHC.absorption < 0) playerUHC.absorption = 0;

            if (playerUHC.health <= 0.0) {
                ev.setCancelled(true);
                boolean el = true;
                String deathmessage = player.getDisplayName() + "§c est Mort.";
                switch (ev.getCause()) {
                    case CONTACT:
                        deathmessage = player.getDisplayName() + "§c s'est fait 1v1 par un Cactus.";
                        break;
                    case SUFFOCATION:
                        deathmessage = player.getDisplayName() + "§cs'est fait BLOCKé hahaha";
                        break;
                    case FALL:
                        deathmessage = player.getDisplayName() + "§c a chuté.";
                        break;
                    case FIRE:
                        deathmessage = player.getDisplayName() + "§c s'est enflammé.";
                        break;
                    case FIRE_TICK:
                        deathmessage = player.getDisplayName() + "§c est parti en fumée.";
                        break;
                    case LAVA:
                        deathmessage = player.getDisplayName() + "§c s'est LAVÉ hahahaha";
                        break;
                    case DROWNING:
                        deathmessage = player.getDisplayName() + "§c s'est suicidé... Enfin il est mort de noyage, qui meurt comme ça sérieux ?";
                        break;
                    case BLOCK_EXPLOSION:
                        deathmessage = player.getDisplayName() + "§c s'est fait pété par un block.";
                        break;
                    case ENTITY_EXPLOSION:
                        deathmessage = player.getDisplayName() + "§c a pété.";
                        break;
                    case VOID:
                        deathmessage = player.getDisplayName() + "§c a tourné dans le vide.";
                        break;
                    case LIGHTNING:
                        deathmessage = player.getDisplayName() + "§c est désormais ÉCLAIRÉ hahahahahahaha";
                        break;
                    case SUICIDE:
                        deathmessage = player.getDisplayName() + "§c s'est fait /kill ez";
                        break;
                    case STARVATION:
                        deathmessage = player.getDisplayName() + "§c est mort de faim. (Faites des dons)";
                        break;
                    case MAGIC:
                        deathmessage = player.getDisplayName() + "§c est mort par des effets de substances liquides mystérieuses...";
                        break;
                    case WITHER:
                        deathmessage = player.getDisplayName() + "§c est mort de l'effet du Wither.";
                        break;
                    case FALLING_BLOCK:
                        deathmessage = player.getDisplayName() + "§c s'est fait tombé dessus (par un block ouais).";
                        break;
                    default:
                        el = false;
                        break;
                }
                if (el) eliminate(player, true, null, deathmessage);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageByEntity(EntityDamageByEntityEvent ev) {
        Entity e = ev.getEntity();
        Entity d = ev.getDamager();

        if (e.getType().equals(EntityType.PLAYER) && main.isState(Gstate.PLAYING)) {
            Player player = (Player) e;
            PlayerUHC playerUHC = main.getPlayerUHC(player);

            if (d.getType().equals(EntityType.ARROW) && ((Arrow)d).getShooter() instanceof Player){
                Player dp = (Player)((Arrow)d).getShooter();
                NumberFormat format = NumberFormat.getInstance();
                format.setRoundingMode(RoundingMode.UP);
                format.setMaximumFractionDigits(1);

                String formattedPlayerHealth;
                if (Scenarios.TEAM_HEALTH.isActivated()) {
                    formattedPlayerHealth = format.format(playerUHC.getTeam().getHealth() - ev.getFinalDamage());
                    dp.sendMessage(main.getPrefix() + "§fL'équipe " + playerUHC.getTeam().getTeam().getDisplayName() + " §fpossède en tout §4§l" + formattedPlayerHealth + Symbols.HEARTH + "§f.");
                } else {
                    formattedPlayerHealth = format.format(Bukkit.getScoreboardManager().getMainScoreboard().getObjective("health").getScore(player.getName()).getScore() - ev.getFinalDamage());
                    dp.sendMessage(main.getPrefix() + player.getDisplayName() + " §fpossède en actuellement §4§l" + formattedPlayerHealth + Symbols.HEARTH + "§f.");
                }
            }

            if (playerUHC.health <= 0.0) {
                ev.setCancelled(true);
                switch (d.getType()) {

                    case ARROW:
                        Arrow a = (Arrow)d;
                        if (a.getShooter() instanceof Player) {
                            Player killer = (Player)a.getShooter();
                            eliminate(player, true, killer, player.getDisplayName() + "§c a été tué par la flèche de " + killer.getDisplayName() + "§c.");
                        } else
                            eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par une flèche.");
                        break;
                    case FIREBALL:
                    case SMALL_FIREBALL:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait explosé par une fireball.");
                        break;
                    case ENDER_PEARL:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par une enderpearl.");
                        break;
                    case WITHER_SKULL:
                    case WITHER:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un Wither.");
                        break;
                    case PRIMED_TNT:
                        TNTPrimed tnt = (TNTPrimed)d;
                        if (tnt.getSource() != null && tnt.getSource().getType().equals(EntityType.PLAYER))
                            eliminate(player, true, (Player)tnt.getSource(), player.getDisplayName() + "§c s'est fait explosé par " + ((Player)tnt.getSource()).getDisplayName() + "§c.");
                        else eliminate(player, true, null, player.getDisplayName() + "§c s'est fait explosé par une TNT.");
                        break;
                    case FALLING_BLOCK:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un block (??).");
                        break;
                    case MINECART_TNT:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un Minecart contenant des explosifs.");
                        break;
                    case CREEPER:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait explosé par un Creeper.");
                        break;
                    case SKELETON:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un squelette.");
                        break;
                    case SPIDER:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par une araignée.");
                        break;
                    case GIANT:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un Géant (??).");
                        break;
                    case ZOMBIE:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un zombie.");
                        break;
                    case SLIME:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par une flèche.");
                        break;
                    case GHAST:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un Ghast.");
                        break;
                    case PIG_ZOMBIE:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un cochon-zombie.");
                        break;
                    case ENDERMAN:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un Enderman.");
                        break;
                    case CAVE_SPIDER:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par une araignée des cavernes.");
                        break;
                    case SILVERFISH:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un poisson d'argent (Silverfish au fait).");
                        break;
                    case BLAZE:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un Blaze.");
                        break;
                    case MAGMA_CUBE:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un cube de magma.");
                        break;
                    case ENDER_DRAGON:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par l'EnderDragon.");
                        break;
                    case WITCH:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par une Witch.");
                        break;
                    case ENDERMITE:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par une Endermite.");
                        break;
                    case GUARDIAN:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un Guardian.");
                        break;
                    case WOLF:
                        Wolf w = (Wolf)d;
                        if (w.isTamed() && w.getOwner() instanceof Player) {
                            eliminate(player, true, (Player)w.getOwner(), player.getDisplayName() + "§c s'est fait tué par "+w.getCustomName()+", le chien de "+((Player)w.getOwner()).getDisplayName()+"§c.");
                        } else eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un chien.");
                        break;
                    case IRON_GOLEM:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un golem.");
                        break;
                    case RABBIT:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un lapin.");
                        break;
                    case ENDER_CRYSTAL:
                        eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par un crystal de l'end.");
                        break;
                    case SPLASH_POTION:
                        ThrownPotion tp = (ThrownPotion)d;
                        if (tp.getShooter() != null && tp.getShooter() instanceof Player)
                            eliminate(player, true, (Player)tp.getShooter(), player.getDisplayName() + "§c a été tué par la potion de " + ((Player)tp.getShooter()).getDisplayName() + "§c.");
                        else eliminate(player, true, null, player.getDisplayName() + "§c s'est fait tué par une potion.");
                        break;
                    case PLAYER:
                        Player killer = (Player)d;
                        eliminate(player, true, killer, player.getDisplayName() + "§c s'est fait tué par " + killer.getDisplayName() + "§c.");
                        break;
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onNerfStrength(EntityDamageByEntityEvent e) {
        if(e.isCancelled()) return;
        if (!(e.getDamager() instanceof LivingEntity)) return;
        LivingEntity le = (LivingEntity)e.getDamager();

        for (PotionEffect effect : le.getActivePotionEffects()) {
            if (!effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) continue;
            int n = effect.getAmplifier() + 1;

            e.setDamage(10 * e.getDamage() / (10.0 + 13.0 * (double) n) + 13.0 * e.getDamage() * (double) n * (double)GameConfig.ConfigurableParams.STRENGTH_NERF.getValue() / 100.0 / (10.0 + 13.0 * (double) n));
        }
    }


    public void eliminate(Player player, boolean saveStuff, Player killer, String deathMessage) {
        PlayerUHC up = main.getPlayerUHC(player);
        up.setAlive(false);

        if(saveStuff) {
            up.setLastLocation(player.getLocation());

            HashMap<Integer, ItemStack> lastArmor = new HashMap<>();
            if (player.getInventory().getHelmet() != null)lastArmor.put(0, player.getInventory().getHelmet());
            if (player.getInventory().getChestplate() != null)lastArmor.put(1, player.getInventory().getChestplate());
            if (player.getInventory().getLeggings() != null)lastArmor.put(2, player.getInventory().getLeggings());
            if (player.getInventory().getBoots() != null)lastArmor.put(3, player.getInventory().getBoots());

            up.setLastArmor(lastArmor);
            up.setLastInv(player.getInventory().getContents());
        }
        if(!(boolean) GameConfig.ConfigurableParams.SPECTATORS.getValue() && !up.getPlayer().isOp() && !up.getPlayer().isWhitelisted()){
            player.setWhitelisted(false);
            player.sendMessage(main.getPrefix() + "§cVous êtes Mort.");

            new BukkitRunnable() {
                public void run() {
                    if (!player.isOnline()) return;

                    player.kickPlayer("§4Merci d'avoir joué ! \n §cLes spectateurs ne sont pas autorisés !");
                }
            }.runTaskLater(main, 400);
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            main.spectators.add(player);
            player.setDisplayName("§8[§7Spectateur§8] §7" + player.getName());
            player.setPlayerListName(player.getDisplayName());
            up.heal();
            player.sendMessage(main.getPrefix() + "§6Votre mode de jeu à été établi en spectateur.");
        }

        if ((boolean)GameConfig.ConfigurableParams.LIGHTNING.getValue())
            player.getWorld().strikeLightningEffect(player.getLocation());
        if(!Scenarios.TIME_BOMB.isActivated() && !Scenarios.GRAVE_ROBBERS.isActivated()) {
            if ((boolean)GameConfig.ConfigurableParams.BARRIER_HEAD.getValue()) {
                player.getLocation().getBlock().setType(Material.NETHER_FENCE);
                Block b = player.getWorld().getBlockAt(player.getLocation().add(0, 1, 0));
                b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);
                Skull skull = (Skull) b.getState();
                skull.setSkullType(SkullType.PLAYER);
                skull.setOwner(player.getName());
                skull.setRotation(BlockFace.NORTH);
                skull.update(true);
            }
            InventoryManager.dropDeathStuff(player);
            InventoryManager.clearInventory(player);
            player.updateInventory();
        }
        if (killer != null)Bukkit.getPluginManager().callEvent(new PlayerEliminationEvent(up, main.getPlayerUHC(killer), main));
        else Bukkit.getPluginManager().callEvent(new PlayerEliminationEvent(up, null, main));

        if (killer != null){
            PlayerUHC killerUHC = main.getPlayerUHC(killer);
            killerUHC.addKill();
            if (killerUHC.getTeam() != null)
                for (PlayerUHC pu : killerUHC.getTeam().getPlayers()) {
                    if (killerUHC.getTeam() != null)
                        main.boards.get(pu).setLine(4, "§c§lKills §c: §l" + killerUHC.getKills() + " §4(" + killerUHC.getTeam().getAlivePlayersKills() + ")");
                    else main.boards.get(pu).setLine(4, "§c§lKills §c: §l" + killerUHC.getKills());
                }
        }

        Bukkit.broadcastMessage(main.getPrefix() + deathMessage);
        for (Player p : Bukkit.getOnlinePlayers())
            p.playSound(p.getLocation(), Sound.WITHER_DEATH, 3, 1);

        if (up.getTeam() != null) {
            UHCTeam team = up.getTeam();
            team.death(up);
            if (team.getAlivePlayers().size() == 0)
                Bukkit.broadcastMessage(main.getPrefix() + "§6L'équipe " + team.getPrefix().toString() + " §6est éliminée...");
        }
        for (Map.Entry<PlayerUHC, ScoreboardSign> en : main.boards.entrySet())
            if (GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA"))
                en.getValue().setLine(3, "§7§lJoueurs §7: §f" + main.getAlivePlayers().size());
            else
                en.getValue().setLine(3, "§7§lTeams : §f" + main.getUHCTeamManager().getTeams().size() + "§8/§7" + UHCTeamManager.baseteams + " §8(§7" + main.getAlivePlayers().size() + "§8 joueurs)");

        if (GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA") && main.getAlivePlayers().size() < 2) {
            main.setState(Gstate.FINISHED);
            new UHCStop(main).runTaskTimer(main, 0, 20);
            if (main.getAlivePlayers().size() == 1) {
                PlayerUHC winner = main.getAlivePlayers().get(0);
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(main.getPrefix() + winner.getPlayer().getPlayer().getDisplayName() + " §6remporte la partie !");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ZOMBIE_REMEDY, 9, 1);
                    main.sendTitle(p, "§c§l§n§kaa§r §e§l§nVictoire§e§l §nde§e§l " +winner.getPlayer().getPlayer().getDisplayName()+ " §c§l§n§kaa", "§6§l§nNombre de Kills §7§l: §f" + winner.getKills(), 20, 180, 20);
                }
            } else {
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(main.getPrefix() + "§6Aucun joueur ne s'en est sorti vivant. §cÉGALITÉE PARFAITE.");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ZOMBIE_REMEDY, 8, 1);
                    main.sendTitle(p, "§5§kaa §c§l§nÉgalité§r §5§kaa", "§cAucun survivant.", 20, 120, 20);
                }
            }
        } else if (!GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA") && main.getUHCTeamManager().getAliveTeams().size() < 2) {
            main.setState(Gstate.FINISHED);
            new UHCStop(main).runTaskTimer(main, 0, 20);
            if (main.getUHCTeamManager().getAliveTeams().size() == 1) {
                UHCTeam team = main.getUHCTeamManager().getAliveTeams().get(0);
                Bukkit.broadcastMessage(main.getPrefix() + "§6La Team " + team.getTeam().getDisplayName() + " §6a gagné !");
                Bukkit.broadcastMessage(main.getPrefix() + "§eNombre de gagnants : " + team.getAlivePlayers().size() + " §7:");
                for (PlayerUHC pu : team.getAlivePlayers())
                    Bukkit.broadcastMessage(main.getPrefix() + pu.getPlayer().getPlayer().getDisplayName() + " §8(§c" + pu.getKills() + " kills§8)");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ZOMBIE_REMEDY, 9, 1);
                    main.sendTitle(p, "§c§l§n§kaa§r §e§l§nVictoire de la team " + team.getTeam().getDisplayName() + " §c§l§n§kaa", "§6§l§nNombre de Survivants §7§l: §f" + team.getAlivePlayers().size(), 20, 180, 20);
                }
            } else if (main.getUHCTeamManager().getAliveTeams().size() == 0) {
                Bukkit.broadcastMessage(main.getPrefix() + "§6Aucune Team ne s'en est sortie vivante. §cÉGALITÉE PARFAITE.");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ZOMBIE_REMEDY, 8, 1);
                    main.sendTitle(p, "§5§kaa §c§l§nÉgalité§r §5§kaa", "§cAucun survivant.", 20, 120, 20);
                }
            }
        }
    }

}
