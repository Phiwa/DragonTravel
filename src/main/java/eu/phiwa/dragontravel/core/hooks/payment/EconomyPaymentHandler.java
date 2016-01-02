package eu.phiwa.dragontravel.core.hooks.payment;

import eu.phiwa.dragontravel.core.DragonTravel;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public class EconomyPaymentHandler implements PaymentHandler {
    private Economy economyProvider;

    @Override
    public boolean setup() {
        if (!DragonTravel.getInstance().getConfigHandler().isByEconomy()) {
            return false;
        }

        RegisteredServiceProvider<Economy> economyRSP = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyRSP != null) {
            economyProvider = economyRSP.getProvider();
            return true;
        }
        // XXX logger
        Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] You enabled economy in the config, but DragonTravel could not find a Vault economy provider.\n" + "DragonTravel will now go and cry a bit. :(");
        return false;
    }

    @Override
    public boolean chargePlayer(ChargeType type, Player player) {

        if (type.hasNoCostPermission(player)) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Payment.Free"));
            return true;
        }

        double amount;
        switch (type) {
            case TRAVEL_TOSTATION:
                amount = DragonTravel.getInstance().getConfig().getDouble("Payment.Economy.Prices.toStation");
                break;
            case TRAVEL_TORANDOM:
                amount = DragonTravel.getInstance().getConfig().getDouble("Payment.Economy.Prices.toRandom");
                break;
            case TRAVEL_TOPLAYER:
                amount = DragonTravel.getInstance().getConfig().getDouble("Payment.Economy.Prices.toPlayer");
                break;
            case TRAVEL_TOCOORDINATES:
                amount = DragonTravel.getInstance().getConfig().getDouble("Payment.Economy.Prices.toCoordinates");
                break;
            case TRAVEL_TOHOME:
                amount = DragonTravel.getInstance().getConfig().getDouble("Payment.Economy.Prices.toHome");
                break;
            case TRAVEL_TOFACTIONHOME:
                amount = DragonTravel.getInstance().getConfig().getDouble("Payment.Economy.Prices.toFactionhome");
                break;
            case TRAVEL_TOTOWNSPAWN:
                amount = DragonTravel.getInstance().getConfig().getDouble("Payment.Economy.Prices.toTownSpawn");
                break;
            case SETHOME:
                amount = DragonTravel.getInstance().getConfig().getDouble("Payment.Economy.Prices.setHome");
                break;
            case FLIGHT:
                amount = DragonTravel.getInstance().getConfig().getDouble("Payment.Economy.Prices.Flight");
                break;
            default:
                throw new UnsupportedOperationException("EconomyPaymentHandler doesn't know how to deal with a ChargeType of " + type.name() + ". Fix immediately!");
        }

        return subtractBalance(player, amount);
    }

    @Override
    public boolean chargePlayerExact(ChargeType type, Player player, double customCost) {
        if (type.hasNoCostPermission(player)) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Payment.Free"));
            return true;
        }

        return subtractBalance(player, customCost);
    }

    private boolean subtractBalance(Player player, double amount) {
        if (amount == 0.0)
            return true;

        if (!economyProvider.has(player, amount)) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Payment.Economy.Error.NotEnoughMoney"));
            return false;
        } else {
            economyProvider.withdrawPlayer(player, amount);

            String message = DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Payment.Economy.Successful.WithdrawMessage");
            message = message.replace("{amount}", Double.toString(amount));
            player.sendMessage(message);
            return true;
        }
    }

    @Override
    public String toString() {
        return ChatColor.GREEN + "Vault";
    }
}