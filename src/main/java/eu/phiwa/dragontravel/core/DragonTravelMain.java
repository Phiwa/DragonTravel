package eu.phiwa.dragontravel.core;

import eu.phiwa.dragontravel.core.anticheatplugins.AntiCheatHandler;
import eu.phiwa.dragontravel.core.anticheatplugins.NoCheatPlusHandler;
import eu.phiwa.dragontravel.core.commands.CommandHandler;
import eu.phiwa.dragontravel.core.filehandlers.*;
import eu.phiwa.dragontravel.core.flights.FlightEditor;
import eu.phiwa.dragontravel.core.listeners.BlockListener;
import eu.phiwa.dragontravel.core.listeners.EntityListener;
import eu.phiwa.dragontravel.core.listeners.PlayerListener;
import eu.phiwa.dragontravel.core.modules.MountingScheduler;
import eu.phiwa.dragontravel.core.modules.StationaryDragon;
import eu.phiwa.dragontravel.core.payment.PaymentHandler;
import eu.phiwa.dragontravel.nms.IRyeDragon;
import eu.phiwa.dragontravel.nms.NMSHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class DragonTravelMain extends JavaPlugin {

    private static DragonTravelMain instance;
    public static DragonTravelMain getInstance() {
        return instance;
    }

    public static HashMap<UUID, Long> dmgReceivers = new HashMap<>();
    public static HashMap<UUID, Boolean> ptogglers = new HashMap<>();
    public static HashMap<Block, Block> globalwaypointmarkers = new HashMap<>();


    // CheatProtection-Pluginstatus
    public static boolean anticheat;
    public static boolean nocheatplus;

    //Handlers
    private Config configHandler;
    private NMSHandler nmsHandler;
    private Messages messagesHandler;
    private FlightsDB dbFlightsHandler;
    private HomesDB dbHomesHandler;
    private StationsDB dbStationsHandler;
    private StatDragonsDB dbStatDragonsHandler;

    // Hashmaps
    public static HashMap<Player, IRyeDragon> listofDragonriders = new HashMap<>();
    public static HashMap<Player, Location> listofDragonsridersStartingpoints = new HashMap<>();
    public static HashMap<String, IRyeDragon> listofStatDragons = new HashMap<>();

    // Payment (Costs are directly read from the config/sign on-the-fly)
    public static Economy economyProvider;

    // Payment-Types
    public static final int TRAVEL_TOSTATION = 1;
    public static final int TRAVEL_TORANDOM = 2;
    public static final int TRAVEL_TOPLAYER = 3;
    public static final int TRAVEL_TOCOORDINATES = 4;
    public static final int TRAVEL_TOHOME = 5;
    public static final int TRAVEL_TOFACTIONHOME = 6;
    public static final int FLIGHT = 7;
    public static final int SETHOME = 8;


    public DragonTravelMain() {
        instance = this;
    }

    @Override
    public void onEnable() {
        instance = this;
        nmsHandler = new NMSHandler();

        // Add the new entity to Minecraft's (Craftbukkit's) entities
        // Returns false if plugin disabled
        if (!nmsHandler.getEntityRegister().registerEntity()) return;

        Bukkit.getPluginManager().registerEvents(new EntityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FlightEditor(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);

        if (!(new File(getDataFolder(), "databases").exists()))
            new File(getDataFolder(), "databases").mkdirs();

        // Config
        configHandler = new Config();
        if (configHandler.getConfig().getString("File.Version") == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not initialize config! Disabling the plugin!");
            this.getPluginLoader().disablePlugin(this);
            return;
        } else {
            Bukkit.getLogger().info("Config loaded successfully.");
        }
        messagesHandler = new Messages();
        dbStationsHandler = new StationsDB();
        dbHomesHandler = new HomesDB();
        dbFlightsHandler = new FlightsDB();
        dbStatDragonsHandler = new StatDragonsDB();
        if (dbStatDragonsHandler.getDbStatDragonsConfig().getConfigurationSection("StatDragons") != null) {
            for (String key : dbStatDragonsHandler.getDbStatDragonsConfig().getConfigurationSection("StatDragons").getKeys(false)) {
                String path = "StatDragons." + key + ".";
                String displayName = ChatColor.translateAlternateColorCodes('&', dbStatDragonsHandler.getDbStatDragonsConfig().getString(path + "displayname"));
                Location loc = new Location(Bukkit.getWorld(dbStatDragonsHandler.getDbStatDragonsConfig().getString(path + "world")), dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "x"), dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "y"), dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "z"), (float) dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "yaw"), (float) dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "pitch"));
                StationaryDragon.createStatDragon(loc, key, displayName, false);
            }
        }

        // Commands
        getCommand("dt").setExecutor(new CommandHandler(this));

        // AntiCheat
        if (AntiCheatHandler.getAntiCheat())
            Bukkit.getLogger().info("[DragonTravel] AntiCheat-support enabled");

        // NoCheatPlus
        if (NoCheatPlusHandler.getNoCheatPlus())
            Bukkit.getLogger().info("[DragonTravel] NoCheatPlus-support enabled");

        if (configHandler.isUsePayment()) {

            if (!configHandler.isByEconomy() && !configHandler.isByResources()) {
                Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Payment has been enabled, but both payment-types are disabled, how should a player be able to pay?! Disabling payment...");
                configHandler.setUsePayment(false);
            }

            if (configHandler.isByEconomy() && configHandler.isByResources()) {
                Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Payment has been set to Economy AND Resources, but you can only use one type of payment! Disabling payment...");
                configHandler.setUsePayment(false);
            }

            // Set up Economy (if config-option is set to true)
            if (configHandler.isByEconomy()) {
                if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                    Bukkit.getLogger().info(String.format("[DragonTravel] Hooked into Vault, using it for economy-support"));
                    Bukkit.getLogger().info(String.format("[DragonTravel] Enabled %s", getDescription().getVersion()));
                    new PaymentHandler(this.getServer()).setupEconomy();
                } else {
                    Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] \"Vault\" was not found,");
                    Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] disabling economy-support!");
                    Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Turn off \"Payment.byEconomy\"");
                    Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] in the config.yml or install Vault!");
                }
            }
        }

        //Mounting Scheduler
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MountingScheduler(), 60L, 30L);
    }

    @Override
    public void onDisable() {
        for (String name : listofStatDragons.keySet()) {
            StationaryDragon.removeStatDragon(name, false);
        }
        Bukkit.getLogger().log(Level.INFO, String.format("[DragonTravel] -----------------------------------------------"));
        Bukkit.getLogger().log(Level.INFO, String.format("[DragonTravel] Successfully disabled %s %s", getDescription().getName(), getDescription().getVersion()));
        Bukkit.getLogger().log(Level.INFO, String.format("[DragonTravel] -----------------------------------------------"));
    }

    public void reload() {
        Bukkit.getLogger().log(Level.INFO, "Reloading all files.");
        Bukkit.getLogger().log(Level.INFO, "WE RECOMMEND NOT TO DO THIS BECAUSE IT MIGHT CAUSE SERIUOS PROBLEMS!");
        Bukkit.getLogger().log(Level.INFO, "SIMPLY RESTART YOUR SERVER INSTEAD; THAT'S MUCH SAFER!");

        for (String name : listofStatDragons.keySet()) {
            StationaryDragon.removeStatDragon(name, false);
        }
        listofStatDragons.clear();

        // Config
        configHandler.loadConfig();
        if (configHandler.getConfig().getString("File.Version") == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not initialize config! Disabling the plugin!");
            this.getPluginLoader().disablePlugin(this);
            return;
        } else
            Bukkit.getLogger().info("Config loaded successfully.");
        messagesHandler.loadMessages();
        dbStationsHandler.init();
        dbHomesHandler.init();
        dbFlightsHandler.init();
        dbStatDragonsHandler.init();

        if (dbStatDragonsHandler.getDbStatDragonsConfig().getConfigurationSection("StatDragons") != null) {
            for (String key : dbStatDragonsHandler.getDbStatDragonsConfig().getConfigurationSection("StatDragons").getKeys(false)) {
                String path = "StatDragons." + key + ".";
                String displayName = ChatColor.translateAlternateColorCodes('&', dbStatDragonsHandler.getDbStatDragonsConfig().getString(path + "displayname"));
                Location loc = new Location(Bukkit.getWorld(dbStatDragonsHandler.getDbStatDragonsConfig().getString(path + "world")), dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "x"), dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "y"), dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "z"), (float) dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "yaw"), (float) dbStatDragonsHandler.getDbStatDragonsConfig().getDouble(path + "pitch"));
                StationaryDragon.createStatDragon(loc, key, displayName, false);
            }
        }

        Bukkit.getLogger().log(Level.INFO, "Successfully reloaded all files.");
    }

    public Config getConfigHandler() {
        return configHandler;
    }

    public void setConfigHandler(Config configHandler) {
        this.configHandler = configHandler;
    }

    public NMSHandler getNmsHandler() {
        return nmsHandler;
    }

    public void setNmsHandler(NMSHandler nmsHandler) {
        this.nmsHandler = nmsHandler;
    }

    public Messages getMessagesHandler() {
        return messagesHandler;
    }

    public void setMessagesHandler(Messages messagesHandler) {
        this.messagesHandler = messagesHandler;
    }

    public FlightsDB getDbFlightsHandler() {
        return dbFlightsHandler;
    }

    public void setDbFlightsHandler(FlightsDB dbFlightsHandler) {
        this.dbFlightsHandler = dbFlightsHandler;
    }

    public HomesDB getDbHomesHandler() {
        return dbHomesHandler;
    }

    public void setDbHomesHandler(HomesDB dbHomesHandler) {
        this.dbHomesHandler = dbHomesHandler;
    }

    public StationsDB getDbStationsHandler() {
        return dbStationsHandler;
    }

    public void setDbStationsHandler(StationsDB dbStationsHandler) {
        this.dbStationsHandler = dbStationsHandler;
    }

    public StatDragonsDB getDbStatDragonsHandler() {
        return dbStatDragonsHandler;
    }

    public void setDbStatDragonsHandler(StatDragonsDB dbStatDragonsHandler) {
        this.dbStatDragonsHandler = dbStatDragonsHandler;
    }
}
