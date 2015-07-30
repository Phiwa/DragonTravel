package eu.phiwa.dragontravel.core.movement.flight;

import eu.phiwa.dragontravel.core.DragonTravel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class FlightEditor implements Listener {

    private HashMap<Player, Flight> editors = new HashMap<>();
    private HashMap<Block, Block> wayPointMarkers = new HashMap<>();

    public void addEditor(Player player, String flightName, String displayName) {
        if (!editors.containsKey(player))
            editors.put(player, new Flight(flightName, displayName));
    }

    public boolean isEditor(Player player) {
        return editors.containsKey(player);
    }

    public boolean removeEditor(Player player) {
        return editors.remove(player) != null;
    }

    @EventHandler
    public void onWP(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if (!editors.containsKey(player))
            return;

        if (player.getItemInHand().getType() != Material.BOWL)
            return;

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Flight flight = editors.get(player);
            if (!flight.getWayPoints().isEmpty()) {
                flight.removelastWayPoint();
            }

            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.WaypointRemoved"));
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Flight flight = editors.get(player);
            WayPoint wp = new WayPoint();
            wp.setX(loc.getBlockX());
            wp.setY(loc.getBlockY());
            wp.setZ(loc.getBlockZ());
            wp.setWorldName(loc.getWorld().getName());
            flight.addWayPoint(wp);

            // Create a marker at the waypoint
            wp.setMarker(player);
            Block block = player.getLocation().getBlock();
            wayPointMarkers.put(block, block);

            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.WaypointAdded"));
        }
    }

    public HashMap<Player, Flight> getEditors() {
        return editors;
    }

    public void setEditors(HashMap<Player, Flight> editors) {
        this.editors = editors;
    }

    public HashMap<Block, Block> getWayPointMarkers() {
        return wayPointMarkers;
    }

    public void setWayPointMarkers(HashMap<Block, Block> wayPointMarkers) {
        this.wayPointMarkers = wayPointMarkers;
    }
}
