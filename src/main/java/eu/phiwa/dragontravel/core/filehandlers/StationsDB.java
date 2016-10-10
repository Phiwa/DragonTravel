package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.permissions.PermissionsHandler;
import eu.phiwa.dragontravel.core.movement.travel.Station;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public class StationsDB {

    private File dbStationsFile;
    private FileConfiguration dbStationsConfig;
    private ConfigurationSection stationSection;

    public StationsDB() {
        init();
    }

    private void init() {
        dbStationsFile = new File("plugins/DragonTravel/databases", "stations.yml");
        try {
            create();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Could not initialize the stations-database.");
            e.printStackTrace();
        }

        dbStationsConfig = new YamlConfiguration();
        load();

        stationSection = dbStationsConfig.getConfigurationSection("Stations");
        if (stationSection == null) {
            stationSection = dbStationsConfig.createSection("Stations");
        }
    }

    private void create() {

        if (dbStationsFile.exists())
            return;

        try {
            dbStationsFile.createNewFile();
            copy(DragonTravel.getInstance().getResource("databases/stations.yml"), dbStationsFile);
            Bukkit.getLogger().log(Level.INFO, "[DragonTravel] Created stations-database.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Could not create the stations-database!");
            e.printStackTrace();
        }


    }

    private void load() {
        try {
            dbStationsConfig.load(dbStationsFile);
            Bukkit.getLogger().log(Level.INFO, "[DragonTravel] Loaded stations-database.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] No stations-database found");
            e.printStackTrace();
        }
    }

    private void copy(InputStream in, File file) {

        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1)
                out.write(buf, 0, len);
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkForStation(Player player) {
        int x, y, z;
        World world;
        Location tempLoc;
        Location playerLoc = player.getLocation();

        for (String string : dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
            world = Bukkit.getWorld(stat.getWorldName());

            if (world == null) {
                Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Skipping station '" + stat.getDisplayName() + "' while checking for a station. There is no world '" + stat.getWorldName() + "' on the server!");
                continue;
            }

            if (!world.getName().equalsIgnoreCase(player.getWorld().getName()))
                continue;

            x = stat.getX();
            y = stat.getY();
            z = stat.getZ();
            tempLoc = new Location(world, x, y, z);

            if (tempLoc.distance(playerLoc) <= DragonTravel.getInstance().getConfigHandler().getMountingLimitRadius())
                return true;
        }
        return false;
    }

    /**
     * Deletes the given station.
     *
     * @param stationname Name of the station to delete
     * @return True if successful, false if not.
     */
    public boolean deleteStation(String stationname) {
        stationSection.set(stationname.toLowerCase(), null);
        try {
            dbStationsConfig.save(dbStationsFile);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Could not delete station from database.");
            return false;
        }
    }

    public void showStations(CommandSender sender) {
        sender.sendMessage("Available stations: ");
        int i = 0;
        for (String string : dbStationsConfig.getConfigurationSection("Stations").getKeys(false)) {
            Station station = getStation(string);
            if (station != null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " - " + (sender instanceof Player ? (PermissionsHandler.hasTravelPermission(sender, "travel", station.getDisplayName()) ? ChatColor.GREEN : ChatColor.RED) : ChatColor.AQUA) + station.getDisplayName()));
                i++;
            }
        }
        sender.sendMessage(String.format("(total %d)", i));
    }

    /**
     * Returns the details of the station with the given name.
     *
     * @param stationName Name of the station which should be returned.
     * @return The station as a station-object.
     */
    public Station getStation(String stationName) {
        Object obj = stationSection.get(stationName.toLowerCase(), null);

        if (obj == null) {
            return null;
        }
        if (obj instanceof ConfigurationSection) {
            Station s = new Station(((ConfigurationSection) obj).getValues(true));
            s.setName(stationName);
            saveStation(s);
            return s;
        } else {
            Station s = (Station) obj;
            s.setName(stationName);
            return s;
        }
    }

    /**
     * Creates a new station.
     *
     * @param station Station to create.
     * @return Returns true if the station was created successfully, false if not.
     */
    public boolean saveStation(Station station) {
        stationSection.set(station.getName(), station);

        try {
            dbStationsConfig.save(dbStationsFile);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Could not write new station to database.");
            return false;
        }
    }

}
