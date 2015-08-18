package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.permissions.PermissionsHandler;
import eu.phiwa.dragontravel.core.movement.flight.Flight;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class FlightsDB {

    private File dbFlightsFile;
    private FileConfiguration dbFlightsConfig;
    private ConfigurationSection flightSection;

    public FlightsDB() {
        init();
    }

    private void init() {
        dbFlightsFile = new File("plugins/DragonTravel/databases", "flights.yml");
        try {
            create();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Could not initialize the flights-database.");
            e.printStackTrace();
        }
        dbFlightsConfig = new YamlConfiguration();
        load();

        flightSection = dbFlightsConfig.getConfigurationSection("Flights");
        if (flightSection == null) {
            flightSection = dbFlightsConfig.createSection("Flights");
        }
    }

    private void create() {
        if (dbFlightsFile.exists()) {
            return;
        }
        try {
            dbFlightsFile.createNewFile();
            copy(DragonTravel.getInstance().getResource("databases/flights.yml"), dbFlightsFile);
            Bukkit.getLogger().log(Level.INFO, "[DragonTravel] Created flights-database.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Could not create the flights-database!");
        }


    }

    private void load() {
        try {
            dbFlightsConfig.load(dbFlightsFile);
            Bukkit.getLogger().log(Level.INFO, "[DragonTravel] Loaded flights-database.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] No flights-database found");
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
     * Deletes the given flight.
     *
     * @param flightName Name of the flight to delete
     * @return True if successful, false if not.
     */
    public void deleteFlight(String flightName) {
        flightSection.set(flightName.toLowerCase(), null);

        try {
            dbFlightsConfig.save(dbFlightsFile);
            return;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Could not delete flight from database.");
            return;
        }
    }

    public void showFlights(CommandSender sender) {
        sender.sendMessage("Available flights: ");
        int i = 0;
        for (String string : flightSection.getKeys(false)) {
            Flight flight = getFlight(string);
            if (flight != null) {
                sender.sendMessage(" - " + (sender instanceof Player ? (PermissionsHandler.hasFlightPermission(sender, flight.getName()) ? ChatColor.GREEN : ChatColor.RED) : ChatColor.AQUA) + flight.getName());
                i++;
            }
        }
        sender.sendMessage(String.format("(total %d)", i));
    }

    /**
     * Returns the details of the flight with the given name.
     *
     * @param flightName Name of the flight which should be returned.
     * @return The flight as a flight-object.
     */
    public Flight getFlight(String flightName) {
        flightName = flightName.toLowerCase();
        Object obj = flightSection.get(flightName, null);

        if (obj == null) {
            return null;
        }
        if (obj instanceof ConfigurationSection) {
            Flight f = new Flight(((ConfigurationSection) obj).getValues(true));
            f.setName(flightName);
            saveFlight(f);
            return f;
        } else {
            Flight f = (Flight) obj;
            f.setName(flightName);
            return f;
        }
    }

    /**
     * Creates a new flight.
     *
     * @param flight Flight to create.
     * @return Returns true if the flight was created successfully, false if not.
     */
    public void saveFlight(Flight flight) {
        flightSection.set(flight.getName(), flight);

        try {
            dbFlightsConfig.save(dbFlightsFile);
            return;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Could not write new flight to database.");
            return;
        }
    }
}
