package ua.leonidius.trading;

import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import ua.leonidius.trading.auction.BetCmd;
import ua.leonidius.trading.auction.StartAuctionCmd;
import ua.leonidius.trading.buy.AddBuyItemCmd;
import ua.leonidius.trading.buy.Buy;
import ua.leonidius.trading.buy.BuyCmd;
import ua.leonidius.trading.help.BuyListCmd;
import ua.leonidius.trading.buy.DelBuyItemCmd;
import ua.leonidius.trading.help.*;
import ua.leonidius.trading.sell.AddSellItemCmd;
import ua.leonidius.trading.sell.DelSellItemCmd;
import ua.leonidius.trading.sell.SellCmd;
import ua.leonidius.trading.help.SellListCmd;
import ua.leonidius.trading.settings.Settings;
import ua.leonidius.trading.utils.Message;

import java.io.File;


/**
 * Created by lion on 01.03.17.
 */


public class Main extends PluginBase {
    public static Main plugin;
    public static Main getPlugin() {
        return plugin;
    }

    public static Config buycfg;
    public static Config discountCfg;
    public static Config sellcfg;

    //public static String plgname;
    //стринг енам вальюс в параметрах команді, можно свои делать!

    @Override
    public void onEnable() {
        plugin = this;
        initConfig();
        Settings.init();
        Message.init(this);
        initCmd();
    }

    private void initConfig() {
        this.getDataFolder().mkdirs();
        this.saveResource("config.yml");
        this.saveResource("buy.yml");
        this.saveResource("sell.yml");
        buycfg = new Config(new File(getDataFolder(), "buy.yml"));
        sellcfg = new Config(new File(getDataFolder(), "sell.yml"));
        discountCfg = new Config (new File(getDataFolder(), "discount.yml"));
    }

    private void initCmd(){
        SimpleCommandMap cm = this.getServer().getCommandMap();
        String prefix = "Shop";
        if (Buy.active) {
            cm.register(prefix, new BuyCmd());
            cm.register(prefix, new AddBuyItemCmd());
            cm.register(prefix, new DelBuyItemCmd());
            cm.register(prefix, new BuyListCmd());
            //addsale delsale
        }
        if (Settings.sell.active) {
            cm.register(prefix, new SellCmd());
            cm.register(prefix, new AddSellItemCmd());
            cm.register(prefix, new DelSellItemCmd());
            cm.register(prefix, new SellListCmd());
        }
        if (Settings.auction.active){
            cm.register(prefix, new StartAuctionCmd());
            cm.register(prefix, new BetCmd());
        }
        if (Buy.active || Settings.sell.active || Settings.auction.active) {
            cm.register(prefix, new IdCmd());
            cm.register(prefix, new HelpCmd());
        }
    }

    /*private void initPlugin(){
        Config cfg = this.getConfig();
        Map plugins = this.getServer().getPluginManager().getPlugins();
        if (cfg.getString("plugin").equalsIgnoreCase("economyapi")) plgname = "eapi";
        else if (cfg.getString("plugin").equalsIgnoreCase("leet")) plgname = "leet";
        else if (cfg.getString("plugin").equalsIgnoreCase("auto") || !cfg.exists("plugin")){
            if (plugins.containsKey("EconomyAPI")) plgname = "eapi";
            else if (plugins.containsKey("Economy-LEET")) plgname = "leet";
        }
        String msg = "Economy plugin set to: ";
        if (plgname == "eapi") this.getLogger().info(msg + "EconomyAPI");
        else if (plgname == "leet") this.getLogger().info(msg + "Economy-LEET");
        else if (plgname == null){
            this.getLogger().emergency("No economy plugin found. Make sure that it is installed or try to specify it's name in the config.");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }*/
}