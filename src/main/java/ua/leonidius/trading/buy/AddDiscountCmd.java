package ua.leonidius.trading.buy;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.utils.ItemName;
import ua.leonidius.trading.utils.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Leonidius20 on 26.09.17.
 */
public class AddDiscountCmd extends PluginCommand implements CommandExecutor {

    @SuppressWarnings("unchecked")
    public AddDiscountCmd () {
        super ("adddiscount", Main.getPlugin());
        String percent = Message.PERCENT.toString();
        setExecutor(this);
        setDescription(Message.CMD_ADDDISCOUNT.toString());
        setUsage("/adddiscount <ID:[meta]> <"+percent+">");
        setPermission("trading.editshoplist");
        getCommandParameters().clear();
        CommandParameter [] cps = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter(percent, CommandParameter.ARG_TYPE_INT, false),
        };
        getCommandParameters().put("string", cps);
        CommandParameter [] cp = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_STRING, false),
                new CommandParameter(percent, CommandParameter.ARG_TYPE_INT, false),
        };
        getCommandParameters().put("default", cp);
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 2) return false;

        Item item = Item.fromString(args[0]);
        int id = item.getId();
        if (id==0) return false;
        int meta = item.getDamage();
        String name = ItemName.get(item);

        if (!Main.buycfg.exists("b-"+id+"-"+meta)) {
            Message.SELL_NOT_SELLING.printError(sender);
            return true;
        }

        int percent;
        try {
            percent = Integer.parseInt(args[1]);
        } catch (Exception e) {
            return false;
        }
        if (percent > 100) {
            Message.LIST_DISCOUNT_MORE_THAN_HUNDRED.printError(sender);
            return true;
        }

        Config config = Main.discountCfg;
        String key = "d-" + id + "-" + meta;

        if (config.exists(key)) {
            Message.LIST_EXISTS.printError(sender);
            return true;
        }

        config.set(key, percent);
        config.save();
        config.reload();

        Message.LIST_DISCOUNT_ADDED_LOG.broadcast(null, sender.getName(), percent, name, id, meta);
        Message.LIST_DISCOUNT_ADDED_LOG.log("NOCOLOR", sender.getName(), percent, name, id, meta);
        return true;
    }

}

