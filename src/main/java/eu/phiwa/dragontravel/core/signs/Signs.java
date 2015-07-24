package eu.phiwa.dragontravel.core.signs;

import org.bukkit.ChatColor;
import org.bukkit.event.block.SignChangeEvent;


public class Signs {

	static ChatColor gold = ChatColor.GOLD;
	static ChatColor red = ChatColor.RED;
	static ChatColor white = ChatColor.WHITE;

	public static void createSign(SignChangeEvent event, String type) {
		event.setLine(0, gold + "DragonTravel");
		event.setLine(1, type);
		event.setLine(2, white + event.getLine(2));
		event.setLine(3, event.getLine(3));
	}
}
