package nl.tabuu.permissionshopz.command;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.dao.NodeDAO;
import nl.tabuu.permissionshopz.dao.PerkDAO;
import nl.tabuu.permissionshopz.dao.ShopDAO;
import nl.tabuu.permissionshopz.data.node.NodeType;
import nl.tabuu.permissionshopz.gui.ShopEditInterface;
import nl.tabuu.permissionshopz.gui.ShopInterface;
import nl.tabuu.permissionshopz.nodehandler.INodeHandler;
import nl.tabuu.tabuucore.command.CommandResult;
import nl.tabuu.tabuucore.command.register.ICommandListener;
import nl.tabuu.tabuucore.command.register.annotation.ChildCommand;
import nl.tabuu.tabuucore.command.register.annotation.CommandExecutor;
import nl.tabuu.tabuucore.debug.Debug;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import nl.tabuu.tabuucore.util.Dictionary;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PermissionShopCommand implements ICommandListener {

    private final Dictionary _local;
    private final PermissionShopZ _plugin;

    public PermissionShopCommand() {
        _plugin = PermissionShopZ.getInstance();
        _local = _plugin.getLocale();
    }

    @CommandExecutor(
            value = "permissionshopz",
            children = {
                    @ChildCommand(label = "edit", method = "shopEdit"),
                    @ChildCommand(label = "reload", method = "reload"),
                    @ChildCommand(label = "loaddata", method = "loadData"),
                    @ChildCommand(label = "cleandata", method = "cleanData"),
                    @ChildCommand(label = "debuginfo", method = "debugInfo")
            }
    )
    private CommandResult shop(Player player, List<?> arguments) {
        new ShopInterface(PermissionShopZ.getInstance().getDefaultShop(), player).open(player);
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz edit")
    private CommandResult shopEdit(Player player, List<?> arguments) {
        new ShopEditInterface(PermissionShopZ.getInstance().getDefaultShop(), player).open(player);
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz reload")
    private CommandResult reload(CommandSender sender, List<?> arguments) {
        _plugin.reload();
        sender.sendMessage(_local.translate("INFO_COMMAND_RELOAD"));
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz loaddata")
    private CommandResult loadData(CommandSender sender, List<?> arguments) {
        PermissionShopZ.getInstance().load();
        sender.sendMessage(_local.translate("INFO_COMMAND_RELOAD"));
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz cleandata")
    private CommandResult cleanData(CommandSender sender, List<?> arguments) {
        ShopDAO shopDAO = PermissionShopZ.getInstance().getShopDao();
        PerkDAO perkDAO = PermissionShopZ.getInstance().getPerkDao();
        NodeDAO nodeDAO = PermissionShopZ.getInstance().getNodeDao();

        int shops = shopDAO.size();
        int perks = perkDAO.size();
        int nodes = nodeDAO.size();

        shopDAO.deleteGarbage();
        perkDAO.deleteGarbage();
        nodeDAO.deleteGarbage();

        perkDAO.deleteGarbage();
        shopDAO.deleteGarbage();

        Object[] replacements = {
                "{SHOPS}", shops - shopDAO.size(),
                "{PERKS}", perks - perkDAO.size(),
                "{NODES}", nodes - nodeDAO.size()
        };

        sender.sendMessage(_local.translate("INFO_DATA_CLEAN", replacements));
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz debuginfo")
    private CommandResult debugInfo(CommandSender sender, List<?> arguments) {
        Map<String, String> replacementMap = new HashMap<>();
        INodeHandler handler = _plugin.getNodeHandler();

        replacementMap.put("{PERMISSION_HANDLER_CLASS}", handler.getClass().getSimpleName());

        for (NodeType nodeType : NodeType.values()) {
            boolean supported = handler.isNodeTypeSupported(nodeType);
            String key = String.format("{NODE_TYPE_%s_SUPPORTED}", nodeType.name());
            replacementMap.put(key, (supported ? "&a" : "&c") + Serializer.BOOLEAN.serialize(supported) + "&r");
        }

        Object[] replacements = replacementMap.entrySet()
                .stream()
                .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                .toArray();

        sender.sendMessage(_local.translate("INFO_COMMAND_COMMANDDEBUG", replacements));

        return CommandResult.SUCCESS;
    }
}