package fr.neyuux.uhc.util;

import java.util.List;

public class Loot {
    private final List<LootItem> loots;
    private final Double exp;

    public Loot(Double exp, List<LootItem> loots) {
        this.loots = loots;
        this.exp = exp;
    }

    public Loot(Integer exp, List<LootItem> loots) {
        this.exp = exp.doubleValue();
        this.loots = loots;
    }

    public List<LootItem> getLoots() {
        return loots;
    }

    public Double getExp() {
        return exp;
    }
}
