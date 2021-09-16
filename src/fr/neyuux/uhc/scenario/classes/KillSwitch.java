package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

public class KillSwitch extends Scenario implements Listener {

    public KillSwitch() {
        super(Scenarios.KILL_SWITCH, new ItemStack(Material.EYE_OF_ENDER));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onKill(PlayerEliminationEvent ev) {
        if (ev.getKiller() != null) {
            PlayerUHC pu = ev.getPlayerUHC();
            PlayerUHC ku = ev.getKiller();
            PlayerInventory pi = pu.getPlayer().getPlayer().getInventory();
            Player k = ku.getPlayer().getPlayer();
            HashMap<Integer, ItemStack> lastArmor = new HashMap<>();
            if (pi.getHelmet() != null)lastArmor.put(0, pi.getHelmet());
            if (pi.getChestplate() != null)lastArmor.put(1, pi.getChestplate());
            if (pi.getLeggings() != null)lastArmor.put(2, pi.getLeggings());
            if (pi.getBoots() != null)lastArmor.put(3, pi.getBoots());

            pu.setLastInv(k.getInventory().getContents());
            pu.setLastArmor(lastArmor);
            k.getInventory().setContents(pi.getContents());
            k.getInventory().setArmorContents(pi.getArmorContents());
            k.sendMessage(getPrefix() + "§aVous venez de switch votre inventaire avec §l" + pu.getPlayer().getName() + "§a.");
        }
    }
}
