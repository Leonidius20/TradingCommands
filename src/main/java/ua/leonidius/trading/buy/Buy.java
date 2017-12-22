package ua.leonidius.trading.buy;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import me.onebone.economyapi.EconomyAPI;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.utils.ItemName;
import ua.leonidius.trading.utils.MaxStackSize;
import ua.leonidius.trading.utils.Message;

/**
 * Created by Leonidius20 on 26.09.17.
 */
public class Buy {
    public static boolean active = Main.getPlugin().getConfig().getBoolean("buy.active", true);
    private static boolean countMaxAmount = Main.getPlugin().getConfig().getBoolean("buy.count-max-amount", true);
    static boolean editLogging = Main.getPlugin().getConfig().getBoolean("general.shopedit-logging", true);
    static char color1 = Main.getPlugin().getConfig().getString("buy.primary-color", "a").charAt(0);
    static char color2 = Main.getPlugin().getConfig().getString("buy.secondary-color", "2").charAt(0);
    static char errorColor = 'c';

    protected static void buy (Player player, Item item){
        int amount = item.getCount();

        if (!canBuy(item)){
            Message.BUY_NOT_SELLING.print(player, errorColor);
            return;
        }

        if (amount < 1){
            Message.BUY_LESS_THAN_ONE.print(player, errorColor);
            return;
        }

        if (amount > 2304){
            amount = 2304;
        }

        if (!canAddItem(player, item)) {
            if (countMaxAmount) {
                amount = getMaxByInventory(player, item);
                if (amount == 0) {
                    Message.BUY_NO_SPACE.print(player, errorColor);
                    return;
                }
                Message.BUY_NO_SPACE_MAX.print(player, amount, color1, color2);
            } else {
                Message.BUY_NO_SPACE.print(player, errorColor);
                return;
            }
        }

        double price = getPrice(item);
        double playerMoney = EconomyAPI.getInstance().myMoney(player);
        if (playerMoney < amount*price) {
            if (countMaxAmount) {
                amount = getMaxByMoney(player, item);
                if (amount == 0) {
                    Message.BUY_NOT_ENOUGH_MONEY.print(player, errorColor);
                    return;
                }
                Message.BUY_NO_MONEY_MAX.print(player, amount, color1, color2);
            } else {
                Message.BUY_NOT_ENOUGH_MONEY.print(player, errorColor);
                return;
            }
        }
        double cost = amount*price;
        EconomyAPI.getInstance().reduceMoney(player, cost);
        player.getInventory().addItem(item);

        int id = item.getId();
        int meta = item.getDamage();
        String name = ItemName.get(item);
        Message.BUY_YOU_BOUGHT.print(player, amount, name, id, meta, cost, color1, color2);
    }

    private static boolean canBuy (Item item){
        int id = item.getId();
        int meta = item.getDamage();
        String key = "b-"+id+"-"+meta;
        return Main.buycfg.exists(key);
    }

    private static double getPrice (Item item) {
        int id = item.getId();
        int meta = item.getDamage();
        String key = "b-"+id+"-"+meta;
        double priceWithoutDiscount = Main.buycfg.getDouble(key);
        //apply discount
        return priceWithoutDiscount;
    }

    private static int getMaxByMoney(Player player, Item item){
        double price = getPrice(item);
        double money = EconomyAPI.getInstance().myMoney(player);
        return (int)Math.floor(money/price);
    }

    private static boolean canAddItem (Player player, Item item){
        return (getMaxByInventory(player, item) >= item.getCount());
    }

    private static int getMaxByInventory(Player player, Item item){
        PlayerInventory inventory = player.getInventory();
        int maxStack = MaxStackSize.get(item);
        int amount = 0;
        for (int i=0; i < 36; i++){
            Item itemInSlot = inventory.getItem(i);
            if (itemInSlot.getId() == Item.AIR){
                amount = amount + maxStack;
            } else if (itemInSlot.getId() == item.getId() && itemInSlot.getDamage() == item.getDamage()){
                amount = amount +  (maxStack-itemInSlot.getCount());
            }
        }
        return amount;
    }

}