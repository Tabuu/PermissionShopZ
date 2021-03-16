package nl.tabuu.permissionshopz.util;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.IDataHolder;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

public class NumberFormat {

    private static final java.text.NumberFormat FORMAT;
    private static final String NUMBER_SUFFIXES_KEY = "NumberSuffixes";
    private static final String USE_NUMBER_SUFFIXES_KEY = "UseNumberSuffix";

    private static NavigableMap<Double, String> SUFFIX_MAP;

    static {
        FORMAT = DecimalFormat.getInstance();
        FORMAT.setMinimumFractionDigits(0);
        FORMAT.setMaximumFractionDigits(2);
        FORMAT.setRoundingMode(RoundingMode.UP);
    }

    public static void reloadSuffixMap() {
        if(SUFFIX_MAP == null)
            SUFFIX_MAP = new TreeMap<>();

        if(SUFFIX_MAP.isEmpty()) {
            IConfiguration config = PermissionShopZ.getInstance().getConfiguration();
            IDataHolder suffixSection = config.getDataSection(NUMBER_SUFFIXES_KEY);

            for (String key : suffixSection.getKeys(false)) {
                int zeroCount = Integer.parseInt(key);
                String suffix = suffixSection.getString(key);
                SUFFIX_MAP.put(Math.pow(10d, zeroCount), suffix);
            }
        }
    }

    public static String formatNumber(double value) {
        IConfiguration config = PermissionShopZ.getInstance().getConfiguration();
        return formatNumber(value, config.getBoolean(USE_NUMBER_SUFFIXES_KEY, false));
    }

    public static String formatNumber(double value, boolean useSuffix) {
        if(!useSuffix)
            return FORMAT.format(value);

        Map.Entry<Double, String> entry = SUFFIX_MAP.floorEntry(value);

        if (Objects.isNull(entry)) return formatNumber(value, false);

        double divider = entry.getKey();
        String suffix = entry.getValue();

        return formatNumber(value / divider, false) + suffix;
    }
}