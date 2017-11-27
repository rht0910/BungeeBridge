package tk.rht0910.bungeebridge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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
	public static String[] show = null;
	public static String ip = null;
	public static int port = 4;

	@Override
	public void onEnable() {
		Log.info("Loading configuration");
		guiTitle = ChatColor.translateAlternateColorCodes(altColorChar, (String) ConfigProvider.load("guiTitle", "&8|&a利用可能なサーバー|Available servers&8|"));
		exclude = (String[])this.getConfig().getList("exclude").toArray();
		BungeeBridge.this.getConfig().options().copyDefaults(true);
		BungeeBridge.this.saveConfig();
		Log.info("Initializing");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		Log.info("Scheduling tasks...");
		new BukkitRunnable() {
			@Override
			public void run() {
				BungeeBridge.this.getServers(null);
			}
		}.runTaskTimer(this, 1200, 600);
		Log.info("Enabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			GuiProvider.open((Player)sender);
		}
		return true;
	}

	@SuppressWarnings({ "null", "unused" })
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
			ArrayList<String> showservers = null;
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
			show = (String[])showservers.toArray();
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

	public void getServers(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServers");

		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
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
}
