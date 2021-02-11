package fr.neyuux.uhc;

import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlayerUHC {

    private final OfflinePlayer player;
    private final Index main;
    private int kills;
    private int diamonds;
    private int golds;
    private int irons;
    private int animals;
    private int monsters;
    public double maxHealth;
    public double health;
    public float absorption;
    public int foodLevel;
    private boolean isAlive, isInvulnerable;
    private UHCTeam team;
    private final HashMap<Integer, ItemStack> lastArmor = new HashMap<>();
    private ItemStack[] lastInv = new ItemStack[]{};
    private Location lastLocation;

    public PlayerUHC(Player player, Index main) {
        this.player = player;
        this.main = main;
        this.kills = 0; this.diamonds = 0; this.golds = 0;
        this.irons = 0; this.monsters = 0; this.animals = 0;
        this.maxHealth = 20.0; this.health = maxHealth; this.absorption = 0; this.foodLevel = 20;
        this.isAlive = false; this.isInvulnerable = false;
    }


    public OfflinePlayer getPlayer() {
        return player;
    }

    public int getKills() {
        return kills;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public int getGolds() {
        return golds;
    }

    public int getIrons() {
        return irons;
    }

    public int getAnimals() {
        return animals;
    }

    public int getMonsters() {
        return monsters;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public boolean isHost() {
        return main.getGameConfig().hosts.contains(player.getUniqueId());
    }

    public boolean isSpec() {
        return main.spectators.contains(player);
    }

    public UHCTeam getTeam() {
        return team;
    }

    public ItemStack[] getLastInv() {
        return lastInv;
    }

    public HashMap<Integer, ItemStack> getLastArmor() {
        return lastArmor;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setTeam(UHCTeam team) {
        this.team = team;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.isInvulnerable = invulnerable;
    }

    public void setLastInv(ItemStack[] items) {
        this.lastInv = items;
    }

    public void setLastArmor(HashMap<Integer, ItemStack> lastArmor) {
        this.lastArmor.clear();
        this.lastArmor.putAll(lastArmor);
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDiamonds(int added) {
        this.diamonds += diamonds;
    }

    public void addGolds(int added) {
        this.golds += golds;
    }

    public void addIrons(int added) {
        this.irons += irons;
    }

    public void addAnimal() {
        this.animals++;
    }

    public void addMonster() {
        this.monsters++;
    }


    public void heal() {
        health = maxHealth;
        foodLevel = 20;
        if (getPlayer().isOnline()) {
            getPlayer().getPlayer().setHealth(health);
            getPlayer().getPlayer().setFoodLevel(foodLevel);
        }
    }

    public void freeze() {
        if (player.isOnline()) {
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 249, false, false));
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 9, false, false));
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 249, false, false));

            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ORB_PICKUP, 2.0F, 0.1F);
        }
    }

    public void unfreeze() {
        if (player.isOnline()) {
            player.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            player.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            player.getPlayer().removePotionEffect(PotionEffectType.JUMP);
        }
    }


    public String getDirectionArrow(Location targetLoc) {
        if (!player.isOnline()) return "";
        Vector vector = targetLoc.toVector().subtract(player.getPlayer().getLocation().toVector());
        Vector playerDirection = targetLoc.getDirection();
        double angle = vector.angle(playerDirection);
        angle = angle * 180 / Math.PI;

        if (angle > 67.5 && angle < 112.5) return Symbols.WEST_ARROW;
        else if (angle > 112.5 && angle < 157.5) return Symbols.SOUTHWEST_ARROW;
        else if (angle > 157.5 && angle < 202.5) return Symbols.SOUTH_ARROW;
        else if (angle > 202.5 && angle < 247.5) return Symbols.SOUTHEAST_ARROW;
        else if (angle > 247.5 && angle < 292.5) return Symbols.EAST_ARROW;
        else if (angle > 292.5 && angle < 337.5) return Symbols.NORTHEAST_ARROW;
        else if (angle > 22.5 && angle < 67.5) return Symbols.NORTHWEST_ARROW;
        else if (angle > 337.5 || angle < 22.5) return Symbols.NORTH_ARROW;

        return Symbols.CROSS;
    }

    public Inventory getSpecInfosInventory(List<String> moreInfos) {
        Inventory inv = Bukkit.createInventory(null, 54, "§7Stuff §6" + player.getName());
        for (int i = 9; i < 18; i++)
            inv.setItem(i, new ItemsStack(Material.BEDROCK, " ").toItemStack());
        inv.setItem(8, new ItemsStack(Material.PAPER, "§7Informations supplémentaires", moreInfos.toArray(new String[0])).toItemStack());
        inv.setItem(7, new ItemsStack(Material.INK_SACK, (short)1, "§cVie du joueur", "§c§lVie : §4" + health + Symbols.HEARTH, "§e§lAbso : §6" + ((CraftPlayer)player.getPlayer().getPlayer()).getHandle().getAbsorptionHearts(), "§d§lSaturation : §5" + foodLevel).toItemStack());
        ItemsStack food = new ItemsStack(Material.BREWING_STAND_ITEM, "§5Effets de potion du joueur");
        for (PotionEffect pe : player.getPlayer().getActivePotionEffects())
            food.addLore("§e" + Index.translatePotionEffect(pe.getType()) + "§l" + (pe.getAmplifier() - 1) + "§7: §6" + Index.getTimer(pe.getDuration() / 20));
        inv.setItem(6, food.toItemStack());
        inv.setItem(5, new ItemsStack(Material.EXP_BOTTLE, "§aNiveau du joueur", "§aNiveau : §l" + player.getPlayer().getLevel(), "§2Expérience : §l" + player.getPlayer().getExp()).toItemStack());

        inv.setItem(9, player.getPlayer().getInventory().getHelmet());
        inv.setItem(10, player.getPlayer().getInventory().getChestplate());
        inv.setItem(11, player.getPlayer().getInventory().getLeggings());
        inv.setItem(12, player.getPlayer().getInventory().getBoots());
        HashMap<Integer, ItemStack> pinv = new HashMap<>();
        int ii;
        for (ItemStack it : player.getPlayer().getInventory().getContents()) {
            if (it == null)continue;
            int slot = 0;
            ii = 0;
            for (ItemStack iit : player.getPlayer().getInventory().getContents()) {
                if (iit != null && iit.equals(it)) slot = ii;
                ii++;
            }
            pinv.put(slot, it);
        }
        for (Map.Entry<Integer, ItemStack> en : pinv.entrySet()) {
            int k = en.getKey();
            if (k >= 9 && k <= 17) k += 18;
            else if (k >= 27 && k <= 35) k -= 18;
            inv.setItem(k + 18, en.getValue());
        }
        return inv;
    }

    public boolean havePlayerAround(int radius){
        if (!player.isOnline()) return false;
        for(Entity ent : player.getPlayer().getNearbyEntities(radius, radius, radius))
            if(ent instanceof Player)
                return true;

        return false;
    }
}
