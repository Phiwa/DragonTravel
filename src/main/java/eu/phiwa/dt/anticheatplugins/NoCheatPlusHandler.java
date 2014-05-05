package eu.phiwa.dt.anticheatplugins;

import org.bukkit.plugin.Plugin;

import eu.phiwa.dt.DragonTravelMain;

/**
 * Copyright (C) 2011-2013 Philipp Wagner mail@phiwa.eu
 * 
 * Credits for one year of development go to Luca Moser
 * (moser.luca@gmail.com/)
 * 
 * This file is part of the Bukkit-plugin DragonTravel.
 * 
 * DragonTravel is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * DragonTravel is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this project. If not, see <http://www.gnu.org/licenses/>.
 */
public class NoCheatPlusHandler {

	// Gets the instance of AntiCheat
	public static boolean getNoCheatPlus() {

		Plugin plugin = DragonTravelMain.pm.getPlugin("NoCheatPlus");

		if (plugin == null)
			return false;

		DragonTravelMain.nocheatplus = true;
		return true;
	}

}
