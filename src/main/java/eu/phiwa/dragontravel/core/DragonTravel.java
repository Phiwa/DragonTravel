package eu.phiwa.dragontravel.core;

import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import eu.phiwa.dragontravel.core.commands.CommandHelpTopic;
import eu.phiwa.dragontravel.core.commands.DragonTravelCommands;
import eu.phiwa.dragontravel.core.filehandlers.*;
import eu.phiwa.dragontravel.core.hooks.anticheat.CheatProtectionHandler;
import eu.phiwa.dragontravel.core.hooks.payment.PaymentManager;
import eu.phiwa.dragontravel.core.hooks.server.IEntityRegister;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.core.hooks.server.NMSHandler;
import eu.phiwa.dragontravel.core.listeners.BlockListener;
import eu.phiwa.dragontravel.core.listeners.EntityListener;
import eu.phiwa.dragontravel.core.listeners.HeroesListener;
import eu.phiwa.dragontravel.core.listeners.PlayerListener;
import eu.phiwa.dragontravel.core.movement.DragonType;
import eu.phiwa.dragontravel.core.movement.flight.Flight;
import eu.phiwa.dragontravel.core.movement.flight.FlightEditor;
import eu.phiwa.dragontravel.core.movement.stationary.StationaryDragon;
import eu.phiwa.dragontravel.core.movement.travel.Home;
import eu.phiwa.dragontravel.core.movement.travel.Station;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;

public class DragonTravel extends JavaPlugin {

    private static DragonTravel instance;

    private CustomCommandsManager commands;
    private CommandHelpTopic help;

    private IEntityRegister entityRegister;
    private PaymentManager paymentManager;
    private DragonManager dragonManager;
    private FlightEditor flightEditor;
    private NMSHandler nmsHandler;

    private Config configHandler;
    private HomesDB dbHomesHandler;
    private Messages messagesHandler;
    private FlightsDB dbFlightsHandler;
    private StationsDB dbStationsHandler;
    private StatDragonsDB dbStatDragonsHandler;

    public DragonTravel() {
        instance = this;
    }

