package eu.phiwa.dragontravel.core.hooks.anticheat;

import fr.neatmonster.nocheatplus.NoCheatPlus;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.entity.Player;

public class NoCheatPlusHandler implements AbstractHandler {

    static {        // This throws an exception if the class isn't loaded
        NoCheatPlus.getAPI();
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