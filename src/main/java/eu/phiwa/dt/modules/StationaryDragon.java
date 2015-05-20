package eu.phiwa.dt.modules;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

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
