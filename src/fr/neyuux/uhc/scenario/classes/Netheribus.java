package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.listeners.FightListener;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Netheribus extends Scenario {
    public Netheribus() {
        super(Scenarios.NETHERIBUS, new ItemStack(Material.NETHERRACK));
    }

    public static int timer = 3600;
    public static double damage = 0.5;

    public static final int[] IGtimers = {0, 30};

    @Override
    protected void activate() {
        if (!(boolean)GameConfig.ConfigurableParams.NETHER.getValue())
            Bukkit.broadcastMessage(Index.getStaticPrefix() + "§cLe Nether doit être activé pour jouer avec Netheribus");
    }

    @Override
    public void execute() {
        IGtimers[0] = timer;
        new BukkitRunnable() {
            @Override
            public void run() {
                IGtimers[0]--;
                if (IGtimers[0] == 0) {
                    Bukkit.broadcastMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cActivation du Scénario ! §cTous les joueurs qui ne sont pas dans le Nether recevront §4§l" + damage + Symbols.HEARTH + " §6de dégâts toutes les 30 secondes à compter de maintenant.");
                    IGtimers[0] = Netheribus.timer;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            IGtimers[1]--;
                            if (IGtimers[1] == 0) {
                                for (PlayerUHC pu : Index.getInstance().getAlivePlayers())
                                    if (pu.getPlayer().isOnline() && !pu.getPlayer().getPlayer().getLocation().getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                                        Player p = pu.getPlayer().getPlayer();
                                        p.damage(0);
                                        if (p.getHealth() > damage * 2.0) p.setHealth(p.getHealth() - damage * 2.0);
                                        else new FightListener(Index.getInstance()).eliminate(p, true, null, p.getDisplayName() + " §dest resté trop longtemps hors du Nether.");
                                        Index.sendActionBar(p, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§4Vous perdez " + damage + Symbols.HEARTH + "  en n'étant pas dans le Nether.");
                                    }
                                IGtimers[1] = 30;
                            }
                            if (!Index.getInstance().isState(Gstate.PLAYING)) cancel();
                        }
                    }.runTaskTimer(Index.getInstance(), 0, 20);
                    cancel();
                }
                if (!Index.getInstance().isState(Gstate.PLAYING)) cancel();
            }
        }.runTaskTimer(Index.getInstance(), 0, 20);
    }

    @Override
    public boolean checkStart() {
        return (boolean) GameConfig.ConfigurableParams.NETHER.getValue();
    }
}
