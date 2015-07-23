package eu.phiwa.dt.modules;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

public class StationaryDragon {

	/**
	 * Creates a stationary dragon
	 */
	public static void createStatDragon(Player player, String name, String displayName, boolean isNew) {
		createStatDragon(player.getLocation(), name, displayName, isNew);
		player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.AddedStatDragon"));
	}

    /**
     * Creates a stationary dragon
     */
    public static void createStatDragon(Location loc, String name, String displayName, boolean isNew) {
        World notchWorld = ((CraftWorld) loc.getWorld()).getHandle();
        final RyeDragon dragon = new RyeDragon(loc, notchWorld);
        notchWorld.addEntity(dragon);
        dragon.fixWings();
        dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', displayName));
        dragon.setCustomNameVisible(true);
        if(isNew)
            DragonTravelMain.dbStatDragonsHandler.createStatDragon(name, displayName, loc);
        DragonTravelMain.listofStatDragons.put(name.toLowerCase(), dragon);
    }

    public static void removeStatDragon(String name, boolean isPermanent){
        RyeDragon dragon = DragonTravelMain.listofStatDragons.get(name);
        dragon.getEntity().remove();
        if(isPermanent)
            DragonTravelMain.dbStatDragonsHandler.deleteStatDragon(name);
    }
}
