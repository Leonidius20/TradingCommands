package ua.leonidius.trading.sell;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import me.onebone.economyapi.EconomyAPI;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.settings.Settings;
import ua.leonidius.trading.utils.Message;
import ua.leonidius.trading.utils.ItemName;

/**
 * Created by lion on 05.03.17.
 */
public class SellCmd extends PluginCommand implements CommandExecutor {

    public SellCmd(){
        super ("sell", Main.getPlugin());
        this.setExecutor(this);
        this.setDescription(Message.CMD_SELL.getCleanText());
        this.setUsage("/sell [ID:meta] ["+Message.AMOUNT.getCleanText()+"]");
        this.getCommandParameters().clear();
        CommandParameter[] params = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_RAW_TEXT, true),
                new CommandParameter(Message.AMOUNT.getCleanText(), CommandParameter.ARG_TYPE_INT, true)
        };
        this.getCommandParameters().put("default", params);
        CommandParameter[] string = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter(Message.AMOUNT.getCleanText(), CommandParameter.ARG_TYPE_INT, true)
        };
        this.getCommandParameters().put("string", string);
    }


    public boolean onCommand (CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            Message.CMD_CONSOLE.print(sender, 'c');
            return true;
        }

        Player player = (Player) sender;

        if (player.getGamemode()==1) return true;

        if (args.length > 2){return false;}

        if(args.length == 0){
            sellFromHand(player, player.getInventory().getItemInHand(), player.getInventory().getHeldItemSlot());
            return true;
        }

        Item item = Item.fromString(args[0]);
        int amount;
        if (args.length == 1) amount = 1;
        else try {amount = Integer.parseInt(args[1]);} catch (Exception e) {return false;}
        item.setCount(amount);
        sellItem(player, item);
        return true;
    }

    private static void sellFromHand (Player player, Item item, int slot){
        int id = item.getId();
        int meta = item.getDamage();
        int amount = item.getCount();
        String name = ItemName.get(item);
        String key = "s-"+id+"-"+meta;
        if (id == 0){
            Message.ID_EMPTY.print(player, 'c');
            return;
        }
        if (Main.sellcfg.exists(key)){
            double price = Main.sellcfg.getDouble(key);
            double cost = price*amount;
            player.getInventory().clear(slot);
            EconomyAPI.getInstance().addMoney(player, cost);
            Message.SELL_YOU_SOLD.print(player, amount, name, id, meta, cost, Settings.general.currency);
            if (Settings.general.tradingLogging) {
                Message.SOLD_LOG.log(player.getName(), amount, name, id, meta, cost, Settings.general.currency, "NOCOLOR");
            }
        } else {
            Message.SELL_NOT_SELLING.print(player, 'c');
        }
    }

    private static void sellItem(Player player, Item item) {
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
            if (Settings.general.countMaxAmount) {
                amount = getItemCount(player, item);
                if (amount == 0) {
                    Message.SELL_NO_ITEM.print(player, 'c');
                    return;
                }
                Message.SELL_NO_ITEM_MAX.print(player, amount, Settings.sell.primaryColor, Settings.sell.secondaryColor);
            } else {
                Message.SELL_NO_ITEM.print(player, 'c');
                return;
            }
        }
        double price = Main.sellcfg.getDouble(key);
        double cost = price*amount;
        String name = ItemName.get(item);
        player.getInventory().removeItem(item);
        EconomyAPI.getInstance().addMoney(player, cost);
        Message.SELL_YOU_SOLD.print(player, amount, name, id, meta, cost,Settings.general.currency, Settings.sell.primaryColor, Settings.sell.secondaryColor);
        if (Settings.general.tradingLogging) {
            Message.SOLD_LOG.log(player.getName(), amount, name, id, meta, cost, Settings.general.currency, "NOCOLOR");
        }
    }

    public static int getItemCount(Player player, Item item){
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
