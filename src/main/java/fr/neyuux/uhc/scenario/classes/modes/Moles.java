package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.GameEndEvent;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.events.PluginReloadEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.TeamPrefix;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.teams.UHCTeamColors;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.bukkit.Material.*;

public class Moles extends Scenario implements Listener {
    public Moles() {
        super(Scenarios.MOLES, new ItemStack(Material.DIAMOND_SWORD));
    }

    public static int timer = 1200, molePerTeams = 1, moleTeams = 2;
    public static boolean hasApocalypse = false, superMoles = false;
    public static List<Kits> activatedKits = Kits.getActivatedKits();

    public static int nOfApoMoles = 20;
    public static boolean areSuperMolesTogether = false;

    public static HashMap<PlayerUHC, UHCTeam> taupes = new HashMap<>();
    public static HashMap<PlayerUHC, Kits> kits = new HashMap<>();
    public static HashMap<PlayerUHC, UHCTeam> superTaupes = new HashMap<>();
    public static ArrayList<PlayerUHC> alreadyUse = new ArrayList<>();
    public static ArrayList<PlayerUHC> alreadyReveal = new ArrayList<>();
    public static ArrayList<PlayerUHC> alreadySuperReveal = new ArrayList<>();
    public static boolean hasChoosedMoles = false, hasChoosedSuperMoles = false;
    public static final int[] IGtimers = {0, 0};

