package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

import static org.bukkit.Material.ENDER_PEARL;

public class Switch extends Scenario implements Listener {
    public Switch() {
        super(Scenarios.SWITCH, new ItemStack(ENDER_PEARL));
    }

    public static int firstSwitch = 1200, switchFrequency = 900, randomTimeLimit = 0;
    public static boolean hasInvSwitch = false, hasSoloSwitch = true, hasTeamBalancing = false;

    public static int[] IGtimers = {0};

    @Override
    protected void activate() {
        if (GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA"))
            UHC.sendHostMessage(UHC.getPrefix() + "§cVeuillez ajouter des équipes pour que le plugin " + scenario.getDisplayName() + " §cpuisse fonctionner.");
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        this.getNewTimer();
        new BukkitRunnable() {
            @Override
            public void run() {
                IGtimers[0]--;
                if (IGtimers[0] == 300 && randomTimeLimit == 0)
                    Bukkit.broadcastMessage(getPrefix() + "§eSwitch dans 5 minutes !");
                else if (IGtimers[0] == 60 && randomTimeLimit == 0)
                    Bukkit.broadcastMessage(getPrefix() + "§eSwitch dans 1 minute !");
                else if (IGtimers[0] < 6 && IGtimers[0] > 1)
                    Bukkit.broadcastMessage(getPrefix() + "§eSwitch dans "+IGtimers[0]+" secondes !");
                else if (IGtimers[0] == 1)
                    Bukkit.broadcastMessage(getPrefix() + "§eSwitch dans 1 seconde !");
                else if (IGtimers[0] == 0) {
                    Bukkit.broadcastMessage(getPrefix() + "§eActivation du Scénario !");
                    switchTeams();
                    Switch.this.getNewTimer();
                }
                if (!UHC.getInstance().isState(Gstate.PLAYING)) cancel();
            }
        }.runTaskTimer(UHC.getInstance(), 0, 20);
    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }



    public static void switchTeams() {
        List<PlayerUHC> playersToSwitch = new ArrayList<>();
        HashMap<PlayerUHC, UHCTeam> baseTeams = new HashMap<>();
        HashMap<PlayerUHC, ItemStack[]> inventories = new HashMap<>();
        Random random = new SecureRandom();

        random.setSeed(System.currentTimeMillis());

        if (hasTeamBalancing) balanceTeams();

        for (UHCTeam t : UHC.getInstance().getUHCTeamManager().getAliveTeams()) {
            if (!hasSoloSwitch && t.getAlivePlayers().size() == 1) continue;
            PlayerUHC pu = new ArrayList<>(t.getAlivePlayers()).get(random.nextInt(t.getAlivePlayers().size()));

            playersToSwitch.add(pu);
            baseTeams.put(pu, pu.getTeam());
            inventories.put(pu, pu.getPlayer().getPlayer().getInventory().getContents());
            pu.setLastLocation(pu.getPlayer().getPlayer().getLocation());
        }

        Collections.shuffle(playersToSwitch);

        for (int e = 0; e < playersToSwitch.size(); e++) {
            PlayerUHC player1UHC = playersToSwitch.get(e);
            PlayerUHC player2UHC;

            if (e == playersToSwitch.size() - 1) player2UHC = playersToSwitch.get(0);
            else player2UHC = playersToSwitch.get(e + 1);

            Player player1 = player1UHC.getPlayer().getPlayer();
            Player player2 = player2UHC.getPlayer().getPlayer();
            List<ItemStack> stuff = Arrays.asList(inventories.get(player2UHC));

            if (hasInvSwitch)
                for (int i = 9; i < stuff.size(); i++) player1.getInventory().setItem(i, stuff.get(i));
            player1.teleport(player2UHC.getLastLocation().clone());
            player1UHC.getTeam().leave(player1UHC);
            baseTeams.get(player2UHC).add(player1);
            player1.sendMessage(UHC.getPrefix() + Scenarios.SWITCH.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §eVous avez switch avec " + baseTeams.get(player2UHC).getTeam().getPrefix() + player2.getName() + " §e!");
            player1.playSound(player1.getLocation(), Sound.ENDERMAN_TELEPORT, 8, 1f);
        }
    }

    public static void balanceTeams() {
        int alivePlayers = UHC.getInstance().getAlivePlayers().size();
        List<UHCTeam> aliveTeams = UHC.getInstance().getUHCTeamManager().getAliveTeams();
        double size = (double) alivePlayers / aliveTeams.size();
        int maxPerTeam = GameConfig.getTeamTypeInt((String) GameConfig.ConfigurableParams.TEAMTYPE.getValue());
        List<UHCTeam> teamsNeedMore = new ArrayList<>();
        List<UHCTeam> teamsNeedLess = new ArrayList<>();

        if (size > maxPerTeam) {
            int i = 1;

            while (size > maxPerTeam) {
                size = (double) alivePlayers / (aliveTeams.size() + i);
                i++;
            }
        } else size = new BigDecimal(size).setScale(0, RoundingMode.DOWN).doubleValue();

        int newTeams = (int) (alivePlayers / size - aliveTeams.size());

        for (int i = newTeams; i > 0; i--) {
            UHCTeam team = UHC.getInstance().getUHCTeamManager().createTeam();

            aliveTeams.add(team);
        }

        for (UHCTeam team : aliveTeams)
            if (team.getAlivePlayers().size() > size) teamsNeedLess.add(team);
            else if (team.getAlivePlayers().size() < size) teamsNeedMore.add(team);

        while (!teamsNeedLess.isEmpty()) {
            if (!teamsNeedMore.isEmpty()) {
                Random random = new Random();
                UHCTeam needLess = teamsNeedLess.get(random.nextInt(teamsNeedLess.size()));
                UHCTeam needMore = teamsNeedMore.get(random.nextInt(teamsNeedMore.size()));

                PlayerUHC playerUHC = needLess.getAlivePlayers().get(random.nextInt(needLess.getAlivePlayers().size()));

                needLess.leave(playerUHC);
                needMore.add(playerUHC.getPlayer().getPlayer());
                if (needMore.getAlivePlayers().size() >= size) teamsNeedMore.remove(needMore);
                if (needLess.getAlivePlayers().size() <= size) teamsNeedLess.remove(needLess);
            } else break;
        }
    }


    private void getNewTimer() {
        IGtimers[0] = (firstSwitch != 0 ? firstSwitch : switchFrequency) - randomTimeLimit + new SecureRandom().nextInt(randomTimeLimit + 1);
        this.checkTimerLimit();
    }

    private void checkTimerLimit() {
        if (IGtimers[0] < 5) {
            this.getNewTimer();
        }
    }

}
