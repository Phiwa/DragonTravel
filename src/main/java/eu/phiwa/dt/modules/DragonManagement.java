package eu.phiwa.dt.modules;

import java.util.Map.Entry;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;
import eu.phiwa.dt.anticheatplugins.CheatProtectionHandler;
import net.minecraft.server.v1_7_R3.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class DragonManagement {

	/**
	 * Dismounts the given player from his dragon if he is a dragonrider and
	 * removes the dragon.
	 * 
	 * @param player Player to dismount from his dragon.
	 * @return
	 */
	public static void dismount(Player player, Boolean interworldtravel) {

		if (!DragonTravelMain.listofDragonriders.containsKey(player)) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NotMounted"));
			// TODO: ---ADD MESSAGE Not mounted
			return;
		}

		RyeDragon dragon = DragonTravelMain.listofDragonriders.get(player);
		if (interworldtravel)
			removeRiderandDragon(dragon.getEntity(), (Boolean) null);
		else
			removeRiderandDragon(dragon.getEntity(), false);
	}

	/**
	 * If player got dismounted because of water/SHIFT-clicking and hasn't
	 * been mounted again by the scheduler yet
	 * 
	 * @param entity Entity of the dragon the Player is sitting on
	 * @return Player riding the specified entity
	 */
	private static Player getRiderByEntity(Entity entity) {

		Player player = null;

		// If player got dismounted because of water/SHIFT-clicking
		// and hasn't been mounted again yet by the scheduler
		// => Get the player using the "scheduler-method"
		for (Entry<Player, RyeDragon> entry : DragonTravelMain.listofDragonriders.entrySet()) {
			if (entry.getValue().getEntity() == entity) {
				player = entry.getKey();
				break;
			}
		}

		return player;
	}

	/**
	 * Find a safe Location to dismount a player with specified X- and
	 * Z-value
	 * 
	 * @param loc Location to find a safe Y-value for
	 * @return Location with safe Y-value
	 */
	private static Location getSaveTeleportLocation(Location loc) {

		Location clone = loc;

		int offset = 1;

		for (;;) {

			while (clone.getBlock().isEmpty() && clone.getY() != 0) {
				clone.setY(clone.getY() - offset);
			}

			if (clone.getY() != 0)
				break;

			clone.setY(256);
		}

		return clone;
	}

	/**
	 * Spawns a dragon and places the given player on it. Afterwards it adds
	 * both, the player and his dragon, to the list of dragonriders.
	 * 
	 * @param player The player to mount onto the dragon
	 */
	public static boolean mount(Player player) {

		// Remove current dragon if the player is already mounted
		if (DragonTravelMain.listofDragonriders.containsKey(player)) {
			RyeDragon dragon = DragonTravelMain.listofDragonriders.get(player);
			removeRiderandDragon(dragon.getEntity(), true);
		}

		if (DragonTravelMain.listofDragonriders.size() >= DragonTravelMain.dragonLimit) {
			if (!player.hasPermission("dt.ignoredragonlimit")) {
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.ReachedDragonLimit"));
				return false;
			}
		}

		// Spawn RyeDragon
		World craftWorld = ((CraftWorld) player.getWorld()).getHandle();
		RyeDragon ryeDragon = new RyeDragon(player.getLocation(), craftWorld);
		craftWorld.addEntity(ryeDragon, SpawnReason.CUSTOM);
		LivingEntity dragon = (LivingEntity) ryeDragon.getEntity();

		CheatProtectionHandler.exemptPlayerFromCheatChecks(player);
		dragon.setPassenger(player);
		dragon.damage(2, dragon.getPassenger());
		DragonTravelMain.listofDragonriders.put(player, ryeDragon);
		DragonTravelMain.listofDragonsridersStartingpoints.put(player, player.getLocation());

		return true;
	}

	/**
	 * Removes all enderdragons in the specified world which do not have
	 * players as passengers
	 * 
	 * @param world World to delete all dragons from
	 * @return
	 */
	public static String removeDragons(org.bukkit.World world) {

		int passed = 0;

		for (Entity entity : world.getEntities()) {

			// Check if EnderDragon
			if (!(entity instanceof CraftEnderDragon))
				continue;

			// Check if EnderDragon has a player as passenger
			if (entity.getPassenger() instanceof Player)
				continue;

			// Remove entity/dragon
			entity.remove();
			passed++;
		}

		// TODO: ---ADD MESSAGE x dragons removed
		String returnMessage = String.format("Removed %d dragons in world ' %s' successfully.", passed, world.getName());
		return returnMessage;
	}

	/**
	 * Removes the given Dragon-Entity and its rider from the list of riders,
	 * teleports the rider to a safe location on the ground and removes the
	 * dragon from the world.
	 * 
	 * @param entity Entity to remove
	 * @param dismountAtcurrentLocation If set to true, the player is
	 *             dismounted at his current location. If set to false, he is
	 *             teleported back to the point he started his travel/flight
	 */
	public static void removeRiderandDragon(Entity entity, Boolean dismountAtcurrentLocation) {

		Player player = (Player) entity.getPassenger();

		if (player == null)
			player = getRiderByEntity(entity);

		DragonTravelMain.listofDragonriders.remove(player);

		// Interworld
		if (dismountAtcurrentLocation == null) {
			Location startLoc = DragonTravelMain.listofDragonsridersStartingpoints.get(player);
			entity.eject();
			entity.remove();
			player.teleport(startLoc);
			return;
		}
		// Normal absteigen
		else if (dismountAtcurrentLocation || !DragonTravelMain.config.getBoolean("TeleportToStartOnDismount")) {
			//TODO: Check if correct if-clause

			// Teleport player to a safe location
			Location saveTeleportLoc = getSaveTeleportLocation(player.getLocation());

			// Eject player and remove dragon from world
			entity.eject();
			entity.remove();

			// Teleport player to safe location
			saveTeleportLoc.setY(saveTeleportLoc.getY() + 1.2);
			player.teleport(saveTeleportLoc);
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.DismountedHere"));
		}
		// Zur�ck zum Start
		else {
			Location startLoc = DragonTravelMain.listofDragonsridersStartingpoints.get(player);
			entity.eject();
			entity.remove();
			player.teleport(startLoc);
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.DismountedToStart"));
		}

		CheatProtectionHandler.unexemptPlayerFromCheatChecks(player);
	}

	/**
	 * Removes the given Dragon-Entity and its rider from the list of riders,
	 * teleports the rider to a safe location on the ground below the
	 * specified location and removes the dragon from the world.
	 * 
	 * @param entity Entity to remove
	 * @param customDismountLoc A custom location to teleport the player to.
	 *             Player is teleported to a safe location on the groundbelow
	 *             this location.
	 */
	public static void removeRiderandDragon(Entity entity, Location customDismountLoc) {

		Player player = (Player) entity.getPassenger();

		// If player got dismounted because of water/SHIFT-clicking
		// and hasn't been mounted again yet by the scheduler
		// => Get the player using the "scheduler-method"
		if (player == null)
			player = getRiderByEntity(entity);

		DragonTravelMain.listofDragonriders.remove(player);
		entity.eject();
		entity.remove();

		player.teleport(customDismountLoc);
		CheatProtectionHandler.unexemptPlayerFromCheatChecks(player);
		return;
	}

}
