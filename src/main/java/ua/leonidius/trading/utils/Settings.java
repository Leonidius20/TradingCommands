package ua.leonidius.trading.utils;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.SimpleConfig;
import me.onebone.economyapi.EconomyAPI;

/**
 * Created by Leonidius20 on 05.01.18.
 */
public class Settings extends SimpleConfig {

    public Settings(Plugin plugin) {
        super(plugin);
        currency = EconomyAPI.getInstance().getMonetaryUnit();
    }

    //active
    public boolean buy_active = true;
    public boolean sell_active = true;
    public boolean auction_active = true;

    //auction
    public int auction_duration = 300000;
    @Path (value = "auction.notify-period")
    public int auction_notify_period = 60000;
    @Path (value = "auction.end-tax")
    public int auction_end_tax = 0;
    @Path (value = "auction.start-tax")
    public int auction_start_tax = 0;
    @Path (value = "auction.default-compensation")
    public double auction_default_compensation = 0;

    //general
    @Path (value = "general.save-translation")
    boolean save_translation = false;
    @Path (value = "general.primary-color")
    String primary_color = "a";
    @Path (value = "general.secondary-color")
    String secondary_color = "2";
    @Path (value = "general.error-color")
    String error_color = "c";
    @Path (value = "general.language")
    String language = "default";

    public String currency;

}
