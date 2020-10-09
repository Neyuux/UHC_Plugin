package fr.neyuux.uhc.scenario;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Scenario {

    public Scenarios scenario;
    public ItemStack menuItem;
    private final HashMap<String, Object> attachements = new HashMap<>();
    public Scenario(Scenarios scenario, ItemStack menuItem) {
        this.scenario = scenario;
        this.menuItem = menuItem;
    }

    public void executeScenario(){
        execute();
    }

    public ItemStack getMenuItem() {
        ItemsStack newItem = new ItemsStack(menuItem);
        newItem.setName(scenario.getDisplayName());

        List<String> textes = new ArrayList<>();
        StringBuilder newmsg = new StringBuilder(ChatColor.GRAY + "");
        int i = 0;
        for(String bout : scenario.getScenarioLore().split(" ")){
            newmsg.append(bout).append(" ");
            if(i == 8){
                textes.add(newmsg.toString());
                newmsg = new StringBuilder(ChatColor.GRAY + "");
                i = 0;
            }
            i++;
        }
        if(!newmsg.toString().equals(ChatColor.GRAY + "")){
            textes.add(newmsg.toString());
        }
        if (!scenario.getValues().isEmpty() && scenario.isActivated()) {
            textes.add("");
            textes.addAll(scenario.getValues());
        }
        newItem.setLore(textes);
        if (scenario.toString().startsWith("NO_")) newItem.addGlowEffect();

        return newItem.toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
    }

    protected abstract void activate(); // ACTIVATION DU SCENARIO
    public abstract void execute(); // EXECUTION DU SCENARIO

    public void openMenu(Player p) {

    } // OUVERTURE DU MENU SI SCENARIO CONFIGURABLE

    public void activateScenario(){
        if (!scenario.isActivated()){
            Bukkit.broadcastMessage(Index.getStaticPrefix() + "§aActivation §7du Scénario " + scenario.getDisplayName());
            scenario.setActivated(true);
            activate();
        }
    }

    public void desactivateScenario() {
        if(scenario.isActivated()){
            Bukkit.broadcastMessage(Index.getStaticPrefix() + "§cDésactivation §7du Scénario " + scenario.getDisplayName());
            scenario.setActivated(false);
        }
    }

    public HashMap<String, Object> getAttachements() {
        return attachements;
    }
}

