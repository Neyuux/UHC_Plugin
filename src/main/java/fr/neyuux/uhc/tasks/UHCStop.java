package fr.neyuux.uhc.tasks;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.enums.Gstate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCStop extends BukkitRunnable {

    private final UHC main;
    public UHCStop(UHC main) {
        this.main = main;
        timer = 30;
    }

    public static int timer = 30;


    @Override
    public void run() {

        if (!main.isState(Gstate.FINISHED)) {
            cancel();
            return;
        }

        if (timer == 30)
            Bukkit.broadcastMessage(UHC.getPrefix() + "§eLa map va se reset dans §c§l" + timer + " §r§csecondes §e!");

        if (timer == 27) for (Player player : Bukkit.getOnlinePlayers())
            main.setKillsScoreboard(player);

        if (timer == 15)
            Bukkit.broadcastMessage(UHC.getPrefix() + "§eLa map va se reset dans §c§l" + timer + " §r§csecondes §e!");

        if (timer == 10)
            Bukkit.broadcastMessage(UHC.getPrefix() + "§eLa map va se reset dans §c§l" + timer + " §r§csecondes §e!");

        if (timer <= 5 && timer > 1)
            Bukkit.broadcastMessage(UHC.getPrefix() + "§eLa map va se reset dans §c§l" + timer + " §r§csecondes §e!");

        if (timer == 1)
            Bukkit.broadcastMessage(UHC.getPrefix() + "§eLa map va se reset dans §c§l" + timer + " §r§cseconde §e!");


        if (timer == 0) {
            main.world.delete();
            main.rel();
            cancel();
        }

        timer--;
    }
}
