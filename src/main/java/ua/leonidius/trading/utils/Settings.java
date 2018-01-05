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
    public boolean buy_active, sell_active, auction_active;

    //auction
    public int auction_duration;
    @Path (value = "auction.notify-period")
    public int auction_notify_period;
    @Path (value = "auction.end-tax")
    public int auction_end_tax;
    @Path (value = "auction.start-tax")
    public int auction_start_tax;

    //general
    @Path (value = "general.save-translation")
    boolean save_translation;
    @Path (value = "general.primary-color")
    String primary_color;
    @Path (value = "general.secondary-color")
    String secondary_color;
    @Path (value = "general.language")
    String language;

    public String currency;

}
