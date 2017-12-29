package ua.leonidius.trading.sell;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.utils.Message;

/**
 * Created by lion on 05.03.17.
 */
public class SellCmd extends PluginCommand implements CommandExecutor {

    @SuppressWarnings("unchecked")
    public SellCmd(){
        super ("sell", Main.getPlugin());
        this.setExecutor(this);
        this.setDescription(Message.CMD_SELL.toString());
        String amount = Message.AMOUNT.toString();
        this.setUsage("/sell [ID:meta] ["+amount+"]");
        this.getCommandParameters().clear();
        CommandParameter[] params = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_RAW_TEXT, true),
                new CommandParameter(amount, CommandParameter.ARG_TYPE_INT, true)
        };
        this.getCommandParameters().put("default", params);
        CommandParameter[] string = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter(amount, CommandParameter.ARG_TYPE_INT, true)
        };
        this.getCommandParameters().put("string", string);
    }


    public boolean onCommand (CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            Message.CMD_CONSOLE.printError(sender);
            return true;
        }

        Player player = (Player) sender;

        if (player.getGamemode()==1) return true;

        if (args.length > 2){return false;}

        if(args.length == 0){
            Sell.sellFromHand(player, player.getInventory().getItemInHand(), player.getInventory().getHeldItemSlot());
            return true;
        }

        Item item = Item.fromString(args[0]);
        int amount;
        if (args.length == 1) amount = 1;
        else try {amount = Integer.parseInt(args[1]);} catch (Exception e) {return false;}
        item.setCount(amount);
        Sell.sellItem(player, item);
        return true;
    }

}
