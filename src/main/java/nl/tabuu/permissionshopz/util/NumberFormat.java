package nl.tabuu.permissionshopz.util;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public class NumberFormat {
    public static String suffixFormat(double value) {
        IConfiguration config = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("config");

        NavigableMap<Double, String> suffixMap = new TreeMap<>();
        ConfigurationSection suffixes = config.getConfigurationSection("NumberSuffixes");
        if (suffixes == null) return value + "";

        Set<String> suffixList = suffixes.getKeys(false);

        for (String string : suffixList) {
            int zeroCount = Integer.parseInt(string);
            String suffix = config.getString("NumberSuffixes." + string);
            suffixMap.put(Math.pow(10d, zeroCount), suffix);
        }

        Map.Entry<Double, String> entry = suffixMap.floorEntry(value);

        if (entry == null) return value + "";

        Double divider = entry.getKey();
        String suffix = entry.getValue();

        double formattedNumber = value / (divider / 10);
        boolean hasDecimal = formattedNumber < 100 && (formattedNumber / 10d) != (formattedNumber / 10);
        return hasDecimal ? (formattedNumber / 10d) + suffix : (formattedNumber / 10) + suffix;
    }
}
