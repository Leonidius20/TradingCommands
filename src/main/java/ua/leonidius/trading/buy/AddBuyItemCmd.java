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
import ua.leonidius.trading.utils.ItemName;

import static ua.leonidius.trading.Main.settings;

/**
 * Created by Leonidius20 on 05.03.17.
 */
public class AddBuyItemCmd extends PluginCommand implements CommandExecutor{

    @SuppressWarnings("unchecked")
    public AddBuyItemCmd(){
        super ("addbuyitem", Main.getPlugin());
        String price = Message.PRICE.toString();
        String[] aliases = {"abi"};
        this.setExecutor(this);
        this.setDescription(Message.CMD_ADDBUYITEM.toString());
        this.setUsage("/addbuyitem <ID:[meta]> <"+price+">");
        this.setAliases(aliases);
        this.setPermission("trading.editshoplist");
        this.getCommandParameters().clear();
        CommandParameter[] def = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_RAW_TEXT, false),
                new CommandParameter(price, CommandParameter.ARG_TYPE_INT, true)
        };
        this.getCommandParameters().put("default", def);
        CommandParameter[] string = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter(price, CommandParameter.ARG_TYPE_INT, false)
        };
        this.getCommandParameters().put("string", string);
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (args.length != 2) return false;
        Item item = Item.fromString(args[0]);
        int id = item.getId();
        if (id==0) return false;
        int meta = item.getDamage();
        String name = ItemName.get(item);
        double price;
        try {price = Integer.parseInt(args[1]);} catch (Exception e) {return false;}

        Config config = Main.buycfg;
        String key = "b-" + id + "-" + meta;

        if (config.exists(key)) {
            Message.LIST_EXISTS.printError(sender);
            return true;
        }

        config.set(key, price);
        config.save();
        config.reload();
        Message.LIST_BUY_ADDED.print(sender, name, id, meta, price, settings.currency);
        Message.LIST_BUY_ADDED_LOG.log(sender.getName(), name, id, meta, price, settings.currency, "NOCOLOR");
        Message.LIST_BUY_ADDED_LOG.broadcast("trading.editshoplist", '7', '7', sender.getName(), name, id, meta, price, settings.currency);
        return true;
    }
}
