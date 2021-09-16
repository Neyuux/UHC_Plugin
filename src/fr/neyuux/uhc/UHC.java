package fr.neyuux.uhc;

import fr.neyuux.uhc.commands.*;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PluginReloadEvent;
import fr.neyuux.uhc.listeners.*;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.tasks.UHCStart;
import fr.neyuux.uhc.teams.TeamPrefix;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.teams.UHCTeamManager;
import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.util.ScoreboardSign;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class UHC extends JavaPlugin {

	public final List<PlayerUHC> players = new ArrayList<>();
	public final List<Player> spectators = new ArrayList<>();
	private Gstate state;
	public final Map<PlayerUHC, ScoreboardSign> boards = new HashMap<>();
	private InventoryManager InventoryManager = new InventoryManager();
	private UHCTeamManager uhcTeamManager = new UHCTeamManager(this);
	public Modes mode = Modes.UHC;
	public UHCStart uhcStart;
	public UHCWorld world = new UHCWorld(this);
	private GameConfig config;
	private static String prefix = Modes.UHC.getPrefix();
	public final HashMap<String, PermissionAttachment> permissions = new HashMap<>();
	private final List<OfflinePlayer> whitelist = new ArrayList<>();
	public boolean hasWhitelist;
	private static UHC instance;
	private static final HashMap<PlayerUHC, BukkitTask> infiniteActionBars = new HashMap<>();

	public static String getPrefixWithoutArrow() {
		return getPrefix().substring(0, getPrefix().length() - 8);
	}

	public static String getPrefix() {
		return prefix + "§8§l"+ Symbols.DOUBLE_ARROW+" §r";
	}

	public List<OfflinePlayer> getWhitelist() {
		return this.whitelist;
	}

	public static UHC getInstance() {
		return instance;
	}

	public void changeMode(Modes mode) {
		this.mode = mode;
		prefix = mode.getPrefix();
		this.world.delete();
		rel();
	}

	public Gstate getState() {
		return this.state;
	}

	public boolean isState(Gstate state) {
		return this.state.equals(state);
	}

	public void setState(Gstate state) {
		this.state = state;
	}


	@Override
	public void onEnable() {
		if (!System.getProperties().containsKey("RELOAD")) {
			Properties prop = new Properties(System.getProperties());
			prop.put("RELOAD", "FALSE");
		} else
			if (System.getProperty("RELOAD").equals("TRUE"))
				return;

		instance = this;
		this.config = new GameConfig(this, Modes.UHC);
		System.out.println("UHC enabling");

		File file = new File(getDataFolder(), "config.yml");
		YamlConfiguration yconfig = YamlConfiguration.loadConfiguration(file);
		try {
			yconfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setState(Gstate.WAITING);
		this.InventoryManager = new InventoryManager();
		getCommand("uhc").setExecutor(new CommandUHC(this));
		getCommand("revive").setExecutor(new CommandRevive(this));
		getCommand("heal").setExecutor(new CommandHeal(this));
		getCommand("finish").setExecutor(new CommandFinish(this));
		getCommand("enchant").setExecutor(new CommandEnchant(this));
		getCommand("scenario").setExecutor(new CommandScenario(this));
		getCommand("helpop").setExecutor(new CommandHelpOp(this));
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PreGameListener(this), this);
		pm.registerEvents(new WorldListener(this), this);
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new FightListener(this), this);
		pm.registerEvents(new ArmorListener(Collections.emptyList()), this);
		pm.registerEvents(this.config, this);

		reloadScoreboard();
		rel();

		super.onEnable();
	}

	@Override
	public void onDisable() {
		System.out.println("UHC disabling");

		this.world.delete();

		super.onDisable();
	}


	public PlayerUHC getPlayerUHC(OfflinePlayer player) {
		for (PlayerUHC pu : this.players)
			if (pu.getPlayer().getUniqueId().equals(player.getUniqueId()))
				return pu;

		return null;
	}

	public static ItemStack getGoldenHead(int amount) {
		return new ItemsStack(Material.GOLDEN_APPLE, amount, "§6Golden Head", "§7Donne §dRégénération 2 pendant 8 secondes", "§7et §e2 coeurs d'absorption").toItemStack();
	}

	public static ItemStack getSpecTear() {
		return new ItemsStack(Material.GHAST_TEAR, "§7§lDevenir Spectateur", "§7Permet de devenir spectateur", "§b>>Clique droit").toItemStack();
	}

	public InventoryManager getInventoryManager() {
		return this.InventoryManager;
	}

	public UHCTeamManager getUHCTeamManager() {
		return this.uhcTeamManager;
	}

	public GameConfig getGameConfig() {
		return this.config;
	}

	public List<PlayerUHC> getAlivePlayers() {
		List<PlayerUHC> list = new ArrayList<>();
		for (PlayerUHC pu : this.players)
			if (pu.isAlive() && pu.getPlayer().isOnline())
				list.add(pu);
		return list;
	}

	public void setLobbyScoreboard(Player player) {
		if (this.boards.containsKey(getPlayerUHC(player))) this.boards.get(getPlayerUHC(player)).destroy();
		ScoreboardSign ss = new ScoreboardSign(player, getPrefixWithoutArrow());
		ss.create();
		ss.setLine(0, player.getDisplayName());
		if (this.mode.equals(Modes.UHC)) {
			ss.setLine(1, "§0");
			ss.setLine(2, "§e§lÉquipe §e: " +(getPlayerUHC(player).getTeam() != null ? getPlayerUHC(player).getTeam().getTeam().getDisplayName() : "§cAucune"));
		}
		ss.setLine(3, "§4");
		ss.setLine(4, "§6§lJoueurs §6: §f" + Bukkit.getServer().getOnlinePlayers().size() + "§6/§e" + GameConfig.ConfigurableParams.SLOTS.getValue());
		ss.setLine(5, "§8------------");
		ss.setLine(6, "§5§oMap by §c§l§oNeyuux_");
		this.boards.put(getPlayerUHC(player), ss);
	}

	public void setGameScoreboard(Player player) {
		if (this.boards.containsKey(getPlayerUHC(player))) this.boards.get(getPlayerUHC(player)).destroy();
		ScoreboardSign ss = new ScoreboardSign(player, getPrefixWithoutArrow());
		PlayerUHC playerUHC = getPlayerUHC(player);
		ss.create();
		ss.setLine(0, player.getDisplayName());
		if (this.mode.equals(Modes.UHC)) {
			ss.setLine(1, "§0");
			ss.setLine(2, "§e§lÉquipe §e: " +(playerUHC.getTeam() != null ? playerUHC.getTeam().getTeam().getDisplayName() : "§cAucune"));
			if (GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA"))
				ss.setLine(3, "§7§lJoueurs §7: §f" + getAlivePlayers().size());
			else
				ss.setLine(3, "§7§lTeams : §f" + this.uhcTeamManager.getTeams().size() + "§8/§7" + UHCTeamManager.baseteams + " §8(§7" + getAlivePlayers().size() + "§8 joueurs)");
			ss.setLine(4, "§c§lKills §c: §l" + playerUHC.getKills() +(playerUHC.getTeam() != null ? " §4("+playerUHC.getTeam().getAlivePlayersKills()+")" : ""));
			ss.setLine(5, "§5");
			ss.setLine(6, "§6§lTimer §6: §e§l" + getTimer(0));
			ss.setLine(7, "§6§lPvP §6: §c§l" + getTimer((int)GameConfig.ConfigurableParams.PVP.getValue()));
			ss.setLine(8, "§6§lBordure §6: §3§l" + getTimer((int)GameConfig.ConfigurableParams.BORDER_TIMER.getValue()));
			ss.setLine(9, "§8");
			ss.setLine(10, "§b§lTaille de la bordure §b: " + Symbols.PLUS_MINUS + "§3" + "bordr" + "§b/§3" + "bordr");
			ss.setLine(11, "§9§lCentre : §6" + "centerdist" + " blocks " + "Arrowto00");
			ss.setLine(12, "§8------------");
			ss.setLine(13, "§5§oMap by §c§l§oNeyuux_");
		} else if (this.mode.equals(Modes.LG)) {
			ss.setLine(1, "§0");
			ss.setLine(2, "§9§lRôle §9: " + "roledisplayname");
			ss.setLine(3, "§7§lJoueurs §7: §f" + getAlivePlayers().size());
			ss.setLine(4, "§c§lKills §c: §l" + playerUHC.getKills());
			ss.setLine(5, "§5");
			ss.setLine(6, "§9Groupes de §6" + "groups");
			ss.setLine(7, "§8");
			ss.setLine(8, "§6§lTimer §6: §e§l" + getTimer(0));
			ss.setLine(9, "§6§lPvP §6:§c§l " + getTimer((int) GameConfig.ConfigurableParams.PVP.getValue()));
			ss.setLine(10, "§6§lBordure §6:§3§l " + getTimer((int) GameConfig.ConfigurableParams.BORDER_TIMER.getValue()));
			ss.setLine(11, "§5");
			ss.setLine(12, "§b§lTaille de la bordure §b: " + Symbols.PLUS_MINUS + "§3" + "bordr" + "§b/§3" + "bordr");
			ss.setLine(13, "§8------------");
			ss.setLine(14, "§5§oMap by §c§l§oNeyuux_");
		}
		this.boards.put(playerUHC, ss);
	}

	public void setKillsScoreboard(Player player) {
		if (this.boards.containsKey(getPlayerUHC(player))) this.boards.get(getPlayerUHC(player)).destroy();
		ScoreboardSign scoreboard = new ScoreboardSign(player, "§4§lKills");
		scoreboard.create();
		int limit = 0;

		Comparator<Map.Entry<PlayerUHC, Integer>> valueComparator = (e1, e2) -> {
			Integer v1 = e1.getValue();
			Integer v2 = e2.getValue();
			return v1.compareTo(v2);
		};
		List<Map.Entry<PlayerUHC, Integer>> listOfEntries = new ArrayList<>();
		for (PlayerUHC pu : UHCTeamManager.baseplayers)
			listOfEntries.add(new AbstractMap.SimpleEntry<>(pu, pu.getKills()));

		listOfEntries.sort(valueComparator.reversed());
		for(Map.Entry<PlayerUHC, Integer> en : listOfEntries) {
			if (limit <= 15) {
				String name;
				if (en.getKey().getTeam() != null)
					name = en.getKey().getTeam().getTeam().getPrefix() + en.getKey().getPlayer().getName() + en.getKey().getTeam().getTeam().getSuffix();
				else name = en.getKey().getPlayer().getName();
				if (en.getValue() != 0)scoreboard.setLine(limit, name + " §7: §e" + en.getValue());
			}
			limit++;
		}
		this.boards.put(getPlayerUHC(player), scoreboard);
	}

	public static int adaptInvSizeForInt(int toAdapt, int marge) {
		for (int size = 0; size <= 6; size++)
			if (size * 9 >= toAdapt + marge) return size * 9;
		return 54;
	}

	public static String getTimer(int seconds) {
		String valeur;
		boolean isOk = false;
		if (seconds == 0) return "0s";
		if (seconds % 60 > 9) {
			valeur = (seconds % 60) + "s";
		} else {
			valeur = "0" + (seconds % 60) + "s";
		}
		if (seconds / 3600 > 0) {
			if (seconds % 3600 / 60 > 9) {
				valeur = (seconds / 3600) + "h" + (seconds % 3600 / 60) + "m" + valeur;
			} else {
				valeur = (seconds / 3600) + "h0" + (seconds % 3600 / 60) + "m" + valeur;
			}
		} else if (seconds / 60 > 0) {
			valeur = (seconds / 60) + "m" + valeur;
		}
		while (!isOk) {
			if (valeur.endsWith("00m") || valeur.endsWith("00s")) {
				valeur = valeur.substring(0, valeur.length() - 3);
			} else isOk = true;
		}
		return valeur;
	}

	public static void playNegativeSound(Player player) {
		player.playSound(player.getLocation(), Sound.DOOR_CLOSE, 8, 2);
	}

	public static void playPositiveSound(Player player) {
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 8f, 1.8f);
	}

	public static void sendHostMessage(String msg) {
		for (PlayerUHC pu : getInstance().players) if (pu.isHost() && pu.getPlayer().isOnline())
			pu.getPlayer().getPlayer().sendMessage(msg);
	}

	public static String translatePotionEffect(PotionEffectType pet) {
		String s;
		String name = pet.getName();
		switch (name) {
			case "ABSORPTION":
				s = "Absorption";
				break;
			case "BLINDNESS":
				s = "Cécité";
				break;
			case "CONFUSION":
				s = "Nausée";
				break;
			case "DAMAGE_RESISTANCE":
				s = "Résistance";
				break;
			case "FAST_DIGGING":
				s = "Hâte";
				break;
			case "FIRE_RESISTANCE":
				s = "Résitance au Feu";
				break;
			case "HARM":
				s = "Dégâts instantanés";
				break;
			case "HEAL":
				s = "Soins instantanés";
				break;
			case "HEALTH_BOOST":
				s = "Bonus de Vie";
				break;
			case "HUNGER":
				s = "Faim";
				break;
			case "INCREASE_DAMAGE":
				s = "Force";
				break;
			case "INVISIBILITY":
				s = "Invisibilité";
				break;
			case "JUMP":
				s = "Bonus de Saut";
				break;
			case "NIGHT_VISION":
				s = "Vision Nocturne";
				break;
			case "POISON":
				s = "Poison";
				break;
			case "REGENERATION":
				s = "Régénération";
				break;
			case "SATURATION":
				s = "Saturation";
				break;
			case "SLOW":
				s = "Lenteur";
				break;
			case "SLOW_DIGGING":
				s = "Fatigue";
				break;
			case "SPEED":
				s = "Rapidité";
				break;
			case "WATER_BREATHING":
				s = "Apnée";
				break;
			case "WEAKNESS":
				s = "Faiblesse";
				break;
			case "WITHER":
				s = "Wither";
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + pet);
		}
		return s;
	}

	public void setHealth(Player player) {
		if(!(boolean)GameConfig.ConfigurableParams.SCOREBOARD_LIFE.getValue()) return;

		if(Scenarios.TEAM_HEALTH.isActivated() && getPlayerUHC(player).getTeam() != null) {
			UHCTeam team = getPlayerUHC(player).getTeam();

			for(PlayerUHC pu : team.getAlivePlayers()) {
				Bukkit.getScoreboardManager().getMainScoreboard().getObjective("health").getScore(pu.getPlayer().getName()).setScore((int)team.getHealth());
				Bukkit.getScoreboardManager().getMainScoreboard().getObjective("healthBelow").getScore(pu.getPlayer().getName()).setScore((int)team.getHealth());
			}
		}
	}

	public void setPlayerHost(Player player, boolean isHost) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		if (isHost) {
			if (!this.whitelist.contains(player)) this.whitelist.add(player);
			if (!this.config.hosts.contains(player.getUniqueId())) this.config.hosts.add(player.getUniqueId());
			if (!this.getPlayerUHC(player).isSpec()) {
				if (scoreboard.getTeam("Joueur").hasEntry(player.getName())) {
					scoreboard.getTeam("Host").addEntry(player.getName());
					player.setDisplayName(scoreboard.getEntryTeam(player.getName()).getPrefix() + player.getName());
					player.setPlayerListName(player.getDisplayName());
				}
			}
			if (this.getGameConfig().deathInvModifier != null && this.getGameConfig().deathInvModifier.equals(player))
				this.getGameConfig().deathInvModifier = null;
			if (this.getGameConfig().starterModifier != null && this.getGameConfig().starterModifier.equals(player))
				this.getGameConfig().starterModifier = null;
			if (this.isState(Gstate.WAITING) || this.isState(Gstate.STARTING))fr.neyuux.uhc.InventoryManager.giveWaitInventory(player);
		} else {
			this.config.hosts.remove(player.getUniqueId());
			if (!this.getPlayerUHC(player).isSpec()) {
				if (scoreboard.getTeam("Host").hasEntry(player.getName())) {
					scoreboard.getTeam("Joueur").addEntry(player.getName());
					player.setDisplayName(scoreboard.getTeam("Joueur").getPrefix() + player.getName() + "§r");
					player.setPlayerListName(player.getDisplayName());
				}
			}
			if (this.isState(Gstate.WAITING) || this.isState(Gstate.STARTING)) player.getInventory().remove(Material.REDSTONE_COMPARATOR);
			player.closeInventory();
		}
	}


	public void updatesGrades() {
		List<UUID> hosts = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers())  {
			if (p.getUniqueId().toString().equals("0234db8c-e6e5-45e5-8709-ea079fa575bb")) hosts.add(p.getUniqueId());

			if (p.getUniqueId().toString().equals("a9198cde-e7b0-407e-9b52-b17478e17f90")) hosts.add(p.getUniqueId());

			if (p.getUniqueId().toString().equals("290d1443-a362-4f79-b616-893bfb1361e5")) hosts.add(p.getUniqueId());

			if (p.getUniqueId().toString().equals("9a4d5447-13e0-43a3-87af-977ba87e77a7") || p.getUniqueId().toString().equals("cb067197-d121-4bfc-ac47-d6b4e40841b2"))
				hosts.add(p.getUniqueId());

			if (p.isOp() && !hosts.contains(p.getUniqueId()))
				hosts.add(p.getUniqueId());
		}

		for (Player p : Bukkit.getOnlinePlayers())
			Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(p.getName());
		for (UUID id : hosts)
			setPlayerHost(Bukkit.getPlayer(id), true);
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setDisplayName(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName()).getPrefix() + p.getName());
			p.setPlayerListName(p.getDisplayName());
		}
	}


	public void rel() {
		Bukkit.getPluginManager().callEvent(new PluginReloadEvent());
		Bukkit.getScheduler().cancelTasks(this);

		this.setState(Gstate.WAITING);
		this.players.clear();
		this.spectators.clear();
		GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(1, false));
		this.uhcTeamManager.clearTeams();
		this.boards.forEach((id, ss) -> ss.destroy());
		this.boards.clear();
		this.InventoryManager = new InventoryManager();
		this.uhcTeamManager = new UHCTeamManager(this);
		Scenario.removeEvents();
		HandlerList.unregisterAll(this.config);
		this.getServer().resetRecipes();
		this.config = new GameConfig(this, this.mode);
		this.world.changePVP(false);
		this.getServer().getPluginManager().registerEvents(this.config, this);
		UHCWorld.setAchievements(false);
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.hasPermission("uhc.*"))
				this.config.hosts.add(p.getUniqueId());

		for (Player p : Bukkit.getOnlinePlayers()) {
			this.players.add(new PlayerUHC(p, this));

			for (Player p2 : Bukkit.getOnlinePlayers())
				p.showPlayer(p2);
			p.teleport(world.getPlatformLoc());
			fr.neyuux.uhc.InventoryManager.clearInventory(p);
			p.getInventory().clear();
			p.updateInventory();
			p.setExp(0f);
			p.setLevel(0);
			p.setMaxHealth(20);
			p.setHealth(20);
			((CraftPlayer) p).getHandle().setAbsorptionHearts(0);
			for (PotionEffect pe : p.getActivePotionEffects()) {
				p.removePotionEffect(pe.getType());
			}
			PotionEffect saturation = new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, true, false);
			p.addPotionEffect(saturation);
			p.setDisplayName(p.getName());
			p.setPlayerListName(p.getName());
			p.setGameMode(GameMode.ADVENTURE);

			fr.neyuux.uhc.InventoryManager.giveWaitInventory(p);
			p.updateInventory();
			setLobbyScoreboard(p);
			UHC.setPlayerTabList(p, getPrefixWithoutArrow() + "\n" + "§fBienvenue sur la map de §c§lNeyuux_" + "\n", "\n" + "§fMerci à moi même.");

			try {
				Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(p.getName());
			} catch (NullPointerException e) {
				reloadScoreboard();
			} finally {
				Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(p.getName());
			}
		}
		updatesGrades();
	}


	public static void reloadScoreboard() {
		Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();
		for (Team t : s.getTeams())
				t.unregister();
		for (Objective ob : s.getObjectives())
			ob.unregister();

		s.registerNewTeam("Host");
		s.getTeam("Host").setPrefix(TeamPrefix.getHostPrefix());
		s.getTeam("Host").setSuffix("§r");
		s.registerNewTeam("Joueur");
		s.getTeam("Joueur").setSuffix("§r");

		s.registerNewObjective("health", "health");
		s.getObjective("health").setDisplayName("§4" + Symbols.LITTLE_HEART);
		s.registerNewObjective("healthBelow", "health");
		s.getObjective("healthBelow").setDisplayName("§4" + Symbols.LITTLE_HEART);

	}



	public static void setPlayerTabList(Player player,String header, String footer) {
		IChatBaseComponent tabTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
		IChatBaseComponent tabFoot = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(tabTitle);
		try {
			Field field = packet.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(packet, tabFoot);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sendPacket(player, packet);
		}
	}



	private static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Class<?> getNMSClass(String name) {
		try {
			return Class.forName("net.minecraft.server."
					+ Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
		try {
			Object chatTitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
					.invoke(null, "{\"text\": \"" + title + "\"}");
			Constructor<?> titleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(
					Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
					int.class, int.class, int.class);
			Object packet = titleConstructor.newInstance(
					Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
					fadeInTime, showTime, fadeOutTime);

			Object chatsTitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
					.invoke(null, "{\"text\": \"" + subtitle + "\"}");
			Constructor<?> timingTitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(
					Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
					int.class, int.class, int.class);
			Object timingPacket = timingTitleConstructor.newInstance(
					Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
					fadeInTime, showTime, fadeOutTime);

			sendPacket(player, packet);
			sendPacket(player, timingPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void sendActionBar(Player p, String message) {
		IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
		try {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
		} catch (NullPointerException e) {e.printStackTrace();}
	}


	public static void sendActionBarForAllPlayers(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendActionBar(p, message);
		}
	}

	public static void sendInfiniteActionBar(PlayerUHC pu, String message) {
		if (!pu.getPlayer().isOnline()) return;
		BukkitRunnable br = new BukkitRunnable() {
			@Override
			public void run() {
				sendActionBar(pu.getPlayer().getPlayer(), message);
			}
		};
		if (infiniteActionBars.containsKey(pu)) stopInfiniteActionBar(pu);
		infiniteActionBars.put(pu, br.runTaskTimer(UHC.getInstance(), 0, 5));
	}

	public static void sendInfiniteActionBarForAllPlayers(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) sendInfiniteActionBar(instance.getPlayerUHC(p), message);
	}

	public static void stopInfiniteActionBar(PlayerUHC pu) {
		if (infiniteActionBars.containsKey(pu)) {
			infiniteActionBars.get(pu).cancel();
			infiniteActionBars.remove(pu);
		}
	}

	public static void stopInfiniteActionBarForAllPlayers() {
		infiniteActionBars.values().forEach(BukkitTask::cancel);
		infiniteActionBars.clear();
	}


	public static void setF3(Player p, boolean isReducted) {
		try {
			Class<?> packetClass = getNMSClass("PacketPlayOutEntityStatus");
			Constructor<?> packetConstructor = packetClass.getConstructor(getNMSClass("Entity"), Byte.TYPE);
			Object packet;
			if (isReducted) packet = packetConstructor.newInstance(getHandle(p), (byte) 22);
			else packet = packetConstructor.newInstance(getHandle(p), (byte) 23);
			sendPacket(p, packet);
		} catch(Exception e) {
			Bukkit.broadcastMessage(getPrefix() + "§4[§cErreur§4]§c Une erreur s'est produite lors de la modification du F3 pour" + p.getName());
			e.printStackTrace();
		}
	}

	private static Object getHandle(Player player) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getHandle = player.getClass().getMethod("getHandle");
		return getHandle.invoke(player);
	}



	public enum Modes {

		LG("§4§lL§8§lG §9§lUHC"),
		UHC("§e§lUHC");

		Modes(String prefix) {
			this.prefix = prefix;
		}

		private final String prefix;

		public String getPrefix() {
			return prefix;
		}

	}

}


