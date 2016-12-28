Permissions
===========

Examples can be found at the end of the page.

===========
Permissions
===========

==========================================              ========================================================================================================================================================================================
Permission                                              | Description
==========================================              ========================================================================================================================================================================================
dt.home                                                 | Travel to and set home (combines the two child permissions)
dt.sethome                                              | Only set own home
dt.travelhome                                           | Only travel to own home
dt.ctravel                                              | Travel to coordinates
dt.ptravel                                              | Travel to a player
dt.fhome                                                | Travel to faction-home (requires Factions)
dt.tspawn                                               | Travel to town spawn (requires Towny)
dt.travel.*                                             | Travel to all stations
dt.travel.<stationname>                                 | Travel to a specified station
dt.flight.*                                             | Using all flights
dt.flight.<flightname>                                  | Using the specified flight
dt.admin.*                                              | All administrative functions
dt.admin.reload                                         | Permission to reload config files
dt.admin.signs                                          | All administrative functions connected to signs
dt.admin.stations                                       | All administrative functions connected to stations
dt.admin.flights                                        | All administrative functions connected to flights
dt.admin.statdragon                                     | Create and remove stationary dragons
dt.admin.remdragons                                     | Remove dragons without riders near the player
dt.nocost.*                                             | Player doesn’t need to pay for anything
dt.nocost.travel.*                                      | Player doesn’t need to pay for travels at all
dt.nocost.travel.command                                | Player doesn’t need to pay for travels by command
dt.nocost.travel.sign                                   | Player doesn’t need to pay for travels by sign
dt.nocost.randomtravel.*                                | Player doesn’t need to pay for travels to a random location at all
dt.nocost.randomtravel.command                          | Player doesn’t need to pay for travels to a random location by command
dt.nocost.randomtravel.sign                             | Player doesn’t need to pay for travels to a random location by sign
dt.nocost.ptravel                                       | Player doesn’t need to pay for travels to players
dt.nocost.ctravel                                       | Player doesn’t need to pay for travels to coordinates
dt.nocost.fhome                                         | Player doesn’t need to pay for travels to faction’s home
dt.nocost.flight.*                                      | Player doesn’t need to pay for flights at all
dt.nocost.flight.command                                | Player doesn’t need to pay for flights by command
dt.nocost.flight.sign                                   | Player doesn’t need to pay for flights by sign
dt.nocost.home.*                                        | Player doesn’t need to pay for home-stuff at all
dt.nocost.home.travel                                   | Player doesn’t need to pay for home-stuff for travelling to his home
dt.nocost.home.set                                      | Player doesn’t need to pay for home-stuff for setting his home
dt.nocost.ctravel                                       | Player doesn’t need to pay for travel to coordinates
dt.nocost.ptravel                                       | Player doesn’t need to pay for travel to players
dt.ptoggle                                              | Allows a player to toggle ptravels to him on/off
dt.notrequireitem.*                                     | Player doesn’t need the “requiredItem” to use DT at all
dt.notrequireitem.travel                                | Player doesn’t need the “requiredItem” to use DT for travels
dt.notrequireitem.flight                                | Player doesn’t need the “requiredItem” to use DT for flights
dt.ignoreusestations.*                                  | Player doesn’t need to be at a station to use anything
dt.ignoreusestations.travels                            | Player doesn’t need to be at a station to use travels
dt.ignoreusestations.flights                            | Player doesn’t need to be at a station to use flights
dt.ignoredragonlimit                                    | Allows player to mount a dragon, even if the server has already
                                                        | reached the limit of dragons set in the config
dt.ignoreminheight                                      | Allows player to mount a dragon, even if he is below
                                                        | the minimum mount height set in the config
dt.ignoredamagerestriction                              | Allows player to mount a dragon, even if the server has already received
                                                        | damage and the cooldown time set in the config has not passed since then.
==========================================              ========================================================================================================================================================================================


========
Examples
========

==========================================              ========================================================================================================================================================================================
dt.travel.stationatspawn                                | A player with this permission can travel to the starion "stationatspawn”
dt.travel.*                                             | A player with this permission can travel to any station
dt.flight.exampleflight                                 | A player with this permission can use the flight “exampleflight”
dt.flight.*                                             | A player with this permission can use all flights
dt.nocost.travel.command                                | A player with this permission can travel by command                                                        | without getting charged for it
==========================================              ========================================================================================================================================================================================
