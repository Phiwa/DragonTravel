package eu.phiwa.dragontravel.core.payment;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.IllegalFormatException;
import java.util.logging.Level;

public class PaymentHandler {

	Server server;

	public PaymentHandler(Server server) {
		this.server = server;
	}

	/**
	 * NORMAL (using default costs from config)
	 *
	 * @param paymenttype A constant defined in DragonTravelMain
	 *                    which is used to get the default cost
	 *                    for this paymenttype from the config.
	 * @param player
	 * @return Payment successful? If usePayment is set to false in the config, this returns "true"
	 */
	public static boolean chargePlayerNORMAL(int paymenttype, Player player) {

		if (!DragonTravelMain.getInstance().getConfigHandler().isUsePayment())
			return true;

		if (DragonTravelMain.getInstance().getConfigHandler().isByResources()) {
			return chargePlayerResourcesNORMAL(paymenttype, player);
		} else if (DragonTravelMain.getInstance().getConfigHandler().isByEconomy()) {
			return chargePlayerEconomyNORMAL(paymenttype, player);
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	private static boolean chargePlayerResourcesNORMAL(int paymenttype, Player player) {
		int amount;

		switch (paymenttype) {
			case DragonTravelMain.TRAVEL_TOSTATION:
				if (player.hasPermission("dt.nocost.travel.command") || player.hasPermission("dt.nocost.travel.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Resources.Prices.toStation");
				break;
			case DragonTravelMain.TRAVEL_TORANDOM:
				if (player.hasPermission("dt.nocost.randomtravel.command") || player.hasPermission("dt.nocost.randomtravel.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Resources.Prices.toRandom");
				break;
			case DragonTravelMain.TRAVEL_TOPLAYER:
				if (player.hasPermission("dt.nocost.ptravel"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Resources.Prices.toPlayer");
				break;
			case DragonTravelMain.TRAVEL_TOCOORDINATES:
				if (player.hasPermission("dt.nocost.ctravel"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Resources.Prices.toCoordinates");
				break;
			case DragonTravelMain.TRAVEL_TOHOME:
				if (player.hasPermission("dt.nocost.home.travel") || player.hasPermission("dt.nocost.home.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Resources.Prices.toHome");
				break;
			case DragonTravelMain.TRAVEL_TOFACTIONHOME:
				if (player.hasPermission("dt.nocost.fhome"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Resources.Prices.toFactionhome");
				break;
			case DragonTravelMain.SETHOME:
				if (player.hasPermission("dt.nocost.home.set") || player.hasPermission("dt.nocost.home.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Resources.Prices.setHome");
				break;
			case DragonTravelMain.FLIGHT:
				if (player.hasPermission("dt.nocost.flight.command") || player.hasPermission("dt.nocost.flight.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Resources.Prices.Flight");
				break;
			default:
				Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Internal Error <Code: 001> "
						+ "occured, please contact the author of the plugin!");
				player.sendMessage("[DragonTravel] An error occured, please contact the admin.");
				return false;

		}

		Inventory inv = player.getInventory();
		ItemStack is = new ItemStack(DragonTravelMain.getInstance().getConfigHandler().getPaymentItemType(), amount);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(DragonTravelMain.getInstance().getConfigHandler().getPaymentItemName());
		is.setItemMeta(im);
		if (inv.contains(is)) {
			inv.removeItem(is);
			player.updateInventory();


			String message = DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Payment.Resources.Successful.WithdrawMessage");
			message = message.replace("{amount}", "%d");
			message = String.format(message, (int) amount);
			player.sendMessage(message);
			return true;
		} else {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Payment.Resources.Error.NotEnoughResources"));
			return false;
		}
	}

	private static boolean chargePlayerEconomyNORMAL(int paymenttype, Player player) {
		double amount;

		switch (paymenttype) {
			case DragonTravelMain.TRAVEL_TOSTATION:
				if (player.hasPermission("dt.nocost.travel.command") || player.hasPermission("dt.nocost.travel.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getDouble("Payment.Economy.Prices.toStation");
				break;
			case DragonTravelMain.TRAVEL_TORANDOM:
				if (player.hasPermission("dt.nocost.randomtravel.command") || player.hasPermission("dt.nocost.randomtravel.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getDouble("Payment.Economy.Prices.toRandom");
				break;
			case DragonTravelMain.TRAVEL_TOPLAYER:
				if (player.hasPermission("dt.nocost.ptravel"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getDouble("Payment.Economy.Prices.toPlayer");
				break;
			case DragonTravelMain.TRAVEL_TOCOORDINATES:
				if (player.hasPermission("dt.nocost.ctravel"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getDouble("Payment.Economy.Prices.toCoordinates");
				break;
			case DragonTravelMain.TRAVEL_TOHOME:
				if (player.hasPermission("dt.nocost.home.travel") || player.hasPermission("dt.nocost.home.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getDouble("Payment.Economy.Prices.toHome");
				break;
			case DragonTravelMain.TRAVEL_TOFACTIONHOME:
				if (player.hasPermission("dt.nocost.fhome"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Economy.Prices.toFactionhome");
				break;
			case DragonTravelMain.SETHOME:
				if (player.hasPermission("dt.nocost.home.set") || player.hasPermission("dt.nocost.home.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getInt("Payment.Economy.Prices.setHome");
				break;
			case DragonTravelMain.FLIGHT:
				if (player.hasPermission("dt.nocost.flight.command") || player.hasPermission("dt.nocost.flight.*"))
					return true;
				amount = DragonTravelMain.getInstance().getConfig().getDouble("Payment.Economy.Prices.Flight");
				break;
			default:
				Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] Internal Error <Code: 001> "
						+ "occured, please contact the author of the plugin!");
				player.sendMessage("[DragonTravel] An error occured, please contact the admin.");
				return false;
		}

		String playername = player.getName();
		double balance = DragonTravelMain.economyProvider.getBalance(playername);

		if (amount == 0.0)
			return true;

		if (balance < amount) {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Payment.Economy.Error.NotEnoughMoney"));
			return false;
		}

		DragonTravelMain.economyProvider.withdrawPlayer(playername, amount);

		String message = DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Payment.Economy.Successful.WithdrawMessage");

		message = message.replace("{amount}", "%.2f");
		try {
			message = String.format(message, amount);
		} catch (IllegalFormatException ex) {
			message = "&cFailed to parse price into message! Cost: " + amount;
		}
		player.sendMessage(message);
		return true;
	}

	/**
	 * CUSTOMCOST (using specified cost)
	 *
	 * @param customcost A custom cost which can be specified.
	 *                   When using a payment via resources,
	 *                   the double is converted to an integer
	 *                   and loses its part behind the point
	 * @param player
	 * @return Payment successful? If usePayment is set to false in the config, this returns "true"
	 */
	public static boolean chargePlayerCUSTOMCOST(double customcost, int paymenttype, Player player) {

		boolean successful = false;

		if (!DragonTravelMain.getInstance().getConfigHandler().isUsePayment())
			return true;

		switch (paymenttype) {
			case DragonTravelMain.TRAVEL_TOSTATION:
				if (player.hasPermission("dt.nocost.travel.sign") || player.hasPermission("dt.nocost.travel.*"))
					return true;
				break;
			case DragonTravelMain.TRAVEL_TORANDOM:
				if (player.hasPermission("dt.nocost.randomtravel.sign") || player.hasPermission("dt.nocost.randomtravel.*"))
					return true;
				break;
			case DragonTravelMain.FLIGHT:
				if (player.hasPermission("dt.nocost.flight.sign") || player.hasPermission("dt.nocost.flight.*"))
					return true;
				break;
			default:
		}

		if (DragonTravelMain.getInstance().getConfigHandler().isByResources()) {
			successful = chargePlayerResourcesCUSTOMCOST(customcost, paymenttype, player);
		} else if (DragonTravelMain.getInstance().getConfigHandler().isByEconomy()) {
			successful = chargePlayerEconomyCUSTOMCOST(customcost, paymenttype, player);
		}

		return successful;
	}

	@SuppressWarnings("deprecation")
	private static boolean chargePlayerResourcesCUSTOMCOST(double customcost, int paymenttype, Player player) {


		int amount = (int) customcost;
		Inventory inv = player.getInventory();

		if (inv.contains(DragonTravelMain.getInstance().getConfigHandler().getPaymentItemType(), amount)) {
			inv.removeItem(new ItemStack(DragonTravelMain.getInstance().getConfigHandler().getPaymentItemType(), amount));
			player.updateInventory();


			String message = DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Payment.Resources.Successful.WithdrawMessage");
			message = message.replace("{amount}", "%d");
			message = String.format(message, (int) amount);
			player.sendMessage(message);
			return true;
		} else {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Payment.Resources.Error.NotEnoughResources"));
			return false;
		}
	}

	private static boolean chargePlayerEconomyCUSTOMCOST(double customcost, int paymenttype, Player player) {

		switch (paymenttype) {
			case DragonTravelMain.TRAVEL_TOSTATION:
				if (player.hasPermission("dt.nocost.travel.sign"))
					return true;
				break;
			case DragonTravelMain.TRAVEL_TORANDOM:
				if (player.hasPermission("dt.nocost.randomtravel.sign"))
					return true;
				break;
			case DragonTravelMain.FLIGHT:
				if (player.hasPermission("dt.nocost.flight.sign"))
					return true;
				break;
			default:
		}

		String playername = player.getName();
		double amount = customcost;
		double balance = DragonTravelMain.economyProvider.getBalance(playername);

		if (amount == 0.0)
			return true;

		if (balance < amount) {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Payment.Economy.Error.NotEnoughMoney"));
			return false;
		}

		DragonTravelMain.economyProvider.withdrawPlayer(playername, amount);

		String message = DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Payment.Economy.Successful.WithdrawMessage");
		message = message.replace("{amount}", "%.2f");
		message = String.format(message, amount);
		player.sendMessage(message);
		return true;
	}

	/**
	 * Is run if the config-option (payment via economy) is set to true
	 * If no economy-provider is available, it disables useEconomy
	 */
	public void setupEconomy() {
		RegisteredServiceProvider<Economy> economyProviderTemp = server.getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProviderTemp != null) {
			DragonTravelMain.economyProvider = economyProviderTemp.getProvider();
			DragonTravelMain.getInstance().getConfigHandler().setByEconomy(true);
		} else {
			Bukkit.getLogger().log(Level.SEVERE, "You enabled economy in the config, "
					+ "but DragonTravel could not find an economy-provider.\n"
					+ "DragonTravel will now go and cry a bit. :(");
			DragonTravelMain.getInstance().getConfigHandler().setByEconomy(false);
		}
	}


}
