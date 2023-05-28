package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.events.PlayerReviveEvent;
import fr.neyuux.uhc.teams.TeamPrefix;
import fr.neyuux.uhc.teams.UHCTeamManager;
import fr.neyuux.uhc.util.ScoreboardSign;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class CommandRevive implements CommandExecutor {

    private final UHC main;
    public CommandRevive(UHC UHC) {
        this.main = UHC;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof ConsoleCommandSender || (sender instanceof Player && main.getPlayerUHC(((Player) sender).getUniqueId()).isHost())) {
            if (main.isState(Gstate.PLAYING))
                if (args.length > 0) {
                    if (Bukkit.getPlayer(args[0]) != null) {
                        Player player = Bukkit.getPlayer(args[0]);
                        PlayerUHC playerUHC = main.getPlayerUHC(player.getUniqueId());
                        if (!playerUHC.isAlive() && playerUHC.isSpec()) {
                            PlayerReviveEvent ev = new PlayerReviveEvent(playerUHC);
                            Bukkit.getPluginManager().callEvent(ev);
                            if (!ev.isCancelled()) {
                                playerUHC.unfreeze();
                                playerUHC.setInvulnerable(true);
                                playerUHC.setAlive(true);
                                main.spectators.remove(player);
                                InventoryManager.clearInventory(player);
                                playerUHC.heal();
                                player.setGameMode(GameMode.SURVIVAL);
                                for (Achievement a : Achievement.values())
                                    if (player.hasAchievement(a)) player.removeAchievement(a);
                                player.updateInventory();
                                PlayerInventory pi = player.getInventory();
                                HashMap<Integer, ItemStack> armor = playerUHC.getLastArmor();
                                if (armor.get(0) != null) pi.setHelmet(armor.get(0));
                                if (armor.get(1) != null) pi.setChestplate(armor.get(1));
                                if (armor.get(2) != null) pi.setLeggings(armor.get(2));
                                if (armor.get(3) != null) pi.setBoots(armor.get(3));
                                player.getInventory().setContents(playerUHC.getLastInv());
                                player.updateInventory();
                                if (playerUHC.getLastLocation() == null) playerUHC.setLastLocation(new Location(Bukkit.getWorld(Long.toString(main.world.getSeed())), 0, 100, 0));
                                player.teleport(playerUHC.getLastLocation());
                                if (playerUHC.isHost())
                                    player.setDisplayName(TeamPrefix.getHostPrefix() + player.getName() + "§r");
                                else player.setDisplayName(player.getName());
                                player.setPlayerListName(player.getDisplayName());
                                if (playerUHC.getTeam() != null) playerUHC.getTeam().revive(playerUHC);
                                for (Player pl : Bukkit.getOnlinePlayers())
                                    pl.playSound(pl.getLocation(), Sound.CHICKEN_EGG_POP, 7f, 2f);
                                Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + sender.getName() + " §6a revive " + player.getDisplayName() + "§6.");
                                for (Map.Entry<PlayerUHC, ScoreboardSign> en : main.boards.entrySet())
                                    if (GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA"))
                                        en.getValue().setLine(3, "§7§lJoueurs §7: §f" + main.getAlivePlayers().size());
                                    else
                                        en.getValue().setLine(3, "§7§lTeams : §f" + main.getUHCTeamManager().getAliveTeams().size() + "§8/§7" + UHCTeamManager.baseteams + " §8(§7" + main.getAlivePlayers().size() + "§8 joueurs)");
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
                            } else sender.sendMessage(UHC.getPrefix() + "§cImpossible de ressuciter ce joueur.");
                        } else sender.sendMessage(UHC.getPrefix() + "§cImpossible de ressuciter ce joueur.");
                    } else sender.sendMessage(UHC.getPrefix() + "§cLe joueur §4\"§e" + args[0] + "§4\" §cn'existe pas.");
                } else sender.sendMessage(UHC.getPrefix() + "§cVeuillez renseigner un joueur.");
            else sender.sendMessage(UHC.getPrefix() + "§cVous ne pouvez pas utiliser cette commande si la partie n'est pas commencée.");
        } else sender.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'utiliser cette commande.");
        return true;
    }
}
