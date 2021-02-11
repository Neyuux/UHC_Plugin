package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.config.GameConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

public class CommandEnchant implements CommandExecutor {

    private final Index main;

    public CommandEnchant(Index index) {
        this.main = index;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR)) return true;
            if (((main.getGameConfig().starterModifier != null) && main.getGameConfig().starterModifier.equals(player)) || ((main.getGameConfig().deathInvModifier != null) && main.getGameConfig().deathInvModifier.equals(player))) {
                Inventory inv = Bukkit.createInventory(null, 54, "§fEnchantement d'un objet");
                GameConfig.setInvCoin(inv, (short)6);
                inv.setItem(4, player.getItemInHand());

                int ord = 0;
                for (int i = 10; i <= 43; i++) {
                    if (ord + 1 > Enchantment.values().length) continue;
                    Enchantment e = Enchantment.values()[ord];
                    ItemsStack it = new ItemsStack(Material.ENCHANTED_BOOK, "§5§l" + translateEnchantName(e));
                    if (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35)
                        while (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35) i++;

                    it.setLore("§bValeur : §d§l" + inv.getItem(4).getEnchantmentLevel(e));

                    it.addUnSafeEnchantement(e, 1);
                    it.addItemFlag(ItemFlag.HIDE_ENCHANTS);
                    it.addLore("", "§a>>Clique gauche pour ajouter", "§c>>Clique droit pour retirer", "§b>>Shift + Clic pour ajouter ou retirer 10");
                    inv.setItem(i, it.toItemStack());
                    ord++;
                }
                player.openInventory(inv);
            }
        }

        return true;
    }

    public static String translateEnchantName(Enchantment e) {
        String t = "Translation";
        if (Enchantment.ARROW_DAMAGE.getName().equals(e.getName())) {
            t = "Puissance";
        } else if (Enchantment.ARROW_FIRE.getName().equals(e.getName())) {
            t = "Flamme";
        } else if (Enchantment.ARROW_INFINITE.getName().equals(e.getName())) {
            t = "Infinité";
        } else if (Enchantment.ARROW_KNOCKBACK.getName().equals(e.getName())) {
            t = "Frappe";
        } else if (Enchantment.DAMAGE_ALL.getName().equals(e.getName())) {
            t = "Tranchant";
        } else if (Enchantment.DAMAGE_ARTHROPODS.getName().equals(e.getName())) {
            t = "Fléau des Arthropodes";
        } else if (Enchantment.DAMAGE_UNDEAD.getName().equals(e.getName())) {
            t = "Châtiment";
        } else if (Enchantment.DEPTH_STRIDER.getName().equals(e.getName())) {
            t = "Agilité Aquatique";
        } else if (Enchantment.DIG_SPEED.getName().equals(e.getName())) {
            t = "Efficacité";
        } else if (Enchantment.DURABILITY.getName().equals(e.getName())) {
            t = "Durabilité";
        } else if (Enchantment.FIRE_ASPECT.getName().equals(e.getName())) {
            t = "Aura de Feu";
        } else if (Enchantment.KNOCKBACK.getName().equals(e.getName())) {
            t = "Recul";
        } else if (Enchantment.LOOT_BONUS_BLOCKS.getName().equals(e.getName())) {
            t = "Fortune";
        } else if (Enchantment.LOOT_BONUS_MOBS.getName().equals(e.getName())) {
            t = "Butin";
        } else if (Enchantment.LUCK.getName().equals(e.getName())) {
            t = "Chance de la Mer";
        } else if (Enchantment.LURE.getName().equals(e.getName())) {
            t = "Appât";
        } else if (Enchantment.OXYGEN.getName().equals(e.getName())) {
            t = "Respiration";
        } else if (Enchantment.PROTECTION_ENVIRONMENTAL.getName().equals(e.getName())) {
            t = "Protection";
        } else if (Enchantment.PROTECTION_EXPLOSIONS.getName().equals(e.getName())) {
            t = "Protection contre les Explosions";
        } else if (Enchantment.PROTECTION_FALL.getName().equals(e.getName())) {
            t = "Chute Amortie";
        } else if (Enchantment.PROTECTION_FIRE.getName().equals(e.getName())) {
            t = "Protection contre le Feu";
        } else if (Enchantment.PROTECTION_PROJECTILE.getName().equals(e.getName())) {
            t = "Protection contre les Projectiles";
        } else if (Enchantment.SILK_TOUCH.getName().equals(e.getName())) {
            t = "Toucher de Soie";
        } else if (Enchantment.THORNS.getName().equals(e.getName())) {
            t = "Épines";
        } else if (Enchantment.WATER_WORKER.getName().equals(e.getName())) {
            t = "Affinité Aquatique";
        }
        return t;
    }
}
