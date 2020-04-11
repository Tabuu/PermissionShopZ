package nl.tabuu.permissionshopz.util;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.tabuucore.api.TitleAPI;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class Message {

    public static void send(Player player, String message){
        if(PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("config").getBoolean("UseTitle"))
            TitleAPI.getInstance().sendActionbar(player, getJSONMessage(message), 0, 5, 0);
        else
            player.sendMessage(message);
    }

    private static String getJSONMessage(String message) {
        String json = "{\"text\":\"MESSAGE\"}".replace("MESSAGE", JSONObject.escape(message));
        return json;
    }

}
