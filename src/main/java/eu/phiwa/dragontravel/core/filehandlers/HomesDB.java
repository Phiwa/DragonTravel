package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.core.objects.Home;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public class HomesDB {

    private File dbHomesFile;
    private FileConfiguration dbHomesConfig;
    private ConfigurationSection homeSection;

    public HomesDB() {
        init();
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void create() {
        if (dbHomesFile.exists())
            return;

        try {
            dbHomesFile.createNewFile();
            copy(DragonTravelMain.getInstance().getResource("databases/homes.yml"), dbHomesFile);
            Bukkit.getLogger().log(Level.INFO, "Created homes-database.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create the homes-database!");
        }


    }

    /**
     * Creates a new home.
     *
     * @param home Home to create.
     * @return Returns true if the home was created successfully, false if not.
     */
    public boolean saveHome(String playerName, Home home) {
        homeSection.set(playerName, home);
        try {
            dbHomesConfig.save(dbHomesFile);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not write new home to config.");
            return false;
        }
    }

    /**
     * Deletes the given home.
     *
     * @param homename Name of the home to delete
     * @return True if successful, false if not.
     */
    public boolean deleteHome(String homename) {
        homename = "Homes." + homename;
        dbHomesConfig.set(homename, null);

        try {
            dbHomesConfig.save(dbHomesFile);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not delete home from config.");
            return false;
        }
    }


    /**
     * Returns the details of the home with the given name.
     *
     * @param playerName Name of the home which should be returned.
     * @return The home as a home-object.
     */
    public Home getHome(String playerName) {
        Object obj = homeSection.get(playerName, null);
        if (obj != null) {
            // Transition support
            if (obj instanceof ConfigurationSection) {
                return new Home(((ConfigurationSection) obj).getValues(true));
            }
        }
        return (Home) obj;
    }

    public void init() {
        dbHomesFile = new File("plugins/DragonTravel/databases", "homes.yml");
        try {
            create();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Could not initialize the homes-database.");
            e.printStackTrace();
        }

        dbHomesConfig = new YamlConfiguration();
        load();

        homeSection = dbHomesConfig.getConfigurationSection("Homes");
        if (homeSection == null) {
            homeSection = dbHomesConfig.createSection("Homes");
        }
    }

    private void load() {
        try {
            dbHomesConfig.load(dbHomesFile);
            Bukkit.getLogger().log(Level.INFO, "Loaded homes-database.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "No homes-database found");
            e.printStackTrace();
        }
    }

    public void showHomes(CommandSender sender) {
        sender.sendMessage("Players who have registered a home: ");
        for (String string : dbHomesConfig.getConfigurationSection("Homes").getKeys(false)) {
            Home home = getHome(string);
            if (home != null)
                sender.sendMessage(" - " + string + " [" + home.worldName + "@" + home.x + "," + home.y + "," + home.z + "]");
        }
    }

}
