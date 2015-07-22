package eu.phiwa.dt.modules;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StationaryDragon {

	/**
	 * Creates a stationary dragon
	 */
	public static void createStatDragon(Player player) {
		World notchWorld = ((CraftWorld) player.getWorld()).getHandle();
		final RyeDragon dragon = new RyeDragon(player.getLocation(), notchWorld);
		notchWorld.addEntity(dragon);
        ((LivingEntity)dragon.getEntity()).damage(2);
		Bukkit.getScheduler().runTaskLater(DragonTravelMain.plugin, new Runnable() {
			@Override
			public void run() {
                ((LivingEntity)dragon.getEntity()).damage(2);
                ((LivingEntity)dragon.getEntity()).setHealth(dragon.getMaxHealth());
                ((LivingEntity)dragon.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false));
			}
		}, 2L);
		player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.AddedStatDragon"));
	}
}
