package eu.phiwa.dt.filehandlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.Flight;
import eu.phiwa.dt.flights.Waypoint;

public class FlightsDB {

	DragonTravelMain plugin;
	
	public FlightsDB (DragonTravelMain plugin) {
		this.plugin = plugin;
	}
		
	public void init() {
	
		DragonTravelMain.dbFlightsFile = new File("plugins/DragonTravel/databases", "flights.yml");
	
		try {
			create();
		}
		catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not initialize the flights-database.");
			e.printStackTrace();
		}
	
		DragonTravelMain.dbFlightsConfig = new YamlConfiguration();
		load();
	
	}
	
	private void create() {
		
		if (DragonTravelMain.dbFlightsFile.exists())
			return;

		try {
			DragonTravelMain.dbFlightsFile.createNewFile();
			copy(getClass().getResourceAsStream("flights.yml"), DragonTravelMain.dbFlightsFile);
			DragonTravelMain.logger.info("[DragonTravel] Created flights-database.");
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not create the flights-database!");
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void load() {
		try {
			DragonTravelMain.dbFlightsConfig.load(DragonTravelMain.dbFlightsFile);
			DragonTravelMain.logger.info("[DragonTravel] Loaded flights-database.");
		}
		catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] No flights-database found");
			e.printStackTrace();
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
		
		if(DragonTravelMain.dbFlightsConfig.getString(flightpath + ".world") == null)
			return null;
		
		Flight flight = new Flight();
		
		flight.name = flightname.toLowerCase();
		
		@SuppressWarnings("unchecked")
		List<String> waypoints = (List<String>) DragonTravelMain.dbFlightsConfig.getList(flightpath + ".waypoints");	
		String worldname = DragonTravelMain.dbFlightsConfig.getString(flightpath + ".world");	
		
		flight.displayname = DragonTravelMain.dbFlightsConfig.getString(flightpath + ".displayname");
		
		if(worldname == null)
			return null;
			
		flight.world = Bukkit.getWorld(worldname);
		
		for(String wpData: waypoints) {
			
			String[] wpDataParts = wpData.split("%");
			Waypoint wp = new Waypoint();
			 		
			try{
				String xString = wpDataParts[0];
				String yString = wpDataParts[1];
				String zString = wpDataParts[2];
				
				wp.x = Integer.parseInt(xString);
				wp.y = Integer.parseInt(yString);
				wp.z = Integer.parseInt(zString);
			}
			catch(NumberFormatException ex) {
				DragonTravelMain.logger.info("[DragonTravel][Error] Unable to read flight '" + flight.displayname + "' from database!");
				return null;
			}
			catch(IndexOutOfBoundsException ex) {
				DragonTravelMain.logger.info("[DragonTravel][Error] Unable to read flight '" + flight.displayname + "' from database!");
				return null;
			}
			flight.addWaypoint(wp);
		}
		
		return flight;
	}
		
	/**
	 * Creates a new flight.
	 * 
	 * @param flight
	 * 			Flight to create.
	 * @return
	 * 			Returns true if the flight was created successfully, false if not.		
	 */
	@SuppressWarnings("static-access")
	public boolean createFlight(Flight flight)  {

		String path = "Flights." + flight.name;
		
		ConfigurationSection sec = DragonTravelMain.dbFlightsConfig.createSection(path);
		DragonTravelMain.dbStationsConfig.createPath(sec, "displayname");
		DragonTravelMain.dbStationsConfig.createPath(sec, "world");
		DragonTravelMain.dbStationsConfig.createPath(sec, "waypoints");
		DragonTravelMain.dbFlightsConfig.set(path + ".displayname", flight.displayname);
		DragonTravelMain.dbFlightsConfig.set(path + ".world", flight.world.getName());
		
		List<String> waypointsAsString = new ArrayList<String>();
		
		for(Waypoint wp: flight.waypoints) {
			String wpString = wp.x + "%" + wp.y + "%" + wp.z;
			waypointsAsString.add(wpString);
		}
		
		DragonTravelMain.dbFlightsConfig.set(path + ".waypoints", waypointsAsString);
		
		try{
			DragonTravelMain.dbFlightsConfig.save(DragonTravelMain.dbFlightsFile);
			return true;
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not write new flight to config.");
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
		
		DragonTravelMain.dbFlightsConfig.set(flightname, null);
		
		try{
			DragonTravelMain.dbFlightsConfig.save(DragonTravelMain.dbFlightsFile);
			return true;
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not delete flight from config.");
			return false;
		}
	}
	
	/**
	 * Prints all flights from the database to the console.
	 */
	public void showFlights() {
		System.out.println("Available flights: ");
		for(String string: DragonTravelMain.dbFlightsConfig.getConfigurationSection("Flights").getKeys(true)) {
			if(string.contains(".displayname")) {
				System.out.println("- " + DragonTravelMain.dbFlightsConfig.getString("Flights." + string));
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
		for(String string: DragonTravelMain.dbFlightsConfig.getConfigurationSection("Flights").getKeys(true)) {
				// TODO: Permission-Check (Normal permission / flight-specific permission) string.split[0] == flight-name
				if(string.contains(".displayname")) {
					player.sendMessage("- " + DragonTravelMain.dbFlightsConfig.getString("Flights." + string));
				}
		}			
	}
}
