package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.movement.travel.Home;
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

    private void create() {
        if (dbHomesFile.exists())
            return;

        try {
            dbHomesFile.createNewFile();
            copy(DragonTravel.getInstance().getResource("databases/homes.yml"), dbHomesFile);
            Bukkit.getLogger().log(Level.INFO, "Created homes-database.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create the homes-database!");
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

    /**
     * Deletes the given home.
     *
     * @param homeName Name of the home to delete
     * @return True if successful, false if not.
     */
    public boolean deleteHome(String homeName) {
        homeName = "Homes." + homeName;
        dbHomesConfig.set(homeName, null);

        try {
            dbHomesConfig.save(dbHomesFile);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not delete home from config.");
            return false;
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

    /**
     * Returns the details of the home with the given name.
     *
     * @param playerId Name of the home which should be returned.
     * @return The home as a home-object.
     */
    public Home getHome(String playerId) {
        Object obj = homeSection.get(playerId.toLowerCase(), null);

        if (obj == null) {
            return null;
        }
        if (obj instanceof ConfigurationSection) {
            Home h = new Home(((ConfigurationSection) obj).getValues(true));
            h.playerName = playerId;
            saveHome(playerId, h);
            return h;
        } else {
            Home h = (Home) obj;
            h.playerName = playerId;
            return h;
        }
    }

    /**
     * Creates a new home.
     *
     * @param home Home to create.
     * @return Returns true if the home was created successfully, false if not.
     */
    public boolean saveHome(String playerId, Home home) {
        homeSection.set(playerId, home);
        try {
            dbHomesConfig.save(dbHomesFile);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not write new home to config.");
            return false;
        }
    }

}
