package eu.phiwa.dragontravel.core.hooks.payment;

import eu.phiwa.dragontravel.core.DragonTravel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class ResourcesPaymentHandler implements PaymentHandler {

    @Override
    public boolean setup() {
        return DragonTravel.getInstance().getConfigHandler().isByResources();
    }

    @Override
    public boolean chargePlayer(ChargeType type, Player player) {
        if (type.hasNoCostPermission(player)) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Payment.Free"));
            return true;
        }

        int amount;
        switch (type) {
            case TRAVEL_TOSTATION:
                amount = DragonTravel.getInstance().getConfig().getInt("Payment.Resources.Prices.toStation");
                break;
            case TRAVEL_TORANDOM:
                amount = DragonTravel.getInstance().getConfig().getInt("Payment.Resources.Prices.toRandom");
                break;
            case TRAVEL_TOPLAYER:
                amount = DragonTravel.getInstance().getConfig().getInt("Payment.Resources.Prices.toPlayer");
                break;
            case TRAVEL_TOCOORDINATES:
                amount = DragonTravel.getInstance().getConfig().getInt("Payment.Resources.Prices.toCoordinates");
                break;
            case TRAVEL_TOHOME:
                amount = DragonTravel.getInstance().getConfig().getInt("Payment.Resources.Prices.toHome");
                break;
            case TRAVEL_TOFACTIONHOME:
                amount = DragonTravel.getInstance().getConfig().getInt("Payment.Resources.Prices.toFactionhome");
                break;
            case SETHOME:
                amount = DragonTravel.getInstance().getConfig().getInt("Payment.Resources.Prices.setHome");
                break;
            case FLIGHT:
                amount = DragonTravel.getInstance().getConfig().getInt("Payment.Resources.Prices.Flight");
                break;
            default:
                throw new UnsupportedOperationException("ResourcesPaymentHandler doesn't know how to deal with a ChargeType of " + type.name() + ". Fix immediately!");
        }

        return removeItems(player, amount);
    }

    @Override
    public boolean chargePlayerExact(ChargeType type, Player player, double customCost) {
        if (type.hasNoCostPermission(player)) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Payment.Free"));
            return true;
        }

        return removeItems(player, (int) customCost);
    }

    @SuppressWarnings("deprecation")
    private boolean removeItems(Player player, int amount) {
        Inventory inv = player.getInventory();
        ItemStack item = new ItemStack(DragonTravel.getInstance().getConfigHandler().getPaymentItemType(), amount);
        // TODO: Bug leading to problems with non-manipulated items (no changes in meta data)
        //ItemMeta im = item.getItemMeta();       
        //im.setDisplayName(ChatColor.translateAlternateColorCodes('&', DragonTravel.getInstance().getConfigHandler().getPaymentItemName()));
        //item.setItemMeta(im);
        if (inv.containsAtLeast(item, amount)) {
            Map<Integer, ItemStack> leftover = inv.removeItem(item);
            if (!leftover.isEmpty()) {
                Bukkit.getLogger().warning("Removing items from " + player.getName() + "'s inventory gave a leftover; allowing payment anyways.");
            }

            player.updateInventory();

            String message = DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Payment.Resources.Successful.WithdrawMessage");
            message = message.replace("{amount}", "%d");
            message = String.format(message, amount);
            player.sendMessage(message);
            return true;
        } else {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Payment.Resources.Error.NotEnoughResources"));
            return false;
        }
    }

    @Override
    public String toString() {
        return ChatColor.BLUE + "items";
    }
}