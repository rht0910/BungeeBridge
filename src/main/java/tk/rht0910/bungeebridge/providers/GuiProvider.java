package tk.rht0910.bungeebridge.providers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import tk.rht0910.bungeebridge.BungeeBridge;
import tk.rht0910.bungeebridge.thread.GuiOpen;
import tk.rht0910.tomeito_core.utils.Log;

public class GuiProvider {
	public static Inventory gui = null;
	public boolean open(Player player) {
		try {
			gui = Bukkit.createInventory(null, 9, BungeeBridge.guiTitle);
			GuiOpen go = new GuiOpen();
			go.run();
		} catch(Throwable e) {
			Log.error("An error occurred. Stack Trace dumped below:");
			e.printStackTrace();
			Log.error("Caused by:");
			e.getCause().printStackTrace();
			return false;
		}
		return true;
	}
}
