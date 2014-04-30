package eu.phiwa.dt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_7_R1.EntityEnderDragon;
import net.minecraft.server.v1_7_R1.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import eu.phiwa.dt.flights.Waypoint;
import eu.phiwa.dt.modules.DragonManagement;

public class RyeDragon extends EntityEnderDragon {

	// Travel
	boolean isTravel = false;

	private double toX;
	private double toY;
	private double toZ;
	private org.bukkit.World toWorld;
	private int travelY;

	private Location destlocOtherworld;


	// Flight
	boolean isFlight = false;

	private List<Waypoint> waypoints = new ArrayList<Waypoint>();
	private Waypoint nextWaypoint;
	private int currentindexWaypoint = 0;
	private int numberOfWaypoints;

	private boolean finalmove = false;
	private boolean move = false;


	// Amount to fly up/down during a flight/travel
	private double XperTick;
	private double YperTick;
	private double ZperTick;

	// Distance to the right coords
	private double distanceX;
	private double distanceY;
	private double distanceZ;

	// Start points for tick calculation
	private double startX;
	private double startY;
	private double startZ;

	Location start;
	Location spawnOtherWorld;
	Entity entity;
	Player rider;


	public RyeDragon(Location loc, World notchWorld) {

		super(notchWorld);

		this.start = loc;
		setPosition(loc.getX(), loc.getY(), loc.getZ());
		yaw = loc.getYaw() + 180;
		while (yaw > 360)
			yaw -= 360;
		while (yaw < 0)
			yaw += 360;
		if (yaw < 45 || yaw > 315)
			yaw = 0F;
		else if (yaw < 135)
			yaw = 90F;
		else if (yaw < 225)
			yaw = 180F;
		else
			yaw = 270F;
	}

	public RyeDragon(World world) {
		super(world);
	}

	public Entity getEntity() {
		if (bukkitEntity != null)
			return bukkitEntity;
		else
			return entity;
	}


	/**
	 * Starts a travel to the specified location
	 *
	 * @param destinationLoc Location to start a travel to
	 */
	public void startTravel(Location destinationLoc, Boolean interworld) {

		if (interworld) {
			toX = locX + 20;
			toY = locY + 10;
			toZ = locZ + 20;
			toWorld = destinationLoc.getWorld();
			destlocOtherworld = destinationLoc.clone();

			this.startX = start.getX();
			this.startY = start.getY();
			this.startZ = start.getZ();

			travelY = (int) toY;

			this.yaw = getCorrectYaw(toX, toZ);
		} else {
			toX = destinationLoc.getBlockX();
			toY = destinationLoc.getBlockY();
			toZ = destinationLoc.getBlockZ();
			toWorld = destinationLoc.getWorld();

			this.startX = start.getX();
			this.startY = start.getY();
			this.startZ = start.getZ();

			travelY = DragonTravelMain.config.getInt("TravelHeight");
		}

		this.entity = getEntity();
		this.rider = (Player) entity.getPassenger();

		isTravel = true;
		move = true;

		setMoveTravel();
	}

	/**
	 * Sets the x,z move for each tick
	 */
	public void setMoveTravel() {

		this.distanceX = this.startX - toX;
		this.distanceY = this.startY - toY;
		this.distanceZ = this.startZ - toZ;

		double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / DragonTravelMain.speed;
		XperTick = Math.abs(distanceX) / tick;
		ZperTick = Math.abs(distanceZ) / tick;
	}

	/**
	 * Normal Travel
	 */
	public void travel() {

		// Returns if the dragon won't move
		if (!move)
			return;

		if (entity.getPassenger() == null)
			return;

		double myX = locX;
		double myY = locY;
		double myZ = locZ;

		if (finalmove) {

			// Flying down/up at the end
			if ((int) locY > (int) toY)
				myY -= DragonTravelMain.speed;
			else if ((int) locY < (int) toY)
				myY += DragonTravelMain.speed;

			// Removing entity
			else {

				// Interworld-travel teleport
				if (entity.getWorld().getName() != toWorld.getName()) {
					this.rider = (Player) entity.getPassenger();

					spawnOtherWorld = destlocOtherworld.clone();
					spawnOtherWorld.setX(destlocOtherworld.getX() + 80);
					spawnOtherWorld.setY(destlocOtherworld.getY() + 80);
					spawnOtherWorld.setZ(destlocOtherworld.getZ() + 80);
					spawnOtherWorld.getChunk().load();

					Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("DragonTravel"), new Runnable() {
						public void run() {
							DragonManagement.dismount(rider, true);
							rider.setFlying(true);
							if (spawnOtherWorld.getZ() < destlocOtherworld.getZ())
								spawnOtherWorld.setYaw((float) (-Math.toDegrees(Math.atan((spawnOtherWorld.getX() - destlocOtherworld.getX()) / (spawnOtherWorld.getZ() - destlocOtherworld.getZ())))));
							else if (spawnOtherWorld.getZ() > destlocOtherworld.getZ())
								spawnOtherWorld.setYaw((float) (-Math.toDegrees(Math.atan((spawnOtherWorld.getX() - destlocOtherworld.getX()) / (spawnOtherWorld.getZ() - destlocOtherworld.getZ())))) + 180.0F);
							rider.teleport(spawnOtherWorld);
							if (!DragonManagement.mount(rider))
								return;
							if (!DragonTravelMain.listofDragonriders.containsKey(rider))
								return;
							rider.setFlying(false);
							RyeDragon dragon = DragonTravelMain.listofDragonriders.get(rider);
							dragon.startTravel(destlocOtherworld, false);
							entity.remove();
						}
					}, 1L);

					// Dismount
				} else {
					DragonManagement.removeRiderandDragon(entity, true);
					return;
				}
			}

			setPosition(myX, myY, myZ);
			return;
		}

