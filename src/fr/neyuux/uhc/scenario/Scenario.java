package fr.neyuux.uhc.scenario;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Scenario {

    public Scenarios scenario;
    public ItemStack menuItem;
    public Scenario(Scenarios scenario, ItemStack menuItem) {
        this.scenario = scenario;
        this.menuItem = menuItem;
    }

    public void activeScenario(Player p){
        if(scenario.getActivated()){
            Bukkit.broadcastMessage(Index.getStaticPrefix() + ChatColor.RED + "Désactivation" + ChatColor.GRAY + " du scénario " +
                    "" + ChatColor.GOLD + scenario.getName());
                    scenario.setActivated(false);

            desactivate();
        } else {
            Bukkit.broadcastMessage(Index.getStaticPrefix()+ChatColor.GREEN+"Activation"+ChatColor.GRAY+" du sc"+ChatColor.GOLD+scenario.getName());
            scenario.setActivated(true);
            activate();
        }
    }

    public void executeScenario(){
        execute();
    }

    public ItemStack getMenuItem(Player p) {
        ItemsStack newItem = new ItemsStack(menuItem);
        newItem.setName(ChatColor.YELLOW + scenario.getName());

        List<String> textes = new ArrayList<>();
        String newmsg = ChatColor.GRAY+"";
        int i = 0;
        for(String bout : scenario.getScenarioLore().split(" ")){
            newmsg = newmsg + bout + " ";
            if(i == 8){
                textes.add(newmsg);
                newmsg = ChatColor.GRAY+"";
                i = 0;
            }
            i++;
        }
        if(newmsg != ChatColor.GRAY+""){
            textes.add(newmsg);
        }
        newItem.setLore(textes);

        return newItem.toItemStack();
    }

    public abstract void activate(); // ACTIVATION DU SCENARIO
    public abstract void execute(); // EXECUTION DU SCENARIO

    public void openMenu(Player p) {} // OUVERTURE DU MENU SI SCENARIO CONFIGURABLE
    public void desactivate() {} // DESACTIVATION DU SCENARIO
}

