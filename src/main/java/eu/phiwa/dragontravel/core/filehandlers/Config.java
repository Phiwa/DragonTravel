package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class Config {

    // Config
    private FileConfiguration config;
    private File configFile;
    private double configVersion = 0.6;

    // Required Item
    private Material requiredItem;
    private boolean requireItemFlight;
    private boolean requireItemTravelCoordinates;
    private boolean requireItemTravelFactionhome;
    private boolean requireItemTravelHome;
    private boolean requireItemTravelPlayer;
    private boolean requireItemTravelRandom;
    private boolean requireItemTravelStation;

    //Economy
    private boolean byEconomy;
    private boolean byResources;
    private boolean usePayment;
    private Material paymentItemType;
    private String paymentItemName;

    // Dragon Antigrief-Options
    private boolean alldragons;
    private boolean onlydragontraveldragons;

    // General
    private boolean requireSkyLight;
    private double speed;
    private int travelHeight;
    private int minMountHeight;
    private int mountingLimitRadius;
    private int dmgCooldown;
    private int dragonLimit;
    private boolean ignoreAntiMobspawnAreas;
    private boolean dismountAtExactLocation;
    private boolean onlysigns;
    private boolean ptoggleDefault;

    public Config() {
        loadConfig();
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

    private void create() {
        if (configFile.exists())
            return;

        try {
            configFile.createNewFile();
            copy(DragonTravel.getInstance().getResource("config.yml"), configFile);
            Bukkit.getLogger().log(Level.INFO, "Created config file.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create the configuration!");
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        configFile = new File(DragonTravel.getInstance().getDataFolder(), "config.yml");
        if (!configFile.exists())
            create();
        config = YamlConfiguration.loadConfiguration(configFile);
        updateConfig();

        // Load some variables from config
        onlydragontraveldragons = config.getBoolean("AntiGriefDragons.ofDragonTravel", true);
        alldragons = config.getBoolean("AntiGriefDragons.all", false);
        ignoreAntiMobspawnAreas = config.getBoolean("AntiGriefDragons.bypassWorldGuardAntiSpawn", true);
        requiredItem = Material.getMaterial(config.getString("RequiredItem.Item", "DRAGON_EGG"));
        requireItemTravelStation = config.getBoolean("RequiredItem.For.toStation", false);
        requireItemTravelRandom = config.getBoolean("RequiredItem.For.toRandom", false);
        requireItemTravelCoordinates = config.getBoolean("RequiredItem.For.toCoordinates", false);
        requireItemTravelPlayer = config.getBoolean("RequiredItem.For.toPlayer", false);
        requireItemTravelHome = config.getBoolean("RequiredItem.For.toHome", false);
        requireItemTravelFactionhome = config.getBoolean("RequiredItem.For.toFactionhome", false);
        requireItemFlight = config.getBoolean("RequiredItem.For.Flight", false);
        dismountAtExactLocation = config.getBoolean("DismountAtExactLocation", false);
        requireSkyLight = config.getBoolean("RequireSkyLight", false);
        speed = config.getDouble("DragonSpeed", 0.5d);
        travelHeight = config.getInt("TravelHeight");
        usePayment = config.getBoolean("Payment.usePayment", false);
        byEconomy = config.getBoolean("Payment.byEconomy", false);
        byResources = config.getBoolean("Payment.byResources", false);
        paymentItemType = Material.getMaterial(config.getString("Payment.Resources.ItemType", "GOLD_INGOT"));
        paymentItemName = ChatColor.translateAlternateColorCodes('&', config.getString("Payment.Resources.ItemName", "Gold Ingot"));
        dragonLimit = config.getInt("DragonLimit", 99999);
        onlysigns = config.getBoolean("OnlySigns", false);
        ptoggleDefault = config.getBoolean("PToggleDefault", false);
        minMountHeight = config.getInt("MinimumMountHeight", -1);
        mountingLimitRadius = config.getInt("MountingLimit.Radius", 4);
        dmgCooldown = config.getInt("DamageCooldown", -1) * 1000;
    }

    private void newlyRequiredConfig() {

        // New options in version 0.2
        if (!config.isSet("PToggleDefault"))
            config.set("PToggleDefault", true);

        // New options in version 0.3
        if (!config.isSet("MaxTravelDistance"))
            config.set("MaxTravelDistance", -1);

        // New options in version 0.4
        if (!config.isSet("DismountAtExactLocation"))
            config.set("DismountAtExactLocation", false);

        // New options in version 0.5
        if (!config.isSet("MinimumMountHeight"))
            config.set("MinimumMountHeight", -1);
        if (!config.isSet("DamageCooldown"))
            config.set("DamageCooldown", -1);

        // New options in version 0.6
        if (!config.isSet("Payment.Resources.ItemType"))
            config.set("Payment.Resources.ItemType", Material.GOLD_INGOT.name());
        if (!config.isSet("Payment.Resources.ItemName"))
            config.set("Payment.Resources.ItemName", "Gold Ingot");
        if (!config.isSet("RequireSkyLight"))
            config.set("RequireSkyLight", false);
        if (!config.isSet("UseMetrics"))
            config.set("UseMetrics", true);
        if (!config.isSet("UseAutoUpdater"))
            config.set("UseAutoUpdater", false);


        // Update the file version
        config.set("File.Version", configVersion);

    }

    private void noLongerRequiredConfig() {
        config.set("Payment.Resources.Item", null);
        config.set("Payment.usePayment", null);
    }

    private void updateConfig() {
        if (config.getDouble("File.Version") != configVersion)
            newlyRequiredConfig();
        noLongerRequiredConfig();
        // Refresh file and config variables for persistence.
        try {
            config.save(configFile);
            config = YamlConfiguration.loadConfiguration(configFile);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "Could not update config, disabling plugin!");
        }
    }

    /*
    GETTERS AND SETTERS
    */

    public int getMountingLimitRadius() {
        return mountingLimitRadius;
    }

    public void setMountingLimitRadius(int mountingLimitRadius) {
        this.mountingLimitRadius = mountingLimitRadius;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public double getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(double configVersion) {
        this.configVersion = configVersion;
    }

    public Material getRequiredItem() {
        return requiredItem;
    }

    public void setRequiredItem(Material requiredItem) {
        this.requiredItem = requiredItem;
    }

    public boolean isRequireItemFlight() {
        return requireItemFlight;
    }

    public void setRequireItemFlight(boolean requireItemFlight) {
        this.requireItemFlight = requireItemFlight;
    }

    public boolean isRequireItemTravelCoordinates() {
        return requireItemTravelCoordinates;
    }

    public void setRequireItemTravelCoordinates(boolean requireItemTravelCoordinates) {
        this.requireItemTravelCoordinates = requireItemTravelCoordinates;
    }

    public boolean isRequireItemTravelFactionhome() {
        return requireItemTravelFactionhome;
    }

    public void setRequireItemTravelFactionhome(boolean requireItemTravelFactionhome) {
        this.requireItemTravelFactionhome = requireItemTravelFactionhome;
    }

    public boolean isRequireItemTravelHome() {
        return requireItemTravelHome;
    }

    public void setRequireItemTravelHome(boolean requireItemTravelHome) {
        this.requireItemTravelHome = requireItemTravelHome;
    }

    public boolean isRequireItemTravelPlayer() {
        return requireItemTravelPlayer;
    }

    public void setRequireItemTravelPlayer(boolean requireItemTravelPlayer) {
        this.requireItemTravelPlayer = requireItemTravelPlayer;
    }

    public boolean isRequireItemTravelRandom() {
        return requireItemTravelRandom;
    }

    public void setRequireItemTravelRandom(boolean requireItemTravelRandom) {
        this.requireItemTravelRandom = requireItemTravelRandom;
    }

    public boolean isRequireItemTravelStation() {
        return requireItemTravelStation;
    }

    public void setRequireItemTravelStation(boolean requireItemTravelStation) {
        this.requireItemTravelStation = requireItemTravelStation;
    }

    public boolean isByEconomy() {
        return byEconomy;
    }

    public void setByEconomy(boolean byEconomy) {
        this.byEconomy = byEconomy;
    }

    public boolean isByResources() {
        return byResources;
    }

    public void setByResources(boolean byResources) {
        this.byResources = byResources;
    }

    public boolean isUsePayment() {
        return usePayment;
    }

    public void setUsePayment(boolean usePayment) {
        this.usePayment = usePayment;
    }

    public Material getPaymentItemType() {
        return paymentItemType;
    }

    public void setPaymentItemType(Material paymentItemType) {
        this.paymentItemType = paymentItemType;
    }

    public String getPaymentItemName() {
        return paymentItemName;
    }

    public void setPaymentItemName(String paymentItemName) {
        this.paymentItemName = paymentItemName;
    }

    public boolean isAlldragons() {
        return alldragons;
    }

    public void setAlldragons(boolean alldragons) {
        this.alldragons = alldragons;
    }

    public boolean isOnlydragontraveldragons() {
        return onlydragontraveldragons;
    }

    public void setOnlydragontraveldragons(boolean onlydragontraveldragons) {
        this.onlydragontraveldragons = onlydragontraveldragons;
    }

    public boolean isRequireSkyLight() {
        return requireSkyLight;
    }

    public void setRequireSkyLight(boolean requireSkyLight) {
        this.requireSkyLight = requireSkyLight;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getMinMountHeight() {
        return minMountHeight;
    }

    public void setMinMountHeight(int minMountHeight) {
        this.minMountHeight = minMountHeight;
    }

    public int getDmgCooldown() {
        return dmgCooldown;
    }

    public void setDmgCooldown(int dmgCooldown) {
        this.dmgCooldown = dmgCooldown;
    }

    public int getDragonLimit() {
        return dragonLimit;
    }

    public void setDragonLimit(int dragonLimit) {
        this.dragonLimit = dragonLimit;
    }

    public boolean isIgnoreAntiMobspawnAreas() {
        return ignoreAntiMobspawnAreas;
    }

    public void setIgnoreAntiMobspawnAreas(boolean ignoreAntiMobspawnAreas) {
        this.ignoreAntiMobspawnAreas = ignoreAntiMobspawnAreas;
    }

    public boolean isDismountAtExactLocation() {
        return dismountAtExactLocation;
    }

    public void setDismountAtExactLocation(boolean dismountAtExactLocation) {
        this.dismountAtExactLocation = dismountAtExactLocation;
    }

    public int getTravelHeight() {
        return travelHeight;
    }

    public void setTravelHeight(int travelHeight) {
        this.travelHeight = travelHeight;
    }

    public boolean isOnlysigns() {
        return onlysigns;
    }

    public void setOnlysigns(boolean onlysigns) {
        this.onlysigns = onlysigns;
    }

    public boolean isPtoggleDefault() {
        return ptoggleDefault;
    }

    public void setPtoggleDefault(boolean ptoggleDefault) {
        this.ptoggleDefault = ptoggleDefault;
    }
}
