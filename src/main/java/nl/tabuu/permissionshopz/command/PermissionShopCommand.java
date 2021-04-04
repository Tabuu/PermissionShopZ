package nl.tabuu.permissionshopz.command;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.gui.PerkEditInterface;
import nl.tabuu.permissionshopz.gui.ShopEditInterface;
import nl.tabuu.permissionshopz.gui.ShopInterface;
import nl.tabuu.permissionshopz.nodehandler.INodeHandler;
import nl.tabuu.permissionshopz.data.node.NodeType;
import nl.tabuu.tabuucore.command.CommandResult;
import nl.tabuu.tabuucore.command.register.ICommandListener;
import nl.tabuu.tabuucore.command.register.annotation.ChildCommand;
import nl.tabuu.tabuucore.command.register.annotation.CommandExecutor;
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
                    @ChildCommand(label = "add", method = "shopAdd"),
                    @ChildCommand(label = "edit", method = "shopEdit"),
                    @ChildCommand(label = "reload", method = "reload"),
                    @ChildCommand(label = "debuginfo", method = "debugInfo")
            }
    )
    private CommandResult shop(Player player, List<?> arguments) {
        new ShopInterface(player).open(player);
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz add")
    private CommandResult shopAdd(Player player, List<?> arguments) {
        Perk perk = new Perk();
        PerkEditInterface edit = new PerkEditInterface(perk);
        edit.open(player);

        PermissionShopZ.getInstance().getPerkDao().create(perk);

        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz edit")
    private CommandResult shopEdit(Player player, List<?> arguments) {
        new ShopEditInterface(player).open(player);
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz reload")
    private CommandResult reload(CommandSender sender, List<?> arguments) {
        _plugin.reload();
        sender.sendMessage(_local.translate("RELOAD_SUCCESS"));
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz debuginfo")
    private CommandResult debugInfo(CommandSender sender, List<?> arguments) {
        Map<String, String> replacementMap = new HashMap<>();
        INodeHandler handler = _plugin.getPermissionHandler();

        replacementMap.put("{PERMISSION_HANDLER_CLASS}", handler.getClass().getSimpleName());

        for(NodeType nodeType : NodeType.values()) {
            boolean supported = handler.isNodeTypeSupported(nodeType);
            String key = String.format("{NODE_TYPE_%s_SUPPORTED}", nodeType.name());
            replacementMap.put(key, (supported ? "&a" : "&c") + Serializer.BOOLEAN.serialize(supported) + "&r");
        }

        Object[] replacements = replacementMap.entrySet()
                .stream()
                .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                .toArray();

        sender.sendMessage(_local.translate("DEBUG_INFO", replacements));

        return CommandResult.SUCCESS;
    }
}