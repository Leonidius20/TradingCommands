package ua.leonidius.trading.help;

import cn.nukkit.command.PluginCommand;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.help.BuyListCmd;
import ua.leonidius.trading.utils.Message;

/**
 * Created by lion on 21.03.17.
 */
public class SellListCmd extends PluginCommand {

    @SuppressWarnings("unchecked")
    public SellListCmd(){
        super ("selllist", Main.getPlugin());
        this.setExecutor(new BuyListCmd());
        String[] aliases = {"slist"};
        this.setDescription(Message.CMD_SELLLIST.toString());
        this.setUsage("/selllist (/slist)");
        this.setAliases(aliases);
        this.getCommandParameters().clear();
    }

}
