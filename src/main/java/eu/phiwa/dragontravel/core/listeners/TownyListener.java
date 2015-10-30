package eu.phiwa.dragontravel.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.entity.EnderDragon;
import com.palmergames.bukkit.towny.event.MobRemovalEvent;
import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.nms.v1_8_R3.RyeDragon;

public class TownyListener implements Listener
{
	/*
	Initial attempt to try to block the entity removal of towny for DragonTravel Dragons, can't seem to get
	this to work. Maybe You can help? For now, default towny settings 
	*/
	/*
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnderDragonRemoval(MobRemovalEvent e)
	{
		if(e.getEntityType() != null){
			if (DragonTravel.getInstance().getConfigHandler().isOnlydragontraveldragons() && e.getEntity() instanceof RyeDragon){
				e.setCancelled(true);
			}
			if (DragonTravel.getInstance().getConfigHandler().isAlldragons() && e.getEntity() instanceof EnderDragon){
				e.setCancelled(true);
			}
			if(e.getEntity().getType().toString()=="ENDER_DRAGON"){
                e.setCancelled(true);				
			}
		}
	}*/
}