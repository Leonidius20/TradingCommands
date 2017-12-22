package ua.leonidius.trading.auction;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import me.onebone.economyapi.EconomyAPI;
import ua.leonidius.trading.Main;
import ua.leonidius.trading.utils.Message;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Leonidius20 on 13.09.17.
 */
public class Auction {
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
            Message.AUC_RUNNING.print(player, 'c');
            return;
        }

        if (player.getGamemode()==1) {
            Message.AUC_CREATIVE.print(player, 'c');
            return;
        }

        if (EconomyAPI.getInstance().myMoney(player) < AucSettings.startTax) {
            Message.AUC_NOT_ENOUGH_MONEY.print(player, AucSettings.startTax, 'c');
            return;
        }

        if (!player.getInventory().contains(i)) {
            Message.SELL_NO_ITEM.print(player, 'c');
            return;
        }

        //стартовые функции
        EconomyAPI.getInstance().reduceMoney(player, AucSettings.startTax);
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
        stopTimer.schedule(stop, AucSettings.duration);

        //таймер для уведомлений всем игрокам
        notifierTimer = new Timer();
        AuctionNotifier notifier = new AuctionNotifier();
        notifierTimer.schedule(notifier, AucSettings.notifyPeriod, AucSettings.notifyPeriod);

        //отправка уведомления о старте в чат
        Date date = new Date(AucSettings.duration);
        Message.AUC_START.broadcast(null, item.getCount(), item.getName(), item.getId(), item.getDamage(), currentBet, trader.getName(), AucSettings.primaryColor, AucSettings.secondaryColor);
        Message.AUC_DURATION.broadcast(null, sdf.format(date), AucSettings.primaryColor, AucSettings.secondaryColor);

        if (AucSettings.startTax!=0) Message.AUC_TAX_TAKEN.print(trader, AucSettings.startTax, AucSettings.primaryColor, AucSettings.secondaryColor);
    }

    static void bet (Player player, double bet){
        if (isActive()) {
            if (player != trader) {
                if ((bet - currentBet) >= 1) {
                    if (player.getInventory().canAddItem(item)) {
                        currentWinner = player;
                        currentBet = bet;
                        Message.AUC_NEW_BID.broadcast(null, player.getName(), bet);
                    } else Message.BUY_NO_SPACE.print(player, 'c');
                } else Message.AUC_SMALLBET.print(player, 'c');
            } else Message.AUC_YOUR.print(player, 'c');
        } else Message.AUC_NOT_RUNNING.print(player, 'c');
    }

    public static boolean isSystemActive(){
        return AucSettings.active;
    }
}

class StopAuction extends TimerTask {

    @Override
    public void run() {
        Auction.notifierTimer.cancel();
        Auction.setActive(false);
        if (Auction.currentWinner!=null) {
            if (Auction.currentWinner.getInventory().canAddItem(Auction.item)) {
                EconomyAPI.getInstance().reduceMoney(Auction.currentWinner, Auction.currentBet);
                Auction.currentWinner.getInventory().addItem(Auction.item);
                double endTax = Math.round((AucSettings.endTax/100)* Auction.currentBet);
                double finalEarnings = Auction.currentBet - endTax;
                EconomyAPI.getInstance().addMoney(Auction.trader, finalEarnings);
                Message.AUC_FINISHED_WINNER.broadcast(null, Auction.currentWinner.getName(), AucSettings.primaryColor, AucSettings.secondaryColor);
                Auction.trader.sendTip(Message.AUC_FINISHED.getText(AucSettings.primaryColor));
                Auction.trader.sendPopup(Message.AUC_YOU_EARNED.getText(finalEarnings, AucSettings.primaryColor, AucSettings.secondaryColor));
            } else {
                Message.AUC_FINISHED_WINNER.broadcast(null, Auction.currentWinner.getName(), AucSettings.primaryColor, AucSettings.secondaryColor);
                Message.AUC_WINNER_NO_SPACE.print(Auction.trader, 'c');
                Message.AUC_YOU_WON_NO_SPACE.print(Auction.currentWinner, 'c');
                EconomyAPI.getInstance().addMoney(Auction.trader, AucSettings.startTax);
            }
        } else {
                Auction.trader.getInventory().addItem(Auction.item);
                Message.AUC_FINISHED_NOWINNER.broadcast(null, AucSettings.primaryColor);
                Auction.trader.sendTip(Message.AUC_FINISHED.getText('c'));
                Auction.trader.sendPopup(Message.AUC_NOBODY.getText('c'));
        }
        Auction.currentWinner=null;
    }
}

class AuctionNotifier extends TimerTask {
    @Override
    public void run() {
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        long timePast = currentTime- Auction.startTime;
        long timeLeft = AucSettings.duration - timePast;
        Date timeLeftDate = new Date (timeLeft);
        Message.AUC_NOTIFICATION.broadcast(null, Auction.item.getCount(), Auction.item.getName(), Auction.item.getId(), Auction.item.getDamage(), Auction.currentBet, Auction.sdf.format(timeLeftDate), AucSettings.primaryColor, AucSettings.secondaryColor);
    }
}

class AucSettings {
    static boolean active = Main.getPlugin().getConfig().getBoolean("auction.active", true);
    static int duration = Main.getPlugin().getConfig().getInt("auction.duration", 300000);
    static int notifyPeriod = Main.getPlugin().getConfig().getInt("auction.notify-period", 60000);
    static int startTax = Main.getPlugin().getConfig().getInt("auction.tax", 50);
    static int endTax = Main.getPlugin().getConfig().getInt("auction.end-tax", 1);
    static char primaryColor = Main.getPlugin().getConfig().getString("auction.primary-color", "a").charAt(0);
    static char secondaryColor = Main.getPlugin().getConfig().getString("auction.secondary-color", "2").charAt(0);
}