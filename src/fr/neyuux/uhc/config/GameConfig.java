package fr.neyuux.uhc.config;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.ItemsStack;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GameConfig implements Listener {

    private final Index main;
    private final Index.Modes mode;
    private final Set<Class<? extends Enum>> enumSet = new HashSet<>();
    private String teamType = "FFA";
    private double borderSize = 2000.0;
    private double finalborderSize = 50.0;
    private double borderSpeed = 1.0;
    public final ArrayList<UUID> hosts = new ArrayList<>();

    public GameConfig(Index main, Index.Modes mode) {
        this.main = main;
        this.mode = mode;
        enumSet.add(GameConfig.Timers.class);
        enumSet.add(GameConfig.Drops.class);
        enumSet.add(GameConfig.DeathDrop.class);
        enumSet.add(GameConfig.CustomCrafts.class);
        enumSet.add(GameConfig.WorldRules.class);
        enumSet.add(GameConfig.MineRules.class);
        enumSet.add(GameConfig.PlayerRules.class);
    }

    public String getTeamType() {
        return teamType;
    }

    public double getBorderSize() {
        return borderSize;
    }

    public double getFinalborderSize() {
        return finalborderSize;
    }

    public double getBorderSpeed() {
        return borderSpeed;
    }

    public void setTeamType(int teamType, boolean random) {
        if (teamType == 0) this.teamType = "FFA";
        else
            if (random)
                this.teamType = "Random To" + teamType;
            else
                this.teamType = "To" + teamType;
    }

    public void setBorderSize(double borderSize) {
        this.borderSize = borderSize;
    }

    public void setFinalborderSize(double finalborderSize) {
        this.finalborderSize = finalborderSize;
    }

    public void setBorderSpeed(double borderSpeed) {
        this.borderSpeed = borderSpeed;
    }



    @EventHandler
    public void onInteractComparator(PlayerInteractEvent ev) {
        if (ev.getItem() != null)
            if (ev.getItem().equals(getComparator()))
                ev.getPlayer().openInventory(getGameConfigInv(ev.getPlayer()));
    }

    @EventHandler
    public void onInvClickComparator(InventoryClickEvent ev) {
        if (ev.getCurrentItem() != null && ev.getCurrentItem().equals(getComparator()))
            ev.getWhoClicked().openInventory(getGameConfigInv(ev.getWhoClicked()));
    }


    @EventHandler
    public void onGameConfigInv(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        Inventory inv = ev.getInventory();
        ItemStack current = ev.getCurrentItem();

        if (current == null) return;

        if (inv.getName().equals(getGameConfigInv(player).getName())) {
            ev.setCancelled(true);

            switch (current.getType()) {
                case ITEM_FRAME:
                    Inventory invModes = Bukkit.createInventory(null, 27, "§2§lMode §2de jeu de la Partie");
                    setInvCoin(invModes, (short)13);
                    setReturnArrow(invModes);
                    invModes.setItem(11, new ItemsStack(Material.GOLDEN_APPLE, ChatColor.translateAlternateColorCodes('&', Index.Modes.UHC.getPrefix()), "§7Passe le mode de jeu en UHC.").toItemStack());
                    invModes.setItem(15, new ItemsStack(Material.MONSTER_EGG, ChatColor.translateAlternateColorCodes('&', Index.Modes.LG.getPrefix()), "§7Passe le mode de jeu en LG UHC").toItemStack());
                    player.openInventory(invModes);
                    break;
                case BOOK_AND_QUILL:
                    player.openInventory(getScenInv());
                    break;
                case APPLE:
                    player.openInventory(getConfigInv());
                    break;
                case BARRIER:
                    Inventory invReset = Bukkit.createInventory(null, InventoryType.HOPPER, "§bReset la §lMap");
                    invReset.setItem(1, new ItemsStack(Material.STAINED_CLAY, (short)5, "§a§lConfirmer le Reset de la Map", "§b>>Clique").toItemStack());
                    invReset.setItem(3, new ItemsStack(Material.STAINED_CLAY, (short)14, "§c§lAnnuler le Reset de la Map", "§b>>Clique").toItemStack());
                    player.openInventory(invReset);
                    break;
                case SKULL_ITEM:
                    player.openInventory(getJoueursInv());
                    break;
            }
        }
    }

    @EventHandler
    public void onModeInv(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        Inventory inv = ev.getInventory();
        ItemStack current = ev.getCurrentItem();

        if (current == null) return;

        if (inv.getName().equals("§2§lMode §2de jeu de la Partie")) {
            ev.setCancelled(true);
            System.out.println(mode + "  " + current.toString() + "  " + current.getType() + "  " + Index.Modes.UHC);
            if (current.getType().equals(Material.GOLDEN_APPLE) && !mode.equals(Index.Modes.UHC)) {
                main.changeMode(Index.Modes.UHC);
                Bukkit.broadcastMessage(main.getPrefix() + "§b" + player.getName() + " §ea passé le mode de jeu sur " + ChatColor.translateAlternateColorCodes('&', Index.Modes.UHC.getPrefix()));
            } else if (current.getType().equals(Material.MONSTER_EGG) && !mode.equals(Index.Modes.LG)) {
                main.changeMode(Index.Modes.LG);
                Bukkit.broadcastMessage(main.getPrefix() + "§b" + player.getName() + " §ea passé le mode de jeu sur " + ChatColor.translateAlternateColorCodes('&', Index.Modes.LG.getPrefix()));
            }

            if (current.equals(getReturnArrow()))
                player.openInventory(getGameConfigInv(player));
        }
    }


    @EventHandler
    public void onResetInv(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        Inventory inv = ev.getInventory();
        ItemStack current = ev.getCurrentItem();

        if (current == null) return;

        if (inv.getName().equals("§bReset la §lMap")) {
            ev.setCancelled(true);
            if (current.getType().equals(Material.STAINED_CLAY))
                if (current.getDurability() == 5) {
                    main.rel();
                    Bukkit.broadcastMessage(main.getPrefix() + "§b" + player.getName() + " §ea reset la map !");
                } else if (current.getDurability() == 14)
                    player.openInventory(getGameConfigInv(player));
        }
    }


    @EventHandler
    public void onScenInv(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        Inventory inv = ev.getInventory();
        ItemStack current = ev.getCurrentItem();

        if (current == null) return;

        if (inv.getName().equals(getScenInv().getName())) {
            ev.setCancelled(true);
            if (current.getType().equals(Material.BOOK_AND_QUILL)) {
                player.openInventory(getScenPageInv(1));
            } else if (current.equals(getReturnArrow()))
                player.openInventory(getGameConfigInv(player));

        } else if (inv.getName().equals(getScenPageInv(1).getName())) {
            ev.setCancelled(true);
            if (current.equals(getNextPaper()))
                player.openInventory(getScenPageInv(2));
            else if (current.equals(getPreviousPaper()))
                player.openInventory(getScenPageInv(1));
            else if (current.equals(getReturnArrow()))
                player.openInventory(getScenInv());

            if (!current.equals(getNextPaper()) && !current.equals(getPreviousPaper()) && !current.equals(getReturnArrow()) && current.hasItemMeta() && current.getItemMeta().hasDisplayName()) {
                Scenarios sc = Scenarios.getByDisplayName(current.getItemMeta().getDisplayName());
                if (!sc.isActivated()) {
                    try {
                        Class<?> c = sc.getScenarioClass();
                        c.getMethod("activateScenario").invoke(c.newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Bukkit.broadcastMessage(main.getPrefix() + "§4[§cErreur§4] §cErreur lors de l'activation du Scénario " + sc.getDisplayName());
                    }
                    ItemMeta itm = current.getItemMeta();
                    itm.setLore(getScenInvLore(sc));
                    current.setItemMeta(itm);
                }
                else {
                    try {
                        Class<?> c = sc.getScenarioClass();
                        c.getMethod("desactivateScenario").invoke(c.newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Bukkit.broadcastMessage(main.getPrefix() + "§4[§cErreur§4] §cErreur lors de la désactivation du Scénario " + sc.getDisplayName());
                    }
                    ItemMeta itm = current.getItemMeta();
                    itm.setLore(getScenInvLore(sc));
                    current.setItemMeta(itm);
                }
            }
        }
    }



    private Inventory getGameConfigInv(HumanEntity player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§c§lConfiguration");
        List<String> ops = new ArrayList<>();
        setInvCoin(inv, (byte)14);

        for (PlayerUHC pu : main.players)
            if (pu.isHost())
                if (pu.getPlayer().isOnline()) ops.add(Bukkit.getPlayer(pu.getPlayer().getName()).getDisplayName());
                else
                    ops.add(Bukkit.getScoreboardManager().getMainScoreboard().getTeam("hostJoueur").getPrefix() + pu.getPlayer().getPlayer().getDisplayName());
        inv.setItem(38, new ItemsStack(Material.SIGN, "§cListe des §lConfigurateurs", ops.toArray(new String[0])).toItemStack());

        inv.setItem(30, new ItemsStack(Material.SKULL_ITEM, (short)3, "§6Joueurs", "§fPermet de gérer", "§fles joueurs", "§f§o(spectateur, etc)").toItemStackwithSkullMeta(player.getName()));

        inv.setItem(32, new ItemsStack(Material.BARRIER, "§bReset la Map", "§fPermet de reset", "§fla map.").toItemStack());

        inv.setItem(13, new ItemsStack(Material.APPLE, "§f§lParamètres de la Partie", "§fPermet de changer les", "§foptions de la partie.").toItemStack());

        inv.setItem(15, new ItemsStack(Material.BOOK_AND_QUILL, "§6§lScénarios", "§fPermet de gérer les", "§fscénarios de la partie.").toItemStack());

        inv.setItem(11, new ItemsStack(Material.ITEM_FRAME, "§2Changer le §lMode §2de jeu", "§fPermet de changer le", "§fmode de jeu de la partie.", "", "§eActuel : §c§l" + ChatColor.translateAlternateColorCodes('&', mode.getPrefix())).toItemStack());

        return inv;
    }

    private Inventory getScenInv() {
        Inventory inv = Bukkit.createInventory(null, 27, "§c§lConfigAffichage §6§lScénario");
        setInvCoin(inv, (byte)4);
        setReturnArrow(inv);

        List<String> activated = new ArrayList<>();
        for (Scenarios sc : Scenarios.getActivatedScenarios()) activated.add("§a§l - §6" + sc.getName());
        ItemsStack it = new ItemsStack(Material.WOOL, (short)6, "§2Liste des Scénarios activés : ", activated.toArray(new String[0]));
        it.addGlowEffect();
        if (activated.isEmpty())
            it.setLore("§cAucun Scénario n'a été encore activé");
        inv.setItem(11, it.toItemStack());

        inv.setItem(13, new ItemsStack(Material.BOOK_AND_QUILL, "§6Modifier les Scénarios", "§7Ouvre le menu des scénarios", "§b>>Clique").toItemStack());

        inv.setItem(15, new ItemsStack(Material.WOOD_PICKAXE, "§eModes de jeux", "§7Ouvre le menu des modes de jeux", "§b>>Clique").toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES));
        if (mode.equals(Index.Modes.LG)) inv.setItem(15, new ItemsStack(Material.MONSTER_EGG, "§9Configurer le LG UHC", "§7Ouvre le menu de configuration du LG UHC", "§b>>Clique").toItemStack());

        return inv;
    }

    private Inventory getScenPageInv(int page) {
        Inventory inv = Bukkit.createInventory(null, 54, "§c§lConfiguration §6§lScénarios");
        setInvCoin(inv, (short)1);
        setReturnArrow(inv);
        if (page != 1)
            setPreviousPaper(inv);
        if (page != 2)
            setNextPaper(inv);

        int ord = 28 * (page - 1);
        for (int i = 10; i <= 43; i++) {
            Scenarios sc = Scenarios.values()[ord];
            if (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35)
                while (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35) i++;
            if (sc.isModeScenario())
                while (sc.isModeScenario()) {
                    ord++;
                    if (ord == Scenarios.values().length) return inv;
                    sc = Scenarios.values()[ord];
                }
            try {
                Class<?> c = sc.getScenarioClass();
                ItemsStack it = new ItemsStack((ItemStack) c.getMethod("getMenuItem").invoke(c.newInstance()));
                it.setLore(getScenInvLore(sc));

                inv.setItem(i, it.toItemStack());
                ord++;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                e.printStackTrace();
                Bukkit.broadcastMessage(main.getPrefix() + "§4[§cErreur§4] §cUne erreur s'est produite lors du chargement du menu des scénarios, veuillez en informer Neyuux_ !");
                return inv;
            }
        }
        return inv;
    }

    private Inventory getConfigInv() {
        Inventory inv = Bukkit.createInventory(null, 6 * 9, "§c§lConfigAffichage §f§lGamePara");
        setInvCoin(inv, (byte)0);
        setReturnArrow(inv);

        if (!mode.equals(Index.Modes.LG)) inv.setItem(39, new ItemsStack(Material.BANNER, (short)15, "§eConfigurer les §lÉquipes", "§bValeur actuelle : ", "§b§l - §eTaille des équipes §b: §e§l" + getTeamType(), "", "§7Ouvre le menu de changement", "§7de la taille des équipes.", "§b>>Clique").toItemStack());

        inv.setItem(11, new ItemsStack(Timers.BORDER.getItemType(), "§3Gérer les options de §lBordure", "§bValeurs actuelles : ", "§b§l - §3Taille initiale §b: §3§l" + borderSize, "§b§l - §bTaille finale : §b§l" + finalborderSize, "§b§l - §9Temps d'activation §b: §9§l" + main.getTimer(Timers.BORDER.getSeconds()), "§b§l - §1Vitesse de bordure §b: §1§l" + borderSpeed, "", "§7Ouvre le menu de changement", "§7de configuration de la bordure", "§b>>Clique").toItemStack());

        inv.setItem(13, new ItemsStack(Timers.PVP.getItemType(), "§CTemps d'activation du §lPvP", "§bValeur actuelle : ", "§b§l - §eTemps d'activation §b: §e§l" + main.getTimer(Timers.PVP.getSeconds()), "", "§7Ouvre le menu de changement", "§7du temps d'activation du PvP", "§b>>Clique").toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES));

        inv.setItem(15, new ItemsStack(Timers.EPISOD.getItemType(), "§6Gérer les options des §lÉpisodes", "§bValeurs actuelles : ", "§b§l - §6§lÉpisodes §b: " + getOFF(), "§b§l - §eDurée des Épisodes §b: §e§l" + main.getTimer(Timers.EPISOD.getSeconds()), "", "§7Ouvre le menu de changement", "§7de configuration des Épisodes", "§b>>Clique").toItemStack());

        List<String> playerRules = new ArrayList<>();
        playerRules.add("§bValeurs actuelles : ");
        for (PlayerRules pr : PlayerRules.values())
            playerRules.add("§b§l - " + pr.getName() + " §b: " + getStringBoolean(pr.getValue()));
        playerRules.add("");
        playerRules.add("§7Ouvre le menu de configuration");
        playerRules.add("§7des options des Joueurs");
        playerRules.add("§b>>Clique");
        inv.setItem(21, new ItemsStack(Material.SKULL_ITEM, "§fGérer les options des §lJoueurs", playerRules.toArray(new String[0])).toItemStack());

        List<String> worldRules = new ArrayList<>();
        worldRules.add("§bValeurs actuelles : ");
        for (WorldRules wr : WorldRules.values())
            worldRules.add("§b§l - " + wr.getName() + " §b: " + getStringBoolean(wr.getValue()));
        worldRules.add("");
        worldRules.add("§7Ouvre le menu de configuration");
        worldRules.add("§7des options du Monde");
        worldRules.add("§b>>Clique");
        inv.setItem(23, new ItemsStack(Material.GRASS, "§2Gérer les options du §lMonde", worldRules.toArray(new String[0])).toItemStack());

        List<String> mineRules = new ArrayList<>();
        mineRules.add("§bValeurs actuelles : ");
        for (MineRules mr : MineRules.values())
            mineRules.add("§b§l - " + mr.getName() + " §b: " + getStringBoolean(mr.getValue()));
        mineRules.add("");
        mineRules.add("§7Ouvre le menu de configuration");
        mineRules.add("§7des options des Mines");
        mineRules.add("§b>>Clique");
        inv.setItem(29, new ItemsStack(Material.STONE_PICKAXE, "§aGérer les options des §lMines", mineRules.toArray(new String[0])).toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES));

        List<String> drops = new ArrayList<>();
        drops.add("§bValeurs actuelles : ");
        for (Drops d : Drops.values())
            drops.add("§b§l - " + d.getName() + " §b: " + d.getName().substring(0, 2) + "§l" + d.getChance() + "%");
        drops.add("");
        drops.add("§7Ouvre le menu de configuration");
        drops.add("§7des options des Drops");
        drops.add("§b>>Clique");
        inv.setItem(31, new ItemsStack(Material.APPLE, "§7Gérer les options des §lDrops", drops.toArray(new String[0])).toItemStack());

        inv.setItem(33, new ItemsStack(Material.CHEST, "§dGérer l'inventaire de §lDépart", "§bValeurs actuelles : ", "§b§l - §fTaille de l'inventaire de départ §b: §f§l" + main.getInventoryManager().getStartInventorySize(), "", "§7Ouvre le menu de configuration", "§7de l'inventaire de départ", "§b>>Clique").toItemStack());

        List<String> deathOption = new ArrayList<>();
        deathOption.add("§bValeurs actuelles : ");
        for (DeathDrop d : DeathDrop.values())
            deathOption.add("§b§l - " + d.getName() + " §b: " + getStringBoolean(d.getValue()));
        deathOption.add("§b§l - §fTaille de l'inventaire de mort §b: §f§l" + main.getInventoryManager().getDeathInventorySize());
        deathOption.add("");
        deathOption.add("§7Ouvre le menu de configuration");
        deathOption.add("§7des options de l'inventaire de mort");
        deathOption.add("§b>>Clique");
        inv.setItem(41, new ItemsStack(Material.ENDER_CHEST, "§5Gérer l'inventaire de §lMort", deathOption.toArray(new String[0])).toItemStack());

        List<String> customCraft = new ArrayList<>();
        customCraft.add("§bValeurs actuelles : ");
        for (CustomCrafts cc : CustomCrafts.values())
            customCraft.add("§b§l - " + cc.getName() + " §b: " + getStringBoolean(cc.isActivated()));
        customCraft.add("");
        customCraft.add("§7Ouvre le menu de configuration");
        customCraft.add("§7des options des crafts customizés");
        customCraft.add("§b>>Clique");
        inv.setItem(49, new ItemsStack(Material.WORKBENCH, "§bGérer les §lcrafts customizés", customCraft.toArray(new String[0])).toItemStack());

        return inv;
    }

    public Inventory getJoueursInv() {
        Inventory inv = Bukkit.createInventory(null, main.adaptInvSizeForInt(main.players.size(), 1), "§c§lConfiguration §6§lJoueurs");
        setReturnArrow(inv);

        for (PlayerUHC pu : main.players)
            if (pu.getPlayer().isOnline()) {
                String team = "";
                if (pu.getTeam() == null)
                    team = "§cAucune";
                else
                    team = pu.getTeam().getPrefix().toString();
                ItemsStack it = new ItemsStack(Material.SKULL_ITEM, (short) 3, pu.getPlayer().getPlayer().getDisplayName(), "§bValeurs actuelles : ", "§b§l - §6Host §b: " + getYesOrNoStringBoolean(pu.isHost()), "§b§l - §7Spectateur §b: " + getYesOrNoStringBoolean(main.spectators.contains(pu.getPlayer().getPlayer())), "§b§l - §eÉquipe §b: §e§l" + team, "", "§7Ouvre le menu de changement", "§7des informatons de " + pu.getPlayer().getName(), "§b>>Clique");
                if (mode.equals(Index.Modes.LG)) {
                    List<String> lore = it.getLore();
                    lore.remove(3);
                    it.setLore(lore);
                }
                inv.addItem(it.toItemStackwithSkullMeta(pu.getPlayer().getName()));
            }
        return inv;
    }



    public ItemStack getComparator() {
        return new ItemsStack(Material.REDSTONE_COMPARATOR, "§c§lConfiguration de la partie", "§7Permet de configurer la partie", "§b>>Clique droit").toItemStack();
    }

    public ItemStack getReturnArrow() {
        return new ItemsStack(Material.ARROW, "§cRetour", "§7Retourner au menu précédent").toItemStack();
    }

    public void setReturnArrow(Inventory inv) {
        inv.setItem(inv.getSize() - 1, getReturnArrow());
    }

    public ItemStack getNextPaper() {
        return new ItemsStack(Material.PAPER, "§ePage Suivante", "§7Ouvrir la page suivante").toItemStack();
    }

    public void setNextPaper(Inventory inv) {
        inv.setItem(inv.getSize() - 10, getNextPaper());
    }

    public ItemStack getPreviousPaper() {
        return new ItemsStack(Material.PAPER, "§ePage Précédante", "§7Ouvrir la page précédante").toItemStack();
    }

    public void setPreviousPaper(Inventory inv) {
        inv.setItem(inv.getSize() - 9, getPreviousPaper());
    }


    private List<String> getScenInvLore(Scenarios sc) {
        List<String> lore = new ArrayList<>();
        try {
            Class<?> c = sc.getScenarioClass();
            ItemsStack scit = new ItemsStack((ItemStack) c.getMethod("getMenuItem").invoke(c.newInstance()));
            lore.addAll(scit.getLore());
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(main.getPrefix() + "§4[§cErreur§4] §cErreur lors de l'activation du Scénario " + sc.getDisplayName());
        }

        lore.add(" ");
        lore.add("§bValeur actuelle : "+(sc.isActivated() ? getON() : getOFF()));
        lore.add(" ");
        if(sc.isActivated()) {
            if(sc.isEditable()) {
                lore.add("§b>>Clic gauche : §eModifier le Scénario");
                lore.add("§b>>Clic droit : §cDésactiver le Scénario");
            } else {
                lore.add("§b>>Clic : §cDésactiver le Scénario");
            }
        } else {
            lore.add("§b>>Clic : §aActiver le Scénario");
        }

        return lore;
    }


    private void setInvCoin(Inventory inv, short color) {
        ItemStack verre = new ItemStack(Material.STAINED_GLASS_PANE, 1, color);
        int slot1 = 0;
        int slot2 = 8;
        int slot3 = inv.getSize() - 9;
        int slot4 = inv.getSize() - 1;
        inv.setItem((slot1), verre);
        inv.setItem((slot1 + 9), verre);
        inv.setItem((slot1 + 1), verre);
        inv.setItem((slot2), verre);
        inv.setItem((slot2 + 9), verre);
        inv.setItem((slot2 - 1), verre);
        inv.setItem((slot3), verre);
        inv.setItem((slot3 - 9), verre);
        inv.setItem((slot3 + 1), verre);
        inv.setItem((slot4), verre);
        inv.setItem((slot4 - 9), verre);
        inv.setItem((slot4 - 1), verre);
    }



    public enum DeathDrop {

        HEAD("§cDrop de la Tête", Material.SKULL_ITEM, (short) 3, false),
        GOLDEN_HEAD("§6Drop d'une Golden Head", Material.GOLDEN_APPLE, (short) 0, false),
        LIGHTNING("§fÉclair", Material.BLAZE_ROD, (short) 0, true),
        BARRIER_HEAD("§4Poteau avec la tête", Material.DARK_OAK_FENCE, (short) 0, true);

        private final String name;
        private final Material type;
        private final short data;
        private boolean isActivated;

        DeathDrop(String name, Material type, short data, boolean isActivated){
            this.name = name;
            this.type = type;
            this.data = data;
            this.isActivated = isActivated;
        }

        public String getName(){
            return this.name;
        }

        public static String getEnumDisplayName() {
            return "§4Drop à la mort";
        }

        public Material getType(){
            return this.type;
        }

        public short getData(){
            return this.data;
        }

        public boolean getValue(){
            return this.isActivated;
        }

        public void setValue(boolean valeur){
            this.isActivated = valeur;
        }

    }

    public enum MineRules {

        ROLLERCOASTER("§6RollerCoaster", Material.COBBLESTONE_STAIRS, (short) 1, "§7Minage en escalier de la couche 32 à 6", true),
        STRIMING("§7Stripmining", Material.BEDROCK, (short) 0, "§7Minage tout droit couche 11", true),
        POKEHOLLING("§bPokeholling", Material.RAILS, (short) 0,  "§7Minage optimisé", true),
        SON_MINAGE("§aSoundMining/EntityMining", Material.NOTE_BLOCK, (short) 0, "§7Se servir du son pour miner", true),
        DIGDOWN("§cDigDown", Material.IRON_SPADE, (short) 0, "§7S'enterrer sous terre pour se cacher d'un fight", false),
        STALK("§4Stalk", Material.IRON_BOOTS, (short) 0, "§7Suivre un joueur dans les grottes", false),
        TRAPS("§eTrap", Material.TRAP_DOOR, (short) 0, "§7Faire des pièges pour tuer", false);

        private final String name;
        private final Material type;
        private final short data;
        private final String lore;
        private boolean isActivated;

        MineRules(String name, Material type, short data, String lore, boolean isActivated){
            this.name = name;
            this.type = type;
            this.data = data;
            this.lore = lore;
            this.isActivated = isActivated;
        }

        public String getName(){
            return this.name;
        }

        public static String getEnumDisplayName() {
            return "§6Règles de Minage";
        }

        public Material getType(){
            return this.type;
        }

        public short getData(){
            return this.data;
        }

        public String getLore(){
            return this.lore;
        }

        public boolean getValue(){
            return this.isActivated;
        }

        public void setValue(boolean value){
            this.isActivated = value;
        }

    }

    public enum PlayerRules {

        CROSS_TEAM("§cCrossTeam", Material.IRON_HOE, "§7Alliance entre ennemis", false),
        FRIENDLYFIRE("§aFriendlyFire", Material.WOOD_SWORD, "§7Se taper entre coéquipiers", false),
        I_PVP("§6iPvP", Material.ANVIL, "§6Infliger des dégâts avant le PvP", false),
        COORDS_F3("§eF3 Coords", Material.PAPER, "§7Activer les coordonnées F3", true),
        SPECTATEURS("§7Specs", Material.GLASS,  "§7Mode spectateur", true),
        SCOREBOARD_LIFE("§dVie dans le Tab", Material.APPLE, "§7Pourcentage de vie dans le tab", true);

        private final String name;
        private final Material type;
        private final String lore;
        private boolean isActivated;

        PlayerRules(String name, Material type, String lore, boolean isActivated){
            this.name = name;
            this.type = type;
            this.lore = lore;
            this.isActivated = isActivated;
        }

        public String getName(){
            return this.name;
        }

        public static String getEnumDisplayName() {
            return "§6Règles des Joueurs";
        }

        public Material getType(){
            return this.type;
        }

        public String getLore(){
            return this.lore;
        }

        public boolean getValue(){
            return this.isActivated;
        }

        public void setValue(boolean b){
            this.isActivated = b;
        }
    }

    public enum Timers {

        INVINCIBILITY("§eInvincibilité", true, 30, Material.DIAMOND_CHESTPLATE),
        PVP("§cPvP", true, 20 * 60, Material.IRON_SWORD),
        BORDER("§9Bordure", true, 60 * 60, Material.BARRIER),
        EPISOD("§6Épisode", false, 20 * 60, Material.COMPASS);

        Timers(String name, boolean isActivated, int seconds, Material type) {
            this.name = name;
            this.isActivated = isActivated;
            this.seconds = seconds;
            this.type = type;
        }

        private final String name;
        private boolean isActivated;
        private int seconds;
        private final Material type;


        public String getName() {
            return name;
        }

        public static String getEnumDisplayName() {
            return "§eTimers";
        }

        public boolean isActivated() {
            return isActivated;
        }

        public void setActivated(boolean isActivated) {
            this.isActivated = isActivated;
        }

        public int getSeconds() {
            return seconds;
        }

        public Material getItemType() {
            return type;
        }


        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }
    }

    public enum CustomCrafts {

        GOLDEN_HEAD("§eGolden Head", true, Material.GOLDEN_APPLE),
        DOUBLE_ARROW("§cDouble Arrow", false,Material.ARROW),
        STRING("§fString", true, Material.STRING),
        SADDLE("§6Selle", false, Material.SADDLE);

        CustomCrafts(String name, boolean isActivated, Material type) {
            this.name = name;
            this.isActivated = isActivated;
            this.type = type;
        }

        private final String name;
        private boolean isActivated;
        private final Material type;


        public String getName() {
            return name;
        }

        public static String getEnumDisplayName() {
            return "§2Crafts customisés";
        }

        public boolean isActivated() {
            return isActivated;
        }

        public void setActivated(boolean isActivated) {
            this.isActivated = isActivated;
        }

        public Material getItemType() {
            return type;
        }

    }

    public enum WorldRules {

        HORSE("§6Chevaux", Material.SADDLE, false),
        DAY_CYCLE("§9Cycle Jour/Nuit", Material.WATCH, false),
        TOWER("§7Towers", Material.DIRT, false),
        MILK("§fSeaux de lait", Material.MILK_BUCKET, true),
        LAVA("§6Seaux de lave", Material.LAVA_BUCKET, true),
        FLINT_AND_STEEL("§7Briquets", Material.FLINT_AND_STEEL, true),
        LAVA$NOT_OVERWORLD("§6Seaux de lave §4hors Overworld", Material.NETHERRACK, false),
        FLINT_AND_STEEL$NOT_OVERWORLD("§7Briquets §4hors Overworld", Material.NETHER_BRICK_STAIRS, false),
        BED$NOT_OVERWORLD("§cLits §4hors Overworld", Material.BED, false),
        XP_QUARTZ_NERF("§aNerf de l'XP du Quartz", Material.QUARTZ_ORE, false);

        private final String name;
        private final Material material;
        private boolean value;


        WorldRules(String name, Material material, boolean value){
            this.name = name;
            this.material = material;
            this.value = value;
        }

        public String getName(){
            return name;
        }

        public static String getEnumDisplayName() {
            return "§2Règles de Mondes";
        }

        public Material getMaterial(){
            return material;
        }

        public void setValue(boolean value){
            this.value = value;
        }

        public boolean getValue(){
            return value;
        }
    }

    public enum Drops {

        APPLE("§cPommes", Material.APPLE, 1, 4, 1),
        FLINT("§8Silex", Material.FLINT, 10, 100, 10),
        FEATHER("§fPlumes", Material.FEATHER, 40, 100, 5),
        LEATHER("§6Cuirs", Material.LEATHER, 40, 100, 5);

        private final String name;
        private final Material type;
        private int chance;
        private final int max;
        private final int difference;

        Drops(String name, Material type, int chance, int max, int difference){
            this.name = name;
            this.type = type;
            this.chance = chance;
            this.max = max;
            this.difference = difference;
        }

        public String getName(){
            return this.name;
        }

        public static String getEnumDisplayName() {
            return "§7Drops";
        }

        public Material getType(){
            return this.type;
        }

        public Integer getChance(){
            return this.chance;
        }

        public void setChance(int valeur){
            this.chance = valeur;
        }

        public Integer getMax(){
            return this.max;
        }

        public Integer getEcart(){
            return this.difference;
        }
    }



    public static String getON() {
        return "§aActivé";
    }

    public static String getOFF() {
        return "§cDésactivé";
    }

    public static String getYes() {
        return "§aOui";
    }

    public static String getNo() {
        return "§cNon";
    }

    public static String getStringBoolean(boolean b) {
        if (b)
            return getON();
        else return getOFF();
    }

    public static String getYesOrNoStringBoolean(boolean b) {
        if (b)
            return getYes();
        else return getNo();
    }
}
