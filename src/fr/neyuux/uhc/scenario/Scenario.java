package fr.neyuux.uhc.scenario;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.config.GameConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.*;

public abstract class Scenario {

    public Scenarios scenario;
    public ItemStack menuItem;
    private static final HashMap<String, Field> cache = new HashMap<>();
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
    public abstract boolean checkStart(); // VERIFICATION SI ON PEUT START

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
            if (scenario.equals(Scenarios.RANDOM_TEAM)) GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(GameConfig.getTeamTypeInt((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()), false));
            if (scenario.equals(Scenarios.SLAVE_MARKET)) GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(0, Scenarios.RANDOM_TEAM.isActivated()));
        }
    }

    public static HashMap<String, Field> getCache() {
        return cache;
    }
    
    public void addCache(String field, String valueName, Class<? extends Scenario> c) {
        try {
            Field f = c.getField(field);
            cache.put(valueName, f);
        } catch (NoSuchFieldException e) {
            Bukkit.broadcastMessage(Index.getStaticPrefix() + "§4[§cErreur§4] §cEchec du chargement des scénarios 2.");
        }
    }

    public static void setCache(String name, String current, Object value) {
        Class<?> sc = Scenarios.getByName(name).getScenarioClass();
        try {
            cache.get(current).set(sc.newInstance(), value);
            sc.getField(cache.get(current).getName()).set(sc.newInstance(), value);
        } catch (IllegalAccessException | NoSuchFieldException | InstantiationException e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(Index.getStaticPrefix() + "§4[§cErreur§4] §cEchec de la modification de la valeur du Scenario " + name + ".");
        }
    }
}

