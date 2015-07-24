package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.core.objects.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public class HomesDB {

    private FileConfiguration dbHomesConfig;
    private File dbHomesFile;

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
    @SuppressWarnings("static-access")
    public boolean createHome(Home home) {
        String path = "Homes." + home.playername;
        ConfigurationSection sec = dbHomesConfig.createSection(path);
        dbHomesConfig.createPath(sec, "x");
        dbHomesConfig.createPath(sec, "y");
        dbHomesConfig.createPath(sec, "z");
        dbHomesConfig.createPath(sec, "world");
        dbHomesConfig.set(path + ".x", home.x);
        dbHomesConfig.set(path + ".y", home.y);
        dbHomesConfig.set(path + ".z", home.z);
        dbHomesConfig.set(path + ".world", home.world.getName());

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
     * @param playername Name of the home which should be returned.
     * @return The home as a home-object.
     */
    public Home getHome(String playername) {
        playername = "Homes." + playername.toLowerCase();
        if (dbHomesConfig.getString(playername + ".world") == null)
            return null;
        Location homeLoc = new Location(
                Bukkit.getWorld(dbHomesConfig.getString(playername + ".world")),
                (double) dbHomesConfig.getInt(playername + ".x"),
                (double) dbHomesConfig.getInt(playername + ".y"),
                (double) dbHomesConfig.getInt(playername + ".z")
        );
        return new Home(playername, homeLoc);
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

    public void showHomes() {
        System.out.println("Player's who registered a home: ");
        dbHomesConfig.getConfigurationSection("Homes").getKeys(true).stream().filter(string -> !string.contains(".")).forEach(string -> System.out.println("- " + string));
    }

    public void showStations(Player player) {
        player.sendMessage("Player's who registered a home: ");
        dbHomesConfig.getConfigurationSection("Homes").getKeys(true).stream().filter(string -> !string.contains(".")).forEach(string -> player.sendMessage("- " + string));
    }

}
