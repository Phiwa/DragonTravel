package eu.phiwa.dragontravel.core.modules;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
 
public class MobBarAPI
{
    private static Map< String , Dragon > dragonMap = new HashMap< String , Dragon >();
 
    public static void setStatus(final Player player, String text, int percent, boolean reset)
            throws IllegalArgumentException, SecurityException, InstantiationException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            NoSuchFieldException
    {
        Dragon dragon = null;
     
        if ( dragonMap.containsKey(player.getName()) && !reset )
        {
            dragon = dragonMap.get(player.getName());
        }
        else
        {
            dragon = new Dragon(text, player.getLocation().add(0 , -200 , 0), percent);
            Object mobPacket = dragon.getSpawnPacket();
            sendPacket(player , mobPacket);
            dragonMap.put(player.getName() , dragon);
        }
     
        if ( text == "" )
        {
            Object destroyPacket = dragon.getDestroyPacket();
            sendPacket(player , destroyPacket);
            dragonMap.remove(player.getName());
        }
        else
        {
            dragon.setName(text);
            dragon.setHealth(percent);
            Object metaPacket = dragon.getMetaPacket(dragon.getWatcher());
            Object teleportPacket = dragon
                    .getTeleportPacket(player.getLocation().add(0 , -200 , 0));
            sendPacket(player , metaPacket);
            sendPacket(player , teleportPacket);
        }
        
        Bukkit.getScheduler().runTaskLater(DragonTravelMain.getInstance(), new Runnable() {
        	
			@Override
			public void run() {
				Object destroyPacket;
				try {
					destroyPacket = dragonMap.get(player.getName()).getDestroyPacket();
		            sendPacket(player , destroyPacket);
		            dragonMap.remove(player.getName());
				} catch (IllegalArgumentException e) {
				} catch (SecurityException e) {
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				} catch (NoSuchMethodException e) {
				} catch (NoSuchFieldException e) {
				} catch (NullPointerException e) {
				}
			}
        	
        }, 120L);
    }
 
