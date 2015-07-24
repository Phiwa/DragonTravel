package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.core.flights.Waypoint;
import eu.phiwa.dragontravel.core.objects.Flight;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class FlightsDB {

	private FileConfiguration dbFlightsConfig;
	private File dbFlightsFile;

	public FlightsDB () {
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
		if (dbFlightsFile.exists()) {
            return;
        }
		try {
			dbFlightsFile.createNewFile();
			copy(DragonTravelMain.getInstance().getResource("databases/flights.yml"), dbFlightsFile);
            Bukkit.getLogger().log(Level.INFO, "Created flights-database.");
		} catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create the flights-database!");
		}
		
		
	}
	
	/**
	 * Creates a new flight.
	 * 
	 * @param flight
	 * 			Flight to create.
	 * @return
	 * 			Returns true if the flight was created successfully, false if not.		
	 */
	public boolean createFlight(Flight flight)  {
		String path = "Flights." + flight.name;
		dbFlightsConfig.set(path + ".displayname", flight.displayname);
		List<String> waypointsAsString = new ArrayList<String>();
		for(Waypoint wp: flight.waypoints) {	
			String wpString = wp.x + "%" + wp.y + "%" + wp.z + "%" + wp.world.getName();
			waypointsAsString.add(wpString);
		}
		dbFlightsConfig.set(path + ".waypoints", waypointsAsString);
		
		try{
			dbFlightsConfig.save(dbFlightsFile);
			return true;
		}
		catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not write new flight to config.");
			return false;
		}
	}

	/**
	 * Deletes the given flight.
	 * 
	 * @param flightname
	 * 			Name of the flight to delete
	 * @return
	 * 			True if successful, false if not.
	 */
	public boolean deleteFlight(String flightname) {
		flightname = "Flights." + flightname.toLowerCase();
		dbFlightsConfig.set(flightname, null);
		try{
			dbFlightsConfig.save(dbFlightsFile);
			return true;
		}
		catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not delete flight from config.");
            return false;
		}
	}

	
	/**
	 * Returns the details of the flight with the given name.
	 * 
	 * @param flightname
	 * 			Name of the flight which should be returned.
	 * @return
	 * 			The flight as a flight-object.
	 */
	public Flight getFlight(String flightname) {
		String flightpath = "Flights." + flightname.toLowerCase();
		Flight flight = new Flight();
		flight.name = flightname.toLowerCase();
		if(!dbFlightsConfig.isConfigurationSection(flightpath)) {
            return null;
        }
		flight.displayname = dbFlightsConfig.getString(flightpath + ".displayname");
		List<String> waypoints = dbFlightsConfig.getStringList(flightpath + ".waypoints");
		for(String wpData: waypoints) {
			String[] wpDataParts = wpData.split("%");
			if(wpDataParts.length < 3) {
                Bukkit.getLogger().log(Level.SEVERE, "Unable to read flight '" + flight.displayname + "' from database! Waypoint " + (waypoints.indexOf(wpData) + 1) + " could not be read!");
				return null;
			}
			Waypoint wp = new Waypoint();
			try{
				String xString = wpDataParts[0];
				String yString = wpDataParts[1];
				String zString = wpDataParts[2];
				String wString = "";
				// Waypoint contains worldname
				if(wpDataParts.length == 4){
					wString = wpDataParts[3];
				}
				// Waypoint does no contain worldname, so we take it from the separate path
				else {
					wString = dbFlightsConfig.getString(flightpath + ".world");
					
				}
				wp.x = Integer.parseInt(xString);
				wp.y = Integer.parseInt(yString);
				wp.z = Integer.parseInt(zString);			
				wp.world = Bukkit.getWorld(wString);
				if(wp.world == null) {
                    Bukkit.getLogger().log(Level.SEVERE, "Unable to read flight '" + flight.displayname + "' from database! World specified for waypoint " + (waypoints.indexOf(wpData) + 1) + " could not be found!");
					return null;
				}
			} catch(NumberFormatException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Unable to read flight '" + flight.displayname + "' from database! Waypoint " + (waypoints.indexOf(wpData) + 1) + " could not be read!");
				return null;
			} catch(IndexOutOfBoundsException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Unable to read flight '" + flight.displayname + "' from database! Waypoint " + (waypoints.indexOf(wpData) + 1) + " could not be read!");
				return null;
			}
			flight.addWaypoint(wp);
		}
		return flight;
	}
		
	public void init() {
		dbFlightsFile = new File("plugins/DragonTravel/databases", "flights.yml");
		try {
			create();
		}
		catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not initialize the flights-database.");
			e.printStackTrace();
		}
		dbFlightsConfig = new YamlConfiguration();
		load();
	}
	
	private void load() {
		try {
			dbFlightsConfig.load(dbFlightsFile);
            Bukkit.getLogger().log(Level.INFO, "Loaded flights-database.");
		}
		catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "No flights-database found");
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints all flights from the database to the console.
	 */
	public void showFlights() {
		System.out.println("Available flights: ");
		for(String string: dbFlightsConfig.getConfigurationSection("Flights").getKeys(true)) {
			if(string.contains(".displayname")) {
				System.out.println("- " + dbFlightsConfig.getString("Flights." + string));
			}
		}
	}
	
	/**
	 * Prints all flights from the database to the specified player.
	 * 
	 * @param player
	 * 			Player to print the flights to
	 */
	public void showFlights(Player player) {
		player.sendMessage("Available flights: ");
		for(String string: dbFlightsConfig.getConfigurationSection("Flights").getKeys(true)) {
				if(string.contains(".displayname")) {
					String flightname = string.replace(".displayname", "");
					if(player.hasPermission("dt.flight.*") || player.hasPermission("dt.flight."+flightname))
						player.sendMessage("- " + dbFlightsConfig.getString("Flights." + string));
				}
		}			
	}
}
