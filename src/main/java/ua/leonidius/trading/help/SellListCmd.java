package ua.leonidius.trading.help;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.utils.ItemName;
import ua.leonidius.trading.utils.Message;

import java.util.Iterator;
import java.util.Set;

import static ua.leonidius.trading.Main.settings;

/**
 * Created by Leonidius20 on 21.03.17.
 */
public class SellListCmd extends PluginCommand implements CommandExecutor {

    @SuppressWarnings("unchecked")
    public SellListCmd(){
        super ("selllist", Main.getPlugin());
        this.setExecutor(this);
        String[] aliases = {"slist"};
        this.setDescription(Message.CMD_SELLLIST.toString());
        this.setUsage("/selllist (/slist)");
        this.setAliases(aliases);
        this.getCommandParameters().clear();
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        String output = Message.LIST_CAN_SELL.toString();
        Config cfg = Main.sellcfg;
        Set keyset = cfg.getAll().keySet();
        Iterator it = keyset.iterator();
        if (it.hasNext()) {
            while (it.hasNext()) {
                String key = it.next().toString();
                String[] idmeta = key.split("-");
                if (idmeta.length == 3) {
                    try {
                        int id = Integer.parseInt(idmeta[1]);
                        int meta = Integer.parseInt(idmeta[2]);
                        String name = ItemName.get(id, meta);
                        double price = cfg.getDouble(key);
                        output = output + " " + TextFormat.YELLOW + name
                                + TextFormat.WHITE + " (" + id + ":" + meta + ")" + " - "
                                + TextFormat.GREEN + price
                                + TextFormat.WHITE + ",";
                    } catch (NumberFormatException e) {}
                }
            }
            output = output +" "+Message.LIST_PRICES_IN.getText(settings.currency, "NOCOLOR");
            sender.sendMessage(output);
        } else {
            Message.LIST_NOTHING.printError(sender);
        }
        return true;
    }
}
