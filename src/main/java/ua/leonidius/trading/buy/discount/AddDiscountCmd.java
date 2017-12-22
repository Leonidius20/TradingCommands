package ua.leonidius.trading.buy.discount;

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

/**
 * Created by Leonidius20 on 26.09.17.
 */
public class AddDiscountCmd extends PluginCommand implements CommandExecutor {

    public AddDiscountCmd () {
        super ("adddiscount", Main.getPlugin());
        setExecutor(this);
        setDescription("");
        setUsage("");
        setPermission("trading.editdiscounts");
        getCommandParameters().clear();
        CommandParameter [] cps = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter("percent", CommandParameter.ARG_TYPE_INT, false),
                new CommandParameter("duration (days)", CommandParameter.ARG_TYPE_INT, true)
        };
        getCommandParameters().put("string", cps);
        CommandParameter [] cp = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_STRING, false),
                new CommandParameter("percent", CommandParameter.ARG_TYPE_INT, false),
                new CommandParameter("duration (days)", CommandParameter.ARG_TYPE_INT, true)
        };
        getCommandParameters().put("default", cp);
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2) return false;

        Item item = Item.fromString(args[0]);
        int id = item.getId();
        if (id==0) return false;
        int meta = item.getDamage();
        String name = ItemName.get(item);
        int percent;
        try {percent = Integer.parseInt(args[1]);} catch (Exception e) {return false;}

        Config config = Main.discountCfg;
        String key = "d-" + id + "-" + meta;

        if (config.exists(key)) {
            Message.LIST_EXISTS.print(sender, 'c');
            return true;
        }

        config.set(key, percent);
        config.save();
        config.reload();

        if (args.length == 3) {
            int duration;
            try {duration = Integer.parseInt(args[2]);} catch (Exception e) {return false;}
            //set timer
        }

        //messages and logging

        return true;
    }
}
