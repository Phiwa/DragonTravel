package eu.phiwa.dt.anticheatplugins;

import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

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
public class NoCheatPlusHandler implements AbstractHandler {

    static {
        // This throws an exception if the class isn't loaded
        NCPExemptionManager.getListener();
    }

    @Override
    public void startExempting(Player player) {
        if (!NCPExemptionManager.isExempted(player, CheckType.MOVING_SURVIVALFLY)
                || !NCPExemptionManager.isExempted(player, CheckType.MOVING_CREATIVEFLY)) {
            NCPExemptionManager.exemptPermanently(player, CheckType.MOVING_SURVIVALFLY);
            NCPExemptionManager.exemptPermanently(player, CheckType.MOVING_CREATIVEFLY);
        }
    }

    @Override
    public void stopExempting(Player player) {
        if (NCPExemptionManager.isExempted(player, CheckType.MOVING_SURVIVALFLY)
                || NCPExemptionManager.isExempted(player, CheckType.MOVING_CREATIVEFLY)) {
            NCPExemptionManager.unexempt(player, CheckType.MOVING_SURVIVALFLY);
            NCPExemptionManager.unexempt(player, CheckType.MOVING_CREATIVEFLY);
        }
    }
}
