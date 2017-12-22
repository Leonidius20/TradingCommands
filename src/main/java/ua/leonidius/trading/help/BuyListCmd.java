package ua.leonidius.trading.help;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.settings.Settings;
import ua.leonidius.trading.utils.Message;
import ua.leonidius.trading.utils.ItemName;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by lion on 04.03.17.
 */
public class BuyListCmd extends PluginCommand implements CommandExecutor{

    public BuyListCmd(){
        super ("buylist", Main.getPlugin());
        this.setExecutor(this);
        String[] aliases = {"blist"};
        this.setDescription(Message.CMD_BUYLIST.getCleanText());
        this.setAliases(aliases);
        this.setUsage("/buylist (/blist)");
        this.getCommandParameters().clear();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        String output;
        Config cfg;
        if (command == Main.getPlugin().getCommand("buylist")){
            output = Message.LIST_CAN_BUY.getCleanText();
            cfg = Main.buycfg;
        } else if (command == Main.getPlugin().getCommand("selllist")){
            output = Message.LIST_CAN_SELL.getCleanText();
            cfg = Main.sellcfg;
        } else {
            return false;
        }
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
                        String price = cfg.getString(key);
                        output = output+" "+TextFormat.YELLOW+name+TextFormat.WHITE+" ("+id+":"+meta+")"+" - "+TextFormat.GREEN+price+TextFormat.WHITE+",";
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
            output = output +" "+Message.LIST_PRICES_IN.getCleanText(Settings.general.currency);
            sender.sendMessage(output);
        } else {
            Message.LIST_NOTHING.print(sender, "NOCOLOR");
        }
        /*Object[] keyobj = cfg.getAll().keySet().toArray();
        for (Object obj: keyobj){
            String key = obj.toString();
            String[] idmeta = key.split("-");
            if (idmeta.length==3) {
                String id = idmeta[1];
                String meta = idmeta[2];
                String name = ItemName.getItemName(Integer.parseInt(id), Integer.parseInt(meta));
                String price = cfg.getString(key);
                output = output + " " + name +" ("+id+":"+meta+")"+" - "+price+",";
            }
        }
        output = output +" "+Message.LIST_PRICES_IN.getText("NOCOLOR");
        sender.sendMessage(output);*/
        return true;
    }
}
