package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Gstate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHeal implements CommandExecutor {

    private final Index main;
    public CommandHeal(Index index) {
        this.main = index;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        if (main.isState(Gstate.PLAYING)) {
            healAll();
            Bukkit.broadcastMessage(main.getPrefix() + "§b" + sender.getName() +" §6a soigné tout le monde !");
        } else sender.sendMessage(main.getPrefix() + "§cLa partie n'est pas démarrée !");

        return true;
    }

    public static void healAll() {
        for (PlayerUHC pu : Index.getInstance().getAlivePlayers()) {
            pu.heal();
            if (pu.getPlayer().isOnline()) {
                pu.getPlayer().getPlayer().sendMessage(Index.getStaticPrefix() + "§dVous avez été soigné !");
                Index.playPositiveSound(pu.getPlayer().getPlayer());
            }
        }
    }
}
