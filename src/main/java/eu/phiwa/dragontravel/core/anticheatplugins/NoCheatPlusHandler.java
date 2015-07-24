package eu.phiwa.dragontravel.core.anticheatplugins;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Copyright (C) 2011-2013 Philipp Wagner
 * mail@phiwa.eu
 * <p>
 * Credits for one year of development go to Luca Moser (moser.luca@gmail.com/)
 * <p>
 * This file is part of the Bukkit-plugin DragonTravel.
 * <p>
 * DragonTravel is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * DragonTravel is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this project.
 * If not, see <http://www.gnu.org/licenses/>.
 */
public class NoCheatPlusHandler {

	// Gets the instance of AntiCheat
	public static boolean getNoCheatPlus() {

		Plugin plugin = Bukkit.getPluginManager().getPlugin("NoCheatPlus");

		if (plugin == null)
			return false;

		DragonTravelMain.nocheatplus = true;
		return true;
	}

}
