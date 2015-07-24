package eu.phiwa.dragontravel.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;

/**
 * Direction for this class and its close associations are heavily inspired by mBaxter's abstraction example
 * under the LGPL licence. https://github.com/mbax/AbstractionExamplePlugin
 */
public class NMSHandler {

    private static final String packageName = "eu.phiwa.dragontravel.nms..";

    private static final Class<?>[] nmsDragonClasses = new Class<?>[]{
            eu.phiwa.dragontravel.nms.v1_8_R3.RyeDragon.class,
            eu.phiwa.dragontravel.nms.v1_7_R3.RyeDragon.class
    };

    private static final Class<?>[] nmsEntityRegisterClasses = new Class<?>[]{
            eu.phiwa.dragontravel.nms.v1_8_R3.EntityRegister.class,
            eu.phiwa.dragontravel.nms.v1_7_R3.EntityRegister.class
    };

    private String sourcePath, version;

    public NMSHandler(){
        go();
    }

    public void go(){
        sourcePath = Bukkit.getServer().getClass().getPackage().getName();
        version = sourcePath.substring(sourcePath.lastIndexOf('.') + 1);
    }

    public IRyeDragon getRyeDragon(Location loc, org.bukkit.World world){
        try {
            final Class<?> clazz = Class.forName(packageName.replace("..", "."+version+".RyeDragon"));
            if(IRyeDragon.class.isAssignableFrom(clazz)){
                return (IRyeDragon) clazz.getConstructor(new Class[]{Location.class, org.bukkit.World.class}).newInstance(loc, world);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IEntityRegister getEntityRegister(){
        try {
            final Class<?> clazz = Class.forName(packageName.replace("..", "."+version+".EntityRegister"));
            if(IEntityRegister.class.isAssignableFrom(clazz)){
                return (IEntityRegister) clazz.getConstructor().newInstance();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
