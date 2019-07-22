API
===

DragonTravel offers an API which can be accessed by other plugins. Currently, the API is still bery limited, but it will grow over time.

Currently implemented features:

* Create and remove stationary dragons
* Send player on a travel to a specified location

|

Import RyeDragonAPI::

    import eu.phiwa.dragontravel.api.RyeDragonAPI;


Use it to do stuff::

    Player player = ...; # Get player object
    Location loc = ...; # Get location object

    RyeDragonAPI api = RyeDragonAPI.getAPI();

    # Send player on travel to location
    try {
        api.sendOnTravel(player, loc);
    } catch (DragonException e1) {
        // Handle problem
        e1.printStackTrace();
    }

    # Create stationary dragon
    try {
        api.makeStationaryDragon(loc, "statdragon", "Fancy Dragon Name", player.getUniqueId());
    } catch (DragonException e2) {
        // Handle problem
        e2.printStackTrace();
    }

    # Remove stationary dragon
    try {
        api.removeStationaryDragon("statdragon");
    } catch (DragonException e3) {
        // Handle problem
        e3.printStackTrace();
    }