		// Getting the correct height
		if ((int) locY < travelY)
			myY += DragonTravelMain.speed;

		if (myX < toX)
			myX += XperTick;
		else
			myX -= XperTick;

		if (myZ < toZ)
			myZ += ZperTick;
		else
			myZ -= ZperTick;

		if ((int) myZ == (int) toZ && ((int) myX == (int) toX || (int) myX == (int) toX + 1 || (int) myX == (int) toX - 1)) {
			finalmove = true;
		}

		setPosition(myX, myY, myZ);
	}


	/**
	 * Starts the specified flight
	 *
	 * @param flight Flight to start
	 */
	public void startFlight(Flight flight) {

		this.entity = getEntity();
		this.waypoints = flight.waypoints;
		this.numberOfWaypoints = waypoints.size();
		this.nextWaypoint = waypoints.get(currentindexWaypoint);
		this.currentindexWaypoint++;

		this.startX = start.getX();
		this.startY = start.getY();
		this.startZ = start.getZ();

		this.move = true;
		this.isFlight = true;

		setMoveFlight();
	}

	/**
	 * Sets the x,y,z move for each tick
	 */
	public void setMoveFlight() {

		this.distanceX = this.startX - nextWaypoint.x;
		this.distanceY = this.startY - nextWaypoint.y;
		this.distanceZ = this.startZ - nextWaypoint.z;

		double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / DragonTravelMain.speed;

		this.XperTick = Math.abs(distanceX) / tick;
		this.YperTick = Math.abs(distanceY) / tick;
		this.ZperTick = Math.abs(distanceZ) / tick;
	}

	/**
	 * Controls the dragon
	 */
	public void flight() {

		// Returns, the dragon won't move
		if (!move)
			return;

		// Initialize variables for current coordinates
		// locX/loY/locZ are variables extended by EntityEnderDragons > LivingEntity > Entity
		double currentX = locX;
		double currentY = locY;
		double currentZ = locZ;


		if ((int) currentX != nextWaypoint.x) {
			if (currentX < nextWaypoint.x)
				currentX += XperTick;
			else
				currentX -= XperTick;
		}

		if ((int) currentY != nextWaypoint.y) {
			if ((int) currentY < nextWaypoint.y) {
				currentY += YperTick;
			} else {
				currentY -= YperTick;
			}
		}

		if ((int) currentZ != nextWaypoint.z) {
			if (currentZ < nextWaypoint.z)
				currentZ += ZperTick;
			else
				currentZ -= ZperTick;
		}


		/*
		 * >> Reached the last waypoint? << Is the next waypoint the last
		 * one? If yes, did the dragon already reach it? Removing the entity
		 * and dismounting the player
		 */


		/*
		 * >> Reached the next (and not last) waypoint? << The next waypoint
		 * is loaded and the dragon moves towards it
		 */
		if ((Math.abs((int) currentX - nextWaypoint.x) == 0 && Math.abs((int) currentZ - nextWaypoint.z) <= 3) || (Math.abs((int) currentZ - nextWaypoint.z) == 0 && Math.abs((int) currentX - nextWaypoint.x) <= 3) && (Math.abs((int) currentY - nextWaypoint.y) <= 5)) {


			if (currentindexWaypoint == numberOfWaypoints) {
				try {
					DragonManagement.removeRiderandDragon(entity, new Location(entity.getWorld(), nextWaypoint.x, nextWaypoint.y, nextWaypoint.z, ((Player) entity.getPassenger()).getLocation().getYaw(), ((Player) entity.getPassenger()).getLocation().getPitch()));
					return;

				} catch (NullPointerException ex) {
					DragonManagement.removeRiderandDragon(entity, new Location(entity.getWorld(), nextWaypoint.x, nextWaypoint.y, nextWaypoint.z));
					return;
				}
			}


			this.nextWaypoint = waypoints.get(currentindexWaypoint);
			this.currentindexWaypoint++;

			// Get the dragons position and set it as start-location for the flight to the next waypoint.
			this.startX = locX;
			this.startY = locY;
			this.startZ = locZ;

			this.yaw = getCorrectYaw(nextWaypoint.x, nextWaypoint.z);

			setMoveFlight();

			return;
		}

		setPosition(currentX, currentY, currentZ);
	}


	/**
	 * Gets the correct yaw for this specific path
	 */

	private float getCorrectYaw(double targetx, double targetz) {

		if (this.locZ > targetz)
			return (float) (-Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz))));
		else if (this.locZ < targetz)
			return (float) (-Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz)))) + 180.0F;
		else
			return this.yaw;
	}

	/**
	 * This method is a natural method of the Enderdragon extended by the
	 * RyeDragon. It's fired when the dragon moves and fires the
	 * travel-method again to keep the dragon flying.
	 *
	 */
	@Override
	public void e() {

		if (entity != null && rider != null)
			if (entity.getPassenger() != null)
				entity.setPassenger(rider);

		// Travel
		if (isTravel) {
			travel();
			return;
		}

		// Flight
		if (isFlight) {
			flight();
			return;
		}
	}
}
