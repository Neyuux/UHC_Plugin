package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.teams.TeamPrefix;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CommandRevive implements CommandExecutor {

    private final Index main;
    public CommandRevive(Index index) {
        this.main = index;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (main.isState(Gstate.PLAYING)) {
            if (args.length > 0) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    Player player = Bukkit.getPlayer(args[0]);
                    PlayerUHC playerUHC = main.getPlayerUHC(player);
                    if (!playerUHC.isAlive() && playerUHC.isSpec()) {
                        playerUHC.unfreeze();
                        playerUHC.setInvulnerable(true);
                        playerUHC.setAlive(true);
                        InventoryManager.clearInventory(player);
                        playerUHC.heal();
                        player.setGameMode(GameMode.SURVIVAL);
                        for (Achievement a : Achievement.values())
                            if (player.hasAchievement(a)) player.removeAchievement(a);
                        player.updateInventory();
                        PlayerInventory pi = player.getInventory();
                        HashMap<Integer, ItemStack> armor = playerUHC.getLastArmor();
                        if (armor.get(0) != null)pi.setHelmet(armor.get(0));
                        if (armor.get(1) != null)pi.setChestplate(armor.get(1));
                        if (armor.get(2) != null)pi.setLeggings(armor.get(2));
                        if (armor.get(3) != null)pi.setBoots(armor.get(3));
                        player.getInventory().setContents(playerUHC.getLastInv());
                        player.updateInventory();
                        player.teleport(playerUHC.getLastLocation());
                        if (playerUHC.isHost()) player.setDisplayName(TeamPrefix.getHostPrefix() + player.getName() + "§r");
                        else player.setDisplayName(player.getName());
                        player.setPlayerListName(player.getDisplayName());
                        if (playerUHC.getTeam() != null) playerUHC.getTeam().revive(playerUHC);
                        for (Player pl : Bukkit.getOnlinePlayers())
                            pl.playSound(pl.getLocation(), Sound.CHICKEN_EGG_POP, 7f ,2f);
                        Bukkit.broadcastMessage(main.getPrefix() + "§b" + sender.getName() + " §6a revive " + player.getDisplayName() + "§6.");
                        player.sendMessage(main.getPrefix() + "§6Vous êtes invincible pendant 5 secondes.");
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
                    } else sender.sendMessage(main.getPrefix() + "§cImpossible de ressuciter ce joueur.");
                } else sender.sendMessage(main.getPrefix() + "§cLe joueur §4\"§e" + args[0] + "§4\" §cn'existe pas.");
            } else sender.sendMessage(main.getPrefix() + "§cVeuillez renseigner un joueur.");
        } else sender.sendMessage(main.getPrefix() + "§cVous ne pouvez pas utiliser cette commande si la partie n'est pas commencé.");
        return true;
    }
}
