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

/**
 * Created by lion on 05.03.17.
 */
public class DelBuyItemCmd extends PluginCommand implements CommandExecutor{

    public DelBuyItemCmd(){
        super ("delbuyitem", Main.getPlugin());
        String[] aliases = {"dbi"};
        this.setExecutor(this);
        this.setDescription(Message.CMD_DELBUYITEM.getCleanText());
        this.setUsage("/delbuyitem <ID>");
        this.setAliases(aliases);
        this.setPermission("trading.editshoplist");
        this.getCommandParameters().clear();
        CommandParameter[] def = new CommandParameter[]{
                new CommandParameter("id:meta", CommandParameter.ARG_TYPE_RAW_TEXT, false),
        };
        this.getCommandParameters().put("default", def);
        CommandParameter[] string = new CommandParameter[]{
                new CommandParameter("id", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
        };
        this.getCommandParameters().put("string", string);
    }

    public boolean onCommand (CommandSender sender, Command command, String label, String[] args){
        if (args.length != 1) return false;
        Item item = Item.fromString(args[0]);
        int id = item.getId();
        if (id==0) return false;
        int meta = item.getDamage();
        String name = ItemName.get(item);
        Config config = Main.buycfg;
        String key = "b-" + id + "-" + meta;
        if (config.exists(key)) {
            config.remove(key);
            config.save();
            config.reload();
            Message.LIST_BUY_DELETED.print(sender, name, id, meta, Buy.color1, Buy.color2);
            if (Buy.editLogging) {
                Message.LIST_BUY_DELETED_LOG.log(sender.getName(), name, id, meta, "NOCOLOR");
            }
            Message.LIST_BUY_DELETED_LOG.broadcast("trading.editshoplist", '7','7', sender.getName(), name, id, meta);
        } else Message.LIST_DOESNOT_EXIST.print(sender, Buy.errorColor);
        return true;
    }
}
