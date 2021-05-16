package fr.neyuux.uhc.scenario;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Scenario {

    public Scenarios scenario;
    public ItemStack menuItem;
    private static final HashMap<String, Field> cache = new HashMap<>();
    protected static final List<Listener> handlers = new ArrayList<>();
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
            Bukkit.broadcastMessage(UHC.getPrefix() + "§aActivation §7du Scénario " + scenario.getDisplayName());
            scenario.setActivated(true);
            activate();
        }
    }

    public void desactivateScenario() {
        if(scenario.isActivated()){
            Bukkit.broadcastMessage(UHC.getPrefix() + "§cDésactivation §7du Scénario " + scenario.getDisplayName());
            scenario.setActivated(false);
            if (scenario.equals(Scenarios.RANDOM_TEAM)) GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(GameConfig.getTeamTypeInt((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()), false));
            if (scenario.equals(Scenarios.SLAVE_MARKET)) GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(0, Scenarios.RANDOM_TEAM.isActivated()));
        }
    }

    public static HashMap<String, Field> getCache() {
        return cache;
    }

    public static void removeEvents() {
        for (Listener l : handlers)
            HandlerList.unregisterAll(l);
    }
    
    public void addCache(String field, String valueName, Class<? extends Scenario> c) {
        try {
            Field f = c.getField(field);
            cache.put(valueName, f);
        } catch (NoSuchFieldException e) {
            Bukkit.broadcastMessage(UHC.getPrefix() + "§4[§cErreur§4] §cEchec du chargement des scénarios 2.");
        }
    }

    public static void setCache(String name, String current, Object value) {
        try {
            Class<?> sc = Scenarios.getByName(name).getScenarioClass();
            cache.get(current).set(sc.newInstance(), value);
            sc.getField(cache.get(current).getName()).set(sc.newInstance(), value);
        } catch (IllegalAccessException | NoSuchFieldException | InstantiationException | NullPointerException e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(UHC.getPrefix() + "§4[§cErreur§4] §cEchec de la modification de la valeur du Scenario " + name + ".");
        }
    }
}

