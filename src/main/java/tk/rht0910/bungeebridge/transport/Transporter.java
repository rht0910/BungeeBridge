package tk.rht0910.bungeebridge.transport;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.rht0910.bungeebridge.BungeeBridge;

public abstract class Transporter {
	private static CommandSender sender = null;
	private static String[] args = null;

	public Transporter(CommandSender sender, String[] args) {
		Transporter.sender = sender;
		Transporter.args = args;
	}

	public static boolean jump() {
		String server = args[0];
		if(sender instanceof Player){
			if(args.length == 0){
				return false;
			}

			Player p = (Player)sender;

			ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("ConnectOther");
                out.writeUTF(sender.getName());
                out.writeUTF(server);
            } catch (IOException ex) {
                // Impossible
            }

			p.sendPluginMessage(BungeeBridge.getPlugin(BungeeBridge.class), "BungeeCord", b.toByteArray());
			return true;
		}

		return false;
	}
}
