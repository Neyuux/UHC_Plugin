package fr.neyuux.uhc.tasks;

import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.UHCWorld;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.modes.Moles;
import fr.neyuux.uhc.scenario.classes.modes.SkyDefender;
import fr.neyuux.uhc.util.ScoreboardSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Map;

public class UHCRunnable extends BukkitRunnable {

    private final UHC main;
    public UHCRunnable(UHC main) {
        this.main = main;
        timer = 0;
        pvpTimer = (int)GameConfig.ConfigurableParams.PVP.getValue();
        borderTimer = (int)GameConfig.ConfigurableParams.BORDER_TIMER.getValue();
        invincibilityTimer = (int)GameConfig.ConfigurableParams.INVINCIBILITY.getValue();
        episodTimer = (int)GameConfig.ConfigurableParams.EPISODS_TIMER.getValue();
    }

    public static int timer = 0;
    public static int pvpTimer = (int)GameConfig.ConfigurableParams.PVP.getValue();
    public static int borderTimer = (int)GameConfig.ConfigurableParams.BORDER_TIMER.getValue();
    public static int invincibilityTimer = (int)GameConfig.ConfigurableParams.INVINCIBILITY.getValue();
    public static int episodTimer = (int)GameConfig.ConfigurableParams.EPISODS_TIMER.getValue();
    public static int episod = 1;

