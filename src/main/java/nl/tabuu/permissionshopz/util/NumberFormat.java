package nl.tabuu.permissionshopz.util;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.tabuucore.configuration.IConfiguration;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NumberFormat {
    public static String suffixFormat(double value) {
        IConfiguration config =PermissionShopZ.getInstance().getConfiguration();

        if(!config.getBoolean("UseNumberSuffix", false)) return String.format("%.2f", value);
        else value = Math.ceil(value);

        NavigableMap<Double, String> suffixMap = new TreeMap<>();

        for (String key : config.getKeys("NumberSuffixes", false)) {
            int zeroCount = Integer.parseInt(key);
            String suffix = config.getString(key);
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