package ua.leonidius.trading.sell;


import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.settings.Settings;
import ua.leonidius.trading.utils.Message;
import ua.leonidius.trading.utils.ItemName;

/**
 * Created by lion on 05.03.17.
 */
public class DelSellItemCmd extends PluginCommand implements CommandExecutor{

    public DelSellItemCmd(){
        super ("delsellitem", Main.getPlugin());
        String[] aliases = {"dsi"};
        this.setExecutor(this);
        this.setDescription(Message.CMD_DELSELLITEM.getText("NOCOLOR"));
        this.setUsage("/delsellitem <ID>");
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
        Config config = Main.sellcfg;
        String key = "s-" + id + "-" + meta;
        if (config.exists(key)) {
            config.remove(key);
            config.save();
            config.reload();
            Message.LIST_SELL_DELETED.print(sender, name, id, meta, Settings.sell.primaryColor, Settings.sell.secondaryColor);
            if (Settings.general.editLogging) {
                Message.LIST_SELL_DELETED_LOG.log(sender.getName(), name, id, meta, "NOCOLOR");
            }
            Message.LIST_SELL_DELETED_LOG.broadcast("trading.editshoplist", '7','7', sender.getName(), name, id, meta);
        } else Message.LIST_DOESNOT_EXIST.print(sender, 'c');
        return true;
    }
}
