package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.InventoryManager;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CommandFinish implements CommandExecutor {

    private final UHC main;

    public CommandFinish(UHC UHC) {
        this.main = UHC;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (main.getGameConfig().starterModifier != null && main.getGameConfig().starterModifier.equals(player)) {
                main.getGameConfig().starterModifier = null;
                InventoryManager.startInventory = new ItemStack[]{};
                main.getInventoryManager().getStartArmor().clear();
                PlayerInventory pi = player.getInventory();
                if (pi.getHelmet() != null)main.getInventoryManager().getStartArmor().put(0, pi.getHelmet());
                if (pi.getChestplate() != null)main.getInventoryManager().getStartArmor().put(1, pi.getChestplate());
                if (pi.getLeggings() != null)main.getInventoryManager().getStartArmor().put(2, pi.getLeggings());
                if (pi.getBoots() != null)main.getInventoryManager().getStartArmor().put(3, pi.getBoots());

                InventoryManager.startInventory = pi.getContents();
                InventoryManager.clearInventory(player);
                InventoryManager.giveWaitInventory(player);
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage(UHC.getPrefix() + "§dVous avez enregistré l'inventaire de Départ !");
                UHC.playPositiveSound(player);
            } else if (main.getGameConfig().deathInvModifier != null && main.getGameConfig().deathInvModifier.equals(player)) {
                main.getGameConfig().deathInvModifier = null;
                main.getInventoryManager().getDeathInventory().clear();
                for (ItemStack it : player.getInventory().getContents()) if (it != null) main.getInventoryManager().getDeathInventory().add(it);
                player.sendMessage(UHC.getPrefix() + "§5Vous avez enregistré l'inventaire de Mort !");
                UHC.playPositiveSound(player);
            } else {
                player.sendMessage(UHC.getPrefix() + "§cVous ne modifiez aucun inventaire !");
                UHC.playNegativeSound(player);
            }
        }
        return true;
    }
}
