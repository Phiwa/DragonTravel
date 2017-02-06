Commands
========

Replace all variables in **< >** with the correct values, variables in **[ ]** are optional.

=======
General
=======

==========================================              ========================================================
Command                                                 Effect
==========================================              ========================================================
*/dt dismount*                                          Dismounts you from the dragon.
*/dt sethome* 	                                        Set your dragon's home.
*/dt ptoggle*	                                          Toggles whether you allow player-travels to you or not.
==========================================              ========================================================


=======
Travels
=======

==========================================              ========================================================
Command                                                 Effect
==========================================              ========================================================
*/dt travel <stationname>* 	                            Takes you to the specified station.
*/dt ctravel <x> <y> <z> [world]* 	                    Takes you to the specified location.
*/dt ptravel <playername>* 	                            Takes you to the specified player.
*/dt home* 	                                            Takes you to your dragon's home.
*/dt fhome* 	                                          Takes you to your faction's home (requires Factions)
*/dt tspawn* 	                                          Takes you to your town's spawn (requires Towny)
*/dt showstats* 	                                      Opens a list of all available stations.
==========================================              ========================================================


=======
Flights
=======

==========================================              ========================================================
Command                                                 Effect
==========================================              ========================================================
*/dt flight <flightname> [player]* 	                    Takes you (or the given player) on the specified Flight.
*/dt showflights* 	                                    Opens a list of all available flights.
*/dt showflight <flightname>* 	                        Shows a list of the flight's waypoints.
==========================================              ========================================================


==============
Administrator
==============

==========================================              ==========================================================================================
Command                                                 Effect
==========================================              ==========================================================================================
*/dt addstatdragon <name> [displayname]* 	              Creates a stationary dragon (with the specified name and the optionally specified different displayname) at the player's position
*/dt remstatdragon <name>* 	                            Removes a stationary dragon (with the specified name)
*/dt setstat <stationname>* 	                          Sets a new station with the specified name at your current location.
*/dt remstat <playername>* 	                            Removes the station with the specified name.
*/dt remdragons* 	                                      Removes all dragons without riders (except stationary dragons)
*/dt rembugdragon*                                      Temporary workaround for buggy dragons without riders. Removes the enderdragon with the shortest distance to you (within a 10 block radius).
*/dt createflight <flightname>* 	                      Creates a flight with the specified name and puts you into the flight-creation mode
*/dt setwp* 	                                          Sets a new waypoint (only works in flight-creation mode)
*/dt remlastwp* 	                                      Removes the most recently set waypoint (only works in flight-creation mode).
*/dt saveflight* 	                                      Saves the currently edited flight and ends the flight-creation mode.
*/dt remflight <flightname>* 	                          Removes the flightwith the specified name.
==========================================              ==========================================================================================
