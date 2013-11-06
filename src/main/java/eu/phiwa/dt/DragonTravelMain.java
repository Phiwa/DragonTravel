package eu.phiwa.dt;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_7_R1.EntityTypes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandsManager;

import eu.phiwa.dt.anticheatplugins.CheatProtectionHandler;
import eu.phiwa.dt.commands.DragonTravelCommands;
import eu.phiwa.dt.filehandlers.Config;
import eu.phiwa.dt.filehandlers.FlightsDB;
import eu.phiwa.dt.filehandlers.HomesDB;
import eu.phiwa.dt.filehandlers.Messages;
import eu.phiwa.dt.filehandlers.StationsDB;
import eu.phiwa.dt.flights.FlightEditor;
import eu.phiwa.dt.listeners.BlockListener;
import eu.phiwa.dt.listeners.EntityListener;
import eu.phiwa.dt.listeners.PlayerListener;
import eu.phiwa.dt.modules.MountingScheduler;
import eu.phiwa.dt.payment.PaymentManager;


public class DragonTravelMain extends JavaPlugin {

	public static PluginManager pm;
	public static DragonTravelMain plugin;
	public static final Logger logger = Logger.getLogger("Minecraft");

	// Commands
	private CommandsManager<CommandSender> commands;

	// General
	public static double speed = 0.5;
	public static boolean ignoreAntiMobspawnAreas;
	public static boolean onlysigns = false;
	public static boolean ptoggleDefault = false;

	// Listeners
	private static EntityListener entityListener;
	private static PlayerListener playerListener;
	private static BlockListener blocklistener;
	private static FlightEditor flighteditor;

	// Config
	public static double configVersion = 0.2;
	public static FileConfiguration config;
	public static Config configHandler;

	public static File databaseFolder;

	public static Messages messagesHandler;
	public static StationsDB dbStationsHandler;
	public static HomesDB dbHomesHandler;
	public static FlightsDB dbFlightsHandler;

	// Hashmaps
	public static HashMap<Player, RyeDragon> listofDragonriders = new HashMap<Player, RyeDragon>();
	public static HashMap<Player, Location> listofDragonsridersStartingpoints = new HashMap<Player, Location>();
	public static HashMap<Block, Block> globalwaypointmarkers = new HashMap<Block, Block>();
	public static HashMap<String, Boolean> ptogglers = new HashMap<String, Boolean>();

	// Required Item
	public static boolean requireItemTravelStation = false;
	public static boolean requireItemTravelRandom = false;
	public static boolean requireItemTravelCoordinates = false;
	public static boolean requireItemTravelPlayer = false;
	public static boolean requireItemTravelHome = false;
	public static boolean requireItemTravelFactionhome = false;
	public static boolean requireItemFlight = false;
	public static Material requiredItem = Material.DRAGON_EGG;

	// Dragon Antigrief-Options
	public static boolean onlydragontraveldragons;
	public static boolean alldragons;

	// DragonLimit
	public static int dragonLimit = 99999;

	// Payment (Costs are directly read from the config/sign on-the-fly)
	public PaymentManager paymentManager;
	public static boolean usePayment = false;
	public static boolean byEconomy = false;
	public static boolean byResources = false;
	public static Material paymentItem = Material.GOLD_NUGGET;

	private final Class<?> dragonClass;

	public DragonTravelMain() {
		this.dragonClass = RyeDragon.class;
	}

	@Override
	public void onLoad() {
		ConfigurationSerialization.registerClass(Home.class);
		ConfigurationSerialization.registerClass(Station.class);
		ConfigurationSerialization.registerClass(Flight.class);
	}

	@Override
	public void onEnable() {

		pm = getServer().getPluginManager();
		PluginDescriptionFile description = getDescription();
		plugin = this;

		// Add the new entity to Minecraft's (Craftbukkit's) entities
		// Returns false if plugin disabled
		if (!registerEntity())
			return;

		// Register EventListener
		entityListener = new EntityListener(this);
		playerListener = new PlayerListener(this);
		flighteditor = new FlightEditor();
		blocklistener = new BlockListener(this);

		pm.registerEvents(playerListener, this);
		pm.registerEvents(entityListener, this);
		pm.registerEvents(flighteditor, this);
		pm.registerEvents(blocklistener, this);

		databaseFolder = new File(plugin.getDataFolder(), "databases");
		if (!(databaseFolder.exists()))
			databaseFolder.mkdirs();

		// Config
		configHandler = new Config(this);
		configHandler.loadConfig();
		if (config.getString("File.Version") == null) {
			logger.log(Level.SEVERE, "Could not initialize config! Disabling the plugin!");
			this.getPluginLoader().disablePlugin(this);
			return;
		} else
			logger.info("Config loaded successfully.");

		// Messages-file
		messagesHandler = new Messages(this);
		if (!messagesHandler.loadMessages())
			return;

		// StationsDB
		dbStationsHandler = new StationsDB(this);
		dbStationsHandler.init();

		// HomesDB
		dbHomesHandler = new HomesDB(this);
		dbHomesHandler.init();

		// StationsDB
		dbFlightsHandler = new FlightsDB(this);
		dbFlightsHandler.init();

		CheatProtectionHandler.setup();

		// Load some variables from config
		onlydragontraveldragons = config.getBoolean("AntiGriefDragons.ofDragonTravel");
		alldragons = config.getBoolean("AntiGriefDragons.all");
		ignoreAntiMobspawnAreas = config.getBoolean("AntiGriefDragons.bypassWorldGuardAntiSpawn");

		Material tmp = Material.matchMaterial(config.getString("RequiredItem.Item", "DRAGON_EGG"));
		if (tmp != null) {
			requiredItem = tmp;
		}

		requireItemTravelStation = config.getBoolean("RequiredItem.For.toStation");
		requireItemTravelRandom = config.getBoolean("RequiredItem.For.toRandom");
		requireItemTravelCoordinates = config.getBoolean("RequiredItem.For.toCoordinates");
		requireItemTravelPlayer = config.getBoolean("RequiredItem.For.toPlayer");
		requireItemTravelHome = config.getBoolean("RequiredItem.For.toHome");
		requireItemTravelFactionhome = config.getBoolean("RequiredItem.For.toFactionhome");
		requireItemFlight = config.getBoolean("RequiredItem.For.Flight");

		speed = config.getDouble("DragonSpeed");

		paymentItem = Material.matchMaterial(config.getString("Payment.Resources.Item", "GOLD_NUGGET"));

		dragonLimit = config.getInt("DragonLimit", 5000);

		onlysigns = config.getBoolean("OnlySigns");

		ptoggleDefault = config.getBoolean("PToggleDefault");

		usePayment = config.getBoolean("Payment.usePayment");
		byEconomy = config.getBoolean("Payment.byEconomy");
		byResources = config.getBoolean("Payment.byResources");

		if (byEconomy && byResources) {
			getLogger().severe("Both Payment.byEconomy and Payment.byResources are set to true. Attempting Economy first..");
		}

		paymentManager = new PaymentManager(getServer());

		getLogger().info(ChatColor.stripColor(String.format("Payment set up using '%s'.", paymentManager.handler.toString())));

		commands = new CommandsManager<CommandSender>() {
			@Override
			public boolean hasPermission(CommandSender player, String perm) {
				return player.hasPermission(perm);
			}
		};

		final CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);
		cmdRegister.register(DragonTravelCommands.DragonTravelParentCommand.class);

