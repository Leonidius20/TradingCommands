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
        String duration = Message.DURATION.toString();
        setExecutor(this);
        setDescription(Message.CMD_ADDDISCOUNT.toString());
        setUsage("/adddiscount <ID:[meta]> <"+percent+"> ["+duration+"]");
        setPermission("trading.editshoplist");
        getCommandParameters().clear();
        CommandParameter [] cps = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter(percent, CommandParameter.ARG_TYPE_INT, false),
                new CommandParameter(duration, CommandParameter.ARG_TYPE_INT, true)
        };
        getCommandParameters().put("string", cps);
        CommandParameter [] cp = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_STRING, false),
                new CommandParameter(percent, CommandParameter.ARG_TYPE_INT, false),
                new CommandParameter(duration, CommandParameter.ARG_TYPE_INT, true)
        };
        getCommandParameters().put("default", cp);
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2) return false;

        Item item = Item.fromString(args[0]);
        final int id = item.getId();
        if (id==0) return false;
        final int meta = item.getDamage();
        final String name = ItemName.get(item);

        int percent;
        try {
            percent = Integer.parseInt(args[1]);
        } catch (Exception e) {
            return false;
        }

        final Config config = Main.discountCfg;
        final String key = "d-" + id + "-" + meta;

        if (config.exists(key)) {
            Message.LIST_EXISTS.printError(sender);
            return true;
        }

        config.set(key, percent);
        config.save();
        config.reload();

        if (args.length == 2) {
            Message.LIST_DISCOUNT_ADDED_LOG.broadcastBuy(null, sender.getName(), percent, name, id, meta);
            if (Buy.Settings.editLogging) {
                Message.LIST_DISCOUNT_ADDED_LOG.log("NOCOLOR", sender.getName(), percent, name, id, meta);
            }
            return true;
        }

        if (args.length == 3) {
            int duration;
            try {duration = Integer.parseInt(args[2]);} catch (Exception e) {return false;}
            class StopDiscount implements Runnable {
                public void run() {
                    if (config.exists(key)) config.remove(key);
                    Message.LIST_DISCOUNT_REMOVED.broadcastBuy (null, name, id, meta);
                }
            }
            Main.getPlugin().getServer().getScheduler().scheduleDelayedTask(Main.getPlugin(), new StopDiscount(), duration*86400000);
            Date date = new Date(duration*86400000);
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
            Message.LIST_DISCOUNT_TEMP_ADDED_LOG.broadcastBuy(null, sender.getName(), percent, name, id, meta, sdf.format(date));
            if (Buy.Settings.editLogging) {
                Message.LIST_DISCOUNT_TEMP_ADDED_LOG.log("NOCOLOR", sender.getName(), percent, name, id, meta, sdf.format(date));
            }
        }

        return true;
    }

}

