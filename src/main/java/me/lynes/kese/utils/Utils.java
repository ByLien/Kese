package me.lynes.kese.utils;

import me.lynes.kese.Kese;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Utils {

    public static String getConf(String str){
        return Kese.getInstance().getConfig().getString(str);
    }

    public static List getList(String str){
        return Kese.getInstance().getConfig().getStringList(str);
    }

    public static void msgPlayer(Player p, String str){
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',str));
    }

}