		// MoutingScheduler
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MountingScheduler(), 60L, 30L);
	}

	@Override
	public void onDisable() {
		logger.log(Level.SEVERE, String.format("[DragonTravel] -----------------------------------------------"));
		logger.log(Level.SEVERE, String.format("[DragonTravel] Successfully disabled %s %s", getDescription().getName(), getDescription().getVersion()));
		logger.log(Level.SEVERE, String.format("[DragonTravel] -----------------------------------------------"));
	}

	private boolean registerEntity() {

		try {
			Class entityTypeClass = EntityTypes.class;

			Field c = entityTypeClass.getDeclaredField("c");
			c.setAccessible(true);
			HashMap c_map = (HashMap) c.get(null);
			c_map.put("RyeDragon", this.dragonClass);

			Field d = entityTypeClass.getDeclaredField("d");
			d.setAccessible(true);
			HashMap d_map = (HashMap) d.get(null);
			d_map.put(this.dragonClass, "RyeDragon");

			Field e = entityTypeClass.getDeclaredField("e");
			e.setAccessible(true);
			HashMap e_map = (HashMap) e.get(null);
			e_map.put(Integer.valueOf(63), this.dragonClass);

			Field f = entityTypeClass.getDeclaredField("f");
			f.setAccessible(true);
			HashMap f_map = (HashMap) f.get(null);
			f_map.put(this.dragonClass, Integer.valueOf(63));

			Field g = entityTypeClass.getDeclaredField("g");
			g.setAccessible(true);
			HashMap g_map = (HashMap) g.get(null);
			g_map.put("RyeDragon", Integer.valueOf(63));

			return true;
		} catch (Exception e) {

			Class<?>[] paramTypes = new Class[] {Class.class, String.class, int.class };

			// MCPC+ compatibility
			// Forge Dev environment; names are not translated into func_foo
			try {
				Method method = EntityTypes.class.getDeclaredMethod("addMapping", paramTypes);
				method.setAccessible(true);
				method.invoke(null, RyeDragon.class, "RyeDragon", 63);
				return true;
			} catch (Exception ex) {
				e.addSuppressed(ex);
			}
			// Production environment: search for the method
			// This is required because the seargenames could change
			// LAST CHECKED FOR VERSION 1.6.4
			try {
				for (Method method : EntityTypes.class.getDeclaredMethods()) {
					if (Arrays.equals(paramTypes, method.getParameterTypes())) {
						method.invoke(null, RyeDragon.class, "RyeDragon", 63);
						return true;
					}
				}
			} catch (Exception ex) {
				e.addSuppressed(ex);
			}

			logger.info("[DragonTravel] [Error] Could not register the RyeDragon-entity!");
			e.printStackTrace();
			pm.disablePlugin(this);

		}
		return false;
	}


	public void reload() {

		logger.log(Level.INFO, "Reloading all files.");
		logger.log(Level.INFO, "WE RECOMMEND NOT TO DO THIS BECAUSE IT MIGHT CAUSE SERIUOS PROBLEMS!");
		logger.log(Level.INFO, "SIMPLY RESTART YOUR SERVER INSTEAD; THAT'S MUCH SAFER!");

		// Config
		configHandler.loadConfig();
		if (config.getString("File.Version") == null) {
			logger.log(Level.SEVERE, "Could not initialize config! Disabling the plugin!");
			this.getPluginLoader().disablePlugin(this);
			return;
		} else
			logger.info("Config loaded successfully.");


		// Messages-file
		if (!messagesHandler.loadMessages())
			return;

		// StationsDB
		dbStationsHandler.init();


		// HomesDB
		dbHomesHandler.init();


		// StationsDB
		dbFlightsHandler.init();


		logger.log(Level.INFO, "Successfully reloaded all files.");
	}
}
