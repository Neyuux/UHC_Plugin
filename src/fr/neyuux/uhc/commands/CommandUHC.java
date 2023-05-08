package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.listeners.FightListener;
import fr.neyuux.uhc.listeners.PlayerListener;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.*;
import fr.neyuux.uhc.scenario.classes.modes.AssaultAndBattery;
import fr.neyuux.uhc.scenario.classes.modes.Moles;
import fr.neyuux.uhc.scenario.classes.modes.SlaveMarket;
import fr.neyuux.uhc.scenario.classes.modes.Switch;
import fr.neyuux.uhc.tasks.UHCRunnable;
import fr.neyuux.uhc.tasks.UHCStart;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.teams.UHCTeamManager;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Material.GOLDEN_APPLE;

public class CommandUHC implements CommandExecutor {

    private final UHC main;
    public CommandUHC(UHC main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        final String helpmessage = "§fAide pour la commande §e"+alias+"§f :§r\n§e/"+alias+" whitelist/wl §a<on/off/add/remove/list/clear>\n§e/"+alias+" classementores" +
                "\n§e/"+alias+" rules\n§e/"+alias+" host §a<add/remove/list>\n§e/"+alias+" spec §a<on/off/list>\n§e/"+alias+ " team §a<list/info>\n§e/"+alias+" chat §a<on/off>"+
                "\n§e/"+alias+" force §a<force>"+
                "\n§e/"+alias+" inv"+
                "\n§e/"+alias+" genworld";

        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerUHC playerUHC = main.getPlayerUHC(player);
            if (args.length > 0) {

                switch (args[0].toLowerCase()) {

                    case "admin":
                        if (playerUHC.isHost()) {
                            player.sendMessage(UHC.getPrefix() + "§cListe des commandes admin : \n" +
                                    "§6/uhc taupelist §c: Affiche la liste des taupes de la partie \n" +
                                    "§6/uhc aablist §cAffiche la liste des assaults and battery de la partie");

                            player.openInventory(main.getGameConfig().getGameConfigInv(player));

                            Bukkit.broadcastMessage(player.getItemInHand().getType() + "");
                        }
                    break;

                    case "whitelist":
                    case "wl":
                        if (main.getGameConfig().hosts.contains(player.getUniqueId())) {
                            final String helpwhitelistmessage = "§6La whitelist permet de trier les joueurs entrant sur le serveur.\nArgument possibles : \n" +
                                    "§eon §6: Active la whitelist (autorise seulement les joueurs whitelistés a rentrer sur le serveur)\n" +
                                    "§eoff §6: Désactive la whitelist§r\n§eadd §a<joueur> §6: Ajoute quelqu'un à la whitelist\n§eremove §a<joueur> §6: retire quelqu'un de la whitelist\n" +
                                    "§elist §6: Affiche la liste des joueurs whitelistés.\n§eclear §6: Vide la liste des joueurs whitelistés";
                            if (args.length > 1) {
                                if (args[1].equalsIgnoreCase("on") && !main.hasWhitelist) {
                                    Bukkit.broadcastMessage(UHC.getPrefix() + "§6La whitelist a été §aactivée §6!");
                                    main.hasWhitelist = true;
                                } else if (args[1].equalsIgnoreCase("off") && main.hasWhitelist) {
                                    Bukkit.broadcastMessage(UHC.getPrefix() + "§6La whitelist a été §cdésactivée §6!");
                                    main.hasWhitelist = false;

                                } else if (args[1].equalsIgnoreCase("add")) {
                                    if (args.length > 2) {
                                        @SuppressWarnings("deprecated")
                                        OfflinePlayer op = Bukkit.getOfflinePlayer(args[2]);
                                        if (op != null) {
                                            main.getWhitelist().add(op);
                                            player.sendMessage(UHC.getPrefix() + "§a§l" + op.getName() + " §6a été whitelisté !");
                                            UHC.sendActionBarForAllPlayers(UHC.getPrefix() + "§a§l" + op.getName() + " §6a été whitelisté !");
                                        } else
                                            player.sendMessage(UHC.getPrefix() + "§cLe joueur §4\"§6" + args[2] + "§4\" §cn'existe pas.");
                                    } else
                                        player.sendMessage(UHC.getPrefix() + "§cVeuillez renseigner un joueur à whitelister.");

                                } else if (args[1].equalsIgnoreCase("remove")) {
                                    if (args.length > 2) {
                                        @SuppressWarnings("deprecated")
                                        OfflinePlayer op = Bukkit.getOfflinePlayer(args[2]);
                                        if (op != null) {
                                            if (main.getWhitelist().contains(op)) {
                                                main.getWhitelist().remove(op);
                                                player.sendMessage(UHC.getPrefix() + "§c§l" + op.getName() + " §6a été retiré de la whitelist !");
                                                UHC.sendActionBarForAllPlayers(UHC.getPrefix() + "§c§l" + op.getName() + " §6a été retiré de la whitelist !");
                                            }
                                        } else
                                            player.sendMessage(UHC.getPrefix() + "§cLe joueur §4\"§6" + args[2] + "§4\" §cn'existe pas.");
                                    } else
                                        player.sendMessage(UHC.getPrefix() + "§cVeuillez renseigner un joueur à retirer de la whitelist.");

                                } else if (args[1].equalsIgnoreCase("list")) {
                                    StringBuilder swl = new StringBuilder();

                                    for (OfflinePlayer p : main.getWhitelist())
                                        swl.append("\n").append(" §e- §l").append(p.getName());
                                    player.sendMessage(UHC.getPrefix() + "§6Liste des joueurs whitelistés :" + swl);

                                } else if (args[1].equalsIgnoreCase("clear")) {
                                    main.getWhitelist().clear();
                                    Bukkit.broadcastMessage(UHC.getPrefix() + "§6La whitelist a été clear !");
                                } else player.sendMessage(UHC.getPrefix() + helpwhitelistmessage);
                            } else player.sendMessage(UHC.getPrefix() + helpwhitelistmessage);
                        } else player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'exécuter cette commande.");
                        break;
                    case "classementores":
                        if (!playerUHC.isAlive() || main.isState(Gstate.FINISHED)) {
                            if (main.getGameConfig().hosts.contains(player.getUniqueId())) {
                                if (main.isState(Gstate.PLAYING) || main.isState(Gstate.FINISHED)) {
                                    Inventory inv = Bukkit.createInventory(null, UHC.adaptInvSizeForInt(main.getAlivePlayers().size(), 0), "§6Classement des minerais");
                                    for (PlayerUHC pu : main.getAlivePlayers())
                                        if (pu.isAlive()) {
                                            if (pu.getTeam() != null) {
                                                inv.addItem(new ItemsStack(Material.SKULL_ITEM, (short) 3,
                                                        pu.getTeam().getPrefix().toString() + pu.getPlayer().getName(),
                                                        "§7Informations sur la partie de §l" + pu.getPlayer().getName(), "", "§bDiamants minés : §6§l" + pu.getDiamonds(), "§eOrs miné : §6§l" + pu.getGolds(),
                                                        "§fFers minés : §6§l" + pu.getIrons(), "§5Animaux tués : §6§l" + pu.getAnimals(), "§8Monstres tués : §6§l" + pu.getMonsters()).toItemStackwithSkullMeta(player.getName()));
                                            } else
                                                inv.addItem(new ItemsStack(Material.SKULL_ITEM, (short) 3,
                                                        "§6" + pu.getPlayer().getName(),
                                                        "§7Informations sur la partie de §l" + pu.getPlayer().getName(), "", "§bDiamants minés : §6§l" + pu.getDiamonds(), "§eOrs miné : §6§l" + pu.getGolds(),
                                                        "§fFers minés : §6§l" + pu.getIrons(), "§5Animaux tués : §6§l" + pu.getAnimals(), "§8Monstres tués : §6§l" + pu.getMonsters()).toItemStackwithSkullMeta(player.getName()));
                                        }
                                    player.openInventory(inv);
                                } else player.sendMessage(UHC.getPrefix() + "§cCette commande n'est disponible qu'en jeu.");
                            } else player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'exécuter cette commande.");
                        } else player.sendMessage(UHC.getPrefix() + "§cVous ne pouvez pas regarder le classement des minerais tant que vous êtes en vie !");
                        break;
                    case "kt":
                    case "killtotal":
                    case "killstotal":
                        if (main.isState(Gstate.PLAYING)) {
                            Comparator<Map.Entry<PlayerUHC, Integer>> valueComparator = (e1, e2) -> {
                                Integer v1 = e1.getValue();
                                Integer v2 = e2.getValue();
                                return v1.compareTo(v2);
                            };
                            List<Map.Entry<PlayerUHC, Integer>> listOfEntries = new ArrayList<>();
                            for (PlayerUHC pu : UHCTeamManager.baseplayers)
                                listOfEntries.add(new AbstractMap.SimpleEntry<>(pu, pu.getKills()));
                            listOfEntries.sort(valueComparator.reversed());

                            player.sendMessage(UHC.getPrefix() + "§6Liste totale des kills de la game :");
                            for(Map.Entry<PlayerUHC, Integer> en : listOfEntries) {
                                int kills = en.getValue();
                                PlayerUHC pu = en.getKey();
                                if (kills == 0) continue;

                                if (pu.getPlayer().isOnline())
                                    player.sendMessage(" - " + pu.getPlayer().getPlayer().getDisplayName() + " §f" + Symbols.ARROW_RIGHT_FULL + " §c§l" + kills + " §ckill" + (kills != 1 ? "s" : ""));
                                else player.sendMessage(" - §b" + pu.getPlayer().getName() + " §f" + Symbols.ARROW_RIGHT_FULL + " §c§l" + kills + " §ckill" + (kills != 1 ? "s" : ""));
                            }
                        } else player.sendMessage(UHC.getPrefix() + "§cCette commande n'est disponible qu'en jeu.");
                        break;
                    case "rules":
                    case "rule":
                    case "uhc":
                    case "regles":
                    case "règles":
                        StringBuilder sb = new StringBuilder(UHC.getPrefix() + "§6Informations sur la partie : \n");
                        sb.append("§eChat : ").append(GameConfig.getStringBoolean(PlayerListener.canChat)).append("\n");
                        sb.append("\n§c").append(Symbols.SQUARE).append(" §8Options des joueurs : \n");
                        for (GameConfig.ConfigurableParams cp : GameConfig.ParamParts.PLAYERRULES.getParams())
                            sb.append(" §e").append(ChatColor.stripColor(cp.getName())).append(" §6(").append(cp.getDescription()).append("§6) §e: §b").append(cp.getVisibleValue()).append("\n");
                        sb.append("\n§c").append(Symbols.SQUARE).append(" §8Options de la border : \n");
                        for (GameConfig.ConfigurableParams cp : GameConfig.ParamParts.BORDER.getParams())
                            sb.append(" §e").append(ChatColor.stripColor(cp.getName())).append(" §6(").append(cp.getDescription()).append("§6) §e: §b").append(cp.getVisibleValue()).append("\n");
                        sb.append(" §eTimer du PvP : §b").append(GameConfig.ConfigurableParams.PVP.getVisibleValue()).append("\n");
                        sb.append("\n§c").append(Symbols.SQUARE).append(" §8Options du Monde : \n");
                        for (GameConfig.ConfigurableParams cp : GameConfig.ParamParts.WORLDRULES.getParams())
                            sb.append(" §e").append(ChatColor.stripColor(cp.getName())).append(" §6(").append(cp.getDescription()).append("§6) §e: §b").append(cp.getVisibleValue()).append("\n");

                        player.sendMessage(sb.toString());
                        break;
                    case "inv":
                    case "inventaire":
                    case "inventory":
                        Inventory inv = Bukkit.createInventory(null, 45, "§dInventaire de §lDépart§8 /inv");
                        int i = 0;
                        for (Map.Entry<Integer, ItemStack> en : main.getInventoryManager().getStartArmor().entrySet()) {
                            inv.setItem(i, en.getValue());
                            i++;
                        }
                        for (int j = 0; j < 9; j++) if (inv.getItem(j) == null || inv.getItem(j).getType().equals(Material.AIR)) inv.setItem(j, new ItemsStack(Material.BEDROCK, "").toItemStack());
                        HashMap<Integer, ItemStack> si = new HashMap<>();
                        i = 0;
                        for (ItemStack it : InventoryManager.startInventory) {
                            si.put(i, it);
                            i++;
                        }
                        for (Map.Entry<Integer, ItemStack> en : si.entrySet()) {
                            int k = en.getKey();
                            if (k >= 9 && k <= 17) k += 18;
                            else if (k >= 27 && k <= 35) k -= 18;
                            inv.setItem(k + 9, en.getValue());
                        }
                        player.openInventory(inv);
                        break;
                    case "team":
                        final String helpteammessage = "§6La commande team permet de recevoir des informations sur les équipes de la partie.\nArgument possibles : \n" +
                                "§einfo §6: Donne des informations sur l'équipe actuelle du joueur§r\n"+
                                "§elist §6: Affiche la liste des équipes et de leurs joueurs.";
                        if (args.length > 1) {
                            if (args[1].equals("list")) {
                                StringBuilder sbl = new StringBuilder(UHC.getPrefix() + "§6Informations sur les équipes de la partie : \n");
                                if (main.getUHCTeamManager().getAliveTeams().size() > 0) {
                                    for (UHCTeam t : main.getUHCTeamManager().getTeams()) {
                                        sbl.append(t.getTeam().getDisplayName()).append(" §6(§e").append(t.getAlivePlayers().size()).append(" joueurs§6) (§c").append(t.getKills()).append(" kills§6) : \n");
                                        for (PlayerUHC pu : t.getAlivePlayers())
                                            sbl.append("§6 - ").append(pu.getPlayer().getPlayer().getDisplayName()).append(" §6(§c").append(pu.getKills()).append(" kills§6)\n");
                                    }
                                } else sbl.append("§cIl n'y a aucune équipe dans la partie.");
                                player.sendMessage(sbl.toString());
                            } else if (args[1].equals("info")) {
                                StringBuilder sbi = new StringBuilder(UHC.getPrefix());
                                if (playerUHC.getTeam() != null) {
                                    UHCTeam t = playerUHC.getTeam();
                                    sbi.append("§6Informations sur l'équipe ").append(t.getTeam().getDisplayName()).append(" §6: \n");
                                    sbi.append("§cKills de l'équipe : §4§l").append(t.getKills()).append("\n");
                                    sbi.append("§7Joueurs de l'équipe §8(").append(t.getPlayers().size()).append(") §f: \n");
                                    for (PlayerUHC pu : t.getPlayers())
                                        if (t.getAlivePlayers().contains(pu))
                                            sbi.append("§f - ").append(pu.getPlayer().getPlayer().getDisplayName()).append(" §f: §c").append(pu.getKills()).append(" kills §8(§aConnecté§8)");
                                        else if (t.getDeathPlayers().contains(pu))
                                            sbi.append("§f - ").append(pu.getPlayer().getPlayer().getDisplayName()).append(" §f: §c").append(pu.getKills()).append(" kills §8(§cMort§8)");
                                        else
                                            sbi.append("§f - ").append(pu.getPlayer().getPlayer().getDisplayName()).append(" §f: §c").append(pu.getKills()).append(" kills §8(§5Déconnecté§8)");
                                } else sbi.append("§cVous n'êtes dans aucune équipe !");
                                player.sendMessage(sbi.toString());
                            } else player.sendMessage(UHC.getPrefix() + helpteammessage);
                        } else player.sendMessage(UHC.getPrefix() + helpteammessage);
                        break;
                    case "host":
                    case "h":
                        final String helphostmessage = "§6La commande host permet de proumevoir quelqu'un host de la partie.\nArgument possibles : \n" +
                                "§eadd §a<joueur> §6: Proumevoit quelqu'un host§r\n§eremove §a<joueur> §6: Enlève le host à quelqu'un§r\n" +
                                "§elist §6: Affiche la liste des host.";
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("add")) {
                                if (main.getGameConfig().hosts.contains(player.getUniqueId())) {
                                    if (args.length > 2) {
                                        Player p = Bukkit.getPlayer(args[2]);
                                        if (p != null) {
                                            if (!main.getPlayerUHC(p).isHost()) {
                                                main.setPlayerHost(p, true);
                                                Bukkit.broadcastMessage(UHC.getPrefix() + "§a§l" + p.getName() + " §6a été promu §lHost §6de la partie !");
                                            } else player.sendMessage(UHC.getPrefix() + "§cCe joueur est déjà §6§lHost §cde la partie !");
                                        } else player.sendMessage(UHC.getPrefix() + "§cLe joueur §4\"§e" + args[2] + "§4\" §cn'existe pas.");
                                    } else player.sendMessage(UHC.getPrefix() + helphostmessage);
                                } else player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'exécuter cette commande.");

                            } else if (args[1].equalsIgnoreCase("remove")) {
                                if (main.getGameConfig().hosts.contains(player.getUniqueId())) {
                                    if (args.length > 2) {
                                        OfflinePlayer op = null;
                                        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers())
                                            if (offlinePlayer != null && offlinePlayer.getName().equals(args[2]))
                                                op = offlinePlayer;
                                        if (op == null) {
                                            player.sendMessage(UHC.getPrefix() + "§cLe joueur §4\"§e" + args[2] + "§4\"§c n'existe pas.");
                                            return true;
                                        }
                                        if (main.getGameConfig().hosts.contains(op.getUniqueId())) {
                                            if (op.isOnline()) main.setPlayerHost(op.getPlayer(), false);
                                            else {
                                                main.getGameConfig().hosts.remove(op.getUniqueId());
                                                if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Host").hasEntry(op.getName()))
                                                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(op.getName());
                                            }
                                            Bukkit.broadcastMessage(UHC.getPrefix() + "§c§l" + op.getName() + " §6n'est plus §lHost §6de la partie !");
                                        } else player.sendMessage(UHC.getPrefix() + "§cCe joueur n'est pas §6§lHost §cde la partie !");
                                    } else player.sendMessage(UHC.getPrefix() + helphostmessage);
                                } else
                                    player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'exécuter cette commande.");

                            } else if (args[1].equalsIgnoreCase("list")) {
                                StringBuilder swl = new StringBuilder();

                                for (PlayerUHC pu : main.players)
                                    if (pu.isHost())
                                        swl.append("\n").append(" §e- §l").append(pu.getPlayer().getName());
                                player.sendMessage(UHC.getPrefix() + "§6Liste des hosts de la partie :" + swl);
                            }
                        } else player.sendMessage(UHC.getPrefix() + helphostmessage);
                        break;
                    case "spec":
                        final String helpspecmessage = "§6La commande spec gérer les spectateurs de la partie.\nArgument possibles : \n" +
                                "§eon §6: Devient spectateur\n§eoff §6: Retire le mode spectateur\n" +
                                "§elist §6: Affiche la liste des spectateurs.";
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("on")) {
                                if (!main.spectators.contains(player)) {
                                    if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING)) {
                                        main.spectators.add(player);
                                        player.setGameMode(GameMode.SPECTATOR);
                                        player.setDisplayName("§8[§7Spectateur§8] §7" + player.getName());
                                        player.setPlayerListName(player.getDisplayName());
                                        InventoryManager.clearInventory(player);
                                        playerUHC.heal();

                                        player.sendMessage(UHC.getPrefix() + "§6Votre mode de jeu à été établi en spectateur.");
                                        player.sendMessage(UHC.getPrefix() + "§7Pour revenir au mode non-spectateur, utilisez la commande §6§l/uhc spec off§7.");
                                    } else player.sendMessage(UHC.getPrefix() + "§cCette commande est inutilisable une fois la partie commencée.");
                                } else player.sendMessage(UHC.getPrefix() + "§cVous êtes déjà en spectateur !");

                            } else if (args[1].equalsIgnoreCase("off")) {
                                if (main.spectators.contains(player)) {
                                    if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING)) {
                                        main.spectators.remove(player);
                                        player.teleport(main.world.getPlatformLoc());
                                        player.setGameMode(GameMode.ADVENTURE);
                                        if (playerUHC.isHost())
                                            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Host").addEntry(player.getName());
                                        else
                                            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(player.getName());
                                        player.setDisplayName(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()).getPrefix() + player.getName());
                                        player.setPlayerListName(player.getDisplayName());
                                        InventoryManager.giveWaitInventory(player);

                                        player.sendMessage(UHC.getPrefix() + "§6Vous n'êtes plus un spectateur.");
                                    } else player.sendMessage(UHC.getPrefix() + "§cCette commande est inutilisable une fois la partie commencée.");
                                } else player.sendMessage(UHC.getPrefix() + "§cVous n'êtes pas en spectateur !");

                            } else if (args[1].equalsIgnoreCase("list")) {
                                StringBuilder swl = new StringBuilder();

                                for (Player p : main.spectators)
                                    swl.append("\n").append(" §e- §l").append(p.getName());
                                player.sendMessage(UHC.getPrefix() + "§6Liste des spectateurs de la partie :" + swl);
                            }
                        } else player.sendMessage(UHC.getPrefix() + helpspecmessage);
                        break;

                    case "start":
                        if (main.uhcStart != null) main.uhcStart.cancelStart();
                        else {
                            main.setState(Gstate.STARTING);
                            new UHCStart(main).runTaskTimer(main, 0, 20);
                        }
                        break;

                    case "force":
                        if (playerUHC.isHost()) {
                            if (args.length > 1) {
                                switch (args[1].toLowerCase()) {
                                    case "pvp":
                                        Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé le PvP !");
                                        UHCRunnable.pvpTimer = 6;
                                        break;
                                    case "border":
                                    case "bordure":
                                        Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé la Bordure !");
                                        UHCRunnable.borderTimer = 6;
                                        break;
                                    case "épisode":
                                    case "episode":
                                    case "episod":
                                        Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé l'épisode !");
                                        UHCRunnable.episodTimer = 6;
                                        break;
                                    case "taupe":
                                    case "taupes":
                                    case "mole":
                                    case "moles":
                                        if (Scenarios.MOLES.isActivated())
                                            if (!Moles.hasChoosedMoles) {
                                                Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé l'annonce des Taupes !");
                                                Moles.IGtimers[0] = 6;
                                            } else {
                                                player.sendMessage(UHC.getPrefix() + "§cLes taupes ont déjà été annoncées");
                                                UHC.playNegativeSound(player);
                                            }
                                        else {
                                            player.sendMessage(UHC.getPrefix() + "§cLe scénario " + Scenarios.MOLES.getDisplayName() + " §cn'est pas activé !");
                                            UHC.playNegativeSound(player);
                                        }
                                        break;
                                    case "supertaupe":
                                    case "supertaupes":
                                    case "supermole":
                                    case "supermoles":
                                        if (Scenarios.MOLES.isActivated())
                                            if (Moles.superMoles)
                                                if (!Moles.hasChoosedSuperMoles) {
                                                    Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé l'annonce des Super Taupes !");
                                                    Moles.IGtimers[1] = 6;
                                                } else {
                                                    player.sendMessage(UHC.getPrefix() + "§cLes super taupes ont déjà été annoncées");
                                                    UHC.playNegativeSound(player);
                                                }
                                            else {
                                                player.sendMessage(UHC.getPrefix() + "§cLes super taupes ne sont pas activées !");
                                                UHC.playNegativeSound(player);
                                            }
                                        else {
                                            player.sendMessage(UHC.getPrefix() + "§cLe scénario " + Scenarios.MOLES.getDisplayName() + " §cn'est pas activé !");
                                            UHC.playNegativeSound(player);
                                        }
                                        break;
                                    case "switch":
                                        if (Scenarios.SWITCH.isActivated()) {
                                            Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé le Switch !");
                                            Switch.IGtimers[0] = 6;
                                        } else {
                                            player.sendMessage(UHC.getPrefix() + "§cLe scénario " + Scenarios.SWITCH.getDisplayName() + " §cn'est pas activé !");
                                            UHC.playNegativeSound(player);
                                        }
                                        break;
                                    case "skyhigh":
                                        if (Scenarios.SKY_HIGH.isActivated())
                                            if (SkyHigh.IGtimers[0] != SkyHigh.timer) {
                                                Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé le SkyHigh !");
                                                SkyHigh.IGtimers[0] = 6;
                                            } else {
                                                player.sendMessage(UHC.getPrefix() + "§cImpossible de force SkyHigh maintenant !");
                                                UHC.playNegativeSound(player);
                                            }
                                        else {
                                            player.sendMessage(UHC.getPrefix() + "§cLe scénario " + Scenarios.SKY_HIGH.getDisplayName() + " §cn'est pas activé !");
                                            UHC.playNegativeSound(player);
                                        }
                                        break;
                                    case "netheribus":
                                        if (Scenarios.NETHERIBUS.isActivated())
                                            if (Netheribus.IGtimers[0] != Netheribus.timer) {
                                                Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé le Netheribus !");
                                                Netheribus.IGtimers[0] = 6;
                                            } else {
                                                player.sendMessage(UHC.getPrefix() + "§cImpossible de force Netheribus maintenant !");
                                                UHC.playNegativeSound(player);
                                            }
                                        else {
                                            player.sendMessage(UHC.getPrefix() + "§cLe scénario " + Scenarios.NETHERIBUS.getDisplayName() + " §cn'est pas activé !");
                                            UHC.playNegativeSound(player);
                                        }
                                        break;
                                    case "finalheal":
                                    case "fh":
                                        if (Scenarios.FINAL_HEAL.isActivated())
                                            if (FinalHeal.IGtimers[0] != FinalHeal.timer) {
                                                Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé le Final Heal !");
                                                FinalHeal.IGtimers[0] = 6;
                                            } else {
                                                player.sendMessage(UHC.getPrefix() + "§cImpossible de force Final Heal maintenant !");
                                                UHC.playNegativeSound(player);
                                            }
                                        else {
                                            player.sendMessage(UHC.getPrefix() + "§cLe scénario " + Scenarios.FINAL_HEAL.getDisplayName() + " §cn'est pas activé !");
                                            UHC.playNegativeSound(player);
                                        }
                                        break;
                                    case "bestpve":
                                        if (Scenarios.BEST_PVE.isActivated())
                                            if (BestPVE.IGtimers[0] != BestPVE.timer) {
                                                Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + player.getName() + " §e a forcé le Best PvE !");
                                                BestPVE.IGtimers[0] = 6;
                                            } else {
                                                player.sendMessage(UHC.getPrefix() + "§cImpossible de force Best PvE maintenant !");
                                                UHC.playNegativeSound(player);
                                            }
                                        else {
                                            player.sendMessage(UHC.getPrefix() + "§cLe scénario " + Scenarios.BEST_PVE.getDisplayName() + " §cn'est pas activé !");
                                            UHC.playNegativeSound(player);
                                        }
                                        break;
                                    default:
                                        player.sendMessage(UHC.getPrefix() + "§cArgument incorrect, liste des timers forçables : §e§opvp, border, episode, taupe, supertaupe, switch, skyhigh, netheribus, fh, bestpve");
                                        break;
                                }
                            } else player.sendMessage(UHC.getPrefix() + "§cArgument incorrect, liste des timers forçables : §e§opvp, border, episode, taupe, supertaupe, switch, skyhigh, netheribus, fh, bestpve");
                        } else {
                            player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'utiliser cette commande.");
                            UHC.playNegativeSound(player);
                        }
                        break;

                    case "help":
                        player.sendMessage(UHC.getPrefix() + helpmessage);

                    case "sm":
                    case "slavemarket":
                        if (!Scenarios.SLAVE_MARKET.isActivated() || main.isState(Gstate.PLAYING) || main.isState(Gstate.FINISHED)) return true;
                        if (args.length > 1) {
                            if (!UHC.getInstance().isState(Gstate.PLAYING) || SlaveMarket.owners.size() != SlaveMarket.nOwners) {
                                if (args[1].equals("candidate")) {
                                    if (!SlaveMarket.candidates.contains(playerUHC)) {
                                        if (!SlaveMarket.owners.contains(playerUHC)) {
                                            player.sendMessage(UHC.getPrefix() + "§aVous avez bien émis une candidature pour devenir acheteur.");
                                            UHC.playPositiveSound(player);
                                            SlaveMarket.candidates.add(playerUHC);
                                            SlaveMarket.candidsInv.setItem(SlaveMarket.candidsInv.firstEmpty(), new ItemsStack(Material.SKULL_ITEM, (short) 3, playerUHC.getPlayer().getPlayer().getDisplayName(), "§7Ce joueur ce propose pour être acheteur.", "", "§b>>Cliquer pour accepter").toItemStackwithSkullMeta(playerUHC.getPlayer().getPlayer().getName()));
                                        } else {
                                            player.sendMessage(UHC.getPrefix() + "§cVous êtes déjà un acheteur.");
                                            UHC.playNegativeSound(player);
                                        }
                                    } else {
                                        player.sendMessage(UHC.getPrefix() + "§cVous êtes déjà candidat pour être acheteur.");
                                    }
                                } else if (args[1].equals("view")) {
                                    if (playerUHC.isHost()) player.openInventory(SlaveMarket.candidsInv);
                                    else player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permissions d'exécuter cette commande");
                                }
                            } else player.sendMessage(UHC.getPrefix() + "§cImpossible d'utiliser cette commande en jeu.");

                        } else player.sendMessage(UHC.getPrefix() + "§cArguments incomplets. Utilisation : §e/uhc sm §4§l<view/candidate>");
                        break;
                    case "bid":
                        if (Scenarios.SLAVE_MARKET.isActivated()) {
                            if (SlaveMarket.owners.size() == SlaveMarket.nOwners) {
                                if (SlaveMarket.owners.contains(playerUHC)) {
                                    if (args.length > 1) {
                                        try {
                                            int j = Integer.parseInt(args[1]);
                                            if (j > SlaveMarket.bid)
                                                if (player.getInventory().contains(Material.DIAMOND)) {
                                                    if (player.getInventory().getItem(player.getInventory().first(Material.DIAMOND)).getAmount() >= j) {
                                                        SlaveMarket.bid = j;
                                                        SlaveMarket.bestBidder = playerUHC;
                                                        SlaveMarket.timers[0] = 4;
                                                        UHC.playPositiveSound(player);
                                                        Bukkit.broadcastMessage(UHC.getPrefix() + Scenarios.SLAVE_MARKET.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " " + player.getDisplayName() + " §ea parié §b" + j + " diamants §e!");
                                                    } else player.sendMessage(UHC.getPrefix() + "§cVous avez pas assez de diamants !");
                                                } else player.sendMessage(UHC.getPrefix() + "§cVous n'avez plus de diamants !");
                                        } catch (NumberFormatException e) {
                                            player.sendMessage(UHC.getPrefix() + "§cLa valeur donnée n'est pas un nombre valide.");
                                            UHC.playNegativeSound(player);
                                        }
                                    } else player.sendMessage(UHC.getPrefix() + "§cVeuillez préciser le nombre de diamants que vous voulez parier.");
                                } else player.sendMessage(UHC.getPrefix() + "§cVous n'êtes pas un owner !");
                            } else player.sendMessage(UHC.getPrefix() + "§cVous ne pouvez pas utiliser cette commande maintenant !");
                        } else {
                            player.sendMessage(UHC.getPrefix() + "§cLe scénario " + Scenarios.SLAVE_MARKET.getDisplayName() + " §cn'est pas activé !");
                            UHC.playNegativeSound(player);
                        }
                        break;

                    case "am":
                    case "anonymous":
                        if (!Scenarios.ANONYMOUS.isActivated()) return true;
                        if (args.length > 1) {
                            Anonymous.usedName = args[1];
                            if (playerUHC.isHost()) {
                                player.sendMessage(UHC.getPrefix() + "§6Vous avez bien mit l'identité du Scénario Anonymous à §b§l" + args[1] + "§6.");
                                UHC.playPositiveSound(player);
                            } else player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'exécuter cette commande.");

                        } else player.sendMessage(UHC.getPrefix() + "§cArguments incomplets. Utilisation : §e/uhc am §4§l<pseudo d'un compte Minecraft>");
                        break;

                    case "ti":
                    case "teaminventory":
                        if (!Scenarios.TEAM_INVENTORY.isActivated() || !main.isState(Gstate.PLAYING)) return true;
                        if (playerUHC.isAlive() && !playerUHC.isSpec())
                            player.openInventory(TeamInventory.inventories.get(playerUHC.getTeam()));
                        break;

                    case "teamsupertaupe":
                        if (!Scenarios.MOLES.isActivated()) return true;
                        if (playerUHC.isHost()) {
                            if (args.length > 1) {
                                System.out.println("e");
                                boolean b = Moles.areSuperMolesTogether;
                                if (args[1].equalsIgnoreCase("off")) b = false;
                                else if (args[1].equalsIgnoreCase("on")) b = true;
                                Moles.areSuperMolesTogether = b;
                                Bukkit.broadcastMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " " + player.getDisplayName() + " §6a " + GameConfig.getStringBoolean(b) + " §6Équipe des Super-Taupes.");
                                UHC.playPositiveSound(player);
                            } else {
                                player.sendMessage(UHC.getPrefix() + "§cVeuillez préciser si vous voulez activer ou non §o(on/off)§c.");
                                UHC.playNegativeSound(player);
                            }
                        } else player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission.");
                        break;
                    case "apotaupes":
                        if (!Scenarios.MOLES.isActivated()) return true;
                        if (playerUHC.isHost()) {
                            if (args.length > 1) {
                                try {
                                    int j = Integer.parseInt(args[1]);
                                    Moles.nOfApoMoles = j;
                                    Bukkit.broadcastMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " " + player.getDisplayName() + " §6a mit le nombre de taupes à §e§l" + j + "§6.");
                                    UHC.playPositiveSound(player);
                                } catch (NumberFormatException e) {
                                    player.sendMessage(UHC.getPrefix() + "§cLa valeur donnée n'est pas un nombre valide.");
                                    UHC.playNegativeSound(player);
                                }
                            } else {
                                player.sendMessage(UHC.getPrefix() + "§cVeuillez préciser le nombre de taupes que vous voulez mettre.");
                                UHC.playNegativeSound(player);
                            }
                        } else player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission.");
                        break;
                    case "reveal":
                        if (!Scenarios.MOLES.isActivated() || !main.isState(Gstate.PLAYING)) return true;
                        if (!Moles.alreadyReveal.contains(playerUHC)) {
                            if (Moles.isTaupe(playerUHC)) {
                                Bukkit.broadcastMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " " + player.getDisplayName() + " §cse révèle être une taupe faisant partie de l'équipe " + Moles.taupes.get(playerUHC).getTeam().getDisplayName() + "§c !");
                                for (Player p : Bukkit.getOnlinePlayers())
                                    p.playSound(p.getLocation(), Sound.GHAST_SCREAM, 8f, 2f);

                                playerUHC.getTeam().getPlayers().remove(playerUHC);
                                playerUHC.getTeam().getAlivePlayers().remove(playerUHC);

                                if (playerUHC.getTeam().getAlivePlayers().size() == 0)
                                    Bukkit.broadcastMessage(UHC.getPrefix() + "§6L'équipe " + playerUHC.getTeam().getTeam().getDisplayName() + " §6est éliminée...");

                                Moles.taupes.get(playerUHC).add(player);
                                Moles.alreadyReveal.add(playerUHC);
                                InventoryManager.give(player, null, new ItemStack(GOLDEN_APPLE));
                                main.boards.get(playerUHC).setLine(0, player.getDisplayName());
                                for (PlayerUHC pu : main.players)
                                    main.boards.get(pu).setLine(3, "§7§lTeams : §f" + main.getUHCTeamManager().getAliveTeams().size() + "§8/§7" + UHCTeamManager.baseteams + " §8(§7" + main.getAlivePlayers().size() + "§8 joueurs)");

                                FightListener.checkWin();
                            } else player.sendMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cVous n'êtes pas une taupe ! (Demander à quelqu'un d'autre de lire ce message est interdit, je te vois venir)");
                        } else player.sendMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cVous êtes déjà reveal !");
                        break;
                    case "superreveal":
                        if (!Scenarios.MOLES.isActivated() || !main.isState(Gstate.PLAYING)) return true;
                        if (!Moles.alreadySuperReveal.contains(playerUHC)) {
                            if (Moles.alreadyReveal.contains(playerUHC)) {
                                if (Moles.isSuperTaupe(playerUHC)) {
                                    Bukkit.broadcastMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " " + player.getDisplayName() + " §fse révèle être une §lSuper Taupe§f faisant partie de l'équipe " + Moles.superTaupes.get(playerUHC).getTeam().getDisplayName() + "§c !");
                                    for (Player p : Bukkit.getOnlinePlayers())
                                        p.playSound(p.getLocation(), Sound.GHAST_SCREAM, 8f, 2f);
                                    playerUHC.getTeam().getPlayers().remove(playerUHC);
                                    playerUHC.getTeam().getAlivePlayers().remove(playerUHC);
                                    if (playerUHC.getTeam().getAlivePlayers().size() == 0)
                                        Bukkit.broadcastMessage(UHC.getPrefix() + "§6L'équipe " + playerUHC.getTeam().getTeam().getDisplayName() + " §6est éliminée...");
                                    Moles.superTaupes.get(playerUHC).add(player);
                                    Moles.alreadySuperReveal.add(playerUHC);
                                    if (Moles.areSuperMolesTogether)
                                        InventoryManager.give(player, null, new ItemStack(GOLDEN_APPLE));
                                    else InventoryManager.give(player, null, new ItemStack(GOLDEN_APPLE, 3));
                                    main.boards.get(playerUHC).setLine(0, player.getDisplayName());
                                    for (PlayerUHC pu : main.players)
                                        main.boards.get(pu).setLine(3, "§7§lTeams : §f" + main.getUHCTeamManager().getAliveTeams().size() + "§8/§7" + UHCTeamManager.baseteams + " §8(§7" + main.getAlivePlayers().size() + "§8 joueurs)");
                                    FightListener.checkWin();
                                } else
                                    player.sendMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " Vous n'êtes pas une super taupe ! (Demander à quelqu'un d'autre de lire ce message est interdit, je te vois venir)");
                            } else player.sendMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cVous devez être reveal pour vous super reveal !");
                        } else player.sendMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cVous êtes déjà super reveal !");
                        break;
                    case "claim":
                        if (!Scenarios.MOLES.isActivated() || !main.isState(Gstate.PLAYING)) return true;
                        if (!Moles.alreadyUse.contains(playerUHC)) {
                            if (Moles.isTaupe(playerUHC)) {
                                Moles.Kits kit = Moles.kits.get(playerUHC);
                                for (ItemStack it : kit.getItems())
                                    InventoryManager.give(player, null, it);
                                player.sendMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cVous avez obtenu les objets de votre kit " + kit.getName() + "§c.");
                                UHC.playPositiveSound(player);
                                Moles.alreadyUse.add(playerUHC);
                            } else player.sendMessage(UHC.getPrefix() + "Vous n'êtes pas une super taupe ! (Demander à quelqu'un d'autre de lire ce message est interdit, je te vois venir)");
                        } else player.sendMessage(UHC.getPrefix() + Scenarios.MOLES.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cVous avez déjà récupéré votre kit !");
                        break;
                    case "t":
                        if (!Scenarios.MOLES.isActivated() || !main.isState(Gstate.PLAYING)) return true;
                        if (Moles.isTaupe(playerUHC)) {
                            List<String> m = new ArrayList<>(Arrays.asList(args));
                            m.remove(0);
                            for (Map.Entry<PlayerUHC, UHCTeam> en : Moles.taupes.entrySet())
                                if (en.getKey().getPlayer().isOnline() && en.getValue().equals(Moles.taupes.get(playerUHC)))
                                    en.getKey().getPlayer().getPlayer().sendMessage("§c[" + Moles.taupes.get(playerUHC).getTeam().getDisplayName() + "§c] " + player.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §c" + Arrays.stream(m.toArray()).map(part -> part + " ").collect(Collectors.joining()));
                        }
                        break;
                    case "st":
                        if (!Scenarios.MOLES.isActivated() || !main.isState(Gstate.PLAYING)) return true;
                        if (Moles.isSuperTaupe(playerUHC)) {
                            List<String> m = new ArrayList<>(Arrays.asList(args));
                            m.remove(0);
                            for (Map.Entry<PlayerUHC, UHCTeam> en : Moles.superTaupes.entrySet())
                                if (en.getKey().getPlayer().isOnline() && en.getValue().equals(Moles.superTaupes.get(playerUHC)))
                                    en.getKey().getPlayer().getPlayer().sendMessage("§f[" + Moles.superTaupes.get(playerUHC).getTeam().getDisplayName() + "§f] " + player.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §c" + Arrays.stream(m.toArray()).map(part -> part + " ").collect(Collectors.joining()));
                        }
                        break;
                    case "taupelist":
                        if (!Scenarios.MOLES.isActivated() || !main.isState(Gstate.PLAYING)) return true;
                        if ((playerUHC.isSpec() || main.isState(Gstate.FINISHED)) && playerUHC.isHost()) {
                            Bukkit.broadcastMessage(UHC.getPrefix() + "§eListe des taupes de la partie :");
                            Bukkit.broadcastMessage("§7------------------------------");
                            Bukkit.broadcastMessage("");
                            for (UHCTeam t : main.getUHCTeamManager().getTeams())
                                if (t.getPrefix().isTaupePrefix() && !t.getPrefix().isSuperTaupePrefix()) {
                                    Bukkit.broadcastMessage(" §0" + Symbols.SQUARE + " " + t.getTeam().getDisplayName() + "§8(§7" + t.getPlayers().size() + "§8) §6:");
                                    for (PlayerUHC pu : t.getPlayers()) {
                                        String details = " §8(";
                                        if (Moles.isSuperTaupe(pu)) details = details + "§c§lSuper Taupe§8) ";
                                        if (!pu.isAlive()) details = details + "§8(§cMort§8) ";
                                        if (pu.isAlive() && !pu.getPlayer().isOnline()) details = details + "§8(§7Déconnecté§8)";
                                        Bukkit.broadcastMessage("  §0- §f" + pu.getPlayer().getName() + details);
                                    }
                                    Bukkit.broadcastMessage("");
                                }
                            if (Moles.areSuperMolesTogether && main.getUHCTeamManager().getTeamByDisplayName("§f§lSuper Taupes") != null) {
                                UHCTeam t = main.getUHCTeamManager().getTeamByDisplayName("§f§lSuper Taupes");
                                Bukkit.broadcastMessage(" §0" + Symbols.SQUARE + " " + t.getTeam().getDisplayName() + "§8(§7" + t.getPlayers().size() + "§8) §6:");
                                for (PlayerUHC pu : t.getPlayers()) {
                                    String details = " §8(";
                                    if (Moles.isSuperTaupe(pu)) details = details + "§c§lSuper Taupe§8) ";
                                    if (!pu.isAlive()) details = details + "§8(§cMort§8) ";
                                    if (pu.isAlive() && !pu.getPlayer().isOnline()) details = details + "§8(§7Déconnecté§8)";
                                    Bukkit.broadcastMessage("  §0- §f" + pu.getPlayer().getName() + details);
                                }
                            }
                            Bukkit.broadcastMessage("§7------------------------------");
                        } else player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'utiliser cette commande.");
                        break;
                    case "aablist":
                        if (!Scenarios.ASSAULT_AND_BATTERY.isActivated() || !main.isState(Gstate.PLAYING)) return true;
                        if ((playerUHC.isSpec() || main.isState(Gstate.FINISHED)) && playerUHC.isHost()) {
                            Bukkit.broadcastMessage(UHC.getPrefix() + "§eListe des Assault et des Battery de la partie :");
                            Bukkit.broadcastMessage("§7------------------------------");
                            Bukkit.broadcastMessage("");
                            for (UHCTeam t : main.getUHCTeamManager().getAliveTeams())
                                if (AssaultAndBattery.hasRole(t.getAlivePlayers().get(0))) {
                                    Bukkit.broadcastMessage(" §0" + Symbols.SQUARE + " " + t.getTeam().getDisplayName() + "§8(§7" + t.getPlayers().size() + "§8) §6:");
                                    for (PlayerUHC pu : t.getPlayers()) {
                                        String details = " §8(";
                                        if (AssaultAndBattery.assaults.contains(pu)) details = details + "§4§lAssault§8) ";
                                        if (AssaultAndBattery.batteries.contains(pu)) details = details + "§a§lBattery§8) ";
                                        if (!pu.isAlive()) details = details + "§8(§cMort§8) ";
                                        if (pu.isAlive() && !pu.getPlayer().isOnline()) details = details + "§8(§7Déconnecté§8)";
                                        Bukkit.broadcastMessage("  §0- §f" + pu.getPlayer().getName() + details);
                                    }
                                    Bukkit.broadcastMessage("");
                                }
                            Bukkit.broadcastMessage("§7------------------------------");
                        } else player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'utiliser cette commande.");
                        break;

                    case "chat":
                        final String helpchatmessage = "§6La commande chat gérer le chat de la partie.\nArgument possibles : \n" +
                                "§eon §6: Actve le chat.\n" +
                                "§eoff §6: Désactive le chat.";
                        if (playerUHC.isHost()) {
                            if (args.length > 1) {
                                if (args[1].equals("on")) {
                                    PlayerListener.canChat = true;
                                    Bukkit.broadcastMessage(UHC.getPrefix() + player.getDisplayName() + " §aa réactivé le Chat !");
                                    for (Player p : Bukkit.getOnlinePlayers())
                                        UHC.playPositiveSound(p);
                                } else if (args[1].equals("off")) {
                                    PlayerListener.canChat = false;
                                    Bukkit.broadcastMessage(UHC.getPrefix() + player.getDisplayName() + " §ca désactivé le Chat !");
                                    for (Player p : Bukkit.getOnlinePlayers())
                                        UHC.playPositiveSound(p);
                                } else player.sendMessage(UHC.getPrefix() + helpchatmessage);
                            } else player.sendMessage(UHC.getPrefix() + helpchatmessage);
                        } else {
                            player.sendMessage(UHC.getPrefix() + "§cVous n'avez pas la permission d'utiliser cette commande.");
                            UHC.playNegativeSound(player);
                        }
                    break;
                    case "genworld":
                    case "generateworld":
                    case "worldgen":
                    case "pregen":
                    case "pregenworld":
                        if (sender.isOp()) {
                            Bukkit.broadcastMessage(UHC.getPrefix() + "§b" + sender.getName() + " §2a commencé la prégénération du monde !");
                            main.world.generateChunks(World.Environment.NORMAL);
                        }

                    break;
                    case "crafts":
                    case "customcrafts":
                    case "craft":
                    case "customcraft":

                    break;
                    default:
                        player.sendMessage(UHC.getPrefix() + helpmessage);
                        break;

                }

            } else player.sendMessage(UHC.getPrefix() + helpmessage);
        } else {
            sender.sendMessage(UHC.getPrefix() + "§cVous devez être un joueur pour effectuer cette commande.");
            return true;
        }

        return true;
    }
}
