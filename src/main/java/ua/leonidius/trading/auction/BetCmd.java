package ua.leonidius.trading.auction;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import ua.leonidius.trading.auction.Auction;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.utils.Message;

/**
 * Created by Leonidius20 on 14.09.17.
 */
public class BetCmd extends PluginCommand implements CommandExecutor{

    @SuppressWarnings("unchecked")
    public BetCmd(){
        super ("bet", Main.getPlugin());
        this.setExecutor(this);
        this.setDescription(Message.CMD_BET.toString());
        this.setUsage("/bet <"+Message.BID.toString()+">");
        this.getCommandParameters().clear();
        CommandParameter[] params = new CommandParameter[]{
                new CommandParameter(Message.BID.toString(), CommandParameter.ARG_TYPE_INT, false)
        };
        this.getCommandParameters().put("default", params);
    }
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            Message.CMD_CONSOLE.printError(sender);
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) return false;
        try {
            Auction.bet(player, Integer.parseInt(args[0]));
        } catch (Exception e) {return false;}
        return true;
    }
}