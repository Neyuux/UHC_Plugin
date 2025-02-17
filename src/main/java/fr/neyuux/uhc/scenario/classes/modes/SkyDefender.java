package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.UHCWorld;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.GameEndEvent;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.tasks.UHCStop;
import fr.neyuux.uhc.teams.TeamPrefix;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.teams.UHCTeamColors;
import fr.neyuux.uhc.teams.UHCTeamManager;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SkyDefender extends Scenario implements Listener {

    private Location bannerLoc;
    private Location teleporterFromLoc;
    private Location teleporterToLoc;
    private final List<UUID> teleportedRecently = new ArrayList<>();

    public SkyDefender() {
        super(Scenarios.SKY_DEFENDER, new ItemStack(Material.BANNER));
    }

    @Override
    protected void activate() {

        Bukkit.getScheduler().runTaskTimer(UHC.getInstance(), () -> {
            System.out.println(String.valueOf(UHC.getInstance().getUHCTeamManager().getTeams()));
        }, 0L, 50L);

        UHCWorld.setSkydefender(true);
        UHCWorld.addWorld("skydefender", true, skydefworld -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.teleport(new Location(skydefworld, 25, 200, 25));
                InventoryManager.giveWaitInventory(player);
            });
        });

        UHCTeamManager teamManager = UHC.getInstance().getUHCTeamManager();

        teamManager.clearTeams();

        File configfile = new File(UHC.getInstance().getDataFolder(), "config.yml");
        YamlConfiguration yconfig = YamlConfiguration.loadConfiguration(configfile);

        yconfig.set("skydefender.banner", Arrays.asList(0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
        yconfig.set("skydefender.teleporter.from", Arrays.asList(0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
        yconfig.set("skydefender.teleporter.to", Arrays.asList(0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
        try {
            yconfig.save(configfile);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(UHC.getPrefix() + "�4[�cErreur�4] �cImpossible de cr�er la config du Sky Defender !");
        }

        teamManager.createTeam(new TeamPrefix(UHC.getInstance(), UHCTeamColors.BLUE, "�9D�fenseur "));
        teamManager.createTeam(new TeamPrefix(UHC.getInstance(), UHCTeamColors.RED, "�c"));
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        File file = new File(UHC.getInstance().getDataFolder(), "config.yml");
        YamlConfiguration yconfig = YamlConfiguration.loadConfiguration(file);
        World world =  Bukkit.getWorld(UHCWorld.MAIN_WORLD);

        UHCWorld.removePlatform();

        this.bannerLoc = getLocationFromDoubleList(yconfig.getDoubleList("skydefender.banner"), world);
        this.teleporterFromLoc = getLocationFromDoubleList(yconfig.getDoubleList("skydefender.teleporter.from"), world);
        this.teleporterToLoc = getLocationFromDoubleList(yconfig.getDoubleList("skydefender.teleporter.to"), world);

        System.out.println(this.bannerLoc);
        System.out.println(this.getDefenderTeam());
        this.getDefenderTeam().getPlayers().forEach(playerUHC -> {
            playerUHC.getPlayer().getPlayer().teleport(this.bannerLoc);
        });

        this.getDefenderTeam().getTeam().setAllowFriendlyFire(false);
        this.getAttackersTeam().getTeam().setAllowFriendlyFire(true);

        UHCWorld.getArmorStand(teleporterFromLoc.clone().add(0, 1, 0)).setCustomName("�5�lTeleporteur");
        UHCWorld.getArmorStand(teleporterToLoc.clone().add(0, 1, 0)).setCustomName("�5�lTeleporteur");
        Bukkit.getScheduler().runTaskTimer(UHC.getInstance(), () -> {
            this.createParticleColumn(this.teleporterFromLoc);
            this.createParticleColumn(this.teleporterToLoc);
        }, 5L, 20L);
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onEndEvent(GameEndEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onPlayerElimination(PlayerEliminationEvent ev) {
        PlayerUHC playerUHC = ev.getPlayerUHC();

        if (playerUHC.getTeam() != null)
            if (playerUHC.getTeam().equals(this.getDefenderTeam()) && this.getDefenderTeam().getAlivePlayers().size() == 1) {
                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                    Bukkit.broadcastMessage(UHC.getPrefix() + Scenarios.SKY_DEFENDER.getDisplayName() + "�8�l"+ Symbols.DOUBLE_ARROW+" �r" + "�9�lTOUS LES D�FENSEURS SONT MORTS ! LE PREMIER ATTAQUANT A CASSER LA BANNIERE REMPORTERA LA VICTOIRE !");
                }, 1L);
            } else if (playerUHC.getTeam().equals(this.getAttackersTeam()) && this.getAttackersTeam().getAlivePlayers().size() == 1) {
                UHC.stopInfiniteActionBarForAllPlayers();
                UHC.getInstance().setState(Gstate.FINISHED);
                new UHCStop(UHC.getInstance()).runTaskTimer(UHC.getInstance(), 0, 20);

                Bukkit.broadcastMessage(UHC.getPrefix() + "�6Victoire des �9�lD�fenseurs �6!");
                Bukkit.broadcastMessage(UHC.getPrefix() + "�eNombre de gagnants : " + this.getDefenderTeam().getAlivePlayers().size() + " �7:");
                for (PlayerUHC pu : this.getDefenderTeam().getAlivePlayers())
                    Bukkit.broadcastMessage(UHC.getPrefix() + pu.getPlayer().getPlayer().getDisplayName() + " �8(�c" + pu.getKills() + " kills�8)");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ZOMBIE_REMEDY, 9, 1);
                    UHC.sendTitle(p, "�c�l�n�kaa�r �e�l�nVictoire des �9�lD�fenseurs �c�l�n�kaa", "�6�l�nNombre de Survivants�r �7�l: �f" + this.getDefenderTeam().getAlivePlayers().size(), 20, 180, 20);
                }
            }
    }

    @EventHandler
    public void onCheckWin(GameEndEvent ev) {
        if (this.getAttackersTeam().getAlivePlayers().size() > 0)
            ev.setCancelled(true);
    }

    @EventHandler
    public void onBannerBreak(BlockBreakEvent ev) {
        Block b = ev.getBlock();

        if (b.getType() != Material.WALL_BANNER && b.getType() != Material.STANDING_BANNER)
            return;

        if (b.getLocation().distanceSquared(this.bannerLoc) > 16)
            return;

        Player winner = ev.getPlayer();

        if (this.getDefenderTeam().getAlivePlayers().size() > 0) {
            ev.setCancelled(true);
            winner.sendMessage(UHC.getPrefix() + Scenarios.SKY_DEFENDER.getDisplayName() + "�8�l"+ Symbols.DOUBLE_ARROW+" �r" + "�cTous les d�fenseurs doivent �tre morts pour pouvoir casser la banni�re !");
            UHC.playNegativeSound(winner);
            return;
        }

        UHC.stopInfiniteActionBarForAllPlayers();
        UHC.getInstance().setState(Gstate.FINISHED);
        new UHCStop(UHC.getInstance()).runTaskTimer(UHC.getInstance(), 0, 20);

        PlayerUHC winnerUHC = UHC.getInstance().getPlayerUHC(winner.getUniqueId());
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(UHC.getPrefix() + winner.getDisplayName() + " �6remporte la partie !");
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ZOMBIE_REMEDY, 9, 1);
            UHC.sendTitle(p, "�c�l�n�kaa�r �e�l�nVictoire�e�l �nde�e�l " + winner.getDisplayName() + " �c�l�n�kaa", "�6�l�nNombre de Kills �7�l: �f" + winnerUHC.getKills(), 20, 180, 20);
        }
    }

    @EventHandler
    public void onMooveTeleporter(PlayerMoveEvent ev) {

        if (UHC.getInstance().getPlayerUHC(ev.getPlayer().getUniqueId()) == null)
            return;

        if (this.getDefenderTeam() == null)
            return;

        if (!this.getDefenderTeam().getAlivePlayers().contains(UHC.getInstance().getPlayerUHC(ev.getPlayer().getUniqueId())))
            return;

        if (this.teleportedRecently.contains(ev.getPlayer().getUniqueId()))
            return;

        Location to = ev.getTo();

        if (to.distanceSquared(this.teleporterFromLoc) < 1.5D) {
            this.teleport(ev, this.teleporterToLoc);
        } else if (to.distanceSquared(this.teleporterToLoc) < 1.5D)
            this.teleport(ev, this.teleporterFromLoc);

    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent ev) {
        switch (ev.getEntityType()) {
            case ZOMBIE:
            case ENDERMAN:
            case SPIDER:
            case SKELETON:
            case CREEPER:
                if (ev.getLocation().getY() > 100)
                    ev.setCancelled(true);
                break;
        }
    }


    private static Location getLocationFromDoubleList(List<Double> list, World world) {
        return new Location(world, list.get(0), list.get(1), list.get(2), list.get(3).floatValue(), list.get(4).floatValue());
    }

    private void teleport(PlayerMoveEvent ev, Location newto) {
        UUID id = ev.getPlayer().getUniqueId();

        this.teleportedRecently.add(id);
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> this.teleportedRecently.remove(id), 40L);

        ev.setTo(newto);
    }



    public void createParticleColumn(Location centerLocation) {
        EnumParticle particle = EnumParticle.PORTAL;

        double radius = 0.8;
        double height = 2.5;
        int particlesPerBlock = 8;

        for (double y = 0; y <= height; y += 0.1) {
            for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (radius * particlesPerBlock)) {
                double x = centerLocation.getX() + radius * Math.cos(angle);
                double z = centerLocation.getZ() + radius * Math.sin(angle);
                Location particleLocation = new Location(centerLocation.getWorld(), x, centerLocation.getY() + y, z);

                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                        particle, 
                        true,                // Indique si les coordonn�es sont relatives ou absolues (true pour relatives)
                        (float) particleLocation.getX(),           // Coordonn�e X
                        (float) particleLocation.getY(),           // Coordonn�e Y
                        (float) particleLocation.getZ(),           // Coordonn�e Z
                        0,                   // D�calage X
                        0,                   // D�calage Y
                        0,                   // D�calage Z
                        0,                   // Vitesse de la particule
                        1                    // Nombre de particules � afficher
                );

                Bukkit.getOnlinePlayers().stream().filter(player -> player.getLocation().distanceSquared(centerLocation) < 50*50).forEach(player -> {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                });
            }
        }
    }

    private UHCTeam getDefenderTeam() {
        UHCTeamManager teamManager = UHC.getInstance().getUHCTeamManager();
        System.out.println(teamManager);
        System.out.println(Scenarios.SKY_DEFENDER.isActivated() + " / " + teamManager.getTeams().size());
        if (Scenarios.SKY_DEFENDER.isActivated() && teamManager.getTeams().size() <= 2)
            return teamManager.getByID(1);
        else
            return null;
    }

    private UHCTeam getAttackersTeam() {
        UHCTeamManager teamManager = UHC.getInstance().getUHCTeamManager();
        if (Scenarios.SKY_DEFENDER.isActivated() && teamManager.getTeams().size() <= 2)
            return teamManager.getByID(2);
        else
            return null;
    }
}
