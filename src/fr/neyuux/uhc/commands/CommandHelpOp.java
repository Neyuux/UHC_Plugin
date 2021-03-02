package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandHelpOp implements CommandExecutor {

    private final Index main;
    public CommandHelpOp(Index main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        for (PlayerUHC pu : main.players)
            if (pu.isHost() && pu.getPlayer().isOnline())
                if (pu.isSpec() || !main.getState().equals(Gstate.PLAYING)) {
                    pu.getPlayer().getPlayer().sendMessage(main.getPrefix() + "§5§lHelp-Op §8" + Symbols.DOUBLE_ARROW + " §b" + sender.getName() + " §8" + Symbols.DOUBLE_ARROW + " §d" + Arrays.stream(args).map(part -> part + " ").collect(Collectors.joining()));
                } else
                    pu.getPlayer().getPlayer().sendMessage(main.getPrefix() + "§5§lHelp-Op §8" + Symbols.DOUBLE_ARROW + " §d" + Arrays.stream(args).map(part -> part + " ").collect(Collectors.joining()));
        return true;
    }
}
