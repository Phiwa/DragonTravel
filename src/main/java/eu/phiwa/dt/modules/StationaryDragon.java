package eu.phiwa.dt.modules;

import net.minecraft.server.v1_6_R3.World;

import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.Player;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;

public class StationaryDragon {

	/**
	 * Creates a stationary dragon
	 */
	public static void createStatDragon(Player player) {
		World notchWorld = ((CraftWorld) player.getWorld()).getHandle();
		RyeDragon dragon = new RyeDragon(player.getLocation(), notchWorld);
		notchWorld.addEntity(dragon);
		player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.AddedStatDragon"));
	}
}
