package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NoBookShelves extends Scenario {
    public NoBookShelves() {
        super(Scenarios.NO_BOOK_SHELVES, new ItemStack(Material.BOOKSHELF));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }
}