    @Override
    public void run() {
        if (!main.isState(Gstate.PLAYING)) {
            cancel();
            return;
        }

        if (timer == 1) {
            for (PlayerUHC pu : main.getAlivePlayers()) pu.unfreeze();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!main.isState(Gstate.PLAYING)) {
                        cancel();
                        return;
                    }
                    if (main.mode.equals(UHC.Modes.UHC)) {
                        for (Map.Entry<PlayerUHC, ScoreboardSign> en : main.boards.entrySet()) {
                            Location l = en.getKey().getPlayer().getPlayer().getLocation();
                            DecimalFormat df = new DecimalFormat();
                            df.setMaximumFractionDigits(1);
                            en.getValue().setLine(11, "�9�lCentre : �6" + df.format(l.distance(new Location(l.getWorld(), 0, l.getY(), 0))) + " blocks " + en.getKey().getDirectionArrow(new Location(l.getWorld(), 0, l.getY(), 0)));
                        }
                    } else cancel();
                }
            }.runTaskTimer(main, 0, 3L);
            new BukkitRunnable() {
                public void run() {
                    if (!main.isState(Gstate.PLAYING) || (Scenarios.MOLES.isActivated() && timer == Moles.timer)) {
                        // ajouter mysteryteams
                        cancel();
                        return;
                    }
                    if (!GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA")) {
                        for (PlayerUHC pu : main.getAlivePlayers())
                            if (pu.getPlayer().isOnline() && pu.getTeam() != null && pu.getTeam().getAlivePlayers().size() > 1) {

                                if (Scenarios.SKY_DEFENDER.isActivated() && !pu.getTeam().getPrefix().symbol.contains("D�fenseur"))
                                    continue;

                                Location l = pu.getPlayer().getPlayer().getLocation();
                                DecimalFormat df = new DecimalFormat();
                                df.setMaximumFractionDigits(1);
                                StringBuilder tm = new StringBuilder();
                                for (PlayerUHC tu : pu.getTeam().getAlivePlayers())
                                    if (!tu.equals(pu)) {
                                        String shealth = "�b";
                                        double h = tu.getPlayer().getPlayer().getHealth() * (100 / tu.getPlayer().getPlayer().getMaxHealth());
                                        if (h <= 80) shealth = "�a";
                                        if (h <= 60) shealth = "�e";
                                        if (h <= 40) shealth = "�6";
                                        if (h <= 20) shealth = "�c";

                                        if (!l.getWorld().equals(tu.getPlayer().getPlayer().getLocation().getWorld())) {
                                            tm.append(shealth).append(" ").append("�c\u2715 �7| ");
                                            continue;
                                        }

                                        String sdist = "�b";
                                        double d = l.distance(tu.getPlayer().getPlayer().getLocation());
                                        if (d >= 30) sdist = "�a";
                                        if (d >= 70) sdist = "�e";
                                        if (d >= 120) sdist = "�6";
                                        if (d >= 180) sdist = "�c";
                                        tm.append(shealth).append(tu.getPlayer().getName()).append(" ").append(sdist).append(pu.getDirectionArrow(new Location(tu.getPlayer().getPlayer().getWorld(), tu.getPlayer().getPlayer().getLocation().getX(), pu.getPlayer().getPlayer().getLocation().getY(), tu.getPlayer().getPlayer().getLocation().getZ()))).append("�7(").append(df.format(d)).append(" b) | ");
                                    }
                                try {
                                    UHC.sendActionBar(pu.getPlayer().getPlayer(), tm.substring(0, tm.length() - 3));
                                } catch (StringIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }
                    } else cancel();
                }
            }.runTaskTimer(main, 0 , 5);
        }

        switch (pvpTimer) {
            case 900:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�cActivation du �lPvP�c dans �615 minutes�c.");
                break;
            case 600:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�cActivation du �lPvP�c dans �610 minutes�c.");
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.WOOD_CLICK, 5, 0.5f);
                break;
            case 300:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�cActivation du �lPvP�c dans �65 minutes�c.");
                break;
            case 180:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�cActivation du �lPvP�c dans �63 minutes�c.");
                break;
            case 120:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�cActivation du �lPvP�c dans �62 minutes�c.");
                break;
            case 60:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�cActivation du �lPvP�c dans �61 minute�c.");
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.WOOD_CLICK, 5, 0.5f);
                break;
            case 30:
            case 10:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�cActivation du �lPvP�c dans �6"+pvpTimer+" secondes�c.");
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.WOOD_CLICK, 5, 0.5f);
                break;
            case 1:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�cActivation du �lPvP�c dans �61 seconde�c.");
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.WOOD_CLICK, 5, 0.5f);
                break;
            case 0:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�c�lPvP activ� !");
                main.world.changePVP(true);
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 5, 0.5f);
                break;
        }

        switch (borderTimer) {
            case 900:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�3Activation du �lBordure�3 dans �615 minutes�3.");
                break;
            case 600:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�3Activation du �lBordure�3 dans �610 minutes�3.");
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.WOOD_CLICK, 5, 0.5f);
                break;
            case 300:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�3Activation du �lBordure�3 dans �65 minutes�3.");
                break;
            case 180:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�3Activation du �lBordure�3 dans �63 minutes�3.");
                break;
            case 120:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�3Activation du �lBordure�3 dans �62 minutes�3.");
                break;
            case 60:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�3Activation du �lBordure�3 dans �61 minute�3.");
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.WOOD_CLICK, 5, 0.5f);
                break;
            case 30:
            case 10:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�3Activation du �lBordure�3 dans �6"+borderTimer+" secondes�3.");
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.WOOD_CLICK, 5, 0.5f);
                break;
            case 1:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�3Activation du �lBordure�3 dans �61 seconde�3.");
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.WOOD_CLICK, 5, 0.5f);
                break;
            case 0:
                Bukkit.broadcastMessage(UHC.getPrefix() + "�3�lBordure activ�e !");
                main.world.startWorldBorder();
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 5, 0.5f);
                break;
        }

        if (invincibilityTimer == 10) Bukkit.broadcastMessage(UHC.getPrefix() + "�eD�sactivation de l'�lInvincibilit�e dans �610 secondes�e.");
        else if (invincibilityTimer == 0) {
            Bukkit.broadcastMessage(UHC.getPrefix() + "�e�lInvincibilit� d�sactiv�e !");
            for (PlayerUHC pu : main.getAlivePlayers()) pu.setInvulnerable(false);
            for (Player p : Bukkit.getOnlinePlayers())
                p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 5, 0.5f);
        }

        if ((boolean)GameConfig.ConfigurableParams.EPISODS.getValue()) {
            if (episodTimer == 300) Bukkit.broadcastMessage(UHC.getPrefix() + "�6Fin de l'�l�pisode�6 "+episod+" dans �e5 minutes�6.");
            else if (episodTimer == 60) Bukkit.broadcastMessage(UHC.getPrefix() + "�6Fin de l'�l�pisode�6 "+episod+" dans �e1 minute�6.");
            else if (episodTimer == 30) Bukkit.broadcastMessage(UHC.getPrefix() + "�6Fin de l'�l�pisode�6 "+episod+" dans �e30 secondes�6.");
            else if (episodTimer > 1 && episodTimer <= 5) Bukkit.broadcastMessage(UHC.getPrefix() + "�6Fin de l'�l�pisode�6 "+episod+" dans �e"+episodTimer+" secondes�6.");
            else if (episodTimer == 1) Bukkit.broadcastMessage(UHC.getPrefix() + "�6Fin de l'�l�pisode�6 "+episod+" dans �e1 seconde�6.");
            else if (episodTimer == 0) {
                Bukkit.broadcastMessage(UHC.getPrefix() + "�6Fin de l'�pisode " + episod + "...");
                episod++;
                Bukkit.broadcastMessage(UHC.getPrefix() + "�b-------------------");
                Bukkit.broadcastMessage(UHC.getPrefix() + "�6�lD�but de l'�pisode �e�l" + episod +" �6�l!");
                episodTimer = (int)GameConfig.ConfigurableParams.EPISODS_TIMER.getValue();
            }

            if (episodTimer != -1) episodTimer--;
        }

        timer++;
        if (pvpTimer != -1) pvpTimer--;
        if (borderTimer != -1) borderTimer--;
        if (invincibilityTimer != -1) invincibilityTimer--;
        for (Map.Entry<PlayerUHC, ScoreboardSign> en : main.boards.entrySet()) {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);
            if (main.mode.equals(UHC.Modes.UHC)) {
                en.getValue().setLine(6, "�6�lTimer �6: �e�l" + UHC.getTimer(timer));

                if (pvpTimer > 0)
                    en.getValue().setLine(7, "�6�lPvP �6: �c�l" + UHC.getTimer(pvpTimer));
                else
                    en.getValue().removeLine(7);

                if (borderTimer > 0)
                    en.getValue().setLine(8, "�6�lBordure �6: �3�l" + UHC.getTimer(borderTimer));
                else
                    en.getValue().removeLine(8);

                en.getValue().setLine(10, "�b�lTaille de la bordure �b: " + Symbols.PLUS_MINUS + "�3" +  Math.round(Bukkit.getWorld(UHCWorld.MAIN_WORLD).getWorldBorder().getSize() / 2.0f));

            } else if (main.mode.equals(UHC.Modes.LG)) {
                en.getValue().setLine(8, "�6�lTimer �6: �e�l" + UHC.getTimer(timer));

                if (pvpTimer > 0)
                    en.getValue().setLine(9, "�6�lPvP �6: �c�l" + UHC.getTimer(pvpTimer));
                else
                    en.getValue().removeLine(9);

                if (borderTimer > 0)
                    en.getValue().setLine(10, "�6�lBordure �6: �3�l" + UHC.getTimer(borderTimer));
                else
                    en.getValue().removeLine(10);
                en.getValue().setLine(12, "�b�lTaille de la bordure �b: " + Symbols.PLUS_MINUS + "�3" + Math.round(Bukkit.getWorld(main.world.getSeed() + "").getWorldBorder().getSize() / 2.0f));
            }
        }
    }
}
