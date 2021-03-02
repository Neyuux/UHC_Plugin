package fr.neyuux.uhc.commands;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.events.SlaveMarketCandidateEvent;
import fr.neyuux.uhc.listeners.PlayerListener;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.Anonymous;
import fr.neyuux.uhc.scenario.classes.TeamInventory;
import fr.neyuux.uhc.scenario.classes.modes.SlaveMarket;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CommandUHC implements CommandExecutor {

    private final Index main;
    public CommandUHC(Index main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        final String helpmessage = "§fAide pour la commande §e"+alias+"§f :§r\n§e/"+alias+" whitelist/wl §a<on/off/add/remove/list/clear>\n§e/"+alias+" classementores" +
                "\n§e/"+alias+" rules\n§e/"+alias+" host §a<add/remove/list>\n§e/"+alias+" spec §a<on/off/list>\n§e/"+alias+ " team §a<list/info>\n§e/"+alias+" chat §a<on/off>";

        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerUHC playerUHC = main.getPlayerUHC(player);
            if (args.length > 0) {

                switch (args[0]) {

                    case "whitelist":
                        if (main.getGameConfig().hosts.contains(player.getUniqueId())) {
                            final String helpwhitelistmessage = "§6La whitelist permet de trier les joueurs entrant sur le serveur.\nArgument possibles : \n" +
                                    "§eon §6: Active la whitelist (autorise seulement les joueurs whitelistés a rentrer sur le serveur)\n" +
                                    "§eoff §6: Désactive la whitelist§r\n§eadd §a<joueur> §6: Ajoute quelqu'un à la whitelist\n§eremove §a<joueur> §6: retire quelqu'un de la whitelist" +
                                    "§elist §6: Affiche la liste des joueurs whitelistés.\n§eclear §6: Vide la liste des joueurs whitelistés";
                            if (args.length > 1) {
                                if (args[1].equalsIgnoreCase("on") && !Bukkit.getServer().hasWhitelist()) {
                                    Bukkit.broadcastMessage(main.getPrefix() + "§6La whitelist a été §aactivée §6!");
                                    Bukkit.getServer().setWhitelist(true);
                                } else if (args[1].equalsIgnoreCase("off") && Bukkit.getServer().hasWhitelist()) {
                                    Bukkit.broadcastMessage(main.getPrefix() + "§6La whitelist a été §cdésactivée §6!");
                                    Bukkit.getServer().setWhitelist(false);

                                } else if (args[1].equalsIgnoreCase("add")) {
                                    if (args.length > 2) {
                                        @SuppressWarnings("deprecated")
                                        OfflinePlayer op = Bukkit.getOfflinePlayer(args[2]);
                                        if (op != null) {
                                            Bukkit.getServer().getWhitelistedPlayers().add(op);
                                            player.sendMessage(main.getPrefix() + "§a§l" + op.getName() + " §6a été whitelisté !");
                                            Index.sendActionBarForAllPlayers(main.getPrefix() + "§a§l" + op.getName() + " §6a été whitelisté !");
                                        } else
                                            player.sendMessage(main.getPrefix() + "§cLe joueur §4\"§6" + args[2] + "§4\" §cn'existe pas.");
                                    } else
                                        player.sendMessage(main.getPrefix() + "§cVeuillez renseigner un joueur à whitelister.");

                                } else if (args[1].equalsIgnoreCase("remove")) {
                                    if (args.length > 2) {
                                        @SuppressWarnings("deprecated")
                                        OfflinePlayer op = Bukkit.getOfflinePlayer(args[2]);
                                        if (op != null) {
                                            if (Bukkit.getServer().getWhitelistedPlayers().contains(op)) {
                                                Bukkit.getServer().getWhitelistedPlayers().remove(op);
                                                player.sendMessage(main.getPrefix() + "§c§l" + op.getName() + " §6a été retiré de la whitelist !");
                                                Index.sendActionBarForAllPlayers(main.getPrefix() + "§c§l" + op.getName() + " §6a été retiré de la whitelist !");
                                            }
                                        } else
                                            player.sendMessage(main.getPrefix() + "§cLe joueur §4\"§6" + args[2] + "§4\" §cn'existe pas.");
                                    } else
                                        player.sendMessage(main.getPrefix() + "§cVeuillez renseigner un joueur à retirer de la whitelist.");

                                } else if (args[1].equalsIgnoreCase("list")) {
                                    StringBuilder swl = new StringBuilder();

                                    for (OfflinePlayer p : Bukkit.getServer().getWhitelistedPlayers())
                                        swl.append("\n").append(" §e- §l").append(p.getName());
                                    player.sendMessage(main.getPrefix() + "§6Liste des joueurs whitelistés :" + swl.toString());

                                } else if (args[1].equalsIgnoreCase("clear")) {
                                    Bukkit.getServer().getWhitelistedPlayers().clear();
                                    Bukkit.broadcastMessage(main.getPrefix() + "§6La whitelist a été clear !");
                                } else player.sendMessage(main.getPrefix() + helpwhitelistmessage);
                            } else player.sendMessage(main.getPrefix() + helpwhitelistmessage);
                        } else player.sendMessage(main.getPrefix() + "§cVous n'avez pas la permission d'exécuter cette commande.");
                        break;
                    case "classementores":
                        if (!playerUHC.isAlive()) {
                            if (main.getGameConfig().hosts.contains(player.getUniqueId())) {
                                if (main.isState(Gstate.PLAYING)) {
                                    Inventory inv = Bukkit.createInventory(null, Index.adaptInvSizeForInt(main.getAlivePlayers().size(), 0), "§6Classement des minerais");
                                    for (PlayerUHC pu : main.getAlivePlayers())
                                        if (pu.isAlive()) {
                                            if (pu.getTeam() != null) {
                                                inv.addItem(new ItemsStack(Material.SKULL_ITEM, (short) 3,
                                                        pu.getTeam().getPrefix().toString() + pu.getPlayer().getName(),
                                                        "§7Informations sur la partie de §l" + pu.getPlayer().getName(), "", "§bDiamants minés : §6§l" + pu.getDiamonds(), "§eOrs miné : §6§l" + pu.getGolds(),
                                                        "§fFers minés : §6§l" + pu.getIrons(), "§5Animaux tués : §6§l" + pu.getAnimals(), "§8Monstres tués : §6§l" + pu.getMonsters()).toItemStack());
                                            } else
                                                inv.addItem(new ItemsStack(Material.SKULL_ITEM, (short) 3,
                                                        "§6" + pu.getPlayer().getName(),
                                                        "§7Informations sur la partie de §l" + pu.getPlayer().getName(), "", "§bDiamants minés : §6§l" + pu.getDiamonds(), "§eOrs miné : §6§l" + pu.getGolds(),
                                                        "§fFers minés : §6§l" + pu.getIrons(), "§5Animaux tués : §6§l" + pu.getAnimals(), "§8Monstres tués : §6§l" + pu.getMonsters()).toItemStack());
                                        }
                                    player.openInventory(inv);
                                } else player.sendMessage(main.getPrefix() + "§cCette commande n'est disponible qu'en jeu.");
                            } else player.sendMessage(main.getPrefix() + "§cVous n'avez pas la permission d'exécuter cette commande.");
                        } else player.sendMessage(main.getPrefix() + "§cVous ne pouvez pas regarder le classement des minerais tant que vous êtes en vie !");
                        break;
                    case "rules":
                        StringBuilder sb = new StringBuilder(main.getPrefix() + "§6Informations sur la partie : \n");
                        sb.append("§eChat : ").append(GameConfig.getStringBoolean(PlayerListener.canChat)).append("\n");
                        sb.append("\n §8Options des joueurs : ");
                        for (GameConfig.ConfigurableParams cp : GameConfig.ParamParts.PLAYERRULES.getParams())
                            sb.append(" §e").append(cp.getName()).append(" §6(").append(cp.getDescription()).append("§) §e: §b").append(cp.getVisibleValue()).append("\n");
                        sb.append("\n §8Options de la border : ");
                        for (GameConfig.ConfigurableParams cp : GameConfig.ParamParts.BORDER.getParams())
                            sb.append(" §e").append(cp.getName()).append(" §6(").append(cp.getDescription()).append("§) §e: §b").append(cp.getVisibleValue()).append("\n");
                        sb.append("\n §eTimer du PvP : §b").append(GameConfig.ConfigurableParams.PVP.getVisibleValue()).append("\n");
                        sb.append("\n §8Options du Monde : ");
                        for (GameConfig.ConfigurableParams cp : GameConfig.ParamParts.WORLDRULES.getParams())
                            sb.append(" §e").append(cp.getName()).append(" §6(").append(cp.getDescription()).append("§) §e: §b").append(cp.getVisibleValue()).append("\n");

                        player.sendMessage(sb.toString());
                        break;
                    case "team":
                        final String helpteammessage = "§6La commande team permet de recevoir des informations sur les équipes de la partie.\nArgument possibles : \n" +
                                "§einfo §6: Donne des informations sur l'équipe actuelle du joueur§r\n"+
                                "§elist §6: Affiche la liste des équipes et de leurs joueurs.";
                        if (args.length > 1) {
                            if (args[1].equals("list")) {
                                StringBuilder sbl = new StringBuilder(main.getPrefix() + "§6Informations sur les équipes de la partie : \n");
                                if (main.getUHCTeamManager().getTeams().size() > 0) {
                                    for (UHCTeam t : main.getUHCTeamManager().getTeams()) {
                                        sbl.append(t.getTeam().getDisplayName()).append(" §6(§e").append(t.getAlivePlayers().size()).append(" joueurs§6) (§c").append(t.getKills()).append(" kills§6) : \n");
                                        for (PlayerUHC pu : t.getAlivePlayers())
                                            sbl.append("§6 - ").append(pu.getPlayer().getPlayer().getDisplayName()).append(" §6(§c").append(pu.getKills()).append(" kills§6)\n");
                                    }
                                } else sbl.append("§cIl n'y a aucune équipe dans la partie.");
                                player.sendMessage(sbl.toString());
                            } else if (args[1].equals("info")) {
                                StringBuilder sbi = new StringBuilder(main.getPrefix());
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
                            } else player.sendMessage(main.getPrefix() + helpteammessage);
                        } else player.sendMessage(main.getPrefix() + helpteammessage);
                        break;
                    case "host":
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
                                                Bukkit.broadcastMessage(main.getPrefix() + "§a§l" + p.getName() + " §6a été promu §lHost §6de la partie !");
                                            } else player.sendMessage(main.getPrefix() + "§cCe joueur est déjà §6§lHost §cde la partie !");
                                        } else player.sendMessage(main.getPrefix() + "§cLe joueur §4\"§e" + args[2] + "§4\" §cn'existe pas.");
                                    } else player.sendMessage(main.getPrefix() + helphostmessage);
                                } else player.sendMessage(main.getPrefix() + "§cVous n'avez pas la permission d'exécuter cette commande.");

                            } else if (args[1].equalsIgnoreCase("remove")) {
                                if (main.getGameConfig().hosts.contains(player.getUniqueId())) {
                                    if (args.length > 2) {
                                        OfflinePlayer op = null;
                                        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers())
                                            if (offlinePlayer.getName().equals(args[2]))
                                                op = offlinePlayer;
                                        if (op == null) {
                                            player.sendMessage(main.getPrefix() + "§cLe joueur §4\"§e" + args[2] + "§4\"§c n'existe pas.");
                                            return true;
                                        }
                                        if (main.getGameConfig().hosts.contains(op.getUniqueId())) {
                                            main.getGameConfig().hosts.remove(player.getUniqueId());
                                            if (main.permissions.get(player.getName()) != null) {
                                                player.removeAttachment(main.permissions.get(player.getName()));
                                                main.permissions.remove(player.getName());
                                            }
                                            Bukkit.broadcastMessage(main.getPrefix() + "§c§l" + op.getName() + " §6n'est plus §lHost §6de la partie !");
                                        } else player.sendMessage(main.getPrefix() + "§cCe joueur n'est pas §6§lHost §cde la partie !");
                                    } else player.sendMessage(main.getPrefix() + helphostmessage);
                                } else
                                    player.sendMessage(main.getPrefix() + "§cVous n'avez pas la permission d'exécuter cette commande.");

                            } else if (args[1].equalsIgnoreCase("list")) {
                                StringBuilder swl = new StringBuilder();

                                for (PlayerUHC pu : main.players)
                                    if (pu.isHost())
                                        swl.append("\n").append(" §e- §l").append(pu.getPlayer().getName());
                                player.sendMessage(main.getPrefix() + "§6Liste des hosts de la partie :" + swl.toString());
                            }
                        } else player.sendMessage(main.getPrefix() + helphostmessage);
                        break;
                    case "spec":
                        final String helpspecmessage = "§6La commande spec gérer les spectateurs de la partie.\nArgument possibles : \n" +
                                "§eon §6: Devient spectateur\n§eoff §6: Retire le mode spectateur\n" +
                                "§elist §6: Affiche la liste des spectateurs.";
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("on")) {
                                if (!main.spectators.contains(player)) {
                                    if (main.isState(Gstate.WAITING)) {
                                        main.spectators.add(player);
                                        player.setGameMode(GameMode.SPECTATOR);
                                        player.setDisplayName("§8[§7Spectateur§8] §7" + player.getName());
                                        player.setPlayerListName(player.getDisplayName());
                                        InventoryManager.clearInventory(player);
                                        playerUHC.heal();

                                        player.sendMessage(main.getPrefix() + "§6Votre mode de jeu à été établi en spectateur.");
                                        player.sendMessage(main.getPrefix() + "§7Pour revenir au mode non-spectateur, utilisez la commande §6§l/uhc spec off§7.");
                                    } else player.sendMessage(main.getPrefix() + "§cCette commande est inutilisable une fois la partie commencée.");
                                } else player.sendMessage(main.getPrefix() + "§cVous êtes déjà en spectateur !");

                            } else if (args[1].equalsIgnoreCase("off")) {
                                if (main.spectators.contains(player)) {
                                    if (main.isState(Gstate.WAITING)) {
                                        main.spectators.remove(player);
                                        player.setGameMode(GameMode.ADVENTURE);
                                        player.setDisplayName(player.getName());
                                        player.setPlayerListName(player.getDisplayName());
                                        InventoryManager.giveWaitInventory(playerUHC);

                                        player.sendMessage(main.getPrefix() + "§6Vous n'êtes plus un spectateur.");
                                    } else player.sendMessage(main.getPrefix() + "§cCette commande est inutilisable une fois la partie commencée.");
                                } else player.sendMessage(main.getPrefix() + "§cVous n'êtes pas en spectateur !");

                            } else if (args[1].equalsIgnoreCase("list")) {
                                StringBuilder swl = new StringBuilder();

                                for (Player p : main.spectators)
                                    swl.append("\n").append(" §e- §l").append(p.getName());
                                player.sendMessage(main.getPrefix() + "§6Liste des spectateurs de la partie :" + swl.toString());
                            }
                        } else player.sendMessage(main.getPrefix() + helpspecmessage);
                        break;

                    case "help":
                        player.sendMessage(main.getPrefix() + helpmessage);

                    case "sm":
                        if (!Scenarios.SLAVE_MARKET.isActivated() && !main.isState(Gstate.PLAYING)) return true;
                        if (args.length > 1) {
                            if (args[1].equals("candidate")) {
                                if (!SlaveMarket.candidates.contains(playerUHC)) {
                                    if (!SlaveMarket.owners.contains(playerUHC)) {
                                        player.sendMessage(main.getPrefix() + "§aVous avez bien émis une candidature pour devenir acheteur.");
                                        Index.playPositiveSound(player);
                                        Bukkit.getPluginManager().callEvent(new SlaveMarketCandidateEvent(playerUHC, main));
                                    } else {
                                        player.sendMessage(main.getPrefix() + "§cVous êtes déjà un acheteur.");
                                        Index.playNegativeSound(player);
                                    }
                                } else {
                                    player.sendMessage(main.getPrefix() + "§cVous êtes déjà candidat pour être acheteur.");
                                }
                            } else if (args[1].equals("view")) {
                                if (playerUHC.isHost()) player.openInventory(SlaveMarket.getCandidInv());
                                else player.sendMessage(main.getPrefix() + "§cVous n'avez pas la permissions d'exécuter cette commande");
                            }
                        }
                        break;
                    case "am":
                        if (!Scenarios.ANONYMOUS.isActivated()) return true;
                        if (playerUHC.isHost() && args.length > 1) {
                            Anonymous.usedName = args[1];
                            player.sendMessage(main.getPrefix() + "§6Vous avez bien mit l'identité du Scénario Anonymous à §b§l" + args[1] + "§6.");
                            Index.playPositiveSound(player);
                        } else player.sendMessage(main.getPrefix() + "§cUtilisation : §e/uhc am §a<pseudo>§c.");
                        break;
                    case "ti":
                        if (!Scenarios.TEAM_INVENTORY.isActivated()) return true;
                        if (playerUHC.isAlive() && !playerUHC.isSpec())
                            player.openInventory(TeamInventory.inventories.get(playerUHC.getTeam()));
                    case "chat":
                        final String helpchatmessage = "§6La commande chat gérer le chat de la partie.\nArgument possibles : " +
                                "§eon §6: Actve le chat.\n" +
                                "§eoff §6: Désactive le chat.";
                        if (args.length > 1) {
                            if (args[1].equals("on")) {
                                PlayerListener.canChat = true;
                                Bukkit.broadcastMessage(main.getPrefix() + player.getDisplayName() + " §aa réactivé le Chat !");
                                for (Player p : Bukkit.getOnlinePlayers())
                                    Index.playPositiveSound(p);
                            } else if (args[1].equals("off")) {
                                PlayerListener.canChat = false;
                                Bukkit.broadcastMessage(main.getPrefix() + player.getDisplayName() + " §ca désactivé le Chat !");
                                for (Player p : Bukkit.getOnlinePlayers())
                                    Index.playPositiveSound(p);
                            } else player.sendMessage(main.getPrefix() + helpchatmessage);
                        } else player.sendMessage(main.getPrefix() + helpchatmessage);
                        break;
                    default:
                        player.sendMessage(main.getPrefix() + helpmessage);
                        break;

                }

            } else player.sendMessage(main.getPrefix() + helpmessage);
        } else {
            sender.sendMessage(main.getPrefix() + "§cVous devez être un joueur pour effectuer cette commande.");
            return true;
        }

        return true;
    }
}
