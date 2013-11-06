package eu.phiwa.dt.payment;

import java.lang.reflect.Constructor;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import com.google.common.collect.Lists;

import eu.phiwa.dt.DragonTravelMain;

public class PaymentManager {
	/**
	 * If your plugin wants to hook into DragonTravel to add a new
	 * PaymentHandler, insert yours at the front of this list with this code:
	 *
	 * <pre>
	 * eu.phiwa.dt.payment.PaymentManager.availableHandlers.add(0, MyPaymentHandler.class);
	 * </pre>
	 *
	 * The code must run before DragonTravel's onEnable().
	 */
	public static List<Class<? extends PaymentHandler>> availableHandlers = Lists.newArrayListWithCapacity(3);

	static {
		availableHandlers.add(EconomyPaymentHandler.class);
		availableHandlers.add(ResourcesPaymentHandler.class);
		availableHandlers.add(FreePaymentHandler.class);
	}

	public PaymentHandler handler = null;

	public PaymentManager(Server server) {
		for (Class<? extends PaymentHandler> clazz : availableHandlers) {
			try {
				Constructor<? extends PaymentHandler> constr = clazz.getConstructor((Class<?>[]) null);
				PaymentHandler ph = constr.newInstance((Object[]) null);
				if (ph.setup()) {
					handler = ph;
					break;
				}
			} catch (Throwable ignored) {
			}
		}

		if (handler == null) {
			DragonTravelMain.plugin.getLogger().severe("Dynamic choosing of PaymentHandler failed! This is probably a bug!");
			DragonTravelMain.plugin.getLogger().severe("Defaulting to no-charge operation.");
			handler = new FreePaymentHandler();
		}
	}

	public boolean chargePlayer(ChargeType type, Player player) {
		return handler.chargePlayer(type, player);
	}

	public boolean chargePlayerCustom(ChargeType type, Player player, double customCost) {
		return handler.chargePlayerExact(type, player, customCost);
	}
}