    public void reload() {
        Bukkit.getLogger().log(Level.INFO, "[DragonTravel] Reloading all files.");
        Bukkit.getLogger().log(Level.INFO, "[DragonTravel] WE RECOMMEND NOT TO DO THIS BECAUSE IT MIGHT CAUSE SERIUOS PROBLEMS!");
        Bukkit.getLogger().log(Level.INFO, "[DragonTravel] SIMPLY RESTART YOUR SERVER INSTEAD; THAT'S MUCH SAFER!");
        dbStatDragonsHandler.unloadStationaryDragons();
        setupFileHandlers();
        Bukkit.getLogger().log(Level.INFO, "[DragonTravel] Successfully reloaded all files.");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(cmd.getPermissionMessage());
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(Station.class, "DT-Station");
        ConfigurationSerialization.registerClass(Home.class, "DT-Home");
        ConfigurationSerialization.registerClass(Flight.class, "DT-Flight");
        ConfigurationSerialization.registerClass(StationaryDragon.class, "DT-StatDragon");
        commands = new CustomCommandsManager();
        final CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);
        cmdRegister.register(DragonTravelCommands.DragonTravelParentCommand.class);
    }

    @Override
    public void onDisable() {
    	
    	// Do not unload database if plugin failed to load
    	// because if unsupported server version
    	if(dbStatDragonsHandler != null)
    		dbStatDragonsHandler.unloadStationaryDragons();      
        
        Bukkit.getLogger().log(Level.INFO, "[DragonTravel] -----------------------------------------------");
        Bukkit.getLogger().log(Level.INFO, String.format("[DragonTravel] Successfully disabled %s %s", getDescription().getName(), getDescription().getVersion()));
        Bukkit.getLogger().log(Level.INFO, "[DragonTravel] -----------------------------------------------");
    }

    @Override
    public void onEnable() {
        instance = this;

        nmsHandler = new NMSHandler();
        entityRegister = nmsHandler.getEntityRegister();
        dragonManager = DragonManager.getDragonManager();
        flightEditor = new FlightEditor();
        
        if (!entityRegister.registerEntity()) return;
        

        setupListeners();
        setupFileHandlers();
        CheatProtectionHandler.setup();

        paymentManager = new PaymentManager();
        Bukkit.getLogger().info(ChatColor.stripColor(String.format("[DragonTravel] Payment set up using '%s'.", paymentManager.handler.toString())));
        
        if (configHandler.isByEconomy() && configHandler.isByResources()) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Payment has been set to Economy AND Resources, but you can only use one type of payment! Disabling payment...");
            configHandler.setUsePayment(false);
        }
        if (getConfig().getBoolean("UseAutoUpdater")) {
            Updater updater = new Updater(this, 34251, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
            if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
                Bukkit.getLogger().log(Level.INFO, "[DragonTravel] There is an update available for DragonTravel on BukkitDev!");
            }
        }
        if (getConfig().getBoolean("UseMetrics"))
            setupMetrics();

        getServer().getHelpMap().addTopic((help = new CommandHelpTopic("DragonTravel")));
        getServer().getHelpMap().addTopic(new CommandHelpTopic("/dt"));

        //Mounting Scheduler
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, IRyeDragon> entry : dragonManager.getRiderDragons().entrySet()) {
                    try {
                        entry.getValue().getEntity().setPassenger(entry.getKey());
                    } catch (Exception ignored) {
                    }
                }
            }
        }, 60L, 30L);
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(flightEditor, this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        if (Bukkit.getPluginManager().getPlugin("Heroes") != null) {
            Bukkit.getPluginManager().registerEvents(new HeroesListener(), this);
        }
    }

    private void setupFileHandlers() {
        if (!(new File(getDataFolder(), "databases").exists())) {
            new File(getDataFolder(), "databases").mkdirs();
        }

        configHandler = new Config();
        if (configHandler.getConfig().getString("File.Version") == null) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Could not initialize config! Disabling the plugin!");
            this.getPluginLoader().disablePlugin(this);
            return;
        } else {
            Bukkit.getLogger().info("[DragonTravel] Config loaded successfully.");
        }

        messagesHandler = new Messages();
        dbStationsHandler = new StationsDB();
        dbHomesHandler = new HomesDB();
        dbFlightsHandler = new FlightsDB();
        dbStatDragonsHandler = new StatDragonsDB();
    }

    private void setupMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            Metrics.Graph dragonsFlyingGraph = metrics.createGraph("Number of dragons flying");
            dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Manned Flight") {
                @Override
                public int getValue() {
                    int x = 0;
                    for (Map.Entry<Player, IRyeDragon> entry : dragonManager.getRiderDragons().entrySet()) {
                        if (entry.getValue().getDragonType().equals(DragonType.MANNED_FLIGHT)) {
                            x++;
                        }
                    }
                    return x;
                }

            });
            dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Timed Flight") {
                @Override
                public int getValue() {
                    int x = 0;
                    for (Map.Entry<Player, IRyeDragon> entry : dragonManager.getRiderDragons().entrySet()) {
                        if (entry.getValue().getDragonType().equals(DragonType.TIMED_FLIGHT)) {
                            x++;
                        }
                    }
                    return x;
                }

            });
            dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Faction Travel") {
                @Override
                public int getValue() {
                    int x = 0;
                    for (Map.Entry<Player, IRyeDragon> entry : dragonManager.getRiderDragons().entrySet()) {
                        if (entry.getValue().getDragonType().equals(DragonType.FACTION_TRAVEL)) {
                            x++;
                        }
                    }
                    return x;
                }

            });
            dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Home Travel") {
                @Override
                public int getValue() {
                    int x = 0;
                    for (Map.Entry<Player, IRyeDragon> entry : dragonManager.getRiderDragons().entrySet()) {
                        if (entry.getValue().getDragonType().equals(DragonType.HOME_TRAVEL)) {
                            x++;
                        }
                    }
                    return x;
                }

            });
            dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Location Travel") {
                @Override
                public int getValue() {
                    int x = 0;
                    for (Map.Entry<Player, IRyeDragon> entry : dragonManager.getRiderDragons().entrySet()) {
                        if (entry.getValue().getDragonType().equals(DragonType.LOC_TRAVEL)) {
                            x++;
                        }
                    }
                    return x;
                }

            });
            dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Player Travel") {
                @Override
                public int getValue() {
                    int x = 0;
                    for (Map.Entry<Player, IRyeDragon> entry : dragonManager.getRiderDragons().entrySet()) {
                        if (entry.getValue().getDragonType().equals(DragonType.PLAYER_TRAVEL)) {
                            x++;
                        }
                    }
                    return x;
                }

            });
            dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Station Travel") {
                @Override
                public int getValue() {
                    int x = 0;
                    for (Map.Entry<Player, IRyeDragon> entry : dragonManager.getRiderDragons().entrySet()) {
                        if (entry.getValue().getDragonType().equals(DragonType.STATION_TRAVEL)) {
                            x++;
                        }
                    }
                    return x;
                }

            });
            dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Pet") {
                @Override
                public int getValue() {
                    int x = 0;
                    for (Map.Entry<Player, IRyeDragon> entry : dragonManager.getRiderDragons().entrySet()) {
                        if (entry.getValue().getDragonType().equals(DragonType.PET)) {
                            x++;
                        }
                    }
                    return x;
                }

            });
            dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Stationary Dragons") {
                @Override
                public int getValue() {
                    return dragonManager.getStationaryDragons().size();
                }

            });
            metrics.start();
        } catch (IOException ignored) {
        }
    }

    public static DragonTravel getInstance() {
        return instance;
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

    public IEntityRegister getEntityRegister() {
        return entityRegister;
    }

    public void setEntityRegister(IEntityRegister entityRegister) {
        this.entityRegister = entityRegister;
    }

    public PaymentManager getPaymentManager() {
        return paymentManager;
    }

    public void setPaymentManager(PaymentManager paymentManager) {
        this.paymentManager = paymentManager;
    }

    public CustomCommandsManager getCommands() {
        return commands;
    }

    public void setCommands(CustomCommandsManager commands) {
        this.commands = commands;
    }

    public CommandHelpTopic getHelp() {
        return help;
    }

    public void setHelp(CommandHelpTopic help) {
        this.help = help;
    }

    public DragonManager getDragonManager() {
        return dragonManager;
    }

    public void setDragonManager(DragonManager dragonManager) {
        this.dragonManager = dragonManager;
    }

    public FlightEditor getFlightEditor() {
        return flightEditor;
    }

    public void setFlightEditor(FlightEditor flightEditor) {
        this.flightEditor = flightEditor;
    }

    public static class CustomCommandsManager extends BukkitCommandsManager {
        public Map<String, Method> getSubcommandMethods(String rootCommand) {
            Method m = this.commands.get(null).get(rootCommand);
            return this.commands.get(m);
        }
    }
}
