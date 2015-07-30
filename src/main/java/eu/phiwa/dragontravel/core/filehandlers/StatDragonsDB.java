package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.movement.stationary.StationaryDragon;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public class StatDragonsDB {

    private File dbStatDragonsFile;
    private FileConfiguration dbStatDragonsConfig;
    private ConfigurationSection statDragonsSection;

    public StatDragonsDB() {
        init();
    }

    public void init() {
        dbStatDragonsFile = new File("plugins/DragonTravel/databases", "statdragons.yml");

        try {
            create();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not initialize the statdragons-database.");
            e.printStackTrace();
        }

        dbStatDragonsConfig = new YamlConfiguration();
        load();

        statDragonsSection = dbStatDragonsConfig.getConfigurationSection("StatDragons");
        if (statDragonsSection == null) {
            statDragonsSection = dbStatDragonsConfig.createSection("StatDragons");
        }

        loadStationaryDragons();
    }

    private void create() {

        if (dbStatDragonsFile.exists())
            return;

        try {
            dbStatDragonsFile.createNewFile();
            copy(DragonTravel.getInstance().getResource("databases/statdragons.yml"), dbStatDragonsFile);
            Bukkit.getLogger().log(Level.INFO, "Created statdragons-database.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create the statdragons-database!");
        }


    }

    private void load() {
        try {
            dbStatDragonsConfig.load(dbStatDragonsFile);
            Bukkit.getLogger().log(Level.INFO, "Loaded statdragons-database.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "No statdragons-database found");
            e.printStackTrace();
        }
    }

    public void loadStationaryDragons() {
        if (dbStatDragonsConfig.getConfigurationSection("StatDragons") != null) {
            for (String key : dbStatDragonsConfig.getConfigurationSection("StatDragons").getKeys(false)) {
                StationaryDragon sDragon = getStatDragon(key);
                DragonTravel.getInstance().getDragonManager().getStationaryDragons().put(key.toLowerCase(), sDragon);
            }
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
     * Returns the details of the dragon with the given name.
     *
     * @param name Name of the dragon which should be returned.
     * @return The dragon as a ryedragon-object.
     */
    public StationaryDragon getStatDragon(String name) {
        name = name.toLowerCase();
        if (!DragonTravel.getInstance().getDragonManager().getStationaryDragons().containsKey(name)) {
            Object obj = statDragonsSection.get(name.toLowerCase(), null);

            if (obj == null) {
                return null;
            }
            if (obj instanceof ConfigurationSection) {
                StationaryDragon s = new StationaryDragon(((ConfigurationSection) obj).getValues(true));
                s.setName(name);
                return s;
            } else {
                StationaryDragon s = (StationaryDragon) obj;
                s.setName(name);
                return s;
            }
        }
        return DragonTravel.getInstance().getDragonManager().getStationaryDragons().get(name);
    }

    public void unloadStationaryDragons() {
        for (StationaryDragon sDragon : DragonTravel.getInstance().getDragonManager().getStationaryDragons().values()) {
            sDragon.removeDragon(false);
        }
        DragonTravel.getInstance().getDragonManager().getStationaryDragons().clear();
    }

    /**
     * Creates a new stat dragon.
     *
     * @param name   Dragon name to create.
     * @param dragon Dragon to save.
     * @return Returns true if the stat dragon was created successfully, false if not.
     */
    public boolean createStatDragon(String name, StationaryDragon dragon) {
        statDragonsSection.set(name, dragon);

        try {
            dbStatDragonsConfig.save(dbStatDragonsFile);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not write new stat dragon to config.");
            return false;
        }
    }

    /**
     * Deletes the given dragon.
     *
     * @param name Name of the stat dragon to delete
     * @return True if successful, false if not.
     */
    public boolean deleteStatDragon(String name) {
        statDragonsSection.set(name.toLowerCase(), null);

        try {
            dbStatDragonsConfig.save(dbStatDragonsFile);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not delete stat dragon from config.");
            return false;
        }
    }

    public void showStatDragons() {
        System.out.println("Stationary Dragons: ");
        statDragonsSection.getKeys(false).forEach(string -> {
            System.out.println("- " + string);
        });
    }

    public void showStatDragons(Player player) {
        player.sendMessage("Stationary Dragons: ");
        statDragonsSection.getKeys(false).forEach(string -> {
            player.sendMessage("- " + string);
        });
    }

    public FileConfiguration getDbStatDragonsConfig() {
        return dbStatDragonsConfig;
    }

    public void setDbStatDragonsConfig(FileConfiguration dbStatDragonsConfig) {
        this.dbStatDragonsConfig = dbStatDragonsConfig;
    }

    public File getDbStatDragonsFile() {
        return dbStatDragonsFile;
    }

    public void setDbStatDragonsFile(File dbStatDragonsFile) {
        this.dbStatDragonsFile = dbStatDragonsFile;
    }
}
