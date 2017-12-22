package ua.leonidius.trading.sell;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import me.onebone.economyapi.EconomyAPI;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.utils.ItemName;
import ua.leonidius.trading.utils.Message;

/**
 * Created by Leonidius20 on 22.12.17.
 */
public class Sell {

    public static boolean active = Main.getPlugin().getConfig().getBoolean("sell.active", true);
    private static boolean countMaxAmount = Main.getPlugin().getConfig().getBoolean("sell.count-max-amount", true);
    static boolean editLogging = Main.getPlugin().getConfig().getBoolean("general.shopedit-logging", true);
    static char color1 = Main.getPlugin().getConfig().getString("sell.primary-color", "a").charAt(0);
    static char color2 = Main.getPlugin().getConfig().getString("sell.secondary-color", "2").charAt(0);
    static char errorColor = 'c';

    static void sellFromHand (Player player, Item item, int slot){
        int id = item.getId();
        int meta = item.getDamage();
        String key = "s-"+id+"-"+meta;

        if (id == 0){
            Message.ID_EMPTY.print(player, errorColor);
            return;
        }

        if (!Main.sellcfg.exists(key)) {
            Message.SELL_NOT_SELLING.print(player, errorColor);
            return;
        }

        int amount = item.getCount();
        String name = ItemName.get(item);
        double price = Main.sellcfg.getDouble(key);
        double cost = price*amount;
        player.getInventory().clear(slot);
        EconomyAPI.getInstance().addMoney(player, cost);
        Message.SELL_YOU_SOLD.print(player, amount, name, id, meta, cost, Main.currency);


    }

    static void sellItem(Player player, Item item) {
        int id = item.getId();
        int meta = item.getDamage();
        int amount = item.getCount();
        String key = "s-" + String.valueOf(id) + "-" + meta;

        if (!Main.sellcfg.exists(key)) {
            Message.SELL_NOT_SELLING.print(player, 'c');
            return;
        }

        if (amount < 1) {
            Message.SELL_LESS_THAN_ONE.print(player, 'c');
            return;
        }

        if (amount > 2304){
            amount = 2304;
        }

        if (!player.getInventory().contains(item)) {
            if (countMaxAmount) {
                amount = getItemCount(player, item);
                if (amount == 0) {
                    Message.SELL_NO_ITEM.print(player, errorColor);
                    return;
                }
                Message.SELL_NO_ITEM_MAX.print(player, amount, color1, color2);
            } else {
                Message.SELL_NO_ITEM.print(player, errorColor);
                return;
            }
        }

        double price = Main.sellcfg.getDouble(key);
        double cost = price*amount;
        String name = ItemName.get(item);
        player.getInventory().removeItem(item);
        EconomyAPI.getInstance().addMoney(player, cost);

        Message.SELL_YOU_SOLD.print(player, amount, name, id, meta, cost, Main.currency, color1, color2);
    }

    private static int getItemCount(Player player, Item item){
        int amount = 0;
        PlayerInventory inventory = player.getInventory();
        for (int i=0; i < 36; i++){
            Item itemInSlot = inventory.getItem(i);
            if (itemInSlot.getId() == item.getId() && itemInSlot.getDamage() == item.getDamage()){
                amount = amount + itemInSlot.getCount();
            }
        }
        return amount;
    }

}
