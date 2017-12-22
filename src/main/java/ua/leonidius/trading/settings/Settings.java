package ua.leonidius.trading.settings;

import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;
import ua.leonidius.trading.Main;

/**
 * Created by Leonidius20 on 21.09.17.
 */
public class Settings {

    public static AucSettings auction;
    //public static BuySettings buy;
    public static SellSettings sell;
    public static GeneralSettings general;

    public static void init (){
        Config cfg = Main.getPlugin().getConfig();

        auction = new AucSettings();
        auction.active = cfg.getBoolean("auction.active", true);
        auction.duration = cfg.getInt("auction.duration", 300000);
        auction.notifyPeriod = cfg.getInt("auction.notify-period", 60000);
        auction.startTax = cfg.getInt("auction.tax", 50);
        auction.endTax = cfg.getInt("auction.end-tax", 1);
        auction.primaryColor = cfg.getString("auction.primary-color", "a").charAt(0);
        auction.secondaryColor = cfg.getString("auction.secondary-color", "2").charAt(0);
        auction.logging = cfg.getBoolean("auction.logging", false);

        //buy = new BuySettings();
        //buy.active = cfg.getBoolean("buy.active", true);
        //buy.primaryColor = cfg.getString("buy.primary-color", "a").charAt(0);
        //buy.secondaryColor = cfg.getString("buy.secondary-color", "2").charAt(0);

        sell = new SellSettings();
        sell.active = cfg.getBoolean("sell.active", true);
        sell.primaryColor = cfg.getString("sell.primary-color", "a").charAt(0);
        sell.secondaryColor = cfg.getString("sell.secondary-color", "2").charAt(0);

        general = new GeneralSettings();
        general.editLogging = cfg.getBoolean("general.shopedit-logging", true);
        general.tradingLogging = cfg.getBoolean("general.transaction-logging", false);
        general.countMaxAmount = cfg.getBoolean("general.maxitem-count", true);
        general.currency = EconomyAPI.getInstance().getMonetaryUnit();
    }

}

