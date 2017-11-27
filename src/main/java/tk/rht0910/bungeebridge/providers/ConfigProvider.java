package tk.rht0910.bungeebridge.providers;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tk.rht0910.bungeebridge.BungeeBridge;

public abstract class ConfigProvider {
	public static Object load(String path, String def) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(BungeeBridge.getPlugin(BungeeBridge.class).getDataFolder(), "config.yml"));
		return config.get(path, def);
	}
}