    private static void sendPacket(Player player, Object packet)
    {
        try
        {
            Object nmsPlayer = ReflectionUtils.getHandle(player);
            Field con_field = nmsPlayer.getClass().getField("playerConnection");
            Object con = con_field.get(nmsPlayer);
            Method packet_method = ReflectionUtils.getMethod(con.getClass() , "sendPacket");
            packet_method.invoke(con , packet);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
 
    private static class Dragon
    {
     
        private static final int MAX_HEALTH = 200;
        private int id;
        private int x;
        private int y;
        private int z;
        private int pitch = 0;
        private int yaw = 0;
        private byte xvel = 0;
        private byte yvel = 0;
        private byte zvel = 0;
        private float health;
        private boolean visible = false;
        private String name;
        private Object world;
     
        private Object dragon;
     
        public Dragon( String name , Location loc , int percent )
        {
            this.name = name;
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
            this.health = percent / 100F * MAX_HEALTH;
            this.world = ReflectionUtils.getHandle(loc.getWorld());
        }
     
        public void setHealth(int percent)
        {
            this.health = percent / 100F * MAX_HEALTH;
        }
     
        public void setName(String name)
        {
            this.name = name;
        }
     
        public Object getSpawnPacket() throws IllegalArgumentException, SecurityException,
                InstantiationException, IllegalAccessException, InvocationTargetException,
                NoSuchMethodException
        {
            Class< ? > Entity = ReflectionUtils.getCraftClass("Entity");
            Class< ? > EntityLiving = ReflectionUtils.getCraftClass("EntityLiving");
            Class< ? > EntityEnderDragon = ReflectionUtils.getCraftClass("EntityEnderDragon");
            dragon = EntityEnderDragon.getConstructor(ReflectionUtils.getCraftClass("World"))
                    .newInstance(world);
         
            Method setLocation = ReflectionUtils.getMethod(EntityEnderDragon , "setLocation" ,
                    new Class< ? >[]
                    { double.class , double.class , double.class , float.class , float.class });
            setLocation.invoke(dragon , x , y , z , pitch , yaw);
         
            Method setInvisible = ReflectionUtils.getMethod(EntityEnderDragon , "setInvisible" ,
                    new Class< ? >[]
                    { boolean.class });
            setInvisible.invoke(dragon , visible);
         
            Method setCustomName = ReflectionUtils.getMethod(EntityEnderDragon , "setCustomName" ,
                    new Class< ? >[]
                    { String.class });
            setCustomName.invoke(dragon , name);
         
            Method setHealth = ReflectionUtils.getMethod(EntityEnderDragon , "setHealth" ,
                    new Class< ? >[]
                    { float.class });
            setHealth.invoke(dragon , health);
         
            Field motX = ReflectionUtils.getField(Entity , "motX");
            motX.set(dragon , xvel);
         
            Field motY = ReflectionUtils.getField(Entity , "motX");
            motY.set(dragon , yvel);
         
            Field motZ = ReflectionUtils.getField(Entity , "motX");
            motZ.set(dragon , zvel);
         
            Method getId = ReflectionUtils.getMethod(EntityEnderDragon , "getId" ,
                    new Class< ? >[] { });
            this.id = (Integer) getId.invoke(dragon);
         
            Class< ? > PacketPlayOutSpawnEntityLiving = ReflectionUtils
                    .getCraftClass("PacketPlayOutSpawnEntityLiving");
         
            Object packet = PacketPlayOutSpawnEntityLiving.getConstructor(new Class< ? >[]
            { EntityLiving }).newInstance(dragon);
         
            return packet;
        }
     
        public Object getDestroyPacket() throws IllegalArgumentException, SecurityException,
                InstantiationException, IllegalAccessException, InvocationTargetException,
                NoSuchMethodException, NoSuchFieldException
        {
            Class< ? > PacketPlayOutEntityDestroy = ReflectionUtils
                    .getCraftClass("PacketPlayOutEntityDestroy");
         
            Object packet = PacketPlayOutEntityDestroy.newInstance();
         
            Field a = PacketPlayOutEntityDestroy.getDeclaredField("a");
            a.setAccessible(true);
            a.set(packet , new int[]
            { id });
         
            return packet;
        }
     
        public Object getMetaPacket(Object watcher) throws IllegalArgumentException,
                SecurityException, InstantiationException, IllegalAccessException,
                InvocationTargetException, NoSuchMethodException
        {
            Class< ? > DataWatcher = ReflectionUtils.getCraftClass("DataWatcher");
         
            Class< ? > PacketPlayOutEntityMetadata = ReflectionUtils
                    .getCraftClass("PacketPlayOutEntityMetadata");
         
            Object packet = PacketPlayOutEntityMetadata.getConstructor(new Class< ? >[]
            { int.class , DataWatcher , boolean.class }).newInstance(id , watcher , true);
         
            return packet;
        }
     
        public Object getTeleportPacket(Location loc) throws IllegalArgumentException,
                SecurityException, InstantiationException, IllegalAccessException,
                InvocationTargetException, NoSuchMethodException
        {
            Class< ? > PacketPlayOutEntityTeleport = ReflectionUtils
                    .getCraftClass("PacketPlayOutEntityTeleport");
         
            Object packet = PacketPlayOutEntityTeleport.getConstructor(new Class< ? >[]
            { int.class , int.class , int.class , int.class , byte.class , byte.class })
                    .newInstance(this.id , loc.getBlockX() * 32 , loc.getBlockY() * 32 ,
                            loc.getBlockZ() * 32 , (byte) ( (int) loc.getYaw() * 256 / 360 ) ,
                            (byte) ( (int) loc.getPitch() * 256 / 360 ));
         
            return packet;
        }
     
        public Object getWatcher() throws IllegalArgumentException, SecurityException,
                InstantiationException, IllegalAccessException, InvocationTargetException,
                NoSuchMethodException
        {
            Class< ? > Entity = ReflectionUtils.getCraftClass("Entity");
            Class< ? > DataWatcher = ReflectionUtils.getCraftClass("DataWatcher");
         
            Object watcher = DataWatcher.getConstructor(new Class< ? >[]
            { Entity }).newInstance(dragon);
         
            Method a = ReflectionUtils.getMethod(DataWatcher , "a" , new Class< ? >[]
            { int.class , Object.class });
         
            a.invoke(watcher , 0 , visible ? (byte) 0 : (byte) 0x20);
            a.invoke(watcher , 6 , (Float) health);
            a.invoke(watcher , 7 , (Integer) 0);
            a.invoke(watcher , 8 , (Byte) (byte) 0);
            a.invoke(watcher , 10 , name);
            a.invoke(watcher , 11 , (Byte) (byte) 1);
            return watcher;
        }
     
    }
 
}