package ua.leonidius.trading.utils;

import cn.nukkit.item.*;

/**
 * Created by Leonidius20 on 18.03.17.
 */
public class MaxStackSize {
    public static int get (Item item){
        int id = item.getId();
        if (id==Item.LINGERING_POTION) return 1;
        else return item.getMaxStackSize();
    }
}
