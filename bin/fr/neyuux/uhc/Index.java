package fr.neyuux.uhc;

import fr.neyuux.uhc.commands.*;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.listeners.UHCListener;
import fr.neyuux.uhc.teams.UHCTeamManager;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class Index extends JavaPlugin {

	public final List<PlayerUHC> players = new ArrayList<>();
	public final List<Player> spectators = new ArrayList<>();
	private Gstate state;
	public final Map<UUID, ScoreboardSign> boards = new HashMap<>();
	private InventoryManager InventoryManager = new InventoryManager(this);
	private UHCTeamManager uhcTeamManager = new UHCTeamManager(this);
	private Modes mode = Modes.UHC;
	private GameConfig config = new GameConfig(this, Modes.UHC);
	private static String prefix = Modes.UHC.getPrefix();
	public static final HashMap<String, List<UUID>> Grades = new HashMap<>();
	public final HashMap<String, PermissionAttachment> permissions = new HashMap<>();
	private static Index instance;

	public final String getPrefix() {
		return prefix + "§8§l» §r";
	}

	public HashMap<String, List<UUID>> getGrades() {
		return Grades;
	}

	public static String getStaticPrefix() {
		return prefix + ChatColor.translateAlternateColorCodes('&', "&8&l» &r");
	}

	public void changeMode(Modes mode) {
		this.mode = mode;
		prefix = mode.getPrefix();
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
		instance = this;
		if (!System.getProperties().containsKey("RELOAD")) {
			Properties prop = new Properties(System.getProperties());
			prop.put("RELOAD", "FALSE");
		} else
			if (System.getProperty("RELOAD").equals("TRUE"))
				return;

		System.out.println("UHC enabling");
		setState(Gstate.WAITING);
		this.InventoryManager = new InventoryManager(this);
		getCommand("uhc").setExecutor(new CommandUHC(this));
		getCommand("revive").setExecutor(new CommandRevive(this));
		getCommand("heal").setExecutor(new CommandHeal(this));
		getCommand("finish").setExecutor(new CommandFinish(this));
		getCommand("enchant").setExecutor(new CommandEnchant(this));
		getCommand("scenario").setExecutor(new CommandScenario(this));
		getCommand("helpop").setExecutor(new CommandHelpOp(this));
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new UHCListener(this), this);
		pm.registerEvents(config, this);
		rel();

		reloadScoreboard();

		super.onEnable();
	}


	public PlayerUHC getPlayerUHC(OfflinePlayer player) {
		for (PlayerUHC pu : players)
			if (pu.getPlayer().getUniqueId().equals(player.getUniqueId()))
				return pu;

		return null;
	}

	public static ItemStack getGoldenHead() {
		return new ItemsStack(Material.GOLDEN_APPLE, ChatColor.translateAlternateColorCodes('&', "&6Golden Head"), ChatColor.translateAlternateColorCodes('&', "&7Donne &dRégénération 2 pendant 8 secondes"), ChatColor.translateAlternateColorCodes('&', "&7et &e2 coeurs d'absorption")).toItemStack();
	}

	public static ItemStack getSpecTear() {
		return new ItemsStack(Material.GHAST_TEAR, ChatColor.translateAlternateColorCodes('&', "&7&lDevenir Spectateur"), ChatColor.translateAlternateColorCodes('&', "&7Permet de devenir spectateur"), ChatColor.translateAlternateColorCodes('&', "&b>>Clique droit")).toItemStack();
	}

	public InventoryManager getStartInventoryManager() {
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
			if (pu.isAlive())
				list.add(pu);
		return list;
	}

	public int adaptInvSizeFromPlayers() {
		for (int size = 0; size == 6; size++)
			if (size * 9 >= getAlivePlayers().size()) {
				return size * 9;
			}
		return 0;
	}

	public void setPlayerHost(Player player, boolean isHost) {
		if (isHost)
			config.hosts.add(player.getUniqueId());
		else
			config.hosts.remove(player.getUniqueId());
	}


	public static void updatesGrades() {
		List<UUID> Dieux = new ArrayList<>();
		List<UUID> DieuxM = new ArrayList<>();
		List<UUID> DieuxX = new ArrayList<>();
		List<UUID> DieuxE = new ArrayList<>();
		List<UUID> Leaders = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers())  {
			if (p.getUniqueId().toString().equals("0234db8c-e6e5-45e5-8709-ea079fa575bb")) Dieux.add(p.getUniqueId());

			if (p.getUniqueId().toString().equals("a9198cde-e7b0-407e-9b52-b17478e17f90")) DieuxM.add(p.getUniqueId());

			if (p.getUniqueId().toString().equals("290d1443-a362-4f79-b616-893bfb1361e5")) DieuxX.add(p.getUniqueId());

			if (p.getUniqueId().toString().equals("9a4d5447-13e0-43a3-87af-977ba87e77a7") || p.getUniqueId().toString().equals("cb067197-d121-4bfc-ac47-d6b4e40841b2"))
				DieuxE.add(p.getUniqueId());

			if (p.isOp() && !Dieux.contains(p.getUniqueId()) && !DieuxM.contains(p.getUniqueId()) && !DieuxX.contains(p.getUniqueId()) && !DieuxE.contains(p.getUniqueId()))
				Leaders.add(p.getUniqueId());
		}
		Grades.put("Dieu", Dieux);
		Grades.put("DieuM", DieuxM);
		Grades.put("DieuX", DieuxX);
		Grades.put("DieuE", DieuxE);
		Grades.put("Leader", Leaders);

		for (Player p : Bukkit.getOnlinePlayers())
			if (!p.hasPermission("uhc.*"))
				Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(p.getName());
			else
				Bukkit.getScoreboardManager().getMainScoreboard().getTeam("hostJoueur").addEntry(p.getName());
		for (Map.Entry<String, List<UUID>> en : Grades.entrySet()) {
			for (UUID id : en.getValue())
				if (!Bukkit.getPlayer(id).hasPermission("uhc.*"))
					Bukkit.getScoreboardManager().getMainScoreboard().getTeam(en.getKey()).addEntry(Bukkit.getPlayer(id).getName());
				else
					Bukkit.getScoreboardManager().getMainScoreboard().getTeam("host" + en.getKey()).addEntry(Bukkit.getPlayer(id).getName());
		}
	}


	public void rel() {
		Bukkit.getScheduler().cancelTasks(this);

		players.clear();
		spectators.clear();
		boards.forEach((id, ss) -> ss.destroy());
		boards.clear();
		InventoryManager = new InventoryManager(this);
		uhcTeamManager = new UHCTeamManager(this);
		config = new GameConfig(this, mode);
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.hasPermission("uhc.*"))
				config.hosts.add(p.getUniqueId());

		for (Player p : Bukkit.getOnlinePlayers()) {
			players.add(new PlayerUHC(p, this));

			for (Player p2 : Bukkit.getOnlinePlayers())
				p.showPlayer(p2);
			//p.teleport(new Location(Bukkit.getWorld("PvPKits"), -6.096, 5.1, -2.486, -89.6f, -0.5f));
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

		s.registerNewTeam("Dieu");
		s.getTeam("Dieu").setPrefix(ChatColor.translateAlternateColorCodes('&', "&c"));
		s.registerNewTeam("DieuM");
		s.getTeam("DieuM").setPrefix(ChatColor.translateAlternateColorCodes('&', "&5"));
		s.registerNewTeam("DieuX");
		s.getTeam("DieuX").setPrefix(ChatColor.translateAlternateColorCodes('&', "&6"));
		s.registerNewTeam("DieuE");
		s.getTeam("DieuE").setPrefix(ChatColor.translateAlternateColorCodes('&', "&3"));
		s.registerNewTeam("Leader");
		s.getTeam("Leader").setPrefix(ChatColor.translateAlternateColorCodes('&', "&2"));
		s.registerNewTeam("Joueur");
		for (Team t : s.getTeams()) {
			Team nht = s.registerNewTeam("host" + t.getName());
			nht.setPrefix(ChatColor.translateAlternateColorCodes('&', "&6[&fHost&6] "));
		}

		s.registerNewObjective("health", "health");
		s.getObjective("health").setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4♥"));

	}



	public static void setPlayerTabList(Player player,String header, String footer) {
			CraftPlayer cplayer = (CraftPlayer) player;
			PlayerConnection connection = cplayer.getHandle().playerConnection;

			IChatBaseComponent top = IChatBaseComponent.ChatSerializer.a("{text: '" + header + "'}");
			IChatBaseComponent bot = IChatBaseComponent.ChatSerializer.a("{text: '" + footer + "'}");

			PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

			try {
				Field headerField = packet.getClass().getDeclaredField("a");
				headerField.setAccessible(true);
				headerField.set(packet, top);
				headerField.setAccessible(!headerField.isAccessible());

			} catch (Exception e) {
				e.printStackTrace();
			}
			connection.sendPacket(packet);



			PacketPlayOutPlayerListHeaderFooter packet2 = new PacketPlayOutPlayerListHeaderFooter();

			try {
				Field headerField = packet2.getClass().getDeclaredField("a");
				headerField.setAccessible(true);
				headerField.set(packet2, bot);
				headerField.setAccessible(!headerField.isAccessible());

			} catch (Exception e) {
				e.printStackTrace();
			}
			connection.sendPacket(packet);
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

	private Class<?> getNMSClass(String name) {
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



	public enum Modes {

		LG("§4§lL§8§lG"),
		UHC("§e§lUHC");

		Modes(String prefix) {
			this.prefix = prefix;
		}

		private String prefix;

		public String getPrefix() {
			return prefix;
		}


		public void setMode() {
			instance.rel();
		}

	}

}


