package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class CommandScenario implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (Scenarios.getActivatedScenarios().size() != 0) {
                Inventory inv = Bukkit.createInventory(null, UHC.adaptInvSizeForInt(Scenarios.getActivatedScenarios().size(), 0), "§6Liste des §lScénarios activés");
                for (Scenarios sc : Scenarios.getActivatedScenarios()) {
                    try {
                        Class<?> c = sc.getScenarioClass();
                        ItemsStack it = new ItemsStack((ItemStack) c.getMethod("getMenuItem").invoke(c.newInstance()));

                        inv.addItem(it.toItemStack());
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                        e.printStackTrace();
                        Bukkit.broadcastMessage(UHC.getPrefix() + "§4[§cErreur§4] §cUne erreur s'est produite lors du chargement du menu des scénarios activés, veuillez en informer Neyuux_ !");
                    }
                }
                player.openInventory(inv);
            } else player.sendMessage(UHC.getPrefix() + "§cAucun Scénario n'a été activé.");
        }

        return true;
    }
}
