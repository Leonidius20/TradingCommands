package ua.leonidius.trading.auction;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import ua.leonidius.trading.auction.Auction;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.sell.Sell;
import ua.leonidius.trading.sell.SellCmd;
import ua.leonidius.trading.utils.Message;

/**
 * Created by Leonidius20 on 14.09.17.
 */
public class StartAuctionCmd extends PluginCommand implements CommandExecutor {

    @SuppressWarnings("unchecked")
    public StartAuctionCmd(){
        super ("auc", Main.getPlugin());
        String amount = Message.AMOUNT.toString();
        String startPrice = Message.START_PRICE.toString();
        this.setExecutor(this);
        this.setDescription(Message.CMD_AUC.toString());
        this.setUsage("/auc [ID:[meta]] ["+amount+"]"+" "+"<"+startPrice+">");
        this.getCommandParameters().clear();

        CommandParameter[] def = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_RAW_TEXT, true),
                new CommandParameter(amount, CommandParameter.ARG_TYPE_INT, true),
                new CommandParameter(startPrice, CommandParameter.ARG_TYPE_INT, true)
        };
        this.getCommandParameters().put("default", def);
        CommandParameter[] string = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter(amount, CommandParameter.ARG_TYPE_INT, false),
                new CommandParameter(startPrice, CommandParameter.ARG_TYPE_INT, false)
        };
        this.getCommandParameters().put("defaultWithString", string);

        //Подажа всех вещей указанного типа, что есть в инвентаре
        CommandParameter[] defNoAmount = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_RAW_TEXT, true),
                new CommandParameter(startPrice, CommandParameter.ARG_TYPE_INT, true)
        };
        this.getCommandParameters().put("defaultNoAmount", defNoAmount);
        CommandParameter[] stringNoAmount = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter(startPrice, CommandParameter.ARG_TYPE_INT, false)
        };
        this.getCommandParameters().put("defaultWithStringNoAmount", stringNoAmount);

        //продажа из рук
        CommandParameter[] fromHands = new CommandParameter[]{
                new CommandParameter(startPrice, CommandParameter.ARG_TYPE_INT, false)
        };
        this.getCommandParameters().put("fromHands", fromHands);

    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            Message.CMD_CONSOLE.printError(sender);
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) return false;

        Item item;

        if (args.length >= 2) item = Item.fromString(args[0]);
        else item = player.getInventory().getItemInHand();
        if (item.getId() == 0) return false;
        if (item.hasCustomName()) {
            Message.AUC_CUSTOM_NAME.printError(player);
            return true;
        }

        int amount;

        if (args.length == 3) try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception e) {return false;}
        else amount = Sell.getItemCount(player, item);
        if (amount < 1) Message.SELL_LESS_THAN_ONE.printError(player);
        item.setCount(amount);

        double startPrice;

        try {
            if (args.length == 3) startPrice = Double.parseDouble(args[2]);
            else if (args.length == 2) startPrice = Double.parseDouble(args[1]);
            else startPrice = Double.parseDouble(args[0]);
        } catch (Exception e) {return false;}

        Auction.startAuction(item, startPrice, player);

        return true;
    }
}
