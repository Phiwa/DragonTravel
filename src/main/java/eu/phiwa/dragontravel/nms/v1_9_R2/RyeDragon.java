package eu.phiwa.dragontravel.nms.v1_9_R2;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.core.movement.DragonType;
import eu.phiwa.dragontravel.core.movement.newmovement.DTMovement;
import net.minecraft.server.v1_9_R2.EntityEnderDragon;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class RyeDragon extends EntityEnderDragon implements IRyeDragon {

	private final int wingCoolDown = 10;

    private DragonType dragonType = DragonType.STATIONARY;
    
    private Player rider;
    
    // Source location
    private Location fromLoc;
    
    // Movement
    private DTMovement movement;
    private int currentMovementWaypointIndex;    

    private double xPerTick;
    private double yPerTick;
    private double zPerTick;

    public RyeDragon(Location loc) {
        this(loc, ((CraftWorld) loc.getWorld()).getHandle());        
    }

    public RyeDragon(Location loc, World notchWorld) {
        super(notchWorld);
        setPosition(loc.getX(), loc.getY(), loc.getZ());
        yaw = loc.getYaw() + 180;
        pitch = 0f;
        while (yaw > 360)
            yaw -= 360;
        while (yaw < 0)
            yaw += 360;
        notchWorld.addEntity(this);
    }

    public RyeDragon(World notchWorld) {
        super(notchWorld);
    }

    /**
     * This method is a natural method of the Enderdragon extended by the RyeDragon.
     * It's fired when the dragon moves and fires the movement()-method again to keep the dragon flying.
     */
    @Override
    public void m() {
    	if (getEntity() != null && rider != null) {
            if (getEntity().getPassenger() != null) {
                //getEntity().setPassenger(rider); //TODO: Reenable
            }
            rider.teleport(getEntity());
        }

        movement();
    }

    public void movement() {
    	
    	if ((int) locX != movement.getWaypoints().get(currentMovementWaypointIndex).getX())
            if (locX < movement.getWaypoints().get(currentMovementWaypointIndex).getX())
                locX += xPerTick;
            else
                locX -= xPerTick;
        if ((int) locY != movement.getWaypoints().get(currentMovementWaypointIndex).getY())
            if ((int) locY < movement.getWaypoints().get(currentMovementWaypointIndex).getY())
                locY += yPerTick;
            else
                locY -= yPerTick;
        if ((int) locZ != movement.getWaypoints().get(currentMovementWaypointIndex).getZ())
            if (locZ < movement.getWaypoints().get(currentMovementWaypointIndex).getZ())
                locZ += zPerTick;
            else
                locZ -= zPerTick;

        if ((Math.abs((int) locZ - movement.getWaypoints().get(currentMovementWaypointIndex).getZ()) <= 3)
          && Math.abs((int) locX - movement.getWaypoints().get(currentMovementWaypointIndex).getX()) <= 3
          && (Math.abs((int) locY - movement.getWaypoints().get(currentMovementWaypointIndex).getY()) <= 5)) {
            if (currentMovementWaypointIndex == movement.getWaypoints().size() - 1) {
                DragonTravel.getInstance().getDragonManager().removeRiderAndDragon(getEntity(), movement.getWaypoints().get(currentMovementWaypointIndex).getAsLocation());
                return;
            }

            this.currentMovementWaypointIndex++;

            this.fromLoc = getEntity().getLocation();

            if (!movement.getWaypoints().get(currentMovementWaypointIndex).getWorldName().equals(this.getEntity().getWorld().getName())) {
                this.teleportTo(movement.getWaypoints().get(currentMovementWaypointIndex).getAsLocation(), true);
                this.currentMovementWaypointIndex++;
            }

            setMovementMove();
        }
    }
        
    @Override
    public void setMovementMove() {
        double distX = fromLoc.getX() - movement.getWaypoints().get(currentMovementWaypointIndex).getX();
        double distY = fromLoc.getY() - movement.getWaypoints().get(currentMovementWaypointIndex).getY();
        double distZ = fromLoc.getZ() - movement.getWaypoints().get(currentMovementWaypointIndex).getZ();
        double tick = Math.sqrt((distX * distX) + (distY * distY)
                + (distZ * distZ)) / DragonTravel.getInstance().getConfigHandler().getSpeed();
        this.xPerTick = Math.abs(distX) / tick;
        this.yPerTick = Math.abs(distY) / tick;
        this.zPerTick = Math.abs(distZ) / tick;
    }
    
    @Override
    public void startMovement(DTMovement movement) {
        this.movement = movement;
        this.currentMovementWaypointIndex = 0;

        this.fromLoc = getEntity().getLocation();

        setMovementMove();
    }
    
    @Override
    public DragonType getDragonType() {
        return dragonType;
    }

    @Override
    public Entity getEntity() {
        if (bukkitEntity != null)
            return bukkitEntity;
        return null;
    }

	/*
    public double x_() {
		return 3;
	}
	 */

    public void fixWings() {
        if (rider != null)
            ((LivingEntity) getEntity()).damage(2, rider);
        Bukkit.getScheduler().runTaskLater(DragonTravel.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (dragonType.equals(DragonType.STATIONARY)) {
                    WingFixerTask wfTask = new WingFixerTask();
                    wfTask.setId(Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonTravel.getInstance(), wfTask, 1L, 21L));
                }
            }
        }, 1L);

    }

    public void setDragonType(DragonType dragonType) {
        this.dragonType = dragonType;
    }

    public int getWingCoolDown() {
        return wingCoolDown;
    }

    public Player getRider() {
        return rider;
    }

    public void setRider(Player rider) {
        this.rider = rider;
    }

    public double getxPerTick() {
        return xPerTick;
    }

    public void setxPerTick(double xPerTick) {
        this.xPerTick = xPerTick;
    }

    public double getyPerTick() {
        return yPerTick;
    }

    public void setyPerTick(double yPerTick) {
        this.yPerTick = yPerTick;
    }

    public double getzPerTick() {
        return zPerTick;
    }

    public void setzPerTick(double zPerTick) {
        this.zPerTick = zPerTick;
    }

    private class WingFixerTask implements Runnable {

        private int id;
        private int cooldown;

        public void setId(int id) {
            this.id = id;
            this.cooldown = wingCoolDown;
        }

        @Override
        public void run() {
            cooldown -= 1;
            if (cooldown <= 0)
                Bukkit.getScheduler().cancelTask(id);
            final Location loc = getEntity().getLocation().add(0, 2, 0);
            final Material m[] = new Material[15];
            final MaterialData md[] = new MaterialData[15];

            int counter = 0;
            for (int y = 0; y <= 2; y++) {
                for (int x = -1; x <= 1; x++) {
                    m[counter] = loc.clone().add(x, -y, 0).getBlock().getType();
                    md[counter] = loc.clone().add(x, -y, 0).getBlock().getState().getData();
                    loc.clone().add(x, -y, 0).getBlock().setType(Material.BARRIER);
                    counter++;
                }
                for (int z = -1; z <= 1; z++) {
                    if (z == 0) continue;
                    m[counter] = loc.clone().add(0, -y, z).getBlock().getType();
                    md[counter] = loc.clone().add(0, -y, z).getBlock().getState().getData();
                    loc.clone().add(0, -y, z).getBlock().setType(Material.BARRIER);
                    counter++;
                }
                if (y == 0) {
                    loc.getBlock().setType(Material.WATER);
                }
                if (y == 1) {
                    loc.clone().add(0, -1, 0).getBlock().setType(Material.AIR);
                }
            }

            Bukkit.getScheduler().runTaskLater(DragonTravel.getInstance(), new Runnable() {
                @Override
                public void run() {
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
                }
            }, 20L);
        }
    }

}
