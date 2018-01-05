package ua.leonidius.trading;

import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import ua.leonidius.trading.auction.BetCmd;
import ua.leonidius.trading.auction.StartAuctionCmd;
import ua.leonidius.trading.buy.*;
import ua.leonidius.trading.help.BuyListCmd;
import ua.leonidius.trading.help.*;
import ua.leonidius.trading.sell.AddSellItemCmd;
import ua.leonidius.trading.sell.DelSellItemCmd;
import ua.leonidius.trading.sell.SellCmd;
import ua.leonidius.trading.help.SellListCmd;
import ua.leonidius.trading.utils.Message;
import ua.leonidius.trading.utils.Settings;

import java.io.File;


/**
 * Created by Leonidius20 on 01.03.17.
 */


public class Main extends PluginBase {
    private static Main plugin;
    public static Main getPlugin() {
        return plugin;
    }

    public static Config buycfg;
    public static Config discountCfg;
    public static Config sellcfg;
    public static Settings settings;

    //public static String plgname;

    @Override
    public void onEnable() {
        plugin = this;
        initConfig();
        Message.init(this);
        initCmd();
    }

    private void initConfig() {
        this.getDataFolder().mkdirs();
        this.saveResource("config.yml");
        this.saveResource("buy.yml");
        this.saveResource("sell.yml");
        this.saveResource("discount.yml");
        settings = new Settings(this);
        settings.load();
        buycfg = new Config(new File(getDataFolder(), "buy.yml"));
        sellcfg = new Config(new File(getDataFolder(), "sell.yml"));
        discountCfg = new Config (new File(getDataFolder(), "discount.yml"));
    }

    private void initCmd(){
        SimpleCommandMap cm = this.getServer().getCommandMap();
        String prefix = "Shop";
        if (settings.buy_active) {
            cm.register(prefix, new BuyCmd());
            cm.register(prefix, new AddBuyItemCmd());
            cm.register(prefix, new DelBuyItemCmd());
            cm.register(prefix, new BuyListCmd());
            cm.register(prefix, new AddDiscountCmd());
            cm.register(prefix, new DelDiscountCmd());
        }
        if (settings.sell_active) {
            cm.register(prefix, new SellCmd());
            cm.register(prefix, new AddSellItemCmd());
            cm.register(prefix, new DelSellItemCmd());
            cm.register(prefix, new SellListCmd());
        }
        if (settings.auction_active){
            cm.register(prefix, new StartAuctionCmd());
            cm.register(prefix, new BetCmd());
        }
        if (settings.buy_active || settings.sell_active || settings.auction_active) {
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