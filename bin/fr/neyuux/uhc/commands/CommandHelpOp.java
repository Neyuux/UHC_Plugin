package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.Index;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHelpOp implements CommandExecutor {

    private Index main;
    public CommandHelpOp(Index index) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
