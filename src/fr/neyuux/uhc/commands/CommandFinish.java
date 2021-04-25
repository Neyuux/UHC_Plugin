package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.InventoryManager;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CommandFinish implements CommandExecutor {

    private final Index main;

    public CommandFinish(Index index) {
        this.main = index;
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
                player.sendMessage(main.getPrefix() + "�dVous avez enregistr� l'inventaire de D�part !");
                Index.playPositiveSound(player);
            } else if (main.getGameConfig().deathInvModifier != null && main.getGameConfig().deathInvModifier.equals(player)) {
                main.getGameConfig().deathInvModifier = null;
                main.getInventoryManager().getDeathInventory().clear();
                for (ItemStack it : player.getInventory().getContents()) if (it != null) main.getInventoryManager().getDeathInventory().add(it);
                player.sendMessage(main.getPrefix() + "�5Vous avez enregistr� l'inventaire de Mort !");
                Index.playPositiveSound(player);
            } else {
                player.sendMessage(main.getPrefix() + "�cVous ne modifiez aucun inventaire !");
                Index.playNegativeSound(player);
            }
            InventoryManager.clearInventory(player);
            InventoryManager.giveWaitInventory(main.getPlayerUHC(player));
            player.setGameMode(GameMode.ADVENTURE);
        }

        return true;
    }
}
