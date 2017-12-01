package tk.rht0910.bungeebridge.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import tk.rht0910.bungeebridge.BungeeBridge;
import tk.rht0910.bungeebridge.providers.ConfigProvider;
import tk.rht0910.tomeito_core.utils.Log;

public class GuiOpen implements Runnable {
	public static Inventory gui = null;
	public static ItemStack is = new ItemStack(Material.INK_SACK, 1, (short) 10);
	public static ItemMeta im = is.getItemMeta();
	private static char altColorChar = '&';
	private static int setCount = 0;
	private static boolean run = true;
	Class<?> clazz = BungeeBridge.class;
	private Player player;

	public void getServers() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServers");
		((BungeeBridge) BungeeBridge.getProvidingPlugin(clazz)).send(player, out.toByteArray());
	}

	public void run() {
		player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		getServers();
		gui = Bukkit.createInventory(null, 9, BungeeBridge.guiTitle);
		for(int i=0; i< BungeeBridge.show.length; i++) {
			Log.info("Checking for " + BungeeBridge.show[i]);
			synchronized (this) {
				((BungeeBridge) BungeeBridge.getProvidingPlugin(clazz)).getIp(player, BungeeBridge.show[i]);
				for(String exc : BungeeBridge.exclude) {
					if(BungeeBridge.namea[i] == exc) {
						Log.info("Excluding " + exc);
						run = false;
						break;
					}
				}
				if(run) {
					Log.info("Server checking for " + BungeeBridge.show[i]);
					Log.info("Name Array: " + BungeeBridge.namea[i]);
					Log.info("IP Address: " + BungeeBridge.ipa[i]);
					Log.info("Port info: " + BungeeBridge.porta[i]);
					if(((BungeeBridge) BungeeBridge.getProvidingPlugin(clazz)).checkServer(BungeeBridge.ipa[i], BungeeBridge.porta[i])) {


						//Bukkit.broadcastMessage("Server is now online: " + BungeeBridge.show[i]);
						ConfigProvider.load(BungeeBridge.show[i], BungeeBridge.show[i]);
						im.setDisplayName(ChatColor.translateAlternateColorCodes(altColorChar, "&r&n" + BungeeBridge.show[i]));
						List<String> lore = new ArrayList<String>();
						lore.add(ChatColor.translateAlternateColorCodes(altColorChar, "&aOnline"));
						im.setLore(lore);
						is.setItemMeta(im);
						gui.setItem(setCount, is);
						setCount++;
					} else {
						Log.info("Offline: " + BungeeBridge.show[i]);
					}
				} else {
					Log.info("Run flag is false, skipping and Run flag changing to true");
					run = true;
				}
			}
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		player.openInventory(gui);
		setCount = 0;
	}
}
