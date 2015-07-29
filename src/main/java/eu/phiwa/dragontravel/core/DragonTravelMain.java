package eu.phiwa.dragontravel.core;

import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import eu.phiwa.dragontravel.core.anticheatplugins.CheatProtectionHandler;
import eu.phiwa.dragontravel.core.commands.CommandHelpTopic;
import eu.phiwa.dragontravel.core.commands.DragonTravelCommands;
import eu.phiwa.dragontravel.core.filehandlers.*;
import eu.phiwa.dragontravel.core.flights.FlightEditor;
import eu.phiwa.dragontravel.core.listeners.BlockListener;
import eu.phiwa.dragontravel.core.listeners.EntityListener;
import eu.phiwa.dragontravel.core.listeners.HeroesListener;
import eu.phiwa.dragontravel.core.listeners.PlayerListener;
import eu.phiwa.dragontravel.core.modules.MountingScheduler;
import eu.phiwa.dragontravel.core.objects.Flight;
import eu.phiwa.dragontravel.core.objects.Home;
import eu.phiwa.dragontravel.core.objects.Station;
import eu.phiwa.dragontravel.core.objects.StationaryDragon;
import eu.phiwa.dragontravel.core.payment.PaymentManager;
import eu.phiwa.dragontravel.nms.IEntityRegister;
import eu.phiwa.dragontravel.nms.IRyeDragon;
import eu.phiwa.dragontravel.nms.NMSHandler;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DragonTravelMain extends JavaPlugin {

    public static HashMap<UUID, Long> dmgReceivers = new HashMap<>();
    public static HashMap<UUID, Boolean> ptogglers = new HashMap<>();
    public static HashMap<Block, Block> globalwaypointmarkers = new HashMap<>();
    public static HashMap<Player, IRyeDragon> listofDragonriders = new HashMap<>();
    public static HashMap<Player, Location> listofDragonsridersStartingpoints = new HashMap<>();
    public static HashMap<String, StationaryDragon> listofStatDragons = new HashMap<>();

    private static DragonTravelMain instance;
    public CustomCommandsManager commands;
    public CommandHelpTopic help;
    //Handlers
    private Config configHandler;
    private NMSHandler nmsHandler;
    private IEntityRegister entityRegister;
    private Messages messagesHandler;
    private FlightsDB dbFlightsHandler;
    private HomesDB dbHomesHandler;
    private StationsDB dbStationsHandler;
    private StatDragonsDB dbStatDragonsHandler;
    private PaymentManager paymentManager;


    public DragonTravelMain() {
        instance = this;
    }

    public static DragonTravelMain getInstance() {
        return instance;
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
    public void onEnable() {
        instance = this;

        nmsHandler = new NMSHandler();
        entityRegister = nmsHandler.getEntityRegister();

        // Add the new entity to Minecraft's (Craftbukkit's) entities
        // Returns false if plugin disabled
        if (!entityRegister.registerEntity()) return;

        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new FlightEditor(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        if (Bukkit.getPluginManager().getPlugin("Heroes") != null) {
            Bukkit.getPluginManager().registerEvents(new HeroesListener(), this);
        }

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
            System.out.println(dbStatDragonsHandler);
            System.out.println(dbStatDragonsHandler.getDbStatDragonsConfig());
            System.out.println(dbStatDragonsHandler.getDbStatDragonsConfig().getConfigurationSection("StatDragons").getValues(true));
            for (String key : dbStatDragonsHandler.getDbStatDragonsConfig().getConfigurationSection("StatDragons").getKeys(false)) {
                StationaryDragon sDragon = dbStatDragonsHandler.getStatDragon(key);
                DragonTravelMain.listofStatDragons.put(key.toLowerCase(), sDragon);
            }
        }

        if (getConfig().getBoolean("UseAutoUpdater")) {
            new Updater(this, 34251, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
        }
        if (getConfig().getBoolean("UseMetrics")) {
            try {
                Metrics metrics = new Metrics(this);
                Metrics.Graph dragonsFlyingGraph = metrics.createGraph("Number of dragons flying");
                dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Flight") {

                    @Override
                    public int getValue() {
                        int x = 0;
                        for (Map.Entry<Player, IRyeDragon> entry : listofDragonriders.entrySet()) {
                            if (entry.getValue().isFlight()) {
                                x++;
                            }
                        }
                        return x;
                    }

                });
                dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Travel") {
                    @Override
                    public int getValue() {
                        int x = 0;
                        for (Map.Entry<Player, IRyeDragon> entry : listofDragonriders.entrySet()) {
                            if (entry.getValue().isTravel()) {
                                x++;
                            }
                        }
                        return x;
                    }

                });
                dragonsFlyingGraph.addPlotter(new Metrics.Plotter("Stationary Dragons") {
                    @Override
                    public int getValue() {
                        return listofStatDragons.size();
                    }

                });
                metrics.start();
            } catch (IOException e) {
            }
        }

        getServer().getHelpMap().addTopic((help = new CommandHelpTopic("DragonTravel")));
        getServer().getHelpMap().addTopic(new CommandHelpTopic("/dt"));

        // Anti-cheat setup
        CheatProtectionHandler.setup();

        if (configHandler.isByEconomy() && configHandler.isByResources()) {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Payment has been set to Economy AND Resources, but you can only use one type of payment! Disabling payment...");
            configHandler.setUsePayment(false);
        }

        paymentManager = new PaymentManager(getServer());
        Bukkit.getLogger().info(ChatColor.stripColor(String.format("Payment set up using '%s'.", paymentManager.handler.toString())));

        //Mounting Scheduler
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MountingScheduler(), 60L, 30L);
    }

    @Override
    public void onDisable() {
        for (StationaryDragon sDragon : listofStatDragons.values()) {
            sDragon.removeDragon(false);
        }
        Bukkit.getLogger().log(Level.INFO, String.format("[DragonTravel] -----------------------------------------------"));
        Bukkit.getLogger().log(Level.INFO, String.format("[DragonTravel] Successfully disabled %s %s", getDescription().getName(), getDescription().getVersion()));
        Bukkit.getLogger().log(Level.INFO, String.format("[DragonTravel] -----------------------------------------------"));
    }

    public void reload() {
        Bukkit.getLogger().log(Level.INFO, "Reloading all files.");
        Bukkit.getLogger().log(Level.INFO, "WE RECOMMEND NOT TO DO THIS BECAUSE IT MIGHT CAUSE SERIUOS PROBLEMS!");
        Bukkit.getLogger().log(Level.INFO, "SIMPLY RESTART YOUR SERVER INSTEAD; THAT'S MUCH SAFER!");

        for (StationaryDragon sDragon : listofStatDragons.values()) {
            sDragon.removeDragon(false);
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
                StationaryDragon sDragon = dbStatDragonsHandler.getStatDragon(key);
                DragonTravelMain.listofStatDragons.put(key.toLowerCase(), sDragon);
            }
        }

        Bukkit.getLogger().log(Level.INFO, "Successfully reloaded all files.");
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

    public static class CustomCommandsManager extends BukkitCommandsManager {
        public Map<String, Method> getSubcommandMethods(String rootCommand) {
            Method m = this.commands.get(null).get(rootCommand);
            return this.commands.get(m);
        }
    }
}