    @Override
    protected void activate() {
        if (!(boolean)GameConfig.ConfigurableParams.FRIENDLY_FIRE.getValue())
            UHC.sendHostMessage(UHC.getPrefix() + "§cVeuillez activer le Friendly Fire pour que le plugin " + scenario.getDisplayName() + " §cpuisse fonctionner.");
        if (GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA"))
            UHC.sendHostMessage(UHC.getPrefix() + "§cVeuillez ajouter des équipes pour que le plugin " + scenario.getDisplayName() + " §cpuisse fonctionner.");
        UHC.sendHostMessage(UHC.getPrefix() + "§eVous avez activé le mode de jeu Taupe Gun. Voici deux commandes pour modifier des options supplémentaires : ");
        UHC.sendHostMessage("§6§l - §a§l/uhc teamsupertaupe §e§l<on/off> §6: §ePermet de faire en sorte que les super taupes soient toutes dans une même équipe. §o(Par défaut : §c§oDésactivé§e§o)");
        UHC.sendHostMessage("§6§l - §a§l/uhc apotaupes §e§l<nombre> §6: §ePermet de modifier le nombre de taupes en mode Apocalypse. §o(Par défaut : §a§l§o20§e§o)");
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        IGtimers[0] = Moles.timer;
        IGtimers[1] = (int) (Moles.timer * 1.5) - Moles.timer;
        System.out.println(IGtimers[0] + " " + IGtimers[1]);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!hasChoosedMoles) {
                    switch (IGtimers[0]) {
                        case 900:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des taupes dans 15 minutes !");
                            break;
                        case 600:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des taupes dans 10 minutes !");
                            break;
                        case 300:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des taupes dans 5 minutes !");
                            break;
                        case 60:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des taupes dans 1 minute !");
                            break;
                        case 30:
                        case 10:
                        case 4:
                        case 3:
                        case 2:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des taupes dans " + IGtimers[0] + " secondes !");
                            break;
                        case 1:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des taupes dans 1 seconde !");
                            break;
                        case 0:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des taupes...");
                            giveMoles();

                            IGtimers[0] = Moles.timer;
                            hasChoosedMoles = true;
                            break;
                    }
                    IGtimers[0]--;
                }
                
                
                if (hasChoosedMoles && !hasChoosedSuperMoles && superMoles) {
                    switch (IGtimers[1]) {
                        case 900:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des §c§lSuper Taupes§e dans 15 minutes !");
                            break;
                        case 600:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des §c§lSuper Taupes§e dans 10 minutes !");
                            break;
                        case 300:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des §c§lSuper Taupes§e dans 5 minutes !");
                            break;
                        case 60:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des §c§lSuper Taupes§e dans 1 minute !");
                            break;
                        case 30:
                        case 10:
                        case 4:
                        case 3:
                        case 2:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des §c§lSuper Taupes§e dans " + IGtimers[1] + " secondes !");
                            break;
                        case 1:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des §c§lSuper Taupes§e dans 1 seconde !");
                            break;
                        case 0:
                            Bukkit.broadcastMessage(getPrefix() + "§eSélection des §c§lSuper Taupes§e...");
                            giveSuperMoles();

                            IGtimers[1] = (int) (Moles.timer * 1.5);
                            hasChoosedSuperMoles = true;
                            cancel();
                            break;
                    }
                    IGtimers[1]--;
                } else if (!superMoles && hasChoosedMoles) cancel();
                
                if (!UHC.getInstance().isState(Gstate.PLAYING)) cancel();
            }
        }.runTaskTimer(UHC.getInstance(), 0, 20);
    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA") &&
                (boolean) GameConfig.ConfigurableParams.FRIENDLY_FIRE.getValue();
    }


    @EventHandler
    public void removeCauseInDeathMessage(PlayerEliminationEvent ev) {
        System.out.println(ev.getDeathMessage());
        ev.setDeathMessage(ev.getPlayerUHC().getTeam().getTeam().getPrefix() + ev.getPlayerUHC().getPlayer().getName() + "§c est Mort.");
    }

    @EventHandler
    public void onRel(PluginReloadEvent ev) {
        nOfApoMoles = 20;
        alreadyUse.clear();
        alreadySuperReveal.clear();
        alreadyReveal.clear();
        hasChoosedMoles = false;
        hasChoosedSuperMoles = false;
        kits.clear();
        taupes.clear();
        superTaupes.clear();
        TeamPrefix.taupeTeams = 0;
    }

    @EventHandler
    public void onEndGame(GameEndEvent ev) {
        boolean noMoreMoles = true;
        boolean noMoreSuperMoles = true;
        for (PlayerUHC pu : taupes.keySet())
            if (pu.isAlive()) {
                noMoreMoles = false;
                break;
            }
        for (PlayerUHC pu : superTaupes.keySet())
            if (pu.isAlive()) {
                noMoreSuperMoles = false;
                break;
            }
        if (UHC.getInstance().getUHCTeamManager().getAliveTeams().size() == 1 && !noMoreMoles &&
            !UHC.getInstance().getUHCTeamManager().getAliveTeams().get(0).getPrefix().isTaupePrefix() &&
            !UHC.getInstance().getUHCTeamManager().getAliveTeams().get(0).getPrefix().isSuperTaupePrefix())
            ev.setCancelled(true);

        if (UHC.getInstance().getUHCTeamManager().getAliveTeams().size() == 1 && !noMoreSuperMoles &&
                !UHC.getInstance().getUHCTeamManager().getAliveTeams().get(0).getPrefix().isSuperTaupePrefix())
        ev.setCancelled(true);
    }


    public void giveMoles(){
        List<PlayerUHC> moles = new ArrayList<>();
        int teams = moleTeams;
        if (!hasApocalypse) {
            Random random = new Random();

            for (UHCTeam team : UHC.getInstance().getUHCTeamManager().getTeams())
                if (team.getAlivePlayers().size() > 0) {
                    List<PlayerUHC> players = new ArrayList<>();
                    for (PlayerUHC u : team.getPlayers()) {
                        if (!isTaupe(u) && !moles.contains(u))
                            players.add(u);
                    }
                    for (int i = 0; i < molePerTeams; i++) moles.add(players.remove(random.nextInt(players.size())));
                }

            while(teams != 0) {
                List<Kits> ks = new ArrayList<>(activatedKits);
                UHCTeam t = UHC.getInstance().getUHCTeamManager().createTeam(new TeamPrefix(UHC.getInstance(), UHCTeamColors.getTaupeNext(), "").toTaupePrefix());
                int maxplayers = BigDecimal.valueOf((double)moles.size() / teams).setScale(0, RoundingMode.UP).toBigInteger().intValue();
                while (maxplayers != 0) {
                    PlayerUHC pu = moles.remove(random.nextInt(moles.size()));
                    if (ks.size() == 0) ks.addAll(new ArrayList<>(activatedKits));
                    Kits kit = ks.remove(random.nextInt(ks.size()));
                    taupes.put(pu, t);
                    kits.put(pu, kit);
                    if (pu.getPlayer().isOnline()) {
                        Player p = pu.getPlayer().getPlayer();
                        UHC.sendTitle(p, "§c§lVous êtes une taupe !", "§6Vous faites partie de l'équipe " + t.getTeam().getDisplayName(), 5, 80, 5);
                        p.sendMessage("§8§l--------------------------------");
                        p.sendMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous êtes une taupe de l'équipe " + pu.getTeam().getTeam().getDisplayName() + " §c!");
                        p.sendMessage("§cDésormais, votre but est de gagner avec votre nouvelle équipe " + t.getTeam().getDisplayName() + "§c.");
                        p.sendMessage("§cVoici la liste des commandes à votre disposition : ");
                        p.sendMessage(" §8§l- §6§l/uhc reveal §8: §cVous permet de révéler votre véritable idendité en échange d'une pomme en or");
                        p.sendMessage(" §8§l- §6§l/uhc t <message> §8: §cVous permet de communiquer avec vos coéquipiers taupe");
                        p.sendMessage(" §8§l- §6§l/uhc claim §8: §cVous permet de recevoir le kit dont vous disposez pour vous aider à trahir");
                        p.sendMessage("§cVotre kit : " + kit.getName());
                        p.sendMessage("§8§l--------------------------------");
                    }
                    maxplayers--;
                }
                teams--;
            }
        } else {
            int nOfTaupes = nOfApoMoles;
            Random random = new Random();
            List<PlayerUHC> players = new ArrayList<>();

            for (PlayerUHC pu : UHC.getInstance().players)
                if (pu.getTeam() != null && !isTaupe(pu) && pu.isAlive()) players.add(pu);
            for (int i = 0; i < nOfTaupes; i++) {
                if (players.size() == 0)
                    break;
                moles.add(players.remove(random.nextInt(players.size())));
            }

            while(teams != 0) {
                List<Kits> ks = new ArrayList<>(activatedKits);
                UHCTeam t = UHC.getInstance().getUHCTeamManager().createTeam(new TeamPrefix(UHC.getInstance(), UHCTeamColors.getTaupeNext(), "").toTaupePrefix());
                int maxplayers = BigDecimal.valueOf((double)moles.size() / teams).setScale(0, RoundingMode.UP).toBigInteger().intValue();
                while (maxplayers != 0) {
                    PlayerUHC pu = moles.remove(random.nextInt(moles.size()));
                    if (ks.size() == 0) ks.addAll(new ArrayList<>(activatedKits));
                    Kits kit = ks.remove(random.nextInt(ks.size()));
                    taupes.put(pu, t);
                    kits.put(pu, kit);
                    if (pu.getPlayer().isOnline()) {
                        Player p = pu.getPlayer().getPlayer();
                        UHC.sendTitle(p, "§c§lVous êtes une taupe !", "§6Vous faites partie de l'équipe " + t.getTeam().getDisplayName(), 5, 80, 5);
                        p.sendMessage("§8§l--------------------------------");
                        p.sendMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous êtes une taupe de l'équipe " + pu.getTeam().getTeam().getDisplayName() + " §c!");
                        p.sendMessage("§cDésormais, votre but est de gagner avec votre nouvelle équipe " + t.getTeam().getDisplayName() + "§c.");
                        p.sendMessage("§cVoici la liste des commandes à votre disposition : ");
                        p.sendMessage(" §8§l- §6§l/uhc reveal §8: §cVous permet de révéler votre véritable idendité en échange d'une pomme en or");
                        p.sendMessage(" §8§l- §6§l/uhc t <message> §8: §cVous permet de communiquer avec vos coéquipiers taupe");
                        p.sendMessage(" §8§l- §6§l/uhc claim §8: §cVous permet de recevoir le kit dont vous disposez pour vous aider à trahir");
                        p.sendMessage("§cVotre kit : " + kit.getName());
                        p.sendMessage("§8§l--------------------------------");
                    }
                    maxplayers--;
                }
                teams--;
            }
        }
        Bukkit.broadcastMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §eLes taupes ont été annoncées !");
        for (PlayerUHC pu : UHC.getInstance().players)
            if (pu.getPlayer().isOnline())
                if (moles.contains(pu)) pu.getPlayer().getPlayer().playSound(pu.getPlayer().getPlayer().getLocation(), Sound.GHAST_SCREAM, 1, 1);
                else UHC.playNegativeSound(pu.getPlayer().getPlayer());
    }

    public void giveSuperMoles() {
        List<PlayerUHC> moles = new ArrayList<>();
        List<UHCTeam> teams = new ArrayList<>();

        for (Map.Entry<PlayerUHC, UHCTeam> en : taupes.entrySet()) if (!teams.contains(en.getValue())) teams.add(en.getValue());

        Random r = new Random();
        for (UHCTeam t : teams) {
            List<PlayerUHC> players = new ArrayList<>();
            for (Map.Entry<PlayerUHC, UHCTeam> en : taupes.entrySet()) if (en.getValue().equals(t)) players.add(en.getKey());
            moles.add(players.get(r.nextInt(players.size())));
        }

        if (areSuperMolesTogether) {
            UHCTeam t = UHC.getInstance().getUHCTeamManager().createTeam(new TeamPrefix(UHC.getInstance(), UHCTeamColors.WHITE, "").toSuperTaupePrefix(null));
            for (PlayerUHC playerUHC : moles) {
                superTaupes.put(playerUHC, t);
                if  (playerUHC.getPlayer().isOnline()) {
                    Player p = playerUHC.getPlayer().getPlayer();
                    UHC.sendTitle(p, "§c§lVous êtes une §f§lSuper Taupe §c§l!", "§6Vous faites partie de l'équipe " + t.getTeam().getDisplayName(), 5, 80, 5);
                    p.sendMessage("§8§l--------------------------------");
                    p.sendMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous êtes la super taupe de l'équipe " + taupes.get(playerUHC).getTeam().getDisplayName() + " §c!");
                    p.sendMessage("§cDésormais, votre but est de gagner avec votre nouvelle équipe " + t.getTeam().getDisplayName() + "§c.");
                    p.sendMessage("§cVoici la liste des commandes à votre disposition : ");
                    p.sendMessage(" §8§l- §6§l/uhc superreveal §8: §cVous permet de révéler votre véritable idendité en échange d'une pomme en or");
                    p.sendMessage(" §8§l- §6§l/uhc st <message> §8: §cVous permet de communiquer avec vos coéquipiers taupe");
                    p.sendMessage("§8§l--------------------------------");
                }
            }
        } else
            for (PlayerUHC playerUHC : moles) {
                UHCTeam t = UHC.getInstance().getUHCTeamManager().createTeam(new TeamPrefix(UHC.getInstance(), taupes.get(playerUHC).getPrefix().color, "").toSuperTaupePrefix(taupes.get(playerUHC)));
                superTaupes.put(playerUHC, t);
                if (playerUHC.getPlayer().isOnline()) {
                    Player p = playerUHC.getPlayer().getPlayer();
                    UHC.sendTitle(p, "§c§lVous êtes une §f§lSuper Taupe §c§l!", "§6Vous devez désormais gagner seul.", 5, 80, 5);
                    p.sendMessage("§8§l--------------------------------");
                    p.sendMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous êtes la super taupe de l'équipe " + taupes.get(playerUHC).getTeam().getDisplayName() + " §c!");
                    p.sendMessage("§cDésormais, votre but est de gagner seul.");
                    p.sendMessage("§cVoici la liste des commandes à votre disposition : ");
                    p.sendMessage(" §8§l- §6§l/uhc superreveal §8: §cVous permet de révéler votre véritable idendité en échange de 3 pommes en or");
                    p.sendMessage("§8§l--------------------------------");
                }
            }
    }

    public static boolean isTaupe(PlayerUHC p){
        return taupes.containsKey(p);
    }

    public static boolean isSuperTaupe(PlayerUHC pu){
        return superTaupes.containsKey(pu);
    }


    public enum Kits {

        AERIEN("§fAerien", ENDER_PEARL, Arrays.asList(
                new ItemStack(ENDER_PEARL, 4),
                new ItemsStack(ENCHANTED_BOOK).toItemStackWithEnchant(new AbstractMap.SimpleEntry<>(Enchantment.PROTECTION_FALL, 4)),
                new ItemStack(FEATHER, 8))),
        ALCHIMISTE("§eAlchimiste", BREWING_STAND_ITEM, Arrays.asList(
                getPotion(PotionEffectType.HARM, 1, 0, true, true, PotionType.INSTANT_DAMAGE),
                getPotion(PotionEffectType.POISON, 20, 0, true, true, PotionType.POISON),
                getPotion(PotionEffectType.WEAKNESS, 40, 0, true, true, PotionType.WEAKNESS),
                getPotion(PotionEffectType.SLOW, 40, 0, true, true, PotionType.SLOWNESS))),
        MINEUR("§7Mineur", DIAMOND_PICKAXE, Arrays.asList(
                new ItemsStack(DIAMOND_PICKAXE).toItemStackWithEnchant(new AbstractMap.SimpleEntry<>(Enchantment.DIG_SPEED, 3)),
                new ItemStack(EXP_BOTTLE, 10))),
        PYROMANE("§6Pyromane", BLAZE_POWDER, Arrays.asList(
                new ItemsStack(FLINT_AND_STEEL).toItemStack(),
                new ItemsStack(LAVA_BUCKET).toItemStack(),
                new ItemsStack(ENCHANTED_BOOK).toItemStackWithEnchant(
                        new AbstractMap.SimpleEntry<>(Enchantment.ARROW_FIRE, 1),
                        new AbstractMap.SimpleEntry<>(Enchantment.FIRE_ASPECT, 1)))),
        SUPPORT("§dSupport", GOLDEN_APPLE, Arrays.asList(
                getPotion(PotionEffectType.HEAL, 1, 0, true, true, PotionType.INSTANT_HEAL),
                getPotion(PotionEffectType.INCREASE_DAMAGE, 30, 0, true, false, PotionType.STRENGTH),
                getPotion(PotionEffectType.REGENERATION, 33, 0, true, true, PotionType.REGEN),
                getPotion(PotionEffectType.SPEED, 45, 0, true, true, PotionType.SPEED)));


        Kits(String name, Material material, List<ItemStack> kit) {
            this.name = name;
            this.kit = kit;
            this.material = material;
            this.isActivated = true;
        }

        private final String name;
        private final List<ItemStack> kit;
        private boolean isActivated;
        private final Material material;

        public String getName() {
            return name;
        }

        public Material getMaterial() {
            return material;
        }

        public List<ItemStack> getItems() {
            return kit;
        }

        public boolean isActivated() {
            return isActivated;
        }

        public void setActivated(boolean isActivated) {
            this.isActivated = isActivated;
            activatedKits = getActivatedKits();
        }


        public static List<Kits> getActivatedKits() {
            ArrayList<Kits> al = new ArrayList<>();
            for (Kits k : Kits.values())
                if (k.isActivated())
                    al.add(k);
            return al;
        }

        public static Kits getByName(String name) {
            for (Kits k : Kits.values())
                if (k.getName().equals(name))
                    return k;
            return null;
        }

        private static ItemStack getPotion(PotionEffectType pet, int duration, int amplifier, boolean particles, boolean isSplash, PotionType pt) {
            ItemStack it = new ItemStack(POTION);
            Potion pot = new Potion(pt);
            PotionMeta ptm = (PotionMeta) it.getItemMeta();
            ptm.clearCustomEffects();
            ptm.addCustomEffect(new PotionEffect(pet, duration * 20, amplifier, particles, particles), true);
            pot.setSplash(isSplash);
            it.setItemMeta(ptm);
            pot.apply(it);

            return it;
        }

    }
}
