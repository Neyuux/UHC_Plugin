package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.ArmorEquipEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ArmorLimiter extends Scenario implements Listener {
    public ArmorLimiter() {
        super(Scenarios.ARMOR_LIMITER, new ItemStack(Material.DIAMOND_CHESTPLATE));
    }

    public static ArmorTypes helmetMax = ArmorTypes.DIAMOND, chestplateMax = ArmorTypes.DIAMOND, leggingsMax = ArmorTypes.DIAMOND,
        bootsMax = ArmorTypes.DIAMOND;
    public static int diamondMax = 3;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onEquip(ArmorEquipEvent ev) {
        Player player = ev.getPlayer();
        ItemStack current = ev.getNewArmorPiece();

        if (current == null) return;

            switch (current.getType()) {
                case LEATHER_HELMET:
                    if (helmetMax.equals(ArmorTypes.NULL))
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de casque en cuir.", player);
                    break;
                case GOLD_HELMET:
                    if (helmetMax.ordinal() < 2)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de casque en or.", player);
                    break;
                case CHAINMAIL_HELMET:
                    if (helmetMax.ordinal() < 3)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de casque en maille.", player);
                    break;
                case IRON_HELMET:
                    if (helmetMax.ordinal() < 4)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de casque en fer.", player);
                    break;
                case DIAMOND_HELMET:
                    if (helmetMax.ordinal() != 5)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de casque en diamant.", player);
                    break;

                case LEATHER_CHESTPLATE:
                    if (chestplateMax.equals(ArmorTypes.NULL))
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de plastron en cuir.", player);
                    break;
                case GOLD_CHESTPLATE:
                    if (chestplateMax.ordinal() < 2)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de plastron en or.", player);
                    break;
                case CHAINMAIL_CHESTPLATE:
                    if (chestplateMax.ordinal() < 3)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de plastron en maille.", player);
                    break;
                case IRON_CHESTPLATE:
                    if (chestplateMax.ordinal() < 4)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de plastron en fer.", player);
                    break;
                case DIAMOND_CHESTPLATE:
                    if (chestplateMax.ordinal() != 5)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de plastron en diamant.", player);
                    break;

                case LEATHER_LEGGINGS:
                    if (leggingsMax.equals(ArmorTypes.NULL))
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de pantalon en cuir.", player);
                    break;
                case GOLD_LEGGINGS:
                    if (leggingsMax.ordinal() < 2)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de pantalon en or.", player);
                    break;
                case CHAINMAIL_LEGGINGS:
                    if (leggingsMax.ordinal() < 3)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de pantalon en chain.", player);
                    break;
                case IRON_LEGGINGS:
                    if (leggingsMax.ordinal() < 4)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de pantalon en fer.", player);
                    break;
                case DIAMOND_LEGGINGS:
                    if (leggingsMax.ordinal() != 5)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de pantalon en diamant.", player);
                    break;

                case LEATHER_BOOTS:
                    if (bootsMax.equals(ArmorTypes.NULL))
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de bottes en cuir.", player);
                    break;
                case GOLD_BOOTS:
                    if (bootsMax.ordinal() < 2)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de bottes en or.", player);
                    break;
                case CHAINMAIL_BOOTS:
                    if (bootsMax.ordinal() < 3)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de bottes en maille.", player);
                    break;
                case IRON_BOOTS:
                    if (bootsMax.ordinal() < 4)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de bottes en fer.", player);
                    break;
                case DIAMOND_BOOTS:
                    if (bootsMax.ordinal() != 5)
                        cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous ne pouvez pas porter de bottes en diamant.", player);
                    break;
            }

            if (!ev.isCancelled()) {
                int d = 0;
                for (ItemStack it : player.getInventory().getArmorContents()) if (it.getType().name().startsWith("DIAMOND_")) d++;
                if (current.getType().name().startsWith("DIAMOND_")) d++;
                if (d > diamondMax)
                    cancel(ev, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW  + "§cVous portez trop de pièces en diamant", player);
            }
    }

    private void cancel(ArmorEquipEvent ev, String message, Player player) {
        ev.setCancelled(true);
        player.sendMessage(message);
        Index.playNegativeSound(player);
    }


    public enum ArmorTypes {

        NULL("§a§lRien"),
        LEATHER("§6§lCuir"),
        GOLD("§e§lOr"),
        CHAINMAIL("§7§lMaille"),
        IRON("§f§lFer"),
        DIAMOND("§b§lDiamant");

        ArmorTypes(String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return name;
        }

    }
}
