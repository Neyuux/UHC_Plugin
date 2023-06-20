package com.gmail.val59000mc.paperlib.features.asyncteleport;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

public interface AsyncTeleport {
    CompletableFuture<Boolean> teleportAsync(Entity paramEntity, Location paramLocation, PlayerTeleportEvent.TeleportCause paramTeleportCause);
}
