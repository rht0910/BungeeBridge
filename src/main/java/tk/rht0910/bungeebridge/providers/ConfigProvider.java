package tk.rht0910.bungeebridge.providers;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class ConfigProvider {
	public static Object load(String path, String def) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("BungeeBridge").getDataFolder(), "config.yml"));
		return config.get(path, def);
	}
}
