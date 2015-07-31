package eu.phiwa.dragontravel.core.hooks.server;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;

/**
 * Direction for this class and its close associations are heavily inspired by mBaxter's abstraction example
 * under the LGPL licence. https://github.com/mbax/AbstractionExamplePlugin
 */
public class NMSHandler {

    private static final String packageName = "eu.phiwa.dragontravel.nms..";

    private String version;

    public NMSHandler() {
        go();
    }

    public void go() {
        String sourcePath = Bukkit.getServer().getClass().getPackage().getName();
        version = sourcePath.substring(sourcePath.lastIndexOf('.') + 1);
    }

    public IRyeDragon getRyeDragon(Location loc) {
        try {
            final Class<?> clazz = Class.forName(packageName.replace("..", "." + version + ".RyeDragon"));
            if (IRyeDragon.class.isAssignableFrom(clazz)) {
                return (IRyeDragon) clazz.getConstructor(new Class[]{Location.class}).newInstance(loc);
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IEntityRegister getEntityRegister() {
        try {
            final Class<?> clazz = Class.forName(packageName.replace("..", "." + version + ".EntityRegister"));
            if (IEntityRegister.class.isAssignableFrom(clazz)) {
                return (IEntityRegister) clazz.getConstructor().newInstance();
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}