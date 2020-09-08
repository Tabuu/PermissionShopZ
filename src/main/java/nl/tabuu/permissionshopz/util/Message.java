package nl.tabuu.permissionshopz.util;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.tabuucore.api.TitleAPI;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.text.ComponentBuilder;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class Message {

    public static void send(Player player, String message) {
        IConfiguration config = PermissionShopZ.getInstance().getConfiguration();
        if (config.getBoolean("UseTitle"))
            TitleAPI.getInstance().sendActionbar(player, getJSONMessage(message), 0, 5, 0);
        else
            player.spigot().sendMessage(ComponentBuilder.parse(message).build());
    }

    private static String getJSONMessage(String message) {
        return "{\"text\":\"MESSAGE\"}".replace("MESSAGE", JSONObject.escape(message));
    }
}