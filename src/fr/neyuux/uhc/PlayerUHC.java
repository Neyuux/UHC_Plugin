package fr.neyuux.uhc;

import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerUHC {

    private final OfflinePlayer player;
    private final Index main;
    private int kills, diamonds, golds, irons, animals, monsters;
    private double maxHealth, health, foodLevel;
    private boolean isAlive, isHost;
    private UHCTeam team;

    public PlayerUHC(Player player, Index main) {
        this.player = player;
        this.main = main;
        this.kills = 0; this.diamonds = 0; this.golds = 0;
        this.irons = 0; this.monsters = 0; this.animals = 0;
        this.maxHealth = 20.0; this.health = maxHealth; this.foodLevel = 20.0;
        this.isAlive = false;
        this.isHost = false;
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

    public boolean isHost() {
        return isHost;
    }

    public UHCTeam getTeam() {
        return team;
    }

    public void setTeam(UHCTeam team) {
        this.team = team;
    }


    public void heal() {
        health = maxHealth;
        foodLevel = 20.0;
    }
}
