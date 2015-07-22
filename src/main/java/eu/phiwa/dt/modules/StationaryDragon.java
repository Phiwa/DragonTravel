package eu.phiwa.dt.modules;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StationaryDragon {

	/**
	 * Creates a stationary dragon
	 */
	public static void createStatDragon(Player player, String name, boolean isNew) {
		createStatDragon(player.getLocation(), name, isNew);
		player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.AddedStatDragon"));
	}

    /**
     * Creates a stationary dragon
     */
    public static void createStatDragon(Location loc, String name, boolean isNew) {
        World notchWorld = ((CraftWorld) loc.getWorld()).getHandle();
        final RyeDragon dragon = new RyeDragon(loc, notchWorld);
        notchWorld.addEntity(dragon);
        ((LivingEntity)dragon.getEntity()).damage(2);
        Bukkit.getScheduler().runTaskLater(DragonTravelMain.plugin, new Runnable() {
            @Override
            public void run() {
                ((LivingEntity) dragon.getEntity()).damage(2);
                ((LivingEntity) dragon.getEntity()).setHealth(dragon.getMaxHealth());
                ((LivingEntity) dragon.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false));
            }
        }, 2L);
        if(isNew)
            DragonTravelMain.dbStatDragonsHandler.createStatDragon(name.toLowerCase(), loc);
        DragonTravelMain.listofStatDragons.put(name.toLowerCase(), dragon);
    }

    public static void removeStatDragon(String name, boolean isPermanent){
        RyeDragon dragon = DragonTravelMain.listofStatDragons.get(name);
        dragon.getEntity().remove();
        if(isPermanent)
            DragonTravelMain.dbStatDragonsHandler.deleteStatDragon(name.toLowerCase());
    }
}
