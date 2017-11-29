package tk.rht0910.bungeebridge;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import tk.rht0910.bungeebridge.providers.ConfigProvider;
import tk.rht0910.bungeebridge.providers.GuiProvider;
import tk.rht0910.tomeito_core.utils.Log;

public class BungeeBridge extends JavaPlugin implements PluginMessageListener, Listener {
	private char altColorChar = '&';
	public static String guiTitle = null;
	public static int count = 1;
	public static String[] exclude = null;
	public static Object[] excludeobj = null;
	public static String[] show = null;
	public static String ip = null;
	public static int port = 4;

	@Override
	public void onEnable() {
		Log.info("Loading configuration");
		try {
			guiTitle = ChatColor.translateAlternateColorCodes(altColorChar, (String) ConfigProvider.load("guiTitle", "&8|&a利用可能なサーバー|Available servers&8|").toString());
			excludeobj = this.getConfig().getList("exclude").toArray();
			exclude = Arrays.asList(excludeobj).toArray(new String[excludeobj.length]);
			BungeeBridge.this.getConfig().options().copyDefaults(true);
			BungeeBridge.this.saveConfig();
			Log.info("Initializing");
			Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
			Log.info("Scheduling tasks...");
			new BukkitRunnable() {
				@Override
				public void run() {
					BungeeBridge.getServers(Bukkit.getPlayer("notch"));
				}
			}.runTaskTimer(this, 1200, 600);
			//Log.info("Running tasks...");
			//Log.info(" - getServers");
			//BungeeBridge.getServers(Bukkit.getPlayer("notch"));
			Log.info("Registering events");
			Bukkit.getPluginManager().registerEvents(this, this);
			Log.info("Enabled");
		} catch(Throwable e) {
			Log.error("An error occurred. stacktrace dumped below:");
			e.printStackTrace();
			Log.error("Get cause:");
			e.getCause().printStackTrace();
			Log.error("The plugin is can't enabling, disabling.");
			Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("BungeeBridge"));
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				BungeeBridge.getServers((Player) sender);
				GuiProvider.open((Player)sender);
			}
		} else if(args[0].equalsIgnoreCase("registerevent")) {
			sender.sendMessage(ChatColor.GREEN + "イベントを登録しています... | Event registering...");
			Bukkit.getPluginManager().registerEvents(this, this);
			sender.sendMessage(ChatColor.GREEN + "イベントを登録しました。 | Event registered.");
			return true;
		}
		return true;
	}

	@SuppressWarnings({ "unused" })
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();

		if (subchannel.equals("PlayerCount")) { // Not used 'PlayerCount'
			String server = in.readUTF();  // Currently do not used
			int playerCount = in.readInt();

			if(playerCount <= 1) {
				count = 1;
			} else if(playerCount >= 64) {
				count = 64;
			} else {
				count = playerCount;
			}

			//player.sendMessage("Player count on server1 and server2 is equal to " + pc);
		} else if(subchannel.equals("GetServers")) {
			String[] servers = in.readUTF().split(", ");
			ArrayList<String> showservers = new ArrayList<String>();
			for(String i: servers) {
				for(String i2: exclude) {
					if(i != i2) {
						showservers.add(i);
						continue;
					} else {
						break;
					}
				}
			}
			Object[] showobj = showservers.toArray();
			show = Arrays.asList(showobj).toArray(new String[showobj.length]);
		} else if(subchannel.equals("ServerIP")) {
			String servername = in.readUTF(); // Never use, get only.
			ip = in.readUTF();
			port = in.readUnsignedShort();
		}
	}

	public void getCount(Player player, String server) {

		if (server == null) {
			server = "ALL";
		}

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerCount");
		out.writeUTF(server);

		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());

	}

	public static void getServers(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServers");
		player.sendPluginMessage(BungeeBridge.getProvidingPlugin(BungeeBridge.class), "BungeeCord", out.toByteArray());
	}

	public static void getIp(Player player, String srv) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ServerIP");
		out.writeUTF(srv);

		player.sendPluginMessage(BungeeBridge.getProvidingPlugin(BungeeBridge.class), "BungeeCord", out.toByteArray());
	}

	public static boolean checkServer(String ip, int port) {
		Socket s = new Socket();
		try {
			s.connect(new InetSocketAddress(ip, port), 100);
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			ItemStack i = (ItemStack) event.getItem();
			String name = i.getItemMeta().hasDisplayName() ? ( i.getItemMeta()).getDisplayName() : i.getType().toString().replace("_", " ").toLowerCase();
			if((i == new ItemStack(Material.COMPASS)) && (name == ChatColor.GREEN + "サーバー一覧")) {
				BungeeBridge.getServers((Player) event.getPlayer());
				GuiProvider.open((Player)event.getPlayer());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		ItemStack c = event.getCurrentItem();
		Inventory i = event.getInventory();
		if(i.getName().equals(GuiProvider.gui.getName())) {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			Bukkit.broadcastMessage(c.getItemMeta().getDisplayName());
			try {
				out.writeUTF("Connect");
				out.writeUTF(c.getItemMeta().getDisplayName().replace("§r§n", ""));
			} catch (IOException ex) {
				// Impossible
				Log.error(ChatColor.RED + "Catched impossible error. The plugin is may not be stable!");
				Log.error("Stack trace:");
				ex.printStackTrace();
				Log.error("Caused by:");
				ex.getCause().printStackTrace();
			}
			p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
			event.setCancelled(true);
			p.closeInventory();
		}
	}
}
