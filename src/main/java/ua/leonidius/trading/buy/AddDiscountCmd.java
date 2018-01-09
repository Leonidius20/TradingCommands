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
                new CommandParameter(duration, CommandParameter.ARG_TYPE_INT, true),
                new CommandParameter("s/m/h/d", CommandParameter.ARG_TYPE_STRING, true)
        };
        getCommandParameters().put("string", cps);
        CommandParameter [] cp = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_STRING, false),
                new CommandParameter(percent, CommandParameter.ARG_TYPE_INT, false),
                new CommandParameter(duration, CommandParameter.ARG_TYPE_INT, true),
                new CommandParameter("s/m/h/d", CommandParameter.ARG_TYPE_STRING, true)
        };
        getCommandParameters().put("default", cp);
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2 || args.length==3) return false;

        Item item = Item.fromString(args[0]);
        final int id = item.getId();
        if (id==0) return false;
        final int meta = item.getDamage();
        final String name = ItemName.get(item);

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
            Message.LIST_DISCOUNT_ADDED_LOG.broadcast(null, sender.getName(), percent, name, id, meta);
            Message.LIST_DISCOUNT_ADDED_LOG.log("NOCOLOR", sender.getName(), percent, name, id, meta);
            return true;
        }

        if (args.length == 4) {
            int value;
            try {
                value = Integer.parseInt(args[2]);
            } catch (Exception e) {return false;}
            //duration in seconds
            int duration;
            char letter = args[3].charAt(0);
            if (letter=='s') duration = value;
            //1 minute = 60 seconds
            else if (letter=='m') duration = value*60;
            //1 hour = 3600 seconds
            else if (letter=='h') duration = value*3600;
            //1 day = 86400 seconds
            else duration = value*86400;
            Date date = new Date(duration*1000);
            class StopDiscount implements Runnable {
                public void run() {
                    if (config.exists(key)) config.remove(key);
                    Message.LIST_DISCOUNT_REMOVED.broadcast(null, name, id, meta);
                }
            }
            Main.getPlugin().getServer().getScheduler().scheduleDelayedTask(Main.getPlugin(), new StopDiscount(), (int)date.getTime());
            //TODO: Fix date formatting
            SimpleDateFormat sdf = new SimpleDateFormat("dd:hh:mm:ss");
            Message.LIST_DISCOUNT_TEMP_ADDED_LOG.broadcast(null, sender.getName(), percent, name, id, meta, sdf.format(date));
            Message.LIST_DISCOUNT_TEMP_ADDED_LOG.log("NOCOLOR", sender.getName(), percent, name, id, meta, sdf.format(date));
        }

        return true;
    }

}

