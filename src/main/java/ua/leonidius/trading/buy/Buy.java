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
public abstract class Buy {

    static void buy (Player player, Item item){
        int amount = item.getCount();

        if (!canBuy(item)){
            Message.BUY_NOT_SELLING.printError(player);
            return;
        }

        if (amount < 1){
            Message.BUY_LESS_THAN_ONE.printError(player);
            return;
        }

        if (amount > 2304){
            amount = 2304;
        }

        if (!canAddItem(player, item)) {
            amount = getMaxByInventory(player, item);
            if (amount == 0) {
                Message.BUY_NO_SPACE.printError(player);
                return;
            }
            Message.BUY_NO_SPACE_MAX.print(player, amount);
        }

        double price = getPrice(item);
        double playerMoney = EconomyAPI.getInstance().myMoney(player);
        if (playerMoney < amount*price) {
            amount = getMaxByMoney(player, item);
            if (amount == 0) {
                Message.BUY_NOT_ENOUGH_MONEY.printError(player);
                return;
            }
            Message.BUY_NO_MONEY_MAX.print(player, amount);
        }
        double cost = amount*price;
        EconomyAPI.getInstance().reduceMoney(player, cost);
        player.getInventory().addItem(item);

        int id = item.getId();
        int meta = item.getDamage();
        String name = ItemName.get(item);
        Message.BUY_YOU_BOUGHT.print(player, amount, name, id, meta, cost, Main.settings.currency);
        if (Main.settings.logging) {
            Message.BUY_LOG.log(player.getName(), amount, name, id, meta, cost, Main.settings.currency);
        }
    }

    private static boolean canBuy (Item item){
        int id = item.getId();
        int meta = item.getDamage();
        String key = "b-"+id+"-"+meta;
        return Main.buycfg.exists(key);
    }

    public static double getPrice (Item item) {
        int id = item.getId();
        int meta = item.getDamage();
        String key = "b-"+id+"-"+meta;

        double priceWithoutDiscount = Main.buycfg.getDouble(key);

        String discountKey = "d-"+id+"-"+meta;

        if (Main.discountCfg.exists(discountKey)) {
            double discountPercent = Main.discountCfg.getDouble(discountKey);
            double discount = (priceWithoutDiscount * discountPercent) / 100;
            return priceWithoutDiscount - discount;
        }

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