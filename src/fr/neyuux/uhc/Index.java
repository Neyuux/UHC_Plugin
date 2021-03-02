package fr.neyuux.uhc;

import fr.neyuux.uhc.commands.*;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.listeners.ArmorListener;
import fr.neyuux.uhc.listeners.FightListener;
import fr.neyuux.uhc.listeners.PlayerListener;
import fr.neyuux.uhc.listeners.PreGameListener;
import fr.neyuux.uhc.listeners.WorldListener;
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
import net.minecraft.server.v1_8_R3.PlayerConnection;
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

public class Index extends JavaPlugin {

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
	private static String prefix = ChatColor.translateAlternateColorCodes('&', Modes.UHC.getPrefix());
	public final HashMap<String, PermissionAttachment> permissions = new HashMap<>();
	private static Index instance;

	public final String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.translateAlternateColorCodes('&', "&8&l"+Symbols.DOUBLE_ARROW+" &r");
	}

	public final String getPrefixWithoutArrow() {
		return getPrefix().substring(0, getPrefix().length() - 8);
	}

	public static String getStaticPrefix() {
		return ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.translateAlternateColorCodes('&', "&8&l"+ Symbols.DOUBLE_ARROW+" &r");
	}

	public static Index getInstance() {
		return instance;
	}

	public void changeMode(Modes mode) {
		this.mode = mode;
		prefix = mode.getPrefix();
		world.delete();
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
		config = new GameConfig(this, Modes.UHC);
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
		pm.registerEvents(config, this);

		reloadScoreboard();
		rel();

		super.onEnable();
	}

	@Override
	public void onDisable() {
		System.out.println("UHC disabling");

		world.delete();

		super.onDisable();
	}


	public PlayerUHC getPlayerUHC(OfflinePlayer player) {
		for (PlayerUHC pu : players)
			if (pu.getPlayer().getUniqueId().equals(player.getUniqueId()))
				return pu;

		return null;
	}

	public static ItemStack getGoldenHead(int amount) {
		return new ItemsStack(Material.GOLDEN_APPLE, amount, ChatColor.translateAlternateColorCodes('&', "&6Golden Head"), ChatColor.translateAlternateColorCodes('&', "&7Donne &dRégénération 2 pendant 8 secondes"), ChatColor.translateAlternateColorCodes('&', "&7et &e2 coeurs d'absorption")).toItemStack();
	}

	public static ItemStack getSpecTear() {
		return new ItemsStack(Material.GHAST_TEAR, ChatColor.translateAlternateColorCodes('&', "&7&lDevenir Spectateur"), ChatColor.translateAlternateColorCodes('&', "&7Permet de devenir spectateur"), ChatColor.translateAlternateColorCodes('&', "&b>>Clique droit")).toItemStack();
	}

	public InventoryManager getInventoryManager() {
		return InventoryManager;
	}

	public UHCTeamManager getUHCTeamManager() {
		return uhcTeamManager;
	}

	public GameConfig getGameConfig() {
		return config;
	}

	public List<PlayerUHC> getAlivePlayers() {
		List<PlayerUHC> list = new ArrayList<>();
		for (PlayerUHC pu : players)
			if (pu.isAlive() && pu.getPlayer().isOnline())
				list.add(pu);
		return list;
	}

	public void setLobbyScoreboard(Player player) {
		if (boards.containsKey(getPlayerUHC(player))) boards.get(getPlayerUHC(player)).destroy();
		ScoreboardSign ss = new ScoreboardSign(player, getPrefixWithoutArrow());
		ss.create();
		ss.setLine(0, player.getDisplayName());
		if (mode.equals(Modes.UHC)) {
			ss.setLine(1, "§0");
			ss.setLine(2, "§e§lÉquipe §e: " +(getPlayerUHC(player).getTeam() != null ? getPlayerUHC(player).getTeam().getTeam().getDisplayName() : "§cAucune"));
		}
		ss.setLine(3, "§4");
		ss.setLine(4, "§6§lSlots §6: §f" + Bukkit.getServer().getOnlinePlayers().size() + "§6/§e" + GameConfig.ConfigurableParams.SLOTS.getValue());
		ss.setLine(5, "§8------------");
		ss.setLine(6, "§5§oMap by §c§l§oNeyuux_");
		boards.put(getPlayerUHC(player), ss);
	}

	public void setGameScoreboard(Player player) {
		if (boards.containsKey(getPlayerUHC(player))) boards.get(getPlayerUHC(player)).destroy();
		ScoreboardSign ss = new ScoreboardSign(player, getPrefixWithoutArrow());
		ss.create();
		ss.setLine(0, player.getDisplayName());
		if (mode.equals(Modes.UHC)) {
			ss.setLine(1, "§0");
			ss.setLine(2, "§e§lÉquipe §e: " +(getPlayerUHC(player).getTeam() != null ? getPlayerUHC(player).getTeam().getTeam().getDisplayName() : "§cAucune"));
			if (GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA"))
				ss.setLine(3, "§7§lJoueurs §7: §f" + getAlivePlayers().size());
			else
				ss.setLine(3, "§7§lTeams : §f" + uhcTeamManager.getTeams().size() + "§8/§7" + UHCTeamManager.baseteams + " §8(§7" + getAlivePlayers().size() + "§8 joueurs)");
			ss.setLine(4, "§c§lKills §c: §l" + getPlayerUHC(player).getKills() +(getPlayerUHC(player).getTeam() != null ? " §4("+getPlayerUHC(player).getTeam().getAlivePlayersKills()+")" : ""));
			ss.setLine(5, "§5");
			ss.setLine(6, "§6§lTimer §6: §e§l" + getTimer(0));
			ss.setLine(7, "§6§lPvP §6: §c§l" + getTimer((int)GameConfig.ConfigurableParams.PVP.getValue()));
			ss.setLine(8, "§6§lBordure §6: §3§l" + getTimer((int)GameConfig.ConfigurableParams.BORDER_TIMER.getValue()));
			ss.setLine(9, "§8");
			ss.setLine(10, "§b§lTaille de la bordure §b: " + Symbols.PLUS_MINUS + "§3" + "bordr" + "§b/§3" + "bordr");
			ss.setLine(11, "§9§lCentre : §6" + "centerdist" + " blocks " + "Arrowto00");
			ss.setLine(12, "§8------------");
			ss.setLine(13, "§5§oMap by §c§l§oNeyuux_");
		} else if (mode.equals(Modes.LG)) {
			ss.setLine(1, "§0");
			ss.setLine(2, "§9§lRôle §9: " + "roledisplayname");
			ss.setLine(3, "§7§lJoueurs §7: §f" + getAlivePlayers().size());
			ss.setLine(4, "§c§lKills §c: §l" + getPlayerUHC(player).getKills());
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
		boards.put(getPlayerUHC(player), ss);
	}
	public void setKillsScoreboard(Player player) {
		if (boards.containsKey(getPlayerUHC(player))) boards.get(getPlayerUHC(player)).destroy();
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
				if (en.getKey().getTeam()!= null)
					name = en.getKey().getTeam().getTeam().getPrefix() + en.getKey().getPlayer().getName() + en.getKey().getTeam().getTeam().getSuffix();
				else name = en.getKey().getPlayer().getName();
				if (en.getValue() != 0)scoreboard.setLine(limit, name + " §7: §e" + en.getValue());
			}
			limit++;
		}
		boards.put(getPlayerUHC(player), scoreboard);
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

	public static String translatePotionEffect(PotionEffectType pet) {
		String s;
		switch (pet.getName()) {
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
			UHCTeam team = getPlayerUHC(player).getTeam();;

			for(PlayerUHC pu : team.getAlivePlayers()) {
				Bukkit.getScoreboardManager().getMainScoreboard().getObjective("health").getScore(pu.getPlayer().getName()).setScore((int)team.getHealth());
				Bukkit.getScoreboardManager().getMainScoreboard().getObjective("healthBelow").getScore(pu.getPlayer().getName()).setScore((int)team.getHealth());
			}
		}
	}

	public void setPlayerHost(Player player, boolean isHost) {
		if (isHost) {
			PermissionAttachment attachment = player.addAttachment(this);
			attachment.setPermission("uhc.*", true);
			permissions.put(player.getName(), attachment);
			if (!config.hosts.contains(player.getUniqueId())) config.hosts.add(player.getUniqueId());
			if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").hasEntry(player.getName()))
				Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Host").addEntry(player.getName());
			player.setDisplayName(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()).getPrefix() + player.getName());
			player.setPlayerListName(player.getDisplayName());
			fr.neyuux.uhc.InventoryManager.giveWaitInventory(getPlayerUHC(player));
		} else {
			config.hosts.remove(player.getUniqueId());
			if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Host").hasEntry(player.getName()))
				Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(player.getName());
			if (permissions.get(player.getName()) != null) {
				player.removeAttachment(permissions.get(player.getName()));
				permissions.remove(player.getName());
			}
			player.getInventory().remove(Material.REDSTONE_COMPARATOR);
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

			if (p.isOp() && !hosts.contains(p))
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
		Bukkit.getScheduler().cancelTasks(this);

		setState(Gstate.WAITING);
		players.clear();
		spectators.clear();
		GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(1, false));
		uhcTeamManager.clearTeams();
		boards.forEach((id, ss) -> ss.destroy());
		boards.clear();
		InventoryManager = new InventoryManager();
		uhcTeamManager = new UHCTeamManager(this);
		Scenario.removeEvents();
		HandlerList.unregisterAll(config);
		config = new GameConfig(this, mode);
		world.changePVP(false);
		getServer().getPluginManager().registerEvents(config, this);
		UHCWorld.setAchievements(false);
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.hasPermission("uhc.*"))
				config.hosts.add(p.getUniqueId());

		for (Player p : Bukkit.getOnlinePlayers()) {
			players.add(new PlayerUHC(p, this));

			for (Player p2 : Bukkit.getOnlinePlayers())
				p.showPlayer(p2);
			p.teleport(new Location(Bukkit.getWorld("Core"), -565, 23.2, 850));
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

			fr.neyuux.uhc.InventoryManager.giveWaitInventory(getPlayerUHC(p));
			p.updateInventory();
			setLobbyScoreboard(p);
			Index.setPlayerTabList(p, getPrefixWithoutArrow() + "\n" + "§fBienvenue sur la map de §c§lNeyuux_" + "\n", "\n" + "§fMerci à moi même.");

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
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		IChatBaseComponent tabTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
		IChatBaseComponent tabFoot = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
		PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(tabTitle);
		try {
			Field field = headerPacket.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(headerPacket, tabFoot);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.sendPacket(headerPacket);
		}
	}



	private void sendPacket(Player player, Object packet) {
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

	public void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
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


	public static void sendActionBarForAllPlayers(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendActionBar(p, message);
		}
	}


	public static void sendActionBar(Player p, String message) {
		IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
	}


	public static void setF3(Player p, boolean isReducted) {
		if (isReducted) {
			try {
				Class<?> packetClass = getNMSClass("PacketPlayOutEntityStatus");
				Constructor<?> packetConstructor = packetClass.getConstructor(getNMSClass("Entity"), Byte.TYPE);
				Object packet = packetConstructor.newInstance(getHandle(p), (byte) 22);
				Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
				sendPacket.invoke(getConnection(p), packet);
			} catch (Exception e) {
				Bukkit.broadcastMessage(getStaticPrefix() + "§4[§cErreur§4]§c Une erreur s'est produite lors de la modification du F3 pour" + p.getName());
				e.printStackTrace();
			}
		} else {
			try {
				Class<?> packetClass = getNMSClass("PacketPlayOutEntityStatus");
				Constructor<?> packetConstructor = packetClass.getConstructor(getNMSClass("Entity"), Byte.TYPE);
				Object packet = packetConstructor.newInstance(getHandle(p), (byte) 23);
				Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
				sendPacket.invoke(getConnection(p), packet);
			} catch (Exception e) {
				Bukkit.broadcastMessage(getStaticPrefix() + "§4[§cErreur§4]§c Une erreur s'est produite lors de la modification du F3 pour" + p.getName());
				e.printStackTrace();
			}
		}
	}

	private static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Field conField = getHandle(player).getClass().getField("playerConnection");
		return conField.get(getHandle(player));
	}

	private static Object getHandle(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getHandle = player.getClass().getMethod("getHandle");
		return getHandle.invoke(player);
	}



	public enum Modes {

		LG("&4&lL&8&lG &9&lUHC"),
		UHC("&e&lUHC");

		Modes(String prefix) {
			this.prefix = prefix;
		}

		private final String prefix;

		public String getPrefix() {
			return prefix;
		}

	}

}


