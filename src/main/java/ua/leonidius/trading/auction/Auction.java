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
public abstract class Auction {

    private static volatile boolean active;
    static Item item;
    static double currentBet;
    static Player trader;
    static Player currentWinner;
    static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    static volatile Timer notifierTimer;
    static volatile long startTime;

    public static class Settings {
        public static boolean active = Main.getPlugin().getConfig().getBoolean("auction.active", true);
        static int duration = Main.getPlugin().getConfig().getInt("auction.duration", 300000);
        static int notifyPeriod = Main.getPlugin().getConfig().getInt("auction.notify-period", 60000);
        static int startTax = Main.getPlugin().getConfig().getInt("auction.tax", 50);
        static int endTax = Main.getPlugin().getConfig().getInt("auction.end-tax", 1);
        public static char color1 = Main.getPlugin().getConfig().getString("auction.primary-color", "a").charAt(0);
        public static char color2 = Main.getPlugin().getConfig().getString("auction.secondary-color", "2").charAt(0);
    }

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

        if (EconomyAPI.getInstance().myMoney(player) < Settings.startTax) {
            Message.AUC_NOT_ENOUGH_MONEY.printError(player, Settings.startTax, Main.currency);
            return;
        }

        if (!player.getInventory().contains(i)) {
            Message.SELL_NO_ITEM.printError(player);
            return;
        }

        //стартовые функции
        EconomyAPI.getInstance().reduceMoney(player, Settings.startTax);
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
        stopTimer.schedule(stop, Settings.duration);

        //таймер для уведомлений всем игрокам
        notifierTimer = new Timer();
        AuctionNotifier notifier = new AuctionNotifier();
        notifierTimer.schedule(notifier, Settings.notifyPeriod, Settings.notifyPeriod);

        //отправка уведомления о старте в чат
        Date date = new Date(Settings.duration);
        Message.AUC_START.broadcastAuc(null, item.getCount(), item.getName(), item.getId(), item.getDamage(), currentBet, Main.currency, trader.getName(), sdf.format(date));
        //Message.AUC_DURATION.broadcastAuc(null, sdf.format(date));

        if (Settings.startTax!=0) Message.AUC_TAX_TAKEN.printAuc(trader, Settings.startTax, Main.currency);
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
            Message.AUC_SMALLBET.printError(player, Main.currency);
            return;
        }

        if (!player.getInventory().canAddItem(item)) {
            Message.BUY_NO_SPACE.printError(player);
            return;
        }

        currentWinner = player;
        currentBet = bet;
        Message.AUC_NEW_BID.broadcastAuc(null, player.getName(), bet, Main.currency);
    }

}

class StopAuction extends TimerTask {

    @Override
    public void run() {
        Auction.notifierTimer.cancel();
        Auction.setActive(false);

        if (Auction.currentWinner==null) {
            Auction.trader.getInventory().addItem(Auction.item);
            Message.AUC_FINISHED_NOWINNER.broadcastAuc(null);
            Auction.trader.sendTip(Message.AUC_FINISHED.getText('c'));
            Auction.trader.sendPopup(Message.AUC_NOBODY.getText('c'));
            return;
        }

        if (!Auction.currentWinner.getInventory().canAddItem(Auction.item)) {
            Message.AUC_FINISHED_WINNER.broadcastAuc(null, Auction.currentWinner.getName());
            Message.AUC_WINNER_NO_SPACE.printError(Auction.trader);
            Message.AUC_YOU_WON_NO_SPACE.printError(Auction.currentWinner);
            EconomyAPI.getInstance().addMoney(Auction.trader, Auction.Settings.startTax);
            return;
        }

        EconomyAPI.getInstance().reduceMoney(Auction.currentWinner, Auction.currentBet);
        Auction.currentWinner.getInventory().addItem(Auction.item);
        double endTax = Math.round((Auction.Settings.endTax/100)* Auction.currentBet);
        double finalEarnings = Auction.currentBet - endTax;
        EconomyAPI.getInstance().addMoney(Auction.trader, finalEarnings);
        Message.AUC_FINISHED_WINNER.broadcastAuc(null, Auction.currentWinner.getName());
        Message.AUC_FINISHED.sendTipAuc(Auction.trader);
        Message.AUC_YOU_EARNED.sendPopupAuc(Auction.trader, finalEarnings, Main.currency);
        Auction.currentWinner=null;
    }
}

class AuctionNotifier extends TimerTask {
    @Override
    public void run() {
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        long timePast = currentTime- Auction.startTime;
        long timeLeft = Auction.Settings.duration - timePast;
        Date timeLeftDate = new Date (timeLeft);
        Message.AUC_NOTIFICATION.broadcastAuc(null, Auction.item.getCount(), Auction.item.getName(), Auction.item.getId(), Auction.item.getDamage(), Auction.currentBet, Main.currency, Auction.sdf.format(timeLeftDate));
    }
}