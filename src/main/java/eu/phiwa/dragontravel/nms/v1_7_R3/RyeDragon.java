package eu.phiwa.dragontravel.nms.v1_7_R3;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.core.flights.Waypoint;
import eu.phiwa.dragontravel.core.modules.DragonManagement;
import eu.phiwa.dragontravel.core.objects.Flight;
import eu.phiwa.dragontravel.nms.IRyeDragon;
import net.minecraft.server.v1_7_R3.EntityEnderDragon;
import net.minecraft.server.v1_7_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class RyeDragon extends EntityEnderDragon implements IRyeDragon {

    int wingcooldown = 10;
    Entity dragonEntity;
    Player rider;
    // Flight
    boolean isFlight = false;
    String flightName;
    // Travel
    boolean isTravel = false;
    Location spawnOtherWorld;
    Location start;
    private int currentindexWaypoint = 0;
    private Location destlocOtherworld;
    // Distance to the right coords
    private double distanceX;
    private double distanceY;
    private double distanceZ;
    private boolean move = false;
    private boolean finalmove = false;
    private Waypoint nextWaypoint;
    private int numberOfWaypoints;
    // Start points for tick calculation
    private double startX;
    private double startY;
    private double startZ;
    private org.bukkit.World toWorld;
    private double toX;

    private double toY;
    private double toZ;
    private int travelY;

    private List<Waypoint> waypoints = new ArrayList<>();
    private double coveredDist;
    private double totalDist;

    // Amount to fly up/down during a flight/travel
    private double XperTick;
    private double YperTick;
    private double ZperTick;

    public RyeDragon(Location loc) {
        this(loc, ((org.bukkit.craftbukkit.v1_7_R3.CraftWorld) loc.getWorld()).getHandle());
    }

    public RyeDragon(Location loc, World notchWorld) {
        super(notchWorld);
        this.start = loc;
        setPosition(loc.getX(), loc.getY(), loc.getZ());
        yaw = loc.getYaw() + 180;
        while (yaw > 360)
            yaw -= 360;
        while (yaw < 0)
            yaw += 360;
        //if (yaw < 45 || yaw > 315)
        //	yaw = 0F;
        //else if (yaw < 135)
        //	yaw = 90F;
        //else if (yaw < 225)
        //	yaw = 180F;
        //else
        //	yaw = 270F;
        notchWorld.addEntity(this);
    }

    /**
     * This method is a natural method of the Enderdragon extended by the RyeDragon.
     * It's fired when the dragon moves and fires the travel-method again to keep the dragon flying.
     */
    @Override
    public void e() {
        if (dragonEntity != null && rider != null)
            if (dragonEntity.getPassenger() != null)
                dragonEntity.setPassenger(rider);

        // Travel
        if (isTravel) {
            travel();
        }
        // Flight
        else if (isFlight) {
            flight();
        }
    }

    /**
     * Starts the specified flight
     *
     * @param flight Flight to start
     */
    @Override
    public void startFlight(Flight flight) {
        this.dragonEntity = getEntity();
        this.flightName = flight.getName();
        this.waypoints = flight.getWaypoints();
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
     * Controls the dragon
     */
    @Override
    public void flight() {

        // Returns, the dragon won't move
        if (!move)
            return;

        // Initialize variables for current coordinates
        // locX/loY/locZ are variables extended by EntityEnderDragons > LivingEntity > Entity
        double currentX = locX;
        double currentY = locY;
        double currentZ = locZ;

        if ((int) currentX != nextWaypoint.getX()) {
            if (currentX < nextWaypoint.getX())
                currentX += XperTick;
            else
                currentX -= XperTick;
        }

        if ((int) currentY != nextWaypoint.getY()) {
            if ((int) currentY < nextWaypoint.getY()) {
                currentY += YperTick;
            } else {
                currentY -= YperTick;
            }
        }

        if ((int) currentZ != nextWaypoint.getZ()) {
            if (currentZ < nextWaypoint.getZ())
                currentZ += ZperTick;
            else
                currentZ -= ZperTick;
        }

        setCoveredDist(getCoveredDist() + Math.hypot(currentX, currentZ));
        if (coveredDist > totalDist) {
            coveredDist = totalDist;
        }
        ((LivingEntity) getEntity()).setHealth(60 * (coveredDist / totalDist));

		/*
           >> Reached the last waypoint? <<
		   Is the next waypoint the last one?
		   If yes, did the dragon already reach it?
		   Removing the entity and dismounting the player
		 */


		/*
		   >> Reached the next (and not last) waypoint? <<
		   The next waypoint is loaded and the dragon moves towards it
		 */
        if ((Math.abs((int) currentZ - nextWaypoint.getZ()) <= 3) && Math.abs((int) currentX - nextWaypoint.getX()) <= 3 && (Math.abs((int) currentY - nextWaypoint.getY()) <= 5)) {
            if (currentindexWaypoint == numberOfWaypoints) {
                try {
                    DragonManagement.removeRiderandDragon(dragonEntity,
                            new Location(Bukkit.getWorld(nextWaypoint.getWorldName()),
                                    nextWaypoint.getX(),
                                    nextWaypoint.getY(),
                                    nextWaypoint.getZ(),
                                    dragonEntity.getPassenger().getLocation().getYaw(),
                                    dragonEntity.getPassenger().getLocation().getPitch())
                    );
                    return;
                } catch (NullPointerException ex) {
                    DragonManagement.removeRiderandDragon(dragonEntity,
                            new Location(Bukkit.getWorld(nextWaypoint.getWorldName()),
                                    nextWaypoint.getX(),
                                    nextWaypoint.getY(),
                                    nextWaypoint.getZ())
                    );
                    return;
                }
            }


            this.nextWaypoint = waypoints.get(currentindexWaypoint);
            this.currentindexWaypoint++;

            // Get the dragons position and set it as start-location for the flight to the next waypoint.
            this.startX = locX;
            this.startY = locY;
            this.startZ = locZ;

            if (!this.nextWaypoint.getWorldName().equals(this.getEntity().getWorld().getName())) {
                this.teleportTo(this.nextWaypoint.getAsLocation(), true);
                this.currentindexWaypoint++;
                this.nextWaypoint = waypoints.get(currentindexWaypoint);
            }

            this.yaw = getCorrectYaw(nextWaypoint.getX(), nextWaypoint.getZ());
            this.pitch = getCorrectPitch(nextWaypoint.getX(), nextWaypoint.getZ(), nextWaypoint.getY());

            this.setPositionRotation(this.nextWaypoint.getX(), this.nextWaypoint.getY(), this.nextWaypoint.getZ(), yaw, pitch);

            setMoveFlight();
            return;
        }

        setPositionRotation(currentX, currentY, currentZ, yaw, pitch);
    }

    /**
     * Sets the x,y,z move for each tick
     */
    @Override
    public void setMoveFlight() {

        this.distanceX = this.startX - nextWaypoint.getX();
        this.distanceY = this.startY - nextWaypoint.getY();
        this.distanceZ = this.startZ - nextWaypoint.getZ();

        double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY)
                + (distanceZ * distanceZ)) / DragonTravelMain.getInstance().getConfigHandler().getSpeed();

        this.XperTick = Math.abs(distanceX) / tick;
        this.YperTick = Math.abs(distanceY) / tick;
        this.ZperTick = Math.abs(distanceZ) / tick;
    }

    /**
     * Starts a travel to the specified location
     *
     * @param destLoc Location to start a travel to
     */
    @Override
    public void startTravel(Location destLoc, boolean interworld) {
        if (interworld) {
            toX = locX + 5 + Math.random() * 200;
            toY = locY + 5 + Math.random() * 200;
            toZ = locZ + 5 + Math.random() * 200;
            destlocOtherworld = destLoc.clone();
            travelY = (int) toY;
        } else {
            toX = destLoc.getBlockX();
            toY = destLoc.getBlockY();
            toZ = destLoc.getBlockZ();
            travelY = DragonTravelMain.getInstance().getConfigHandler().getTravelHeight();
        }

        this.yaw = getCorrectYaw(toX, toZ);
        this.pitch = getCorrectPitch(toX, toZ, toY);
        this.startX = start.getX();
        this.startY = start.getY();
        this.startZ = start.getZ();
        toWorld = destLoc.getWorld();

        this.dragonEntity = getEntity();
        this.rider = (Player) dragonEntity.getPassenger();

        isTravel = true;
        move = true;

        setMoveTravel();
    }

    /**
     * Normal Travel
     */
    @Override
    public void travel() {

        // Returns if the dragon won't move
        if (!move)
            return;

        if (dragonEntity.getPassenger() == null)
            return;

        double myX = locX;
        double myY = locY;
        double myZ = locZ;

        if (finalmove) {

            // Flying down/up at the end
            if ((int) locY > (int) toY)
                myY -= DragonTravelMain.getInstance().getConfigHandler().getSpeed();
            else if ((int) locY < (int) toY)
                myY += DragonTravelMain.getInstance().getConfigHandler().getSpeed();

                // Removing entity
            else {

                // Interworld-travel teleport
                if (!dragonEntity.getWorld().getName().equals(toWorld.getName())) {
                    this.rider = (Player) dragonEntity.getPassenger();
                    double worldVariance = 80;
                    spawnOtherWorld = destlocOtherworld.clone();
                    spawnOtherWorld.setX(destlocOtherworld.getX() + worldVariance);
                    spawnOtherWorld.setY(destlocOtherworld.getY() + worldVariance);
                    spawnOtherWorld.setZ(destlocOtherworld.getZ() + worldVariance);
                    spawnOtherWorld.getChunk().load();

                    Bukkit.getScheduler().runTaskLater(DragonTravelMain.getInstance(), () -> {
                        DragonManagement.dismount(rider, true);

                        if (rider.getAllowFlight())
                            rider.setFlying(true);

                        if (spawnOtherWorld.getZ() < destlocOtherworld.getZ())
                            spawnOtherWorld.setYaw((float) (-Math.toDegrees(Math.atan((spawnOtherWorld.getX() - destlocOtherworld.getX()) / (spawnOtherWorld.getZ() - destlocOtherworld.getZ())))));
                        else if (spawnOtherWorld.getZ() > destlocOtherworld.getZ())
                            spawnOtherWorld.setYaw((float) (-Math.toDegrees(Math.atan((spawnOtherWorld.getX() - destlocOtherworld.getX()) / (spawnOtherWorld.getZ() - destlocOtherworld.getZ())))) + 180.0F);

                        rider.teleport(spawnOtherWorld);

                        if (!DragonManagement.mount(rider, false))
                            return;

                        if (!DragonTravelMain.listofDragonriders.containsKey(rider))
                            return;

                        rider.setFlying(false);
                        IRyeDragon dragon = DragonTravelMain.listofDragonriders.get(rider);
                        dragon.startTravel(destlocOtherworld, false);
                        dragonEntity.remove();
                    }, 1L);

                    // Dismount
                } else {
                    DragonManagement.removeRiderandDragon(dragonEntity, true);
                    return;
                }
            }
            this.yaw = getCorrectYaw(myX, myZ);
            this.pitch = getCorrectPitch(myX, myY, myZ);
            setPositionRotation(myX, myY, myZ, yaw, pitch);
            return;
        }

        // Getting the correct height
        if ((int) locY < travelY)
            myY += DragonTravelMain.getInstance().getConfigHandler().getSpeed();

        if (myX < toX)
            myX += XperTick;
        else
            myX -= XperTick;

        if (myZ < toZ)
            myZ += ZperTick;
        else
            myZ -= ZperTick;

        if ((int) myZ == (int) toZ && ((int) myX == (int) toX
                || (int) myX == (int) toX + 1 || (int) myX == (int) toX - 1)) {
            finalmove = true;
        }
        this.yaw = getCorrectYaw(myX, myZ);
        this.pitch = getCorrectPitch(myX, myY, myZ);
        setPositionRotation(myX, myY, myZ, yaw, pitch);
        coveredDist = Math.hypot(getEntity().getLocation().getBlockX() - start.getBlockX(), getEntity().getLocation().getBlockZ() - start.getBlockZ());
        if (coveredDist > totalDist) {
            coveredDist = totalDist;
        }
        ((LivingEntity) getEntity()).setHealth(60 * (coveredDist / totalDist));
    }

    /**
     * Sets the x,z move for each tick
     */
    @Override
    public void setMoveTravel() {

        this.distanceX = this.startX - toX;
        this.distanceY = this.startY - toY;
        this.distanceZ = this.startZ - toZ;

        double tick = Math.sqrt((distanceX * distanceX)
                        + (distanceY * distanceY)
                        + (distanceZ * distanceZ)
        ) / DragonTravelMain.getInstance().getConfigHandler().getSpeed();
        XperTick = Math.abs(distanceX) / tick;
        ZperTick = Math.abs(distanceZ) / tick;
    }

    public void fixWings() {
        if (isFlight || isTravel) {
            if (rider != null)
                ((LivingEntity) getEntity()).damage(2, rider);
            else return;
        }
        WingFixerTask wfTask = new WingFixerTask();
        wfTask.setId(Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonTravelMain.getInstance(), wfTask, 1L, 21L));
    }

    /**
     * Gets the correct yaw for this specific path
     */
    @Override
    public float getCorrectYaw(double targetx, double targetz) {

        if (this.locZ > targetz)
            return (float) (-Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz))));
        else if (this.locZ < targetz)
            return (float) (-Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz)))) + 180.0F;
        else
            return this.yaw;
    }

    /**
     * Gets the correct pitch for this specific path
     */
    @Override
    public float getCorrectPitch(double targetx, double targetz, double targety) {
        double distanceZ = this.locZ - targetz;
        double distanceX = this.locX - targetx;
        double distanceUp = this.locY - targety;
        float pitch = (float) -Math.toDegrees(Math.atan2(Math.sqrt(distanceZ * distanceZ + distanceX * distanceX), distanceUp) + Math.PI);
        if (pitch < -90)
            pitch = -90;
        else if (pitch > 90)
            pitch = 90;
        return pitch;
    }

    @Override
    public Entity getEntity() {
        if (bukkitEntity != null)
            return bukkitEntity;
        else
            return dragonEntity;
    }

    public double getCoveredDist() {
        return coveredDist;
    }

    public void setCoveredDist(double coveredDist) {
        this.coveredDist = coveredDist;
    }

    public double getTotalDist() {
        return totalDist;
    }

    public void setTotalDist(double totalDist) {
        this.totalDist = totalDist;
    }

	/*
	public double x_() {
		return 3;
	}
	 */

    private class WingFixerTask implements Runnable {

        private int id;

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            wingcooldown -= 1;
            if (wingcooldown <= 0)
                Bukkit.getScheduler().cancelTask(id);
            final Location loc = getEntity().getLocation().add(0, 2, 0);

            final Material m[] = new Material[15];
            final MaterialData md[] = new MaterialData[15];

            int counter = 0;
            for (int y = 0; y <= 2; y++) {
                for (int x = -1; x <= 1; x++) {
                    m[counter] = loc.clone().add(x, -y, 0).getBlock().getType();
                    md[counter] = loc.clone().add(x, -y, 0).getBlock().getState().getData();
                    loc.clone().add(x, -y, 0).getBlock().setType(Material.BEDROCK);
                    counter++;
                }
                for (int z = -1; z <= 1; z++) {
                    if (z == 0) continue;
                    m[counter] = loc.clone().add(0, -y, z).getBlock().getType();
                    md[counter] = loc.clone().add(0, -y, z).getBlock().getState().getData();
                    loc.clone().add(0, -y, z).getBlock().setType(Material.BEDROCK);
                    counter++;
                }
                if (y == 0) {
                    loc.getBlock().setType(Material.WATER);
                }
                if (y == 1) {
                    loc.clone().add(0, -1, 0).getBlock().setType(Material.AIR);
                }
            }

            Bukkit.getScheduler().runTaskLater(DragonTravelMain.getInstance(), () -> {
                int count = 0;
                for (int y = 0; y <= 2; y++) {
                    for (int x = -1; x <= 1; x++) {
                        loc.clone().add(x, -y, 0).getBlock().setType(m[count]);
                        loc.clone().add(x, -y, 0).getBlock().getState().setData(md[count]);
                        count++;
                    }
                    for (int z = -1; z <= 1; z++) {
                        if (z == 0) continue;
                        loc.clone().add(0, -y, z).getBlock().setType(m[count]);
                        loc.clone().add(0, -y, z).getBlock().getState().setData(md[count]);
                        count++;
                    }
                }
            }, 20L);
        }
    }
}
