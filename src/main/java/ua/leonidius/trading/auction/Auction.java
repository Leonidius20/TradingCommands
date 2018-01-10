package ua.leonidius.trading.auction;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import me.onebone.economyapi.EconomyAPI;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.utils.Message;
import java.text.SimpleDateFormat;
import java.util.*;

import static ua.leonidius.trading.Main.settings;
import static ua.leonidius.trading.auction.Auction.*;

/**
 * Created by Leonidius20 on 13.09.17.
 */
public abstract class Auction {

    private static volatile boolean active;
    static Item item;
    static double currentBet;
    static Player trader;
    static Player currentWinner;
    static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    static volatile Timer notifierTimer;
    static volatile long startTime;

    private static boolean isActive() {
        return active;
    }

    static void setActive(boolean a){
        active = a;
    }

    static void startAuction (Item i, double bet, Player player){
        if (isActive()) {
            Message.AUC_RUNNING.printError(player);
            return;
        }

        if (player.getGamemode()==1) {
            Message.AUC_CREATIVE.printError(player);
            return;
        }

        if (EconomyAPI.getInstance().myMoney(player) < settings.auction_start_tax) {
            Message.AUC_NOT_ENOUGH_MONEY.printError(player, settings.auction_start_tax, settings.currency);
            return;
        }

        if (!player.getInventory().contains(i)) {
            Message.SELL_NO_ITEM.printError(player);
            return;
        }

        //стартовые функции
        EconomyAPI.getInstance().reduceMoney(player, settings.auction_start_tax);
        player.getInventory().removeItem(i);
        setActive(true);
        item = i;
        currentBet = bet;
        trader = player;

        //засекаем текущее время
        Date currentDate = new Date();
        startTime = currentDate.getTime();

        //таймер остановки аукциона
        Timer stopTimer = new Timer();
        StopAuction stop = new StopAuction();
        stopTimer.schedule(stop, settings.auction_duration);

        //таймер для уведомлений всем игрокам
        notifierTimer = new Timer();
        AuctionNotifier notifier = new AuctionNotifier();
        notifierTimer.schedule(notifier, settings.auction_notify_period, settings.auction_notify_period);

        //отправка уведомления о старте в чат
        Date date = new Date(settings.auction_duration);
        Message.AUC_START.broadcast(null, item.getCount(), item.getName(), item.getId(), item.getDamage(), currentBet, settings.currency, trader.getName(), sdf.format(date));

        if (settings.auction_start_tax!=0) Message.AUC_TAX_TAKEN.print(trader, settings.auction_start_tax, settings.currency);
    }

    static void bet (Player player, double bet){
        if (!isActive()) {
            Message.AUC_NOT_RUNNING.printError(player);
            return;
        }

        if (player == trader) {
            Message.AUC_YOUR.printError(player);
            return;
        }

        if ((bet - currentBet) < 1) {
            Message.AUC_SMALLBET.printError(player, settings.currency);
            return;
        }

        if (!player.getInventory().canAddItem(item)) {
            Message.BUY_NO_SPACE.printError(player);
            return;
        }

        if (currentWinner!=null) {
            EconomyAPI.getInstance().addMoney(currentWinner, currentBet);
            Message.AUC_BID_RETURNED.print(currentWinner);
        }
        EconomyAPI.getInstance().reduceMoney(player, bet);
        currentWinner = player;
        currentBet = bet;
        Message.AUC_NEW_BID.broadcast(null, player.getName(), bet, settings.currency);
    }

}

class StopAuction extends TimerTask {

    @Override
    public void run() {
        Auction.notifierTimer.cancel();
        Auction.setActive(false);

        if (currentWinner==null) {
            trader.getInventory().addItem(item);
            Message.AUC_FINISHED_NOWINNER.broadcast(null);
            trader.sendTip(Message.AUC_FINISHED.getText('c'));
            trader.sendPopup(Message.AUC_NOBODY.getText('c'));
            return;
        }

        if (!currentWinner.getInventory().canAddItem(item)) {
            Message.AUC_FINISHED_WINNER.broadcast(null, currentWinner.getName());
            Message.AUC_WINNER_NO_SPACE.printError(trader);
            Message.AUC_YOU_WON_NO_SPACE.printError(currentWinner);
            EconomyAPI.getInstance().addMoney(trader, settings.auction_start_tax);
            EconomyAPI.getInstance().addMoney(currentWinner, currentBet);
            if (trader.getInventory().canAddItem(item)) {
                trader.getInventory().addItem(item);
            } else {
                String key = "s-"+item.getId()+"-"+item.getDamage();
                if (Main.sellcfg.exists(key)) {
                    double compensation = Main.sellcfg.getDouble(key);
                    EconomyAPI.getInstance().addMoney(trader, compensation);
                    Message.AUC_NO_SPACE_FOR_RETURNING_COMPENSATION.print(trader, compensation, settings.currency);
                } else if (settings.auction_default_compensation!=0) {
                    EconomyAPI.getInstance().addMoney(trader, settings.auction_default_compensation);
                    Message.AUC_NO_SPACE_FOR_RETURNING_COMPENSATION.print(trader, settings.auction_default_compensation, settings.currency);
                } else {
                    Message.AUC_NO_SPACE_FOR_RETURNING.print(trader);
                }
            }
            return;
        }

        currentWinner.getInventory().addItem(item);
        double endTax = Math.round((settings.auction_end_tax/100)* currentBet);
        double finalEarnings = currentBet - endTax;
        EconomyAPI.getInstance().addMoney(trader, finalEarnings);
        Message.AUC_FINISHED_WINNER.broadcast(null, currentWinner.getName());
        Message.AUC_FINISHED.tip(trader);
        Message.AUC_YOU_EARNED.popup(trader, finalEarnings, settings.currency);
        currentWinner=null;
    }
}

class AuctionNotifier extends TimerTask {
    @Override
    public void run() {
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        long timePast = currentTime- Auction.startTime;
        long timeLeft = settings.auction_duration - timePast;
        Date timeLeftDate = new Date (timeLeft);
        Message.AUC_NOTIFICATION.broadcast(null, item.getCount(), item.getName(), item.getId(), item.getDamage(), currentBet, settings.currency, Auction.sdf.format(timeLeftDate));
    }
}