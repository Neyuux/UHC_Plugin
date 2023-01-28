package fr.neyuux.uhc.tasks;

import com.google.common.collect.Lists;
import fr.neyuux.uhc.*;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.GameStartEvent;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.modes.SlaveMarket;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.teams.UHCTeamManager;
import fr.neyuux.uhc.util.Interval;
import fr.neyuux.uhc.util.Loot;
import fr.neyuux.uhc.util.LootItem;
import fr.neyuux.uhc.util.VarsLoot;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class UHCStart extends BukkitRunnable {

    private static int timer = 11;
    private final UHC main;
    public UHCStart(UHC main) {
        this.main = main;
        main.uhcStart = this;
    }

    @Override
    public void run() {

        if (!main.isState(Gstate.STARTING)) {
            cancelStart();
            return;
        }

        InventoryManager.setAllPlayersLevels(timer, (float)timer / 10);
        for(Player pls : Bukkit.getOnlinePlayers()) {
            if (timer == 10) sendStartTimerTitle(pls);
            else if (timer <= 5 && timer != 0)
                sendStartTimerTitle(pls);
            if (timer==10)
                pls.playSound(pls.getLocation(), Sound.SUCCESSFUL_HIT, 8, 1);
            else if (timer==5)
                pls.playSound(pls.getLocation(), Sound.SUCCESSFUL_HIT, 8, 1.1f);
            else if (timer==4)
                pls.playSound(pls.getLocation(), Sound.SUCCESSFUL_HIT, 8, 1.2f);
            else if (timer==3)
                pls.playSound(pls.getLocation(), Sound.SUCCESSFUL_HIT, 8, 1.5f);
            else if (timer==2)
                pls.playSound(pls.getLocation(), Sound.SUCCESSFUL_HIT, 8, 1.7f);
            else if (timer==1)
                pls.playSound(pls.getLocation(), Sound.SUCCESSFUL_HIT, 8, 2);
        }

        if (timer != 11 && timer != 0)
            if (timer == 1)
                Bukkit.broadcastMessage(UHC.getPrefix() + "§eLancement du jeu dans §c§l" + timer + "§c seconde §e!");
            else
                Bukkit.broadcastMessage(UHC.getPrefix() + "§eLancement du jeu dans §c§l" + timer + "§c secondes §e!");

        timer--;

        if (timer == 0) {
            for (Scenarios sc : Scenarios.getActivatedScenarios())
                try {
                    Class<?> c = sc.getScenarioClass();
                    boolean b = (boolean) c.getMethod("checkStart").invoke(c.newInstance());

                    if (!b) {
                        Bukkit.broadcastMessage(UHC.getPrefix() + "§6Le Scénario " + sc.getDisplayName() + " §6empêche de lancer la partie à cause d'une mauvaise configuration.");
                        cancelStart();
                        return;
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                    e.printStackTrace();
                    Bukkit.broadcastMessage(UHC.getPrefix() + "§4[§cErreur§4] §cUne erreur s'est produite lors de la vérification pour start des scénarios, veuillez en informer Neyuux_ !");
                }
        }
        if (timer == -1) {
            GameStartEvent ev = new GameStartEvent();
            Bukkit.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                cancelStart();
                return;
            }
            if (Scenarios.SLAVE_MARKET.isActivated() && !SlaveMarket.canStart) {
                cancel();
                timer = 11;
                main.uhcStart = null;
                return;
            }

            main.setState(Gstate.PLAYING);
            for (PlayerUHC p : main.players)
                if (!p.isSpec())
                    p.setAlive(true);

            if (GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString().startsWith("Random"))
                main.getUHCTeamManager().randomTeams();

            UHCTeamManager.baseplayers = main.getAlivePlayers();
            for (PlayerUHC playerUHC : main.getAlivePlayers()) {
                if (GameConfig.getTeamTypeInt(GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString()) != 1 && playerUHC.getTeam() == null) {
                    for (UHCTeam t : main.getUHCTeamManager().getTeams())
                        if (playerUHC.getTeam() == null && t.getPlayers().size() < GameConfig.getTeamTypeInt(GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString()))
                            t.add(playerUHC.getPlayer().getPlayer());
                }
                InventoryManager.clearInventory(playerUHC.getPlayer().getPlayer());
            }

            List<UHCTeam> toDelete = new ArrayList<>();
            for (UHCTeam t : main.getUHCTeamManager().getTeams())
                if (t.getPlayers().size() == 0) toDelete.add(t);
                else System.out.println(t.getTeam().getDisplayName() + " / " + t.getPlayers());
            toDelete.forEach(uhcTeam -> main.getUHCTeamManager().removeTeam(uhcTeam));
            UHCTeamManager.baseteams = main.getUHCTeamManager().getTeams().size();

            InventoryManager.clearAllPlayersEffects();
            for (Player p : Bukkit.getOnlinePlayers())
                main.setGameScoreboard(p);
            Objective health = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("health");
            Objective healthBelow = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("healthBelow");
            if ((boolean)(GameConfig.ConfigurableParams.SCOREBOARD_LIFE.getValue())) {
                if (Scenarios.TEAM_HEALTH.isActivated()) {
                    health.unregister();
                    healthBelow.unregister();
                    health = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("health", "dummy");
                    healthBelow = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("healthBelow", "dummy");
                    healthBelow.setDisplayName("§4" + Symbols.HEARTH);
                }
                health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                healthBelow.setDisplaySlot(DisplaySlot.BELOW_NAME);
            } else {
                if (health != null && healthBelow != null) {
                    health.unregister();
                    healthBelow.unregister();
                }
            }
            Bukkit.broadcastMessage(UHC.getPrefix() + "§2Lancement de la Pré-Génération du monde...");
            for (PlayerUHC p : main.getAlivePlayers()) {
                if (GameConfig.getTeamTypeInt(GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString()) == 1) main.world.addSpawnLoad();
                UHC.setF3(p.getPlayer().getPlayer(), !(boolean)GameConfig.ConfigurableParams.COORDS_F3.getValue());
                if (Scenarios.TEAM_HEALTH.isActivated()) {
                    health.getScore(p.getPlayer().getName()).setScore((int)p.getTeam().getHealth());
                    healthBelow.getScore(p.getPlayer().getName()).setScore((int)p.getTeam().getHealth());
                }
                if (p.isSpec() && !p.isHost() && !(boolean)GameConfig.ConfigurableParams.SPECTATORS.getValue())
                    p.getPlayer().getPlayer().kickPlayer(UHC.getPrefix() + "§cLes spectateurs se sont pas autorisés.");
            }
            if (GameConfig.getTeamTypeInt(GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString()) != 1)
                for (int i = 0; i < main.getUHCTeamManager().getTeams().size(); i++)
                    main.world.addSpawnLoad();
            main.world.loadChunks();
            main.world.setTime(0);
            main.world.setDayCycle((Boolean)GameConfig.ConfigurableParams.DAY_CYCLE.getValue());
            main.world.initialiseWorldBorder();
            if((boolean) GameConfig.ConfigurableParams.CRAFT_GOLDEN_HEAD.getValue()) {
                ShapedRecipe recette = new ShapedRecipe(UHC.getGoldenHead(1));
                recette.shape("@@@", "@T@", "@@@");
                recette.setIngredient('@', Material.GOLD_INGOT).setIngredient('T', Material.SKULL_ITEM, 3);
                Bukkit.getServer().addRecipe(recette);
            }

            if((boolean) GameConfig.ConfigurableParams.STRING.getValue()) {
                ShapedRecipe r1 = new ShapedRecipe(new ItemStack(Material.STRING, 1));
                r1.shape("WW ", "WW ", "   ");
                r1.setIngredient('W', Material.WOOL);
                Bukkit.getServer().addRecipe(r1);

                ShapedRecipe r2 = new ShapedRecipe(new ItemStack(Material.STRING, 1));
                r2.shape(" WW", " WW", "   ");
                r2.setIngredient('W', Material.WOOL);
                Bukkit.getServer().addRecipe(r2);

                ShapedRecipe r3 = new ShapedRecipe(new ItemStack(Material.STRING, 1));
                r3.shape("   ", "WW ", "WW ");
                r3.setIngredient('W', Material.WOOL);
                Bukkit.getServer().addRecipe(r3);

                ShapedRecipe r4 = new ShapedRecipe(new ItemStack(Material.STRING, 1));
                r4.shape("   ", " WW", " WW");
                r4.setIngredient('W', Material.WOOL);
                Bukkit.getServer().addRecipe(r4);
            }

            if((boolean) GameConfig.ConfigurableParams.SADDLE.getValue()) {
                ShapedRecipe r = new ShapedRecipe(new ItemStack(Material.SADDLE, 1));
                r.shape("LLL", "LIL", "S S");
                r.setIngredient('I', Material.IRON_INGOT).setIngredient('S', Material.STRING).setIngredient('L', Material.LEATHER);
                Bukkit.getServer().addRecipe(r);
            }

            /* DROPS POMMES*/
            VarsLoot.getBlocksLoots().put(Material.LEAVES, new Loot(0, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.APPLE, 1), (double)GameConfig.ConfigurableParams.APPLE.getValue(), new Interval<>(1, 1)))));
            VarsLoot.getBlocksLoots().put(Material.LEAVES_2, new Loot(0, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.APPLE, 1), (double)GameConfig.ConfigurableParams.APPLE.getValue(), new Interval<>(1, 1)))));
            /* FIN DROPS POMMES*/
            //STONE
            VarsLoot.getBlocksLoots().put(Material.STONE, new Loot(0, Lists.newArrayList(new LootItem(new ItemStack(Material.COBBLESTONE, 1), 100.0, new Interval<>(1, 1)))));
            /* DROPS FLINT */
            double flint = (double)GameConfig.ConfigurableParams.FLINT.getValue();
            VarsLoot.getBlocksLoots().put(Material.GRAVEL, new Loot(0, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.FLINT, 1), flint, new Interval<>(1, 1)),
                    new LootItem(new ItemStack(Material.GRAVEL, 1), 100-flint, new Interval<>(1, 1)))));
            /* FIN DROPS FLINT*/
            /*MINERAIS*/
            VarsLoot.getBlocksLoots().put(Material.IRON_ORE, new Loot(0, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.IRON_ORE, 1), 100.0, new Interval<>(1, 1)))));
            VarsLoot.getBlocksLoots().put(Material.DIAMOND_ORE, new Loot(2, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.DIAMOND, 1), 100.0, new Interval<>(1, 1)))));
            VarsLoot.getBlocksLoots().put(Material.GOLD_ORE, new Loot(0, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.GOLD_ORE, 1), 100.0, new Interval<>(1, 1)))));
            VarsLoot.getBlocksLoots().put(Material.COAL_ORE, new Loot(1, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.COAL, 1), 80.0, new Interval<>(1, 1)))));
            VarsLoot.getBlocksLoots().put(Material.LAPIS_ORE, new Loot(1, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.INK_SACK, 1, (short) 4), 100.0, new Interval<>(4, 8)))));
            VarsLoot.getBlocksLoots().put(Material.REDSTONE_ORE, new Loot(1.5, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.REDSTONE, 1), 100.0, new Interval<>(4, 5)))));
            VarsLoot.getBlocksLoots().put(Material.GLOWING_REDSTONE_ORE, new Loot(1.5, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.REDSTONE, 1), 100.0, new Interval<>(4, 5)))));
            /*FIN MINERAIS*/
            /*ANIMAUX*/
            VarsLoot.getEntitiesLoots().put(EntityType.CHICKEN, new Loot(1, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.FEATHER, 1), (double)GameConfig.ConfigurableParams.FEATHER.getValue(), new Interval<>(1, 3)),
                    new LootItem(new ItemStack(Material.RAW_CHICKEN, 1), 100.0, new Interval<>(1, 1)))));

            VarsLoot.getEntitiesLoots().put(EntityType.COW, new Loot(1, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.LEATHER, 1), (double)GameConfig.ConfigurableParams.LEATHER.getValue(), new Interval<>(1, 2)),
                    new LootItem(new ItemStack(Material.RAW_BEEF, 1), 100.0, new Interval<>(1, 3)))));
            /*FIN ANIMAUX*/

            for (PlayerUHC p : main.getAlivePlayers()) {
                Player player = p.getPlayer().getPlayer();
                p.freeze();
                p.setInvulnerable(true);
                InventoryManager.clearInventory(player);
                player.setLevel(0);
                player.setExp(0f);
                player.setGameMode(GameMode.SURVIVAL);
                for (Achievement a : Achievement.values())
                    if (player.hasAchievement(a)) player.removeAchievement(a);
                player.updateInventory();
                main.getInventoryManager().giveStartInventory(player);
                player.updateInventory();
            }
            UHCWorld.setAchievements((Boolean)GameConfig.ConfigurableParams.ACHIEVEMENTS.getValue());
            if (GameConfig.getTeamTypeInt(GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString()) == 1) for (PlayerUHC p : main.getAlivePlayers()) {
                p.getPlayer().getPlayer().teleport(main.world.getSpawns().remove(0));
                for (Player pl : Bukkit.getOnlinePlayers())
                    pl.playSound(pl.getLocation(), Sound.CHICKEN_EGG_POP, 7f ,2f);
                if (!Scenarios.ANONYMOUS.isActivated())
                    Bukkit.broadcastMessage(UHC.getPrefix() + p.getPlayer().getPlayer().getDisplayName() + " §ea été téléporté !");
                p.setLastLocation(p.getPlayer().getPlayer().getLocation().add(0, -38, 0));
            }
            else {
                for (UHCTeam t : main.getUHCTeamManager().getTeams()) {
                    Location l = main.world.getSpawns().remove(0);
                    for (PlayerUHC playerUHC : t.getPlayers()) {
                        playerUHC.getPlayer().getPlayer().teleport(l);
                        playerUHC.getPlayer().getPlayer().setDisplayName(t.getPrefix().toString() + playerUHC.getPlayer().getPlayer().getName());
                        if (Scenarios.SLAVE_MARKET.isActivated() && SlaveMarket.owners.contains(playerUHC))
                            playerUHC.getPlayer().getPlayer().setDisplayName(t.getPrefix().toString() + "§l" + playerUHC.getPlayer().getPlayer().getName());
                        playerUHC.getPlayer().getPlayer().setPlayerListName(playerUHC.getPlayer().getPlayer().getDisplayName());
                        for (Player pl : Bukkit.getOnlinePlayers())
                            pl.playSound(pl.getLocation(), Sound.CHICKEN_EGG_POP, 7f ,2f);
                        if (!Scenarios.ANONYMOUS.isActivated())
                            Bukkit.broadcastMessage(UHC.getPrefix() + playerUHC.getPlayer().getPlayer().getDisplayName() + " §ea été téléporté !");
                        playerUHC.setLastLocation(playerUHC.getPlayer().getPlayer().getLocation().add(0, -39, 0));
                    }
                    t.getTeam().setAllowFriendlyFire((boolean)GameConfig.ConfigurableParams.FRIENDLY_FIRE.getValue());
                }
            }
            for (Scenarios sc : Scenarios.getActivatedScenarios())
                try {
                    sc.getScenarioClass().getMethod("execute").invoke(sc.getScenarioClass().newInstance());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                    e.printStackTrace();
                    Bukkit.broadcastMessage(UHC.getPrefix() + "§4[§cErreur§4] §cUne erreur s'est produite lors de l'exécutions des scénarios, veuillez en informer Neyuux_ !");
                }

            new UHCRunnable(main).runTaskTimer(main, 1, 20);
            cancel();
            timer = 11;
            main.uhcStart = null;
        }
    }

    private void sendStartTimerTitle(Player pls) {
        if (timer != 1)
            UHC.sendTitle(pls, ChatColor.translateAlternateColorCodes('&', main.mode.getPrefix()), "§eLancement dans §6§l" + timer + " §esecondes.", 0, 20, 0);
        else
            UHC.sendTitle(pls, ChatColor.translateAlternateColorCodes('&', main.mode.getPrefix()), "§eLancement dans §6§l" + timer + " §eseconde.", 0, 20, 0);
    }

    public void cancelStart() {
        main.setState(Gstate.WAITING);
        cancel();
        if (timer != -1) Bukkit.broadcastMessage(UHC.getPrefix() + "§cLe démarrage de la partie a été annulé !");
        timer = 11;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setLevel(0);
            p.setExp(0f);
        }
        main.uhcStart = null;
    }
}
