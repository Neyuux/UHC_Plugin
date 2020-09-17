package fr.neyuux.uhc;

import fr.neyuux.uhc.commands.CommandEnchant;
import fr.neyuux.uhc.commands.*;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.listeners.UHCListener;
import fr.neyuux.uhc.teams.UHCTeamManager;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class Index extends JavaPlugin {

	private final static String prefix = "§e§lUHC";
	public final List<PlayerUHC> players = new ArrayList<PlayerUHC>();
	public final List<Player> spectators = new ArrayList<Player>();
	private Gstate state;
	public final Map<UUID, ScoreboardSign> boards = new HashMap<UUID, ScoreboardSign>();
	private StartInventoryManager startInventoryManager = new StartInventoryManager(this);
	private UHCTeamManager uhcTeamManager = new UHCTeamManager(this);
	private GameConfig config = new GameConfig(this);
	public static final HashMap<String, List<UUID>> Grades = new HashMap<String, List<UUID>>();
	public final HashMap<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();

	public final String getPrefix() {
		return prefix + "§8§l» §r";
	}

	public static final String getStaticPrefix() {
		return prefix;
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

		System.out.println("UHC enabling");
		setState(Gstate.WAITING);
		this.startInventoryManager = new StartInventoryManager(this);
		getCommand("uhc").setExecutor(new CommandUHC(this));
		getCommand("revive").setExecutor(new CommandRevive(this));
		getCommand("heal").setExecutor(new CommandHeal(this));
		getCommand("finish").setExecutor(new CommandFinish(this));
		getCommand("enchant").setExecutor(new CommandEnchant(this));
		getCommand("scenario").setExecutor(new CommandScenario(this));
		getCommand("helpop").setExecutor(new CommandHelpOp(this));
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new UHCListener(this), this);
		rel();

		reloadScoreboard();

		super.onEnable();
	}


	public PlayerUHC getPlayerUHC(OfflinePlayer player) {
		for (PlayerUHC pu : players)
			if (pu.getPlayer().equals(player.getUniqueId()))
				return pu;

		return null;
	}

	public StartInventoryManager getStartInventoryManager() {
		return startInventoryManager;
	}

	public UHCTeamManager getUHCTeamManager() {
		return uhcTeamManager;
	}

	public GameConfig getGameConfig() {
		return config;
	}

	public static ItemStack getItem(Material type, int amount, List<String> lore, String name, short durability) {
		ItemStack it = new ItemStack(type, amount, durability);
		ItemMeta itm = it.getItemMeta();
		itm.setDisplayName(name);
		itm.setLore(lore);
		it.setItemMeta(itm);

		return it;
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

	}


	public static Scoreboard reloadScoreboard() {
		Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();
		for (Team t : s.getTeams())
				t.unregister();
		for (Objective ob : s.getObjectives())
			ob.unregister();

		List<String> tc = new ArrayList<String>();
		tc.add("Rouge"); //F
		tc.add("Bleu");//C
		tc.add("Vert");//D
		tc.add("Rose");//E
		tc.add("Jaune");//G
		tc.add("Noir");//B
		tc.add("");

		if (!s.getTeams().isEmpty() && tc.size() != 7) {
			while (!s.getTeams().isEmpty() && tc.size() != 7) {
				System.out.println("slt");
			}
		} else {
			System.out.println(tc + " " + s.getTeams());
		}

		for (String st : tc) {
			String tabplace = "non";
			if (st.equalsIgnoreCase("Rouge")) tabplace = "F";
			if (st.equalsIgnoreCase("Bleu")) tabplace = "C";
			if (st.equalsIgnoreCase("Vert")) tabplace = "D";
			if (st.equalsIgnoreCase("Rose")) tabplace = "E";
			if (st.equalsIgnoreCase("Jaune")) tabplace = "G";
			if (st.equalsIgnoreCase("Noir")) tabplace = "B";
			if (st.equalsIgnoreCase("")) tabplace = "A";

			String color = "§f";
			if (st.equalsIgnoreCase("Rouge")) color = "§4";
			if (st.equalsIgnoreCase("Bleu")) color = "§1";
			if (st.equalsIgnoreCase("Vert")) color = "§a";
			if (st.equalsIgnoreCase("Rose")) color = "§d";
			if (st.equalsIgnoreCase("Jaune")) color = "§e";
			if (st.equalsIgnoreCase("Noir")) color = "§8";

			s.registerNewTeam(tabplace + "ADieu" + st);
			s.getTeam(tabplace + "ADieu" + st).setDisplayName("Dieu" + st);
			s.getTeam(tabplace + "ADieu" + st).setPrefix("§c§lDieu. " + color + "§l");
			s.getTeam(tabplace + "ADieu" + st).setSuffix("§d§k§laa§r");

			s.registerNewTeam(tabplace + "BDieuM" + st);
			s.getTeam(tabplace + "BDieuM" + st).setDisplayName("DieuM" + st);
			s.getTeam(tabplace + "BDieuM" + st).setPrefix("§5§lDieu. " + color + "§l");
			s.getTeam(tabplace + "BDieuM" + st).setSuffix("§6§k§laa§r");

			s.registerNewTeam(tabplace + "CDieuX" + st);
			s.getTeam(tabplace + "CDieuX" + st).setDisplayName("DieuX" + st);
			s.getTeam(tabplace + "CDieuX" + st).setPrefix("§6§lDieu. " + color + "§l");
			s.getTeam(tabplace + "CDieuX" + st).setSuffix("§5§k§laa§r");

			s.registerNewTeam(tabplace + "DDieuE" + st);
			s.getTeam(tabplace + "DDieuE" + st).setDisplayName("DieuE" + st);
			s.getTeam(tabplace + "DDieuE" + st).setPrefix("§3§lDieu. " + color + "§l");
			s.getTeam(tabplace + "DDieuE" + st).setSuffix("§0§k§laa§r");

			s.registerNewTeam(tabplace + "EDémon" + st);
			s.getTeam(tabplace + "EDémon" + st).setDisplayName("Démon" + st);
			s.getTeam(tabplace + "EDémon" + st).setPrefix("§b§lDémon. " + color + "§l");
			s.getTeam(tabplace + "EDémon" + st).setSuffix("§c§k§laa§r");

			s.registerNewTeam(tabplace + "FLeader" + st);
			s.getTeam(tabplace + "FLeader" + st).setDisplayName("Leader" + st);
			s.getTeam(tabplace + "FLeader" + st).setPrefix("§2Leader. " + color + "§l");
			s.getTeam(tabplace + "FLeader" + st).setSuffix("§0§kaa§r");

			if(!st.equalsIgnoreCase("")) {
				s.registerNewTeam(tabplace + "G" + color + st + "§r");
				s.getTeam(tabplace + "G" + color + st + "§r").setDisplayName(color + st + "§r");
				s.getTeam(tabplace + "G" + color + st + "§r").setPrefix(color + st + "§l ");
				s.getTeam(tabplace + "G" + color + st + "§r").setSuffix("§r");

			}
		}
		s.registerNewTeam("AGJoueur");
		s.getTeam("AGJoueur").setDisplayName("Joueur");
		s.registerNewObjective("§4♥", "health");

		Teams.reloadTeams();

		return s;
	}



	public static void setPlayerTabList(Player p,String abovelist, String underlist) {
		EntityPlayer pl = (((CraftPlayer)p).getHandle());
		PlayerConnection c = pl.playerConnection;
		IChatBaseComponent header = IChatBaseComponent.ChatSerializer.a("{'text': '" + abovelist+ "'}");
		IChatBaseComponent msg = IChatBaseComponent.ChatSerializer.a("{'text': '" + underlist + "'}");
		PacketPlayOutPlayerListHeaderFooter l = new PacketPlayOutPlayerListHeaderFooter(header);

		c.sendPacket(l);

		try {
			Field field = l.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(l, msg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.sendPacket(l);
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

}


