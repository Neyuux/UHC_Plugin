package com.gmail.val59000mc.paperlib.features.bedspawnlocation;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BedSpawnLocation {
    CompletableFuture<Location> getBedSpawnLocationAsync(Player paramPlayer, boolean paramBoolean);
}
