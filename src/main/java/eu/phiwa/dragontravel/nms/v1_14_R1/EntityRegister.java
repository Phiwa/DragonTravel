package eu.phiwa.dragontravel.nms.v1_14_R1;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IEntityRegister;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class EntityRegister implements IEntityRegister {
	private static CustomEntityRegistry ENTITY_REGISTRY;
	
	/*
	SOURCE :
	https://github.com/CitizensDev/Citizens2/blob/master/v1_15_R1/src/main/java/net/citizensnpcs/nms/v1_14_R1/util/NMSImpl.java
	*/
    public void registerEntityClass(Class<?> clazz) {
        if (ENTITY_REGISTRY == null)
            return;

        Class<?> search = clazz;
        while ((search = search.getSuperclass()) != null && Entity.class.isAssignableFrom(search)) {
            EntityTypes<?> type = ENTITY_REGISTRY.findType(search);
            MinecraftKey key = ENTITY_REGISTRY.getKey(type);
            if (key == null || type == null)
                continue;
            int code = ENTITY_REGISTRY.a(type);
            ENTITY_REGISTRY.put(code, key, type);
            return;
        }
        throw new IllegalArgumentException("unable to find valid entity superclass for class " + clazz.toString());
    }
	
	@Override
	public boolean registerEntity() {
		try {
			registerEntityClass( RyeDragon.class );
			return true;
		} catch (Exception e) {
			Bukkit.getLogger().info("[DragonTravel] [Error] Could not register the RyeDragon-entity!");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(DragonTravel.getInstance());
		}
		return false;
	}
}
