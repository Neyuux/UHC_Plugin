package fr.neyuux.uhc.config;

import fr.neyuux.uhc.*;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.ArmorLimiter;
import fr.neyuux.uhc.scenario.classes.modes.Moles;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.teams.UHCTeamManager;
import fr.neyuux.uhc.util.ItemsStack;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fr.neyuux.uhc.config.GameConfig.ConfigurableParams.*;

public class GameConfig implements Listener {

    private final Index main;
    private final Index.Modes mode;
    public final ArrayList<UUID> hosts = new ArrayList<>();
    public Player starterModifier;
    public Player deathInvModifier;

    public GameConfig(Index main, Index.Modes mode) {
        this.main = main;
        this.mode = mode;
        if (!UHCWorld.isCreated()) {
            main.world.create();
            Bukkit.broadcastMessage(main.getPrefix() + "§2Monde §a\"" + main.world.getSeed() + "\"§2 créé.");
        }
        main.players.forEach(playerUHC -> main.setLobbyScoreboard(playerUHC.getPlayer().getPlayer()));
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
                    player.closeInventory();
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
            } else if (current.getType().equals(Material.WOOD_PICKAXE)) {
                player.openInventory(getModesInv());
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
                    if (Scenarios.haveModeScenarios() && sc.isModeScenario()) {
                        player.sendMessage(main.getPrefix() + "§cImpossible de rajouter le mode de jeu §6\"" + sc.getDisplayName() + "§6\" §calors qu'il y a déjà un mode de jeu activé !");
                    } else {
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
                }
                else {
                    if (ev.isRightClick() || !sc.isEditable()) {
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
                    if (ev.isLeftClick() && sc.isEditable()) {
                        player.openInventory(sc.getConfigInv());
                    }
                }
            }

        } else if (inv.getName().startsWith("§cConfig ")) {
            ev.setCancelled(true);
            Scenarios sc = Scenarios.getByDisplayName(inv.getName().substring(9));
            if (!current.getType().equals(Material.AIR) && !current.equals(getReturnArrow())) {
                Field value = Scenario.getCache().get(current.getItemMeta().getDisplayName());
                if (value.getType().equals(boolean.class)) {
                    Boolean b = null;
                    try {
                        b = value.getBoolean(sc.getScenarioClass().newInstance());
                        b = !b;
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    Scenario.setCache(sc.getName(), current.getItemMeta().getDisplayName(), b);
                    player.openInventory(sc.getConfigInv());
                } else if (value.getType().equals(ArmorLimiter.ArmorTypes.class)) {
                    try {
                        ArmorLimiter.ArmorTypes at = (ArmorLimiter.ArmorTypes)value.get(sc.getScenarioClass().newInstance());
                        int ordinal = at.ordinal();
                        if (ev.getClick().isRightClick() && ordinal != 0)
                            ordinal--;
                        if (ev.getClick().isLeftClick() && ordinal != ArmorLimiter.ArmorTypes.values().length - 1)
                            ordinal++;
                        at = ArmorLimiter.ArmorTypes.values()[ordinal];
                        Scenario.setCache(sc.getName(), current.getItemMeta().getDisplayName(), at);
                        player.openInventory(sc.getConfigInv());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (value.getType().equals(int.class))
                        player.openInventory(getScenModifInv("Integer", sc, current));
                    else if (value.getType().equals(double.class))
                        player.openInventory(getScenModifInv("Double", sc, current));
                    else if (value.getType().equals(List.class))
                        player.openInventory(getScenModifInv("List", sc, current));
                }
            } else if (current.equals(getReturnArrow()))
                if (!sc.isModeScenario())
                    player.openInventory(getScenPageInv(1));
                else
                    player.openInventory(getModesInv());

        } else if (inv.getName().startsWith("§cModif  ")) {
            ev.setCancelled(true);
            Scenarios sc = Scenarios.getByDisplayName(inv.getName().substring(9));
            if (!current.getType().equals(Material.AIR) && !current.equals(getReturnArrow())) {
                if (current.getType().equals(Material.BANNER)) {
                    if (inv.getSize() == 9) {
                        try {
                            String si = current.getItemMeta().getDisplayName();
                            si = si.replace("§a", "").replace("§c", "").replace("§l", "").replace(" ", "");
                            int i = Integer.parseInt(si);
                            if (inv.getItem(4).getItemMeta().getLore().get(0).contains("minutes")) i *= 60;
                            int value = Scenario.getCache().get(inv.getItem(4).getItemMeta().getDisplayName()).getInt(sc.getScenarioClass().newInstance()) + i;
                            if (value < 1) value = 1;
                            Scenario.setCache(sc.getName(), inv.getItem(4).getItemMeta().getDisplayName(), value);
                            ItemsStack it = new ItemsStack(Material.AIR);
                            for (ItemStack prit : sc.getConfigInv().getContents())
                                if (prit != null && !prit.getType().equals(Material.AIR))
                                    if (prit.getType().equals(inv.getItem(4).getType()) && prit.getDurability() == inv.getItem(4).getDurability())
                                        it = new ItemsStack(prit);
                            inv.setItem(4, it.toItemStack());
                        } catch (Exception e) {
                            Bukkit.broadcastMessage(main.getPrefix() + "§4[§cErreur§4] §cErreur lors de la modification de la valeur du scenario");
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            String si = current.getItemMeta().getDisplayName();
                            si = si.replace("§a", "").replace("§c", "").replace("§l", "").replace(" ", "");
                            double i = Double.parseDouble(si);
                            double value = Scenario.getCache().get(inv.getItem(4).getItemMeta().getDisplayName()).getDouble(sc.getScenarioClass().newInstance()) + i;
                            value = Math.round(value * 10.0) / 10.0;
                            if (value < 0.1) value = 0.1;
                            Scenario.setCache(sc.getName(), inv.getItem(4).getItemMeta().getDisplayName(), value);
                            ItemsStack it = new ItemsStack(Material.AIR);
                            for (ItemStack prit : sc.getConfigInv().getContents())
                                if (prit != null && !prit.getType().equals(Material.AIR))
                                    if (prit.getType().equals(inv.getItem(4).getType()) && prit.getDurability() == inv.getItem(4).getDurability())
                                        it = new ItemsStack(prit);
                            inv.setItem(4, it.toItemStack());
                        } catch (Exception e) {
                            Bukkit.broadcastMessage(main.getPrefix() + "§4[§cErreur§4] §cErreur lors de la modification de la valeur du scenario");
                            e.printStackTrace();
                        }
                    }
                } else {
                    Moles.Kits k = Moles.Kits.getByName(current.getItemMeta().getDisplayName());
                    if (k != null) {
                        k.setActivated(!k.isActivated());
                        ItemMeta itm = current.getItemMeta();
                        List<String> lore = new ArrayList<>();
                        for (ItemStack it : k.getItems())
                            lore.add("§8x§7" + it.getAmount() + " " + it.getType().name().toLowerCase());
                        lore.add("");
                        lore.add("§bValeur : " + getStringBoolean(k.isActivated()));
                        itm.setLore(lore);
                        current.setItemMeta(itm);
                    }
                }
            } else if (current.equals(getReturnArrow())) player.openInventory(sc.getConfigInv());
        }
    }

    @EventHandler
    public void modifyArmorLimiterValues(InventoryOpenEvent ev) {
        Inventory inv = ev.getInventory();

        if (inv.getName().equals("§cConfig " + Scenarios.ARMOR_LIMITER.getDisplayName()) || inv.getName().equals(getScenPageInv(1).getName()) || inv.getName().equals("§6Liste des §lScénarios activés"))
            for (ItemStack it : inv.getContents())
                if (it != null && it.hasItemMeta() && it.getItemMeta().hasLore()) {
                    ItemMeta itm = it.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    for (String s : itm.getLore()) {
                        for (ArmorLimiter.ArmorTypes k : ArmorLimiter.ArmorTypes.values())
                            if (s.contains(k.name())) s = s.replace(k.name(), k.getName());
                        lore.add(s);
                    }
                    itm.setLore(lore);
                    it.setItemMeta(itm);
                }
    }


    @EventHandler
    public void onParamInv(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        Inventory inv = ev.getInventory();
        ItemStack current = ev.getCurrentItem();

        if (current == null) return;

        if (inv.getName().equals(getConfigInv().getName())) {
            ev.setCancelled(true);

            if (!current.equals(getReturnArrow()) && !current.getType().equals(Material.STAINED_GLASS_PANE) && !current.getType().equals(Material.AIR)) {
                ParamParts pp = ParamParts.getByMaterial(current.getType());
                if (pp == null) return;
                player.openInventory(getConfigPartInv(pp));
            } else if (current.equals(getReturnArrow())) player.openInventory(getGameConfigInv(player));
        }
        else if (inv.getName().startsWith("§f§lParam ")) {
            ev.setCancelled(true);

            if (!current.equals(getReturnArrow()) && !current.getType().equals(Material.AIR)) {
                Inventory invParam = null;
                if (current.getItemMeta().getDisplayName().equals("§5Inventaire de §lMort") || current.getItemMeta().getDisplayName().equals("§dInventaire de §lDépart")) {
                    String dm;
                    if (current.getType().equals(Material.CHEST)) {
                        invParam = Bukkit.createInventory(null, 54, "§fModif §d§lStarter");
                        dm = "Départ";
                    } else {
                        invParam = Bukkit.createInventory(null, 54, "§fModif §5§lDeathInv");
                        dm = "Mort";
                    }
                    invParam.setItem(8, getReturnArrow());
                    for (int i = 9; i < 18; i++)
                        invParam.setItem(i, new ItemsStack(Material.BEDROCK, " ").toItemStack());

                    invParam.setItem(0, new ItemsStack(Material.WOOL, (short)5, "§a§lModifier l'invetaire de " + dm, "", "§b>>Clique").toItemStack());
                    invParam.setItem(4, new ItemsStack(Material.WOOL, (short)14, "§c§lSupprimer l'inventaire de " + dm, "", "§b>>Touche de Drop").toItemStack());

                    if (dm.equals("Départ")) {
                        int i = 9;
                        for (Map.Entry<Integer, ItemStack> en : main.getInventoryManager().getStartArmor().entrySet()) {
                            invParam.setItem(i, en.getValue());
                            i++;
                        }
                        HashMap<Integer, ItemStack> si = new HashMap<>();
                        i = 0;
                        for (ItemStack it : main.getInventoryManager().startInventory) {
                            si.put(i, it);
                            i++;
                        }
                        for (Map.Entry<Integer, ItemStack> en : si.entrySet()) {
                            int k = en.getKey();
                            if (k >= 9 && k <= 17) k += 18;
                            else if (k >= 27 && k <= 35) k -= 18;
                            invParam.setItem(k + 18, en.getValue());
                        }
                    } else {
                        int i = 18;
                        for (ItemStack en : main.getInventoryManager().getDeathInventory()) {
                            invParam.setItem(i, en);
                            i++;
                        }
                    }
                } else {
                    ConfigurableParams param = getByName(current.getItemMeta().getDisplayName());
                    Object value = param.getValue();

                    if (value instanceof Boolean) {
                        boolean b = (boolean)value;
                        b = !b;
                        param.setValue(b);
                        invParam = getConfigPartInv(param.getPart());
                    }
                    else if (value instanceof Integer) {
                        int[] diff = param.getIntDifferences();
                        invParam = Bukkit.createInventory(null, Index.adaptInvSizeForInt(diff.length * 2, 3), "§fModif " + current.getItemMeta().getDisplayName());
                        invParam.setItem(5, getReturnArrow());
                        invParam.setItem(4, new ItemsStack(current.getType(), current.getDurability(), current.getItemMeta().getDisplayName(), current.getItemMeta().getLore().get(0)).toItemStackWithUnbreakableAndItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE));

                        int s = 0;
                        int l = 0;
                        for (int d : diff) {
                            int dt = d;
                            if (param.isTimer() && !param.equals(INVINCIBILITY)) dt /= 60;
                            invParam.setItem(s, getNumberAddOrRemoveBanner(dt, Arrays.asList("§6Ajoute §a§l" + dt + "§6 au nombre :", "§6\"" + param.getName() + "§6\"", "", "§b>>Clique")));
                            invParam.setItem(l + 6 + ArrayUtils.indexOf(IntStream.range(0,diff.length).map(i -> diff[diff.length-i-1])
                                    .toArray(), d), getNumberAddOrRemoveBanner(-dt, Arrays.asList("§6Retire §c§l" + -dt + "§6 au nombre :", "§6\"" + param.getName() + "§6\"", "", "§b>>Clique")));
                            if ((s - 2) % 9 == 0 && s != 0) {
                                s = s + 6;
                                l += 9;
                            }
                            s++;
                        }
                    }
                    else if (value instanceof Double) {
                        double[] diff = param.getDoubleDifferences();
                        invParam = Bukkit.createInventory(null, Index.adaptInvSizeForInt(diff.length * 2, 3), "§fModif " + current.getItemMeta().getDisplayName());
                        invParam.setItem(5, getReturnArrow());
                        invParam.setItem(4, new ItemsStack(current.getType(), current.getDurability(), current.getItemMeta().getDisplayName(), current.getItemMeta().getLore().get(0)).toItemStackWithUnbreakableAndItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE));

                        int s = 0;
                        int l = 0;
                        double[] rdiff = diff.clone();
                        ArrayUtils.reverse(rdiff);
                        for (double d : diff) {
                            int t = s;
                            int u = ArrayUtils.indexOf(rdiff, d);
                            if (rdiff.length <= 3) u += 1;
                            if (l > 0) t = s + (6 - diff.length);
                            invParam.setItem(t, getNumberAddOrRemoveBanner(d, Arrays.asList("§6Ajoute §a§l" + d + "§6 au nombre :", "§6\"" + param.getName() + "§6\"", "", "§b>>Clique")));
                            invParam.setItem(l + 5 + u, getNumberAddOrRemoveBanner(-d, Arrays.asList("§6Retire §c§l" + -d + "§6 au nombre :", "§6\"" + param.getName() + "§6\"", "", "§b>>Clique")));
                            if ((s - 2) % 9 == 0 && s != 0) {
                                s = s + 6;
                                l += 9;
                            }
                            s++;
                        }
                    }
                    if (param.equals(TEAMTYPE)) {
                        int[] diff = new int[]{1, 5, 10};
                        invParam = Bukkit.createInventory(null, Index.adaptInvSizeForInt(diff.length * 2, 3), "§fModif " + current.getItemMeta().getDisplayName());
                        invParam.setItem(5, getReturnArrow());
                        invParam.setItem(4, new ItemsStack(current.getType(), current.getDurability(), current.getItemMeta().getDisplayName(), current.getItemMeta().getLore().get(0)).toItemStackWithUnbreakableAndItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE));

                        int s = 0;
                        int l = 0;
                        for (int d : diff) {
                            int dt = d;
                            if (param.isTimer()) dt /= 60;
                            invParam.setItem(s, getNumberAddOrRemoveBanner(dt, Arrays.asList("§6Agrandit le nombre de", "§6joueurs par équipe de §a§l" + dt, "", "§b>>Clique")));
                            invParam.setItem(l + 6 + ArrayUtils.indexOf(IntStream.range(0,diff.length).map(i -> diff[diff.length-i-1])
                                    .toArray(), d), getNumberAddOrRemoveBanner(-dt, Arrays.asList("§6Réduit le nombre de", "§6joueurs par équipe de §c§l" + -dt, "", "§b>>Clique")));
                            if ((s - 2) % 9 == 0 && s != 0) {
                                s = s + 6;
                                l += 9;
                            }
                            s++;
                        }
                    }
                }
                if (invParam != null) player.openInventory(invParam);
            } else if (current.equals(getReturnArrow())) player.openInventory(getConfigInv());
        }
        else if (inv.getName().startsWith("§fModif")) {
            ev.setCancelled(true);
            ConfigurableParams cp = getByName(inv.getName().substring(8));
            ParamParts pp;

            if (current.hasItemMeta() && !current.getType().equals(Material.AIR) && !current.equals(getReturnArrow())) {
                if (current.getItemMeta().getDisplayName().startsWith("§a§lModifier l'invetaire de ")) {
                    if (current.getItemMeta().getDisplayName().endsWith("Départ")) {
                        if (starterModifier != null) {
                            player.sendMessage(main.getPrefix() + "§c\"§4§l" + starterModifier.getDisplayName() + "§c\" modifie déjà l'inventaire !");
                            Index.playNegativeSound(player);
                            return;
                        }
                        starterModifier = player;
                        player.sendMessage(main.getPrefix() + "§6Vous allez modifier l'inventaire de Départ. §9Pour le quiiter effectuez la commande §7§l/finish§9. §9Pour enchanter un objet, utiliez §7§l/enchant§9.");
                        player.setGameMode(GameMode.CREATIVE);
                        InventoryManager.clearInventory(starterModifier);
                        main.getInventoryManager().giveStartInventory(starterModifier);
                    } else {
                        if (deathInvModifier != null) {
                            player.sendMessage(main.getPrefix() + "§c\"§4§l" + deathInvModifier.getDisplayName() + "§c\" modifie déjà l'inventaire !");
                            Index.playNegativeSound(player);
                            return;
                        }
                        deathInvModifier = player;
                        player.sendMessage(main.getPrefix() + "§6Vous allez modifier l'inventaire de Mort. §9Pour le quiiter effectuez la commande §7§l/finish§9. §9Pour enchanter un objet, utiliez §7§l/enchant§9.");
                        player.setGameMode(GameMode.CREATIVE);
                        InventoryManager.clearInventory(deathInvModifier);
                        InventoryManager.giveDeathInventory(deathInvModifier);
                    }
                    player.closeInventory();
                    Index.playPositiveSound(player);
                } else if (current.getItemMeta().getDisplayName().startsWith("§c§lSupprimer l'inventaire de ") && ev.getClick().equals(ClickType.DROP)) {
                    if (current.getItemMeta().getDisplayName().endsWith("Départ")) {
                        main.getInventoryManager().startInventory = new ItemStack[]{};
                        main.getInventoryManager().getStartArmor().clear();
                        player.sendMessage(main.getPrefix() + "§cVous avez supprimé l'inventaire de Départ.");
                    } else {
                        main.getInventoryManager().getDeathInventory().clear();
                        player.sendMessage(main.getPrefix() + "§cVous avez supprimé l'inventaire de Mort.");
                    }
                    Index.playPositiveSound(player);
                    player.closeInventory();
                } else if (current.getType().equals(Material.BANNER) && ((BannerMeta)current.getItemMeta()).getBaseColor().equals(DyeColor.WHITE) && !current.getItemMeta().getDisplayName().equals("§eTaille des équipes")) {
                    Object value = cp.getValue();

                    if (value instanceof Integer) {
                        int i = Integer.parseInt(current.getItemMeta().getDisplayName().replace("§a+ ", "").replace("§l", "").replace("§c- ", "-"));
                        if (cp.equals(EPISODS_TIMER) || cp.equals(PVP) || cp.equals(BORDER_TIMER))
                            i *= 60;
                        if ((int)value + i <= cp.getIntMax() && (int)value + i >= cp.getIntMin())
                            cp.setValue((int)value + i);
                        else
                            if ((int)value + i <= cp.getIntMax())
                                cp.setValue(cp.getIntMin());
                            else if ((int)value + i >= cp.getIntMin())
                                cp.setValue(cp.getIntMax());
                    } else if (value instanceof Double) {
                        double d = Double.parseDouble(current.getItemMeta().getDisplayName().replace("§a+ ", "").replace("§l", "").replace("§c- ", "-"));
                        if ((double)value + d <= cp.getDoubleMax() && (double)value + d >= cp.getDoubleMin())
                            cp.setValue(Math.round(((double)value + d) * 10.0) / 10.0);
                        else
                            if ((double)value + d <= cp.getDoubleMax())
                                cp.setValue(cp.getDoubleMin());
                            else if ((double)value + d >= cp.getDoubleMin())
                                cp.setValue(cp.getDoubleMax());
                    } else if (cp.equals(TEAMTYPE)) {
                        int tti = getTeamTypeInt((String)value);
                        int i = Integer.parseInt(current.getItemMeta().getDisplayName().replace("§a+ ", "").replace("§l", "").replace("§c- ", "-"));
                        if ((tti + i <= (int) SLOTS.getValue()) && ((tti + i) >= 0))
                            cp.setValue(getTeamTypeString(tti + i, Scenarios.RANDOM_TEAM.isActivated()));
                        else
                            if (tti + i <= (int) SLOTS.getValue())
                                cp.setValue(getTeamTypeString(1, Scenarios.RANDOM_TEAM.isActivated()));
                            else if (tti + i >= 1)
                                cp.setValue(getTeamTypeString((int) SLOTS.getValue(), Scenarios.RANDOM_TEAM.isActivated()));

                        main.getUHCTeamManager().clearTeams();
                        if (getTeamTypeInt((String)cp.getValue()) > 1) {
                            int p = main.players.size();
                            for (PlayerUHC puhc : main.players)
                                if (puhc.isSpec()) p--;
                            int nt = BigDecimal.valueOf((double) p / getTeamTypeInt((String) cp.getValue())).setScale(0, RoundingMode.UP).toBigInteger().intValue();
                            if (nt == 0) nt = 1;
                            while (nt != 0) {
                                if (nt < 0) throw new IllegalArgumentException("nt est inferieur a 0");
                                main.getUHCTeamManager().createTeam();
                                nt--;
                            }
                        }
                        if (((String)TEAMTYPE.getValue()).startsWith("To"))
                            for (PlayerUHC pl : main.players)
                                pl.getPlayer().getPlayer().getInventory().setItem(4, getChooseTeamBanner());
                        else
                            for (PlayerUHC pl : main.players)
                                pl.getPlayer().getPlayer().getInventory().remove(Material.BANNER);
                    }
                    ItemMeta itm = inv.getItem(4).getItemMeta();
                    itm.setLore(Collections.singletonList("§bValeur : " + cp.getName().substring(0, 2) + "§l" + cp.getVisibleValue()));
                    inv.getItem(4).setItemMeta(itm);
                    main.players.forEach(playerUHC -> main.boards.get(playerUHC).setLine(4, "§6§lSlots §6: §f" + Bukkit.getServer().getOnlinePlayers().size() + "§6/§e" + GameConfig.ConfigurableParams.SLOTS.getValue()));
                }
            } else if (current.equals(getReturnArrow())) {
                if (inv.getName().endsWith("§d§lStarter")) pp = ParamParts.STARTER;
                else if (inv.getName().endsWith("§5§lDeathInv")) pp = ParamParts.DEATHDROP;
                else pp = ConfigurableParams.getByName(inv.getName().replace("§fModif ", "")).getPart();
                player.openInventory(getConfigPartInv(pp));
            }
        }
        else if (inv.getName().equals("§fEnchantement d'un objet")) {
            ev.setCancelled(true);

            if (current.getType().equals(Material.ENCHANTED_BOOK)) {
                Enchantment e = current.getEnchantments().keySet().toArray(new Enchantment[0])[0];
                int a = 1;
                if (ev.getClick().isShiftClick()) a = 10;
                if (ev.getClick().isLeftClick())
                    inv.getItem(4).addUnsafeEnchantment(e, inv.getItem(4).getEnchantmentLevel(e) + a);
                else if (ev.getClick().isRightClick()) {
                    if (inv.getItem(4).getEnchantmentLevel(e) <= a) inv.getItem(4).removeEnchantment(e);
                    else inv.getItem(4).addUnsafeEnchantment(e, inv.getItem(4).getEnchantmentLevel(e) - a);
                }
                ItemMeta itm = current.getItemMeta();
                List<String> lore = itm.getLore();
                lore.remove(0);
                lore.add(0, "§bValeur : §d§l" + inv.getItem(4).getEnchantmentLevel(e));
                itm.setLore(lore);
                current.setItemMeta(itm);
            }
        }
    }

    @EventHandler
    public void onCloseEnchantInv(InventoryCloseEvent ev) {
        if (ev.getInventory().getName().equals("§fEnchantement d'un objet")) {
            ev.getPlayer().setItemInHand(ev.getInventory().getItem(4));
        }
    }

    @EventHandler
    public void onPlayersInv(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        ItemStack current = ev.getCurrentItem();
        Inventory inv = ev.getInventory();

        if (current == null) return;

        if (inv.getName().equals(getJoueursInv().getName())) {
            ev.setCancelled(true);

            if (current.getType().equals(Material.SKULL_ITEM)) {
                ItemStack ic = current.clone();
                ItemMeta icm = ic.getItemMeta();
                List<String> l = icm.getLore();
                l.remove(l.size() - 1);
                l.remove(l.size() - 1);
                l.remove(l.size() - 1);
                l.remove(l.size() - 1);
                icm.setLore(l);
                ic.setItemMeta(icm);
                player.openInventory(getPlayerInv(Bukkit.getPlayer(((SkullMeta) current.getItemMeta()).getOwner()), ic));
            } else if (current.equals(getReturnArrow()))
                player.openInventory(getGameConfigInv(player));
        }
        else if (inv.getName().startsWith("§5Modif ")) {
            ev.setCancelled(true);
            Player p = Bukkit.getPlayer(((SkullMeta)inv.getItem(4).getItemMeta()).getOwner());
            PlayerUHC puhc = main.getPlayerUHC(p);

            if (current.getType().equals(Material.BANNER)) {
                if (getTeamTypeInt((String)TEAMTYPE.getValue()) <= 1) {
                    player.sendMessage(main.getPrefix() + "§cLes équipes sont désactivées.");
                    Index.playNegativeSound(player);
                    return;
                }
                player.openInventory(getModifTeamPlayerInv(p, 1, inv.getItem(4)));
            } else if (current.getType().equals(Material.DIODE)) {
                main.setPlayerHost(p, !puhc.isHost());
                if (puhc.isHost()) Bukkit.broadcastMessage(main.getPrefix() + player.getDisplayName() + " §6a mit §a§l" + p.getName() + " §6§lHost§6 de la partie.");
                else Bukkit.broadcastMessage(main.getPrefix() + player.getDisplayName() + "§6 a retiré la fontion de §lHost§6 à §c§l" + p.getName());
                Index.playPositiveSound(p);
                Index.playPositiveSound(player);
                player.closeInventory();
            } else if (current.getType().equals(Material.GHAST_TEAR)) {
                if (puhc.isSpec()) {
                    main.spectators.remove(p);
                    main.players.add(main.getPlayerUHC(p));
                    p.setGameMode(GameMode.ADVENTURE);
                    p.setDisplayName(p.getName());
                    p.setPlayerListName(player.getDisplayName());
                    InventoryManager.giveWaitInventory(puhc);

                    p.sendMessage(main.getPrefix() + player.getDisplayName() + "§6 vous a retiré du mode Spectateur");
                    player.sendMessage(main.getPrefix() + "§6Vous avez retiré " + p.getDisplayName() + " §6du mode Spectateur.");
                } else {
                    main.spectators.add(p);
                    p.setGameMode(GameMode.SPECTATOR);
                    p.setDisplayName("§8[§7Spectateur§8] §7" + p.getName());
                    p.setPlayerListName(p.getDisplayName());
                    InventoryManager.clearInventory(p);
                    puhc.heal();

                    p.sendMessage(main.getPrefix() + player.getDisplayName() + " §6a établi votre mode de jeu en spectateur.");
                    p.sendMessage(main.getPrefix() + "§7Pour revenir au mode non-spectateur, utilisez la commande §6§l/uhc spec off§7.");
                    player.sendMessage(main.getPrefix() + "§6Vous avez placé " + p.getDisplayName() + " §6en mode Spectateur.");
                }
                Index.playPositiveSound(p);
                Index.playPositiveSound(player);
                player.closeInventory();
            } else if (current.getType().equals(Material.BARRIER) && ev.getClick().equals(ClickType.DROP)) {
                Bukkit.broadcastMessage(main.getPrefix() + player.getDisplayName() + "§c a exclu §b" + p.getName() + "§c.");
                Index.playPositiveSound(player);
                player.closeInventory();
                p.kickPlayer("§cExclu par " + player.getDisplayName());
            } else if (current.equals(getReturnArrow()))
                player.openInventory(getJoueursInv());
        }
        else if (inv.getName().startsWith("§e§lTeam ")) {
            ev.setCancelled(true);
            Player p = Bukkit.getPlayer(((SkullMeta)inv.getItem(4).getItemMeta()).getOwner());
            PlayerUHC puhc = main.getPlayerUHC(p);
            String sp = inv.getName();
            while (!sp.startsWith("["))
                sp = sp.substring(1);
            sp = sp.substring(1);
            while (!sp.endsWith("/"))
                sp = sp.substring(0, sp.length() - 1);
            sp = sp.substring(0, sp.length() - 1);

            if (current.equals(getNextPaper()))
                player.openInventory(getModifTeamPlayerInv(p, Integer.parseInt(sp) + 1, inv.getItem(4)));
            else if (current.equals(getPreviousPaper()))
                player.openInventory(getModifTeamPlayerInv(p, Integer.parseInt(sp) - 1, inv.getItem(4)));

            else if (current.getType().equals(Material.BANNER)) {
                if (current.getItemMeta().getDisplayName().startsWith("§fRéinitialiser l'équipe de §b")) {
                    if (puhc.getTeam() != null) {
                        player.sendMessage(main.getPrefix() + "§eVous avez bien réinitialisé l'équipe de §b" + p.getName() + "§e.");
                        Index.playPositiveSound(player);
                        p.sendMessage(main.getPrefix() + player.getDisplayName() + " §ea réinitialisé votre équipe.");
                        Index.playPositiveSound(p);
                        puhc.getTeam().leave(puhc);
                        BannerMeta bm = (BannerMeta)p.getInventory().getItem(4).getItemMeta();
                        bm.setPatterns(null);
                        bm.setBaseColor(DyeColor.WHITE);
                        p.getInventory().getItem(4).setItemMeta(bm);
                        player.openInventory(getModifTeamPlayerInv(p, Integer.parseInt(sp), inv.getItem(4)));
                    }
                } else {
                    UHCTeam t = main.getUHCTeamManager().getTeamByDisplayName(current.getItemMeta().getDisplayName());
                    if (puhc.getTeam() != null && puhc.getTeam().equals(t)) return;

                    player.sendMessage(main.getPrefix() + "§eVous avez bien mit §b" + p.getName() + " §edans l'équipe " + t.getTeam().getDisplayName() + "§e.");
                    Index.playPositiveSound(player);
                    p.sendMessage(main.getPrefix() + player.getDisplayName() + " §evous a mit dans l'équipe " + t.getTeam().getDisplayName() + "§e.");
                    Index.playPositiveSound(p);
                    if (puhc.getTeam() != null) puhc.getTeam().leave(puhc);
                    t.add(p);
                    BannerMeta bm = (BannerMeta)p.getInventory().getItem(4).getItemMeta();
                    bm.setPatterns(((BannerMeta)t.getBanner().getItemMeta()).getPatterns());
                    bm.setBaseColor(((BannerMeta)t.getBanner().getItemMeta()).getBaseColor());
                    p.getInventory().getItem(4).setItemMeta(bm);
                    player.openInventory(getModifTeamPlayerInv(p, Integer.parseInt(sp), inv.getItem(4)));
                }
            }
            else if (current.equals(getReturnArrow())) player.openInventory(getPlayerInv(p, inv.getItem(4)));
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

        inv.setItem(30, new ItemsStack(Material.SKULL_ITEM, (short)3, "§5Joueurs", "§fPermet de gérer", "§fles joueurs", "§f§o(spectateur, etc)").toItemStackwithSkullMeta(player.getName()));

        inv.setItem(32, new ItemsStack(Material.BARRIER, "§bReset la Map", "§fPermet de reset", "§fla map.").toItemStack());

        inv.setItem(13, new ItemsStack(Material.APPLE, "§f§lParamètres de la Partie", "§fPermet de changer les", "§foptions de la partie.").toItemStack());

        inv.setItem(15, new ItemsStack(Material.BOOK_AND_QUILL, "§6§lScénarios", "§fPermet de gérer les", "§fscénarios de la partie.").toItemStack());

        inv.setItem(11, new ItemsStack(Material.ITEM_FRAME, "§2Changer le §lMode §2de jeu", "§fPermet de changer le", "§fmode de jeu de la partie.", "", "§eActuel : §c§l" + ChatColor.translateAlternateColorCodes('&', mode.getPrefix())).toItemStack());

        if (main.uhcStart == null)
            inv.setItem(40, new ItemsStack(Material.STAINED_CLAY, (short)5, "§a§lCommencer la Partie", "", "§b>>Clique").toItemStack());
        else
            inv.setItem(40, new ItemsStack(Material.STAINED_CLAY, (short)14, "§c§lArrêter le démarrage de la Partie", "", "§b>>Clique").toItemStack());

        return inv;
    }

    private Inventory getScenInv() {
        Inventory inv = Bukkit.createInventory(null, 27, "§c§lConfigAffichage §6§lScénario");
        setInvCoin(inv, (byte)4);
        setReturnArrow(inv);

        List<String> activated = new ArrayList<>();
        for (Scenarios sc : Scenarios.getActivatedScenarios())
            if (!sc.isModeScenario())
                activated.add("§a§l - §6" + sc.getName());
            else
                activated.add("§a§l - §6§l" + sc.getName());
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

    private Inventory getScenModifInv(String type, Scenarios sc, ItemStack current) {
        Inventory inv;

        switch (type) {
            case "Integer":
                inv = Bukkit.createInventory(null, 9, "§cModif  " + sc.getDisplayName());
                inv.setItem(5, getReturnArrow());
                inv.setItem(4, current);
                inv.setItem(0, getNumberAddOrRemoveBanner(1, Arrays.asList("§6Ajoute §c§l" + 1 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(1, getNumberAddOrRemoveBanner(5, Arrays.asList("§6Ajoute §c§l" + 5 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(2, getNumberAddOrRemoveBanner(10, Arrays.asList("§6Ajoute §c§l" + 10 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(6, getNumberAddOrRemoveBanner(-10, Arrays.asList("§6Retire §c§l" + 10 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(7, getNumberAddOrRemoveBanner(-5, Arrays.asList("§6Retire §c§l" + 5 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(8, getNumberAddOrRemoveBanner(-1, Arrays.asList("§6Retire §c§l" + 1 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                break;
            case "Double":
                inv = Bukkit.createInventory(null, 18, "§cModif  " + sc.getDisplayName());
                inv.setItem(5, getReturnArrow());
                inv.setItem(4, current);
                inv.setItem(0, getNumberAddOrRemoveBanner(0.1, Arrays.asList("§6Ajoute §c§l" + 0.1 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(1, getNumberAddOrRemoveBanner(0.5, Arrays.asList("§6Ajoute §c§l" + 0.5 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(2, getNumberAddOrRemoveBanner(1, Arrays.asList("§6Ajoute §c§l" + 1 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(6, getNumberAddOrRemoveBanner(-1, Arrays.asList("§6Retire §c§l" + 1 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(7, getNumberAddOrRemoveBanner(-0.5, Arrays.asList("§6Retire §c§l" + 0.5 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(8, getNumberAddOrRemoveBanner(-0.1, Arrays.asList("§6Retire §c§l" + 0.1 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(9, getNumberAddOrRemoveBanner(5, Arrays.asList("§6Ajoute §c§l" + 5 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(10, getNumberAddOrRemoveBanner(10, Arrays.asList("§6Ajoute §c§l" + 10 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(11, getNumberAddOrRemoveBanner(15, Arrays.asList("§6Ajoute §c§l" + 15 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(15, getNumberAddOrRemoveBanner(-15, Arrays.asList("§6Retire §c§l" + 15 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(16, getNumberAddOrRemoveBanner(-10, Arrays.asList("§6Retire §c§l" + 10 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                inv.setItem(17, getNumberAddOrRemoveBanner(-5, Arrays.asList("§6Retire §c§l" + 5 + "§6 au nombre ", current.getItemMeta().getDisplayName())));
                break;
            case "List":
                inv = Bukkit.createInventory(null, Index.adaptInvSizeForInt(Moles.Kits.values().length, 1), "§cModif  " + sc.getDisplayName());
                setReturnArrow(inv);
                for (Moles.Kits k : Moles.Kits.values()) {
                    List<String> lore = new ArrayList<>();
                    for (ItemStack it : k.getItems())
                        lore.add("§8x§7" + it.getAmount() + " " + it.getType().name().toLowerCase());
                    lore.add("");
                    lore.add("§bValeur : " + getStringBoolean(k.isActivated()));
                    inv.addItem(new ItemsStack(k.getMaterial(), k.getName(), lore.toArray(new String[0])).toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return inv;
    }

    public Inventory getModesInv() {
        Inventory inv = Bukkit.createInventory(null, 54, "§c§lConfiguration §6§lScénarios");
        setInvCoin(inv, (short)1);
        setReturnArrow(inv);

        int ord = 0;
        for (int i = 10; i <= 43; i++) {
            if (ord == Scenarios.values().length) return inv;
            Scenarios sc = Scenarios.values()[ord];
            if (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35)
                while (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35) i++;
            if (!sc.isModeScenario())
                while (!sc.isModeScenario()) {
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
        int[] slots = new int[] {39, 11, 13, 15 , 21, 23, 29, 31, 33, 41, 49};
        setInvCoin(inv, (byte)0);
        setReturnArrow(inv);

        for (ParamParts pp : ParamParts.values()) {
            int i = 0;
            int invslot = slots[i];
            while (inv.getItem(invslot) != null) {
                i++;
                invslot = slots[i];
            }
            inv.setItem(invslot, new ItemsStack(pp.getType(), pp.getMaterialData(), pp.getName().substring(0, 2) + "Gérer les options " + pp.getName(), pp.getLoreConfigInv().toArray(new String[0])).toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES));
        }
        return inv;
    }

    private Inventory getConfigPartInv(ParamParts pp) {
        if (pp == null) throw new IllegalArgumentException("pp ne peut pas etre nul");
        Inventory inv = Bukkit.createInventory(null, Index.adaptInvSizeForInt(pp.getParams().size(), 1), "§f§lParam " + pp.getName().replace("l'inventaire", "l'inv").replace("customizés", "custom"));
        setReturnArrow(inv);

        for(ConfigurableParams cp : pp.getParams())
            inv.addItem(new ItemsStack(cp.getType(), cp.getMatData(), cp.getName(), "§bValeur : " + cp.getName().substring(0, 2) + "§l" + cp.getVisibleValue(), "", "§7Ouvre le menu de", "§7changement de la valeur", "§b>>Clique").toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES));
        if (pp.equals(ParamParts.DEATHDROP)) inv.addItem(new ItemsStack(Material.ENDER_CHEST, "§5Inventaire de §lMort", "§bTaille : §5§l" + InventoryManager.getDeathInventorySize(), "", "§7Ouvre le menu de", "§7changement de l'inventaire de Mort", "§b>>Clique").toItemStack());
        if (pp.equals(ParamParts.STARTER)) inv.addItem(new ItemsStack(Material.CHEST, "§dInventaire de §lDépart", "§bTaille : §d§l" + main.getInventoryManager().getStartInventorySize(), "", "§7Ouvre le menu de", "§7changement de l'inventaire de Départ", "§b>>Clique").toItemStack());

        return inv;
    }


    private Inventory getJoueursInv() {
        Inventory inv = Bukkit.createInventory(null, Index.adaptInvSizeForInt(main.players.size(), 1), "§c§lConfiguration §5§lJoueurs");
        setReturnArrow(inv);

        for (PlayerUHC pu : main.players)
            if (pu.getPlayer().isOnline()) {
                String team;
                if (pu.getTeam() == null)
                    team = "§cAucune";
                else
                    team = pu.getTeam().getTeam().getDisplayName();
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


    private Inventory getPlayerInv(Player player, ItemStack current) {
        Inventory inv = Bukkit.createInventory(null, 54, "§5Modif §b" + player.getName());
        PlayerUHC playerUHC = main.getPlayerUHC(player);
        setInvCoin(inv, (short)10);
        setReturnArrow(inv);
        inv.setItem(4, current);

        ItemsStack banner = new ItemsStack(UHCTeamManager.getTeamBanner(playerUHC));
        String at = "§cAucune";
        if (playerUHC.getTeam() != null) at = playerUHC.getTeam().getTeam().getDisplayName();
        banner.setName("§5Changer §b" + player.getName() + " §e§ld'Équipe");
        banner.setLore("§bValeur actuelle : " + at, "", "§b>>Cliquez pour changer", "§bl'équipe du joueur");
        inv.setItem(20, banner.toItemStack());

        inv.setItem(22, new ItemsStack(Material.DIODE, "§5Mettre §b" + player.getName() + " §6§lHost", "§bValeur actuelle : " + getYesOrNoStringBoolean(playerUHC.isHost()), "", "§b>>Cliquer pour changer").toItemStack());

        inv.setItem(24, new ItemsStack(Material.GHAST_TEAR, "§5Mettre §b" + player.getName() + " §5en §7§lSpectateur", "§bValeur actuelle : " + getYesOrNoStringBoolean(playerUHC.isSpec()), "", "§b>>Cliquer pour changer").toItemStack());

        inv.setItem(40, new ItemsStack(Material.BARRIER, "§cExclure §b" + player.getName() + " §cde la partie", "", "§b>>Touche de drop").toItemStack());

        return inv;
    }

    private Inventory getModifTeamPlayerInv(Player player, int page, ItemStack current) {
        int maxpages = BigDecimal.valueOf((double) main.getUHCTeamManager().getTeams().size() / 28.0).setScale(0, RoundingMode.UP).toBigInteger().intValue();
        Inventory inv = Bukkit.createInventory(null, Index.adaptInvSizeForInt(main.getUHCTeamManager().getTeams().size(), 18), "§e§lTeam " + player.getName() + " §8["+page+"/" + maxpages + "]");
        setInvCoin(inv, (short)4);
        setReturnArrow(inv);
        inv.setItem(4, current);
        if (page < maxpages)
            inv.setItem(44, getNextPaper());
        if (page > 1)
            inv.setItem(inv.getSize() - 18, getPreviousPaper());
        inv.setItem(inv.getSize() - 5, new ItemsStack(Material.BANNER, (short)15, "§fRéinitialiser l'équipe de §b" + player.getName(), "", "§b>>Clique").toItemStackwithItemFlag(ItemFlag.HIDE_POTION_EFFECTS));

        int ord = 28 * (page - 1);
        for (int i = 10; i < inv.getSize() - 10; i++) {
            if (ord >= main.getUHCTeamManager().getTeams().size()) continue;
            UHCTeam t = main.getUHCTeamManager().getTeams().toArray(new UHCTeam[0])[ord];
            if (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35)
                while (i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35) i++;
            ItemsStack it = new ItemsStack(t.getBanner());
            it.setName(t.getTeam().getDisplayName());
            it.setLore("§eJoueurs : ");
            for (int p = 0; p < getTeamTypeInt((String)TEAMTYPE.getValue()); p++)
                if (t.getPlayers().size() - 1 >= p)
                    it.addLore(t.getPrefix().color.getColor() + " - " + t.getListPlayers().get(p).getPlayer().getPlayer().getPlayerListName());
                else it.addLore(t.getPrefix().color.getColor() + " - ");
            it.addLore("", "§b>>Cliquez pour mettre §f" + player.getName(), "§bdans cette équipe.");
            inv.setItem(i, it.toItemStack());
            ord++;
        }
        return inv;
    }



    public static ItemStack getComparator() {
        return new ItemsStack(Material.REDSTONE_COMPARATOR, "§c§lConfiguration de la partie", "§7Permet de configurer la partie", "§b>>Clique droit").toItemStack();
    }

    public static ItemStack getReturnArrow() {
        return new ItemsStack(Material.ARROW, "§cRetour", "§7Retourner au menu précédent").toItemStack();
    }

    public static void setReturnArrow(Inventory inv) {
        inv.setItem(inv.getSize() - 1, getReturnArrow());
    }

    public static ItemStack getNextPaper() {
        return new ItemsStack(Material.PAPER, "§ePage Suivante", "§7Ouvrir la page suivante").toItemStack();
    }

    public static void setNextPaper(Inventory inv) {
        inv.setItem(inv.getSize() - 10, getNextPaper());
    }

    public static ItemStack getPreviousPaper() {
        return new ItemsStack(Material.PAPER, "§ePage Précédante", "§7Ouvrir la page précédante").toItemStack();
    }

    public static void setPreviousPaper(Inventory inv) {
        inv.setItem(inv.getSize() - 9, getPreviousPaper());
    }

    public static ItemStack getChooseTeamBanner() {
        return new ItemsStack(Material.BANNER, (short)15, "§e§lChanger d'Équipe", "", "§b>>Clique").toItemStackwithItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
    }

    private ItemStack getNumberAddOrRemoveBanner(double nbmoinsplus, List<String> lore) {
        ItemStack b = new ItemStack(Material.BANNER);
        BannerMeta bm = (BannerMeta) b.getItemMeta();
        int amount = (int)nbmoinsplus;
        if (amount != nbmoinsplus)
            amount = 0;
        bm.setBaseColor(DyeColor.WHITE);
        bm.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        if (nbmoinsplus > 0) {
            bm.addPattern(new Pattern(DyeColor.LIME, PatternType.STRIPE_CENTER));
            bm.addPattern(new Pattern(DyeColor.LIME, PatternType.STRIPE_MIDDLE));
            bm.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            bm.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
            bm.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            bm.addPattern(new Pattern(DyeColor.WHITE, PatternType.BORDER));
            b.setAmount(amount);
            bm.setDisplayName("§a+ §l" + nbmoinsplus);
            if (amount != 0) bm.setDisplayName("§a+ §l" + amount);
        } else {
            bm.addPattern(new Pattern(DyeColor.RED, PatternType.STRIPE_MIDDLE));
            bm.addPattern(new Pattern(DyeColor.WHITE, PatternType.BORDER));
            b.setAmount(amount);
            bm.setDisplayName("§c- §l" + -nbmoinsplus);
            if (amount != 0) bm.setDisplayName("§c- §l" + -amount);
        }
        bm.setLore(lore);
        b.setItemMeta(bm);
        return b;
    }


    private List<String> getScenInvLore(Scenarios sc) {
        List<String> lore = new ArrayList<>();
        try {
            Class<?> c = sc.getScenarioClass();
            ItemsStack scit = new ItemsStack((ItemStack) c.getMethod("getMenuItem").invoke(c.newInstance()));
            ItemMeta itm = scit.toItemStack().getItemMeta();
            for (String s : itm.getLore()) {
                for (ArmorLimiter.ArmorTypes k : ArmorLimiter.ArmorTypes.values())
                    if (s.contains(k.name())) s = s.replace(k.name(), k.getName());
                lore.add(s);
            }
            scit.setLore(lore);
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


    public static void setInvCoin(Inventory inv, short color) {
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



    public enum ParamParts {

        DEATHDROP("§5de l'inventaire de §lMort", Material.ENDER_CHEST),
        STARTER("§dde l'inventaire de §lDépart", Material.CHEST),
        MINERULES("§ades §lMines", Material.STONE_PICKAXE),
        PLAYERRULES("§fdes §lJoueurs", Material.SKULL_ITEM),
        PvP("§cdu §lPvP", Material.IRON_SWORD),
        CUSTOMCRAFTS("§bdes §lCrafts customizés", Material.WORKBENCH),
        WORLDRULES("§2du §lMonde", Material.GRASS),
        DROPS("§7des §lDrops", Material.APPLE),
        BORDER("§3de la §lBordure", Material.BARRIER),
        EPISOD("§6des §lÉpisodes", Material.WATCH),
        TEAMS("§edes §lÉquipes", Material.BANNER, (short)15);

        ParamParts(String name, Material material) {
            this.name = name;
            this.material = material;
            this.matdata = (short)0;
        }

        ParamParts(String name, Material material, short matdata) {
            this.name = name;
            this.material = material;
            this.matdata = matdata;
        }

        private final String name;
        private final short matdata;
        private final Material material;

        public String getName() {
            return name;
        }

        public Material getType() {
            return material;
        }

        public short getMaterialData() {
            return matdata;
        }


        public List<String> getLoreConfigInv() {
            List<String> lore = new ArrayList<>();
            lore.add("§bValeurs actuelles :");
            for (ConfigurableParams dd : ConfigurableParams.values())
                if (dd.getPart().equals(this))
                    lore.add("§b - " + dd.getName() +" §b: " + dd.getName().substring(0, 2) + "§l" + dd.getVisibleValue());
                if (this.equals(ParamParts.DEATHDROP))
                    lore.add("§b§l - §5Taille de l'inventaire de mort §b: §5§l" + InventoryManager.getDeathInventorySize());
                if (this.equals(ParamParts.STARTER))
                    lore.add("§b§l - §fTaille de l'inventaire de départ §b: §f§l" + Index.getInstance().getInventoryManager().getStartInventorySize());
            lore.add("");
            lore.add("§7Ouvre le menu de configuration");
            lore.add("§7des options " + getName().substring(2).replace("§l", ""));
            lore.add("§b>>Clique");
            return lore;
        }

        public List<ConfigurableParams> getParams() {
            return Arrays.stream(ConfigurableParams.values()).filter(cp -> cp.getPart().equals(this)).collect(Collectors.toList());
        }


        public static ParamParts getByMaterial(Material m) {
            for (ParamParts pp : ParamParts.values())
                if (pp.getType().equals(m))
                    return pp;
            return null;
        }

        public static ParamParts getByName(String name) {
            for (ParamParts pp : ParamParts.values())
                if (pp.getName().equals(name))
                    return pp;
            return null;
        }

    }

    public enum ConfigurableParams {

        HEAD("§cDrop de la Tête", ParamParts.DEATHDROP, Material.SKULL_ITEM, (short) 3, false),
        GOLDEN_HEAD("§6Drop d'une Golden Head", ParamParts.DEATHDROP, Material.GOLDEN_APPLE, false),
        LIGHTNING("§fÉclair", ParamParts.DEATHDROP, Material.BLAZE_ROD, true),
        BARRIER_HEAD("§4Poteau avec la tête", ParamParts.DEATHDROP, Material.DARK_OAK_FENCE, true),

        ROLLERCOASTER("§6RollerCoaster", ParamParts.MINERULES, Material.COBBLESTONE_STAIRS, (short) 1, true, "§7Minage en escalier de la couche 32 à 6"),
        STRIPMINE("§7Stripmining", ParamParts.MINERULES, Material.BEDROCK, true, "§7Minage tout droit couche 11"),
        POKEHOLE("§bPokeholling", ParamParts.MINERULES, Material.RAILS, true, "§7Minage optimisé"),
        SOUND_MINING("§aSoundMining/EntityMining", ParamParts.MINERULES, Material.NOTE_BLOCK, true, "§7Se servir du son pour miner"),
        DIGDOWN("§cDigDown", ParamParts.MINERULES, Material.IRON_SPADE, true, "§7S'enterrer sous terre pour se cacher d'un fight"),
        STALK("§4Stalk", ParamParts.MINERULES, Material.IRON_BOOTS, false, "§7Suivre un joueur dans les grottes"),
        TRAPS("§eTrap", ParamParts.MINERULES, Material.TRAP_DOOR, false, "§7Faire des pièges pour tuer"),

        CROSS_TEAM("§cCrossTeam", ParamParts.PLAYERRULES, Material.IRON_HOE, false, "§7Alliance entre ennemis"),
        FRIENDLY_FIRE("§aFriendlyFire", ParamParts.PLAYERRULES, Material.WOOD_SWORD, false, "§7Se taper entre coéquipiers"),
        I_PVP("§6iPvP", ParamParts.PLAYERRULES, Material.ANVIL, false, "§7Infliger des dégâts avant le PvP"),
        COORDS_F3("§eF3 Coords", ParamParts.PLAYERRULES, Material.PAPER, true, "§7Activer les coordonnées F3"),
        ACHIEVEMENTS("§2Achievements", ParamParts.PLAYERRULES, Material.GOLD_NUGGET, true, "§7Annonce des succès dans le chat."),
        SPECTATORS("§7Specs", ParamParts.PLAYERRULES, Material.GLASS, true,  "§7Mode spectateur"),
        ABSORPTION("§eAbsorption", ParamParts.PLAYERRULES, Material.GOLDEN_APPLE, 2.0, 1024.0, 00.0, new double[]{0.5, 1.0, 2.0, 4.0}),
        SCOREBOARD_LIFE("§dVie dans le Tab", ParamParts.PLAYERRULES, Material.APPLE, true, "§7Pourcentage de vie dans le tab"),

        INVINCIBILITY("§eInvincibilité", ParamParts.PvP, Material.DIAMOND_CHESTPLATE, 30, Integer.MAX_VALUE, 1, new int[]{1, 10, 30}),
        PVP("§cPvP", ParamParts.PvP, Material.IRON_SWORD, 1800, Integer.MAX_VALUE, 1, new int[]{60, 900, 1800}),

        CRAFT_GOLDEN_HEAD("§eGolden Head", ParamParts.CUSTOMCRAFTS, Material.GOLDEN_APPLE, false),
        DOUBLE_ARROW("§cDouble Arrow", ParamParts.CUSTOMCRAFTS, Material.ARROW, false),
        STRING("§fString", ParamParts.CUSTOMCRAFTS, Material.STRING, false),
        SADDLE("§6Selle", ParamParts.CUSTOMCRAFTS, Material.SADDLE, false),

        STRENGTH_NERF("§4Nerf de l'effet de Force", ParamParts.WORLDRULES, Material.POTION, 50.0, 100.0, 1.0, new double[]{1.0, 5.0, 10.0}),
        HORSE("§6Chevaux", ParamParts.WORLDRULES, Material.SADDLE, false),
        DAY_CYCLE("§9Cycle Jour/Nuit", ParamParts.WORLDRULES, Material.WATCH, false),
        TOWER("§7Towers", ParamParts.WORLDRULES, Material.DIRT, false),
        MILK("§fSeaux de lait", ParamParts.WORLDRULES, Material.MILK_BUCKET, true),
        NETHER("§cNether", ParamParts.WORLDRULES, Material.NETHERRACK, true, "§7Gère l'activation du Nether"),
        END("§3End", ParamParts.WORLDRULES, Material.ENDER_STONE, false, "§7Gère l'activation de l'End"),
        LAVA("§6Seaux de lave", ParamParts.WORLDRULES, Material.LAVA_BUCKET, true),
        FLINT_AND_STEEL("§7Briquets", ParamParts.WORLDRULES, Material.FLINT_AND_STEEL, true),
        LAVA$NOT_OVERWORLD("§6Seaux de lave §4hors Overworld", ParamParts.WORLDRULES, Material.NETHER_STALK, false),
        FLINT_AND_STEEL$NOT_OVERWORLD("§7Briquets §4hors Overworld", ParamParts.WORLDRULES, Material.NETHER_BRICK_STAIRS, false),
        BED$NOT_OVERWORLD("§cLits §4hors Overworld", ParamParts.WORLDRULES, Material.BED, false),
        QUARTZ_XP_NERF("§aNerf de l'XP du Quartz", ParamParts.WORLDRULES, Material.QUARTZ_ORE, false),

        APPLE("§cPommes", ParamParts.DROPS, Material.APPLE, 1.0, 4.0, 0.1, new double[]{0.1, 0.5, 1.0}),
        FLINT("§8Silex", ParamParts.DROPS, Material.FLINT, 10.0, 100.0, 1.0, new double[]{0.5, 1, 5, 15}),
        FEATHER("§fPlumes", ParamParts.DROPS, Material.FEATHER, 40.0, 100.0, 1.0, new double[]{0.5, 1, 5, 15}),
        LEATHER("§6Cuirs", ParamParts.DROPS, Material.LEATHER, 40.0, 100.0, 1.0, new double[]{0.5, 1, 5, 15}),

        BORDER_TIMER("§9Temps d'activation", ParamParts.BORDER, Material.WATCH, 3600, Integer.MAX_VALUE, 1, new int[]{60, 900, 1800}),
        BORDERSIZE("§3Taille initiale", ParamParts.BORDER, Material.FENCE_GATE, 2000.0, 5000.0, 2.0, new double[]{1.0, 50.0, 100.0, 500.0}),
        FINAL_BORDERSIZE("§bTaille finale", ParamParts.BORDER, Material.DARK_OAK_FENCE, 100.0, 5000.0, 2.0, new double[]{1.0, 5.0, 15.0}),
        BORDERSPEED("§1Vitesse de bordure", ParamParts.BORDER, Material.LEASH, 1.0, 50.0, 0.1, new double[]{0.1, 0.5, 1.0, 5.0}),

        EPISODS("§e§lActivation des Épisodes", ParamParts.EPISOD, Material.WOOL, (short)5, false, "§7Activation des épisodes"),
        EPISODS_TIMER("§eDurée des Épisodes", ParamParts.EPISOD, Material.WATCH, 1200, Integer.MAX_VALUE, 1, new int[]{60, 300, 900}),

        TEAMTYPE("§eTaille des équipes", ParamParts.TEAMS, Material.BANNER, "FFA"),
        SLOTS("§7Slots", ParamParts.TEAMS, Material.DIODE, Math.min(Bukkit.getServer().getMaxPlayers(), 50), Bukkit.getServer().getMaxPlayers(), 2, new int[]{1, 5, 10});

        ConfigurableParams(String name, ParamParts part, Material material, short matdata, Object value, String desc) {
            this.name = name;
            this.part = part;
            this.material = material;
            this.matdata = matdata;
            this.value = value;
            this.desc = desc;
        }

        ConfigurableParams(String name, ParamParts part, Material material, short matdata, Object value) {
            this.name = name;
            this.part = part;
            this.material = material;
            this.matdata = matdata;
            this.value = value;
            this.desc = "";
        }

        ConfigurableParams(String name, ParamParts part, Material material, Object value, String desc) {
            this.name = name;
            this.part = part;
            this.material = material;
            this.matdata = (short)0;
            this.value = value;
            this.desc = desc;
        }

        ConfigurableParams(String name, ParamParts part, Material material, int value, int max, int min, int[] differences) {
            this.name = name;
            this.part = part;
            this.material = material;
            this.matdata = (short)0;
            this.value = value;
            this.imax = max;
            this.imin = min;
            this.idifferences = differences;
            desc = "";
        }

        ConfigurableParams(String name, ParamParts part, Material material, double value, double max, double min, double[] differences) {
            this.name = name;
            this.part = part;
            this.material = material;
            this.matdata = (short)0;
            this.value = value;
            this.dmax = max;
            this.dmin = min;
            this.ddifferences = differences;
            desc = "";
        }

        ConfigurableParams(String name, ParamParts part, Material material, Object value) {
            this.name = name;
            this.part = part;
            this.material = material;
            this.matdata = (short)0;
            this.value = value;
            desc = "";
        }

        private final String name;
        private final String desc;
        private final Material material;
        private final short matdata;
        private int imax, imin;
        private double dmax, dmin;
        private int[] idifferences;
        private double[] ddifferences;
        private Object value;
        private final ParamParts part;

        public double[] getDoubleDifferences() {
            return ddifferences;
        }

        public double getDoubleMin() {
            return dmin;
        }

        public int getIntMin() {
            return imin;
        }

        public double getDoubleMax() {
            return dmax;
        }

        public int getIntMax() {
            return imax;
        }

        public int[] getIntDifferences() {
            return idifferences;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public String getVisibleValue() {
            if (value instanceof Integer) {
                if (this.isTimer()) {
                    int v = (int)value;
                    if (v != 0)
                        return "" + Index.getTimer((v));
                    else return getOFF();
                }
            } else if (value instanceof Boolean) return getStringBoolean(((boolean) value));
            if (this.getPart().equals(ParamParts.DROPS) || this.equals(STRENGTH_NERF)) return value.toString() + '%';
            return value.toString();
        }

        public String getDescription() {
            return desc;
        }

        public ParamParts getPart() {
            return part;
        }

        public Material getType() {
            return material;
        }

        public short getMatData() {
            return matdata;
        }

        public boolean isTimer() {
            return this.equals(BORDER_TIMER) || this.equals(PVP) || this.equals(INVINCIBILITY) || this.equals(EPISODS_TIMER);
        }

        public void setValue(Object value) {
            this.value = value;
        }


        public static ConfigurableParams getByName(String name) {
            for (ConfigurableParams cp : values())
                if (cp.getName().equals(name))
                    return cp;
            return null;
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

    public static String getTeamTypeString(int size, boolean random) {
        if (Scenarios.SLAVE_MARKET.isActivated()) return "SlaveMarket";
        if (size == 1) return "FFA";
        else
        if (random)
            return "Random To" + size;
        else
            return "To" + size;
    }

    public static int getTeamTypeInt(String teamtype) {
        if (teamtype.equals("FFA")) return 1;
        else
            return Integer.parseInt(teamtype.replace("Random", "").replace("To", ""));
    }
}
