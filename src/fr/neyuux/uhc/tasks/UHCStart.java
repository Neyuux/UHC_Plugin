package fr.neyuux.uhc.tasks;

import com.google.common.collect.Lists;
import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.UHCWorld;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.modes.SlaveMarket;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.teams.UHCTeamColors;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class UHCStart extends BukkitRunnable {

    private static int timer = 11;
    public static BukkitTask waitTask;
    private static int id = 0;
    private final Index main;
    public UHCStart(Index main) {
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
                Bukkit.broadcastMessage(main.getPrefix() + "§eLancement du jeu dans §c§l" + timer + "§c seconde §e!");
            else
                Bukkit.broadcastMessage(main.getPrefix() + "§eLancement du jeu dans §c§l" + timer + "§c secondes §e!");

        timer--;

        if (timer == 0) {
            for (Scenarios sc : Scenarios.getActivatedScenarios())
                try {
                    Class<?> c = sc.getScenarioClass();
                    boolean b = (boolean) c.getMethod("checkStart").invoke(c.newInstance());

                    if (!b) {
                        Bukkit.broadcastMessage(main.getPrefix() + "§6Le Scénario " + sc.getDisplayName() + " §6empêche de lancer la partie à cause d'une mauvaise configuration.");
                        cancelStart();
                        return;
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                    e.printStackTrace();
                    Bukkit.broadcastMessage(main.getPrefix() + "§4[§cErreur§4] §cUne erreur s'est produite lors de la vérification pour start des scénarios, veuillez en informer Neyuux_ !");
                }
        }
        if (timer == -1) {
            main.setState(Gstate.PLAYING);
            for (PlayerUHC p : main.players)
                if (!p.isSpec())
                    p.setAlive(true);
            if (GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString().startsWith("Random"))
                main.getUHCTeamManager().randomTeams();
            UHCTeamManager.baseteams = main.getUHCTeamManager().getTeams().size();
            UHCTeamManager.baseplayers = main.getAlivePlayers();
            for (PlayerUHC playerUHC : main.getAlivePlayers()) {
                if (!GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA") && playerUHC.getTeam() == null)
                    for (UHCTeam t : main.getUHCTeamManager().getTeams())
                        if (playerUHC.getTeam() == null && (!((String) GameConfig.ConfigurableParams.TEAMTYPE.getValue()).startsWith("To") || t.getPlayers().size() < Integer.parseInt(((String) GameConfig.ConfigurableParams.TEAMTYPE.getValue()).substring(2))))
                            t.add(playerUHC.getPlayer().getPlayer());
                InventoryManager.clearInventory(playerUHC.getPlayer().getPlayer());
            }

            if (Scenarios.SLAVE_MARKET.isActivated()) {
                if (SlaveMarket.nOwners == 1) SlaveMarket.nOwners = 2;
                if (SlaveMarket.nOwners > UHCTeamColors.values().length) SlaveMarket.nOwners = UHCTeamColors.values().length;
                if (!SlaveMarket.randomChoiceOwners) {
                    Bukkit.broadcastMessage(main.getPrefix() + "§6Veuillez choisir les acheteurs de la partie. Pour ce faire, les joueurs voulant l'être doivent effectuer la commande §b§l/uhc sm candidate §6et les hosts doivent accepter les joueurs.");
                    for (PlayerUHC playerUHC : main.players)
                        if (playerUHC.isHost())
                            playerUHC.getPlayer().getPlayer().sendMessage(main.getPrefix() + "§6Pour ouvrir l'inventaire des joueurs candidats, effectuez la commande §b§l/uhc sm view§6.");
                    final int[] waitTicks = {6000};
                    waitTask = new BukkitRunnable() {
                        public void run() {
                            InventoryManager.setAllPlayersLevels((short) (Math.floorDiv(waitTicks[0], 20) + 1), waitTicks[0] / 6000f);
                            if (waitTicks[0] == 0) {
                                cancel();
                                main.uhcStart = null;
                                waitTask = null;
                                Bukkit.broadcastMessage(main.getPrefix() + "§cVous avez mit trop de temps à choisir les acheteurs.");
                                main.rel();
                            }
                            waitTicks[0]--;
                        }
                    }.runTaskTimer(main, 0L, 1L);
                } else
                    while (SlaveMarket.owners.size() != SlaveMarket.nOwners)
                        SlaveMarket.owners.add(main.players.get(new Random().nextInt(main.players.size())));

                new BukkitRunnable() {
                    final int[] i = {0, 0};
                    public void run() {
                        if (SlaveMarket.owners.size() == SlaveMarket.nOwners) {
                            if (i[1] == 0) {
                                Bukkit.broadcastMessage(main.getPrefix() + "§aIl est temps d'annoncer les acheteurs de la partie.");
                                InventoryManager.setAllPlayersLevels(0, 0f);
                                i[1] = 1;
                                main.getUHCTeamManager().clearTeams();
                                for (int i = 1; i <= SlaveMarket.nOwners; i++)
                                    main.getUHCTeamManager().createTeam();
                                BukkitRunnable ownersPresentation = new BukkitRunnable() {
                                    public void run() {
                                        if (id == 0) {
                                            cancel();
                                            main.uhcStart = null;
                                            return;
                                        }
                                        UHCTeam t = main.getUHCTeamManager().getTeams().get(i[0]);
                                        PlayerUHC owner = SlaveMarket.owners.get(i[0]);
                                        Player p = owner.getPlayer().getPlayer();

                                        Bukkit.broadcastMessage(main.getPrefix() + t.getPrefix().color.getColor() + "L'acheteur de l'équipe " + t.getTeam().getDisplayName() + " sera §f" + p.getName() + t.getPrefix().color.getColor() +".");
                                        for (Player player : Bukkit.getOnlinePlayers())
                                            player.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 8, 1);
                                        t.add(p);
                                        //tp p
                                        p.setDisplayName(p.getDisplayName().substring(0, 2) + "§l" + p.getDisplayName().substring(2));
                                        p.setPlayerListName(p.getDisplayName());

                                        i[0]++;
                                        if (i[0] == main.getUHCTeamManager().getTeams().size()) {
                                            id = 0;
                                            i[1] = 2;
                                            cancel();
                                            main.uhcStart = null;
                                        }
                                    }
                                };
                                ownersPresentation.runTaskTimer(main, 40L, 60L);
                                id = ownersPresentation.getTaskId();
                            } else if (i[1] == 2) {
                                cancel();
                                main.uhcStart = null;
                            }
                        } else
                            if (waitTask == null) {
                                cancel();
                                main.uhcStart = null;
                            }
                    }
                }.runTaskTimer(main, 0L, 20L);
            }

            InventoryManager.clearAllPlayersEffects();
            for (Player p : Bukkit.getOnlinePlayers())
                main.setGameScoreboard(p);
            Objective health = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("health");
            Objective healthBelow = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("healthBelow");
            if ((boolean)(GameConfig.ConfigurableParams.SCOREBOARD_LIFE.getValue())) {
                if (Scenarios.TEAM_HEALTH.isActivated()) {
                    health.unregister();
                    healthBelow.unregister();
                    health = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("healthBelow", "dummy");
                    healthBelow = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("healthBelow", "dummy");
                }
                health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                healthBelow.setDisplaySlot(DisplaySlot.BELOW_NAME);
            } else {
                health.unregister();
                healthBelow.unregister();
            }
            Bukkit.broadcastMessage(main.getPrefix() + "§2Lancement de la Pré-Génération du monde...");
            for (PlayerUHC p : main.getAlivePlayers()) {
                main.world.addSpawnLoad();
                Index.setF3(p.getPlayer().getPlayer(), !(boolean)GameConfig.ConfigurableParams.COORDS_F3.getValue());
                if (Scenarios.TEAM_HEALTH.isActivated()) {
                    health.getScore(p.getPlayer().getName()).setScore((int)p.getTeam().getHealth());
                    healthBelow.getScore(p.getPlayer().getName()).setScore((int)p.getTeam().getHealth());
                }
                if (p.isSpec() && !(boolean)GameConfig.ConfigurableParams.SPECTATORS.getValue())
                    p.getPlayer().getPlayer().kickPlayer(main.getPrefix() + "§cLes spectateurs se sont pas autorisés.");
            }
            main.world.loadChunks();
            main.world.setTime(0);
            main.world.setDayCycle((Boolean)GameConfig.ConfigurableParams.DAY_CYCLE.getValue());
            main.world.initialiseWorldBorder();
            if((boolean) GameConfig.ConfigurableParams.CRAFT_GOLDEN_HEAD.getValue()) {
                ShapedRecipe recette = new ShapedRecipe(Index.getGoldenHead(1));
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
                    new LootItem(new ItemStack(Material.COAL, 1), 100.0, new Interval<>(1, 1)))));
            VarsLoot.getBlocksLoots().put(Material.LAPIS_ORE, new Loot(1, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.INK_SACK, 1, (short) 4), 100.0, new Interval<>(4, 8)))));
            VarsLoot.getBlocksLoots().put(Material.REDSTONE_ORE, new Loot(1.5, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.REDSTONE, 1), 100.0, new Interval<>(4, 5)))));
            VarsLoot.getBlocksLoots().put(Material.GLOWING_REDSTONE_ORE, new Loot(1.5, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.REDSTONE, 1), 100.0, new Interval<>(4, 5)))));
            /*FIN MINERAIS*/
            /*ANIMAUX*/
            VarsLoot.getEntitiesLoots().put(EntityType.CHICKEN, new Loot(1, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.FEATHER, 1), 40+5*(double)GameConfig.ConfigurableParams.FEATHER.getValue(), new Interval<>(1, 3)),
                    new LootItem(new ItemStack(Material.RAW_CHICKEN, 1), 100.0, new Interval<>(1, 1)))));

            VarsLoot.getEntitiesLoots().put(EntityType.COW, new Loot(1, Lists.newArrayList(
                    new LootItem(new ItemStack(Material.LEATHER, 1), 40+5*(double)GameConfig.ConfigurableParams.LEATHER.getValue(), new Interval<>(1, 3)),
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
            if (main.getUHCTeamManager().getTeams().isEmpty()) for (PlayerUHC p : main.getAlivePlayers()) {
                p.getPlayer().getPlayer().teleport(main.world.getSpawns().remove(0));
                for (Player pl : Bukkit.getOnlinePlayers())
                    pl.playSound(pl.getLocation(), Sound.CHICKEN_EGG_POP, 7f ,2f);
                Bukkit.broadcastMessage(main.getPrefix() + p.getPlayer().getPlayer().getDisplayName() + " §ea été téléporté !");
                p.setLastLocation(p.getPlayer().getPlayer().getLocation().add(0, -38, 0));
            }
            else {
                for (UHCTeam t : main.getUHCTeamManager().getTeams()) {
                    Location l = main.world.getSpawns().remove(0);
                    for (PlayerUHC playerUHC : t.getPlayers()) {
                        playerUHC.getPlayer().getPlayer().teleport(l);
                        for (Player pl : Bukkit.getOnlinePlayers())
                            pl.playSound(pl.getLocation(), Sound.CHICKEN_EGG_POP, 7f ,2f);
                        Bukkit.broadcastMessage(main.getPrefix() + playerUHC.getPlayer().getPlayer().getDisplayName() + " §ea été téléporté !");
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
                    Bukkit.broadcastMessage(main.getPrefix() + "§4[§cErreur§4] §cUne erreur s'est produite lors de l'exécutions des scénarios, veuillez en informer Neyuux_ !");
                }

            new UHCRunnable(main).runTaskTimer(main, 1, 20);
            cancel();
            timer = 11;
            main.uhcStart = null;
        }
    }

    private void sendStartTimerTitle(Player pls) {
        if (timer != 1)
            main.sendTitle(pls, ChatColor.translateAlternateColorCodes('&', main.mode.getPrefix()), "§eLancement dans §6§l" + timer + " §esecondes.", 0, 20, 0);
        else
            main.sendTitle(pls, ChatColor.translateAlternateColorCodes('&', main.mode.getPrefix()), "§eLancement dans §6§l" + timer + " §eseconde.", 0, 20, 0);
    }

    public void cancelStart() {
        main.setState(Gstate.WAITING);
        cancel();
        if (timer != -1) Bukkit.broadcastMessage(main.getPrefix() + "§cLe démarrage de la partie a été annulé !");
        timer = 11;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setLevel(0);
            p.setExp(0f);
        }
        main.uhcStart = null;
    }
}
