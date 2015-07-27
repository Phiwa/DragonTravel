package eu.phiwa.dragontravel.core.listeners;

import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import eu.phiwa.dragontravel.core.DragonTravelMain;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeroesListener implements Listener {

    @EventHandler
    public void onWeapon(WeaponDamageEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            if (event.getEntity().getPassenger() != null) {
                if (event.getEntity().getPassenger() instanceof Player) {
                    Player player = (Player) event.getEntity().getPassenger();
                    if (DragonTravelMain.listofDragonriders.containsKey(player))
                        if (!DragonTravelMain.getInstance().getConfig().getBoolean("VulnerableRiders"))
                            event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onMagic(SkillDamageEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            if (event.getEntity().getPassenger() != null) {
                if (event.getEntity().getPassenger() instanceof Player) {
                    Player player = (Player) event.getEntity().getPassenger();
                    if (DragonTravelMain.listofDragonriders.containsKey(player))
                        if (!DragonTravelMain.getInstance().getConfig().getBoolean("VulnerableRiders"))
                            event.setCancelled(true);
                }
            }
        }
    }

}
