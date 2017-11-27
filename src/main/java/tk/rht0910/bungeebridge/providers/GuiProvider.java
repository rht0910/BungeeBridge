package tk.rht0910.bungeebridge.providers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tk.rht0910.bungeebridge.BungeeBridge;
import tk.rht0910.tomeito_core.utils.Log;

public abstract class GuiProvider {
	public static Inventory gui = null;
	public static ItemStack is = new ItemStack(Material.INK_SACK, 1, (short) 10);
	public static ItemMeta im = is.getItemMeta();
	private static char altColorChar = '&';

	@SuppressWarnings("null")
	public static boolean open(Player player) {
		try {
			gui = Bukkit.createInventory(null, 9, BungeeBridge.guiTitle);
			for(int i=0; i < BungeeBridge.show.length; i++) {
				BungeeBridge.getIp(player, BungeeBridge.show[1]);
				if(BungeeBridge.checkServer(BungeeBridge.ip, BungeeBridge.port)) {
					ConfigProvider.load(BungeeBridge.show[i], BungeeBridge.show[i]);
					im.setDisplayName(ChatColor.translateAlternateColorCodes(altColorChar, "&n" + BungeeBridge.show[i]));
					List<String> lore = null;
					lore.add(ChatColor.translateAlternateColorCodes(altColorChar, "&aOnline"));
					im.setLore(lore);
					is.setItemMeta(im);
					gui.setItem(i, is);
				}
			}
			player.openInventory(gui);
		} catch(Throwable e) {
			Log.error("An error occurred. Stack Trace dumped below:");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
