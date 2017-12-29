package ua.leonidius.trading.help;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.utils.TextFormat;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.auction.Auction;
import ua.leonidius.trading.auction.BetCmd;
import ua.leonidius.trading.auction.StartAuctionCmd;
import ua.leonidius.trading.buy.AddBuyItemCmd;
import ua.leonidius.trading.buy.Buy;
import ua.leonidius.trading.buy.BuyCmd;
import ua.leonidius.trading.buy.DelBuyItemCmd;
import ua.leonidius.trading.sell.AddSellItemCmd;
import ua.leonidius.trading.sell.DelSellItemCmd;
import ua.leonidius.trading.sell.Sell;
import ua.leonidius.trading.sell.SellCmd;
import ua.leonidius.trading.utils.Message;

/**
 * Created by lion on 21.03.17.
 */
public class HelpCmd extends PluginCommand implements CommandExecutor {

    @SuppressWarnings("unchecked")
    public HelpCmd(){
        super ("shophelp", Main.getPlugin());
        String[] aliases = {"shelp"};
        this.setExecutor(this);
        this.setDescription(Message.CMD_HELP.toString());
        this.setUsage("/shophelp (/shelp)");
        this.setAliases(aliases);
        this.getCommandParameters().clear();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (Buy.Settings.active) {
            PluginCommand buy = new BuyCmd();
            sender.sendMessage(TextFormat.AQUA + buy.getUsage() + TextFormat.WHITE + " - " + buy.getDescription());
            PluginCommand blist = new BuyListCmd();
            sender.sendMessage(TextFormat.AQUA + blist.getUsage() + TextFormat.WHITE + " - " + blist.getDescription());
            if (sender.hasPermission("trading.editshoplist")) {
                PluginCommand abi = new AddBuyItemCmd();
                sender.sendMessage(TextFormat.AQUA + abi.getUsage() + TextFormat.WHITE + " - " + abi.getDescription());
                PluginCommand dbi = new DelBuyItemCmd();
                sender.sendMessage(TextFormat.AQUA + dbi.getUsage() + TextFormat.WHITE + " - " + dbi.getDescription());
            }
        }
        if (Sell.Settings.active){
            PluginCommand sell = new SellCmd();
            sender.sendMessage(TextFormat.AQUA+sell.getUsage()+TextFormat.WHITE+" - "+sell.getDescription());
            PluginCommand slist = new SellListCmd();
            sender.sendMessage(TextFormat.AQUA+slist.getUsage()+TextFormat.WHITE+" - "+slist.getDescription());
            if (sender.hasPermission("trading.editshoplist")) {
                PluginCommand asi = new AddSellItemCmd();
                sender.sendMessage(TextFormat.AQUA + asi.getUsage() + TextFormat.WHITE + " - " + asi.getDescription());
                PluginCommand dsi = new DelSellItemCmd();
                sender.sendMessage(TextFormat.AQUA + dsi.getUsage() + TextFormat.WHITE + " - " + dsi.getDescription());
            }
        }
        if (Auction.Settings.active) {
            PluginCommand auc = new StartAuctionCmd();
            sender.sendMessage(TextFormat.AQUA + auc.getUsage() + TextFormat.WHITE + " - " + auc.getDescription());
            PluginCommand bet = new BetCmd();
            sender.sendMessage(TextFormat.AQUA + bet.getUsage() + TextFormat.WHITE + " - " + bet.getDescription());
        }
        if (Buy.Settings.active || Sell.Settings.active || Auction.Settings.active) {
            PluginCommand id = new IdCmd();
            sender.sendMessage(TextFormat.AQUA + id.getUsage() + TextFormat.WHITE + " - " + id.getDescription());
        }
        return true;
    }
}