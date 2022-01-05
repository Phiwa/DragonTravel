/*
This is just a copy/paste of NMS for v1_17_R1.
*/


package eu.phiwa.dragontravel.nms.v1_18_R1;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IEntityRegister;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class EntityRegister implements IEntityRegister {
    private static CustomEntityRegistry ENTITY_REGISTRY;
    private static final Map<Class<?>, EntityTypes<?>> DRAGONTRAVEL_ENTITY_TYPES = new HashMap<Class<?>, EntityTypes<?>>();

    public void registerEntityClass(Class<?> clazz) {
        if (ENTITY_REGISTRY == null)
            return;

        Class<?> search = clazz;
        while ((search = search.getSuperclass()) != null && Entity.class.isAssignableFrom(search)) {
            EntityTypes<?> type = ENTITY_REGISTRY.findType(search);
            MinecraftKey key = ENTITY_REGISTRY.getKey(type);
            if (key == null || type == null)
                continue;
            DRAGONTRAVEL_ENTITY_TYPES.put(clazz, type);
            int code = ENTITY_REGISTRY.a(type);
            ENTITY_REGISTRY.put(code, key, type);
            return;
        }
        throw new IllegalArgumentException("unable to find valid entity superclass for class " + clazz);
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
