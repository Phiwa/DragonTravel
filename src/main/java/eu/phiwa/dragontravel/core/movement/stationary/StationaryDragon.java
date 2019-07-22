package eu.phiwa.dragontravel.core.movement.stationary;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("DT-StatDragon")
public class StationaryDragon implements ConfigurationSerializable {

    private String displayName;
    private String owner;
    private String name;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private double yaw;
    private double pitch;

    private IRyeDragon dragon;

    public StationaryDragon(Map<String, Object> data) {
        this.x = (double) data.get("x");
        this.y = (double) data.get("y");
        this.z = (double) data.get("z");
        this.yaw = (double) data.get("yaw");
        this.pitch = (double) data.get("pitch");
        this.worldName = (String) data.get("world");
            this.displayName = (String) data.get("displayname");
        if (data.containsKey("owner")) {
            this.owner = (String) data.get("owner");
        } else {
            this.owner = "admin";
        }

        this.dragon = createDragon(false);
    }

    /**
     * Creates a stationary dragon
     */
    private IRyeDragon createDragon(boolean isNew) {
        final IRyeDragon dragon = DragonTravel.getInstance().getNmsHandler().getRyeDragon(toLocation());
        dragon.fixWings();
        dragon.setCustomDragonName(ChatColor.translateAlternateColorCodes('&', displayName));
        dragon.setCustomNameVisible(true);
        if (isNew)
            DragonTravel.getInstance().getDbStatDragonsHandler().createStatDragon(name, this);
        return dragon;
    }

    private Location toLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, (float) yaw, (float) pitch);
    }

    public StationaryDragon(Player player, String name, String displayName, Location loc, boolean isNew) {
        this(name, displayName, loc, player.getUniqueId().toString(), isNew);
    }

    public StationaryDragon(String name, String displayName, Location loc, String owner, boolean isNew) {
        this.owner = owner;
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.worldName = loc.getWorld().getName();
        this.name = name.toLowerCase();
        this.displayName = displayName;

        this.dragon = createDragon(isNew);
        DragonTravel.getInstance().getDragonManager().getStationaryDragons().put(name.toLowerCase(), this);
    }

    public void removeDragon(boolean isPermanent) {
        dragon.getEntity().remove();
        if (isPermanent)
            DragonTravel.getInstance().getDbStatDragonsHandler().deleteStatDragon(name);
        	DragonTravel.getInstance().getDragonManager().getStationaryDragons().remove(name);
    }

    public String toString() {
        return "\n" + "--- StatDragon ---" + '\n' + "Name: " + name + '\n' + "Display Name: " + displayName + '\n' + "Owner: " + owner + '\n' + "X: " + x + '\n' + "Y: " + y + '\n' + "Z: " + z + '\n' + "Yaw: " + yaw + '\n' + "Pitch: " + pitch + '\n' + "World: " + worldName + '\n' + "---------------" + '\n';
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("x", x);
        ret.put("y", y);
        ret.put("z", z);
        ret.put("yaw", yaw);
        ret.put("pitch", pitch);
        ret.put("world", worldName);
        ret.put("owner", owner);
        ret.put("displayname", displayName);
        return ret;
    }

    public IRyeDragon getDragon() {
        return dragon;
    }

    public void setDragon(IRyeDragon dragon) {
        this.dragon = dragon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }
}
