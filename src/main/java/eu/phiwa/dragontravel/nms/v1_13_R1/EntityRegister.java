package eu.phiwa.dragontravel.nms.v1_13_R1;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IEntityRegister;
import org.bukkit.Bukkit;

public class EntityRegister implements IEntityRegister {

	@Override
	public boolean registerEntity() {
		try {
			CustomEntityRegistry.registerCustomEntity(63, "RyeDragon", RyeDragon.class);
			return true;
		} catch (Exception e) {
			Bukkit.getLogger().info("[DragonTravel] [Error] Could not register the RyeDragon-entity!");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(DragonTravel.getInstance());
		}
		return false;
	}
}
