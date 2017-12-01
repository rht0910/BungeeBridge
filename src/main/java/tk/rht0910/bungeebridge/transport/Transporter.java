package tk.rht0910.bungeebridge.transport;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Player;

import tk.rht0910.bungeebridge.BungeeBridge;
import tk.rht0910.tomeito_core.utils.Log;

public class Transporter {
	private static Player sender = null;
	private static String args = null;
	Class<?> clazz = BungeeBridge.class;

	public Transporter(Player player, String arg) {
		Transporter.sender = player;
		Transporter.args = arg;
	}

	public boolean jump() {
		String server = args;
		if(sender instanceof Player){

			Player p = (Player)sender;

			ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("ConnectOther");
                out.writeUTF(p.getName());
                out.writeUTF(server);
            } catch (IOException ex) {
                // Impossible
				Log.error("Stack trace:");
				ex.printStackTrace();
				Log.error("Caused by:");
				ex.getCause().printStackTrace();
            }

			p.sendPluginMessage(((BungeeBridge) BungeeBridge.getProvidingPlugin(clazz)), "BungeeCord", b.toByteArray());
			return true;
		}

		return false;
	}
}
