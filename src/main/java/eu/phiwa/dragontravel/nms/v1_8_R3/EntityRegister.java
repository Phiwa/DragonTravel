package eu.phiwa.dragontravel.nms.v1_8_R3;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.nms.IEntityRegister;
import eu.phiwa.dragontravel.nms.IRyeDragon;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class EntityRegister implements IEntityRegister {

    @Override
    public boolean registerEntity() {
        try {
            Class<EntityTypes> entityTypeClass = EntityTypes.class;

            Field c = entityTypeClass.getDeclaredField("c");
            c.setAccessible(true);
            HashMap<String, Class<?>> c_map = (HashMap<String, Class<?>>) c.get(null);
            c_map.put("RyeDragon", RyeDragon.class);

            Field d = entityTypeClass.getDeclaredField("d");
            d.setAccessible(true);
            HashMap<Class<?>, String> d_map = (HashMap<Class<?>, String>) d.get(null);
            d_map.put(RyeDragon.class, "RyeDragon");

            Field e = entityTypeClass.getDeclaredField("e");
            e.setAccessible(true);
            HashMap<Integer, Class<?>> e_map = (HashMap<Integer, Class<?>>) e.get(null);
            e_map.put(Integer.valueOf(63), RyeDragon.class);

            Field f = entityTypeClass.getDeclaredField("f");
            f.setAccessible(true);
            HashMap<Class<?>, Integer> f_map = (HashMap<Class<?>, Integer>) f.get(null);
            f_map.put(RyeDragon.class, Integer.valueOf(63));

            Field g = entityTypeClass.getDeclaredField("g");
            g.setAccessible(true);
            HashMap<String, Integer> g_map = (HashMap<String, Integer>) g.get(null);
            g_map.put("RyeDragon", Integer.valueOf(63));

            return true;
        } catch (Exception e) {

            Class<?>[] paramTypes = new Class[]{Class.class, String.class, int.class};

            // MCPC+ compatibility
            // Forge Dev environment; names are not translated into func_foo
            try {
                Method method = EntityTypes.class.getDeclaredMethod("addMapping", paramTypes);
                method.setAccessible(true);
                method.invoke(null, RyeDragon.class, "RyeDragon", 63);
                return true;
            } catch (Exception ex) {
                e.addSuppressed(ex);
            }
            // Production environment: search for the method
            // This is required because the seargenames could change
            // LAST CHECKED FOR VERSION 1.6.4
            try {
                for (Method method : EntityTypes.class.getDeclaredMethods()) {
                    if (Arrays.equals(paramTypes, method.getParameterTypes())) {
                        method.invoke(null, RyeDragon.class, "RyeDragon", 63);
                        return true;
                    }
                }
            } catch (Exception ex) {
                e.addSuppressed(ex);
            }

            Bukkit.getLogger().info("[DragonTravel] [Error] Could not register the RyeDragon-entity!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(DragonTravelMain.getInstance());

        }
        return false;
    }

    @Override
    public void spawnEntity(Location loc, IRyeDragon dragon) {
        World notchWorld = ((CraftWorld) loc.getWorld()).getHandle();
        notchWorld.addEntity(((CraftEntity) dragon.getEntity()).getHandle());
    }
}
