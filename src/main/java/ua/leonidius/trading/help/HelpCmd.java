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
import ua.leonidius.trading.buy.*;
import ua.leonidius.trading.sell.AddSellItemCmd;
import ua.leonidius.trading.sell.DelSellItemCmd;
import ua.leonidius.trading.sell.Sell;
import ua.leonidius.trading.sell.SellCmd;
import ua.leonidius.trading.utils.Message;

import static ua.leonidius.trading.Main.settings;

/**
 * Created by Leonidius20 on 21.03.17.
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
        if (settings.buy_active) {
            PluginCommand buy = new BuyCmd();
            sender.sendMessage(TextFormat.AQUA + buy.getUsage() + TextFormat.WHITE + " - " + buy.getDescription());
            PluginCommand blist = new BuyListCmd();
            sender.sendMessage(TextFormat.AQUA + blist.getUsage() + TextFormat.WHITE + " - " + blist.getDescription());
            if (sender.hasPermission("trading.editshoplist")) {
                PluginCommand abi = new AddBuyItemCmd();
                sender.sendMessage(TextFormat.AQUA + abi.getUsage() + TextFormat.WHITE + " - " + abi.getDescription());
                PluginCommand dbi = new DelBuyItemCmd();
                sender.sendMessage(TextFormat.AQUA + dbi.getUsage() + TextFormat.WHITE + " - " + dbi.getDescription());
                PluginCommand ad = new AddDiscountCmd();
                sender.sendMessage(TextFormat.AQUA + ad.getUsage() + TextFormat.WHITE + " - " + ad.getDescription());
                PluginCommand dd = new DelDiscountCmd();
                sender.sendMessage(TextFormat.AQUA + dd.getUsage() + TextFormat.WHITE + " - " + dd.getDescription());
            }
        }
        if (settings.sell_active){
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
        if (settings.auction_active) {
            PluginCommand auc = new StartAuctionCmd();
            sender.sendMessage(TextFormat.AQUA + auc.getUsage() + TextFormat.WHITE + " - " + auc.getDescription());
            PluginCommand bet = new BetCmd();
            sender.sendMessage(TextFormat.AQUA + bet.getUsage() + TextFormat.WHITE + " - " + bet.getDescription());
        }
        if (settings.buy_active || settings.sell_active || settings.auction_active) {
            PluginCommand id = new IdCmd();
            sender.sendMessage(TextFormat.AQUA + id.getUsage() + TextFormat.WHITE + " - " + id.getDescription());
        }
        return true;
    }
}