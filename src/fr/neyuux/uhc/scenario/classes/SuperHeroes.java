package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SuperHeroes extends Scenario implements Listener {
    public SuperHeroes() {
        super(Scenarios.SUPER_HEROES, new ItemStack(Material.NETHER_STAR));
    }

    public static boolean hasResistance = true, hasStrength = true, hasSpeed = true, hasJumpBoost = true, hasDoubleHealth = true, hasInvisibility;
    public static HashMap<PlayerUHC, PotionEffectType> powers = new HashMap<>();
    private static final HashMap<PlayerUHC, PotionEffectType> needPower = new HashMap<>();

    @Override
    public void activate() {
        if ((boolean) GameConfig.ConfigurableParams.MILK.getValue())
            Bukkit.broadcastMessage(Index.getStaticPrefix() + "§cVeuillez désactiver le seau de lait pour que " + scenario.getDisplayName() + " §cpuisse fonctionner.");
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);

        Bukkit.getScheduler().runTaskLater(Index.getInstance(), () -> {
            ArrayList<PotionEffectType> p = new ArrayList<>(setPowerList());
            if (Index.getInstance().getUHCTeamManager().getAliveTeams().size() != 0) {
                for (UHCTeam t : Index.getInstance().getUHCTeamManager().getAliveTeams()) {
                    p.clear(); p.addAll(setPowerList());
                    for (PlayerUHC pu : t.getPlayers()) {
                        if (p.isEmpty()) p.addAll(setPowerList());
                        addPower(pu, p.remove(new Random().nextInt(p.size())));
                    }
                }
            } else {
                for (PlayerUHC pu : Index.getInstance().getAlivePlayers())
                    addPower(pu, p.get(new Random().nextInt(p.size())));
            }
        }, 100L);
    }

    @Override
    public boolean checkStart() {
        return !(boolean)GameConfig.ConfigurableParams.MILK.getValue();
    }


    @EventHandler
    public void onJBFallDamage(EntityDamageEvent ev) {
        if (ev.getEntityType().equals(EntityType.PLAYER) && ev.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            PlayerUHC pu = Index.getInstance().getPlayerUHC((Player)ev.getEntity());
            if (pu.isAlive() && powers.containsKey(pu) && powers.get(pu).equals(PotionEffectType.JUMP)) ev.setCancelled(true);
        }
    }

    @EventHandler
    public void nerfRez(EntityDamageEvent ev) {
        if (ev.getEntityType().equals(EntityType.PLAYER)) {
            PlayerUHC pu = Index.getInstance().getPlayerUHC((Player)ev.getEntity());
            if (pu.isAlive() && powers.containsKey(pu) && powers.get(pu).equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                System.out.println("Nerf Réz : base réz : " + ev.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) + " base total : " + ev.getFinalDamage());
                ev.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, -(ev.getDamage() - (ev.getDamage() * 0.7)));
                System.out.println("after nerf réz: " + ev.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) + " after nerf total : " + ev.getFinalDamage());
            }
        }
    }

    @EventHandler
    public void onJoinNeedPower(PlayerJoinEvent ev) {
        if (needPower.containsKey(Index.getInstance().getPlayerUHC(ev.getPlayer()))) {
            addPower(Index.getInstance().getPlayerUHC(ev.getPlayer()), needPower.get(Index.getInstance().getPlayerUHC(ev.getPlayer())));
        }
    }


    private void addPower(PlayerUHC pu, PotionEffectType power) {
        powers.put(pu, power);
        if (pu.getPlayer().isOnline()) {
            Player p = pu.getPlayer().getPlayer();
            if (power.equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, true, true));
                p.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §eVous obtenez le pouvoir §7§lRésistance 2§e!");
            } else if (power.equals(PotionEffectType.JUMP)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3, true, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, true, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, true));
                p.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §eVous obtenez le pouvoir §a§lSaut Amélioré 4§e !");
            } else if (power.equals(PotionEffectType.INCREASE_DAMAGE)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, true, true));
                p.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §eVous obtenez le pouvoir §4§lForce §e!");
            } else if (power.equals(PotionEffectType.SPEED)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0, true, true));
                p.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §eVous obtenez le pouvoir §b§lRapidité 2§e!");
            } else if (power.equals(PotionEffectType.HEALTH_BOOST)) {
                p.setMaxHealth(p.getMaxHealth() * 2);
                p.setHealth(p.getMaxHealth());
                p.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §eVous obtenez le pouvoir §c§lDouble Vie §e!");
            } else if (power.equals(PotionEffectType.INVISIBILITY)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                p.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §eVous obtenez le pouvoir §3§lInvisibilité §e!");
            }
        } else {
            if (!power.equals(PotionEffectType.HEALTH_BOOST)) {
                needPower.put(pu, power);
            } else {
                pu.maxHealth *= 2;
                pu.health *= 2;
            }
        }
    }

    private static ArrayList<PotionEffectType> setPowerList() {
        ArrayList<PotionEffectType> list = new ArrayList<>();
        if (hasDoubleHealth) list.add(PotionEffectType.HEALTH_BOOST);
        if (hasResistance) list.add(PotionEffectType.DAMAGE_RESISTANCE);
        if (hasJumpBoost) list.add(PotionEffectType.JUMP);
        if (hasStrength) list.add(PotionEffectType.INCREASE_DAMAGE);
        if (hasSpeed) list.add(PotionEffectType.SPEED);
        if (hasInvisibility) list.add(PotionEffectType.INVISIBILITY);
        return list;
    }
}
