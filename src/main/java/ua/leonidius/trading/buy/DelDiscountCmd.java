package ua.leonidius.trading.buy;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.utils.Message;

/**
 * Created by Leonidius20 on 01.01.18.
 */
public class DelDiscountCmd extends PluginCommand implements CommandExecutor {

    @SuppressWarnings("unchecked")
    public DelDiscountCmd () {
        super ("deldiscount", Main.getPlugin());
        setExecutor(this);
        setDescription(Message.CMD_DELDISCOUNT.toString());
        setUsage("/deldiscount <ID:[meta]>");
        setPermission("trading.editshoplist");
        getCommandParameters().clear();
        CommandParameter[] cps = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
        };
        getCommandParameters().put("string", cps);
        CommandParameter [] cp = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_STRING, false),
        };
        getCommandParameters().put("default", cp);
    }


    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1) return false;

        Item item = Item.fromString(args[0]);
        int id = item.getId();
        if (id==0) return false;
        int meta = item.getDamage();
        Config config = Main.discountCfg;
        String key = "d-" + id + "-" + meta;

        if (!config.exists(key)) {
            Message.LIST_DOESNOT_EXIST.printError(sender);
            return true;
        }

        config.remove(key);
        config.save();
        config.reload();

        Message.LIST_DISCOUNT_REMOVED_BY_PLAYER.broadcastBuy(null, sender.getName(), item.getName(), id, meta);
        if (Buy.Settings.editLogging) {
            Message.LIST_DISCOUNT_REMOVED_BY_PLAYER.log("NOCOLOR", sender.getName(), item.getName(), id, meta);
        }
        return true;
    }
}
