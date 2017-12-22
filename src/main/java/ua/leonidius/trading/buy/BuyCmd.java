package ua.leonidius.trading.buy;

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
import ua.leonidius.trading.utils.ItemName;
import ua.leonidius.trading.utils.MaxStackSize;
import ua.leonidius.trading.utils.Message;

/**
 * Created by lion on 05.03.17.
 */
public class BuyCmd extends PluginCommand implements CommandExecutor{

    public BuyCmd(){
        super ("buy", Main.getPlugin());
        this.setExecutor(this);
        this.setDescription(Message.CMD_BUY.getCleanText());
        this.setUsage("/buy <ID> ["+Message.AMOUNT.getCleanText()+"]");
        this.getCommandParameters().clear();
        CommandParameter[] def = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_RAW_TEXT, false),
                new CommandParameter(Message.AMOUNT.getCleanText(), CommandParameter.ARG_TYPE_INT, true)
        };
        this.getCommandParameters().put("default", def);
        CommandParameter[] string = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter(Message.AMOUNT.getCleanText(), CommandParameter.ARG_TYPE_INT, true)
        };
        this.getCommandParameters().put("string", string);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Message.CMD_CONSOLE.print(sender, 'c');
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 1 && args.length != 2) return false;

        Item item = Item.fromString(args[0]);
        if (item.getId()==0) return false;

        int amount;
        if (args.length == 1){
            amount = 1;
        } else try {amount = Integer.parseInt(args[1]);} catch (Exception e) {return false;}
        item.setCount(amount);

        Buy.buy(player, item);

        return true;
    }

}