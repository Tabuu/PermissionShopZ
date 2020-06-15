package nl.tabuu.permissionshopz.command;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.PerkManager;
import nl.tabuu.permissionshopz.gui.ShopEditInterface;
import nl.tabuu.permissionshopz.gui.ShopInterface;
import nl.tabuu.permissionshopz.util.Message;
import nl.tabuu.tabuucore.command.CommandResult;
import nl.tabuu.tabuucore.command.argument.ArgumentType;
import nl.tabuu.tabuucore.command.register.ICommandListener;
import nl.tabuu.tabuucore.command.register.annotation.ChildCommand;
import nl.tabuu.tabuucore.command.register.annotation.CommandExecutor;
import nl.tabuu.tabuucore.util.BukkitUtils;
import nl.tabuu.tabuucore.util.Dictionary;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PermissionShopCommand implements ICommandListener {

    private Dictionary _local;
    private PerkManager _manager;

    public PermissionShopCommand() {
        _local = PermissionShopZ.getInstance().getLocal();
        _manager = PermissionShopZ.getInstance().getPerkManager();
    }

    @CommandExecutor(
            command = "permissionshopz",
            children = {
                    @ChildCommand(label = "add", method = "shopAdd"),
                    @ChildCommand(label = "remove", method = "shopRemove"),
                    @ChildCommand(label = "reload", method = "reload")
            }
    )
    private CommandResult shop(Player player, List<?> arguments) {
        new ShopInterface(player).open(player);
        return CommandResult.SUCCESS;
    }

    @CommandExecutor(
            command = "permissionshopz add",
            argumentSequence = { ArgumentType.STRING, ArgumentType.DOUBLE, ArgumentType.STRING },
            parameter = ArgumentType.STRING
    )
    @SuppressWarnings("unchecked")
    private CommandResult shopAdd(Player player, List<?> arguments) {
        String name = (String) arguments.get(0);
        double cost = (Double) arguments.get(1);
        List<String> nodes = (List<String>) arguments.subList(2, arguments.size());
        ItemStack item = BukkitUtils.getItemInMainHand(player);

        if (item.getType().equals(Material.AIR)) {
            Message.send(player, _local.translate("ERROR_INVALID_ITEM"));
            return CommandResult.SUCCESS;
        }

        _manager.createPerk(name, cost, item, nodes.toArray(new String[0]));
        Message.send(player, _local.translate("PERK_ADD_SUCCESS", "{NAME}", name));

        return CommandResult.SUCCESS;
    }

    @CommandExecutor(command = "permissionshopz remove")
    private CommandResult shopRemove(Player player, List<?> arguments) {
        new ShopEditInterface(player).open(player);
        return CommandResult.SUCCESS;
    }

    @CommandExecutor(command = "permissionshopz reload")
    private CommandResult reload(CommandSender sender, List<?> arguments) {
        PermissionShopZ.getInstance().reload();
        sender.sendMessage(_local.translate("RELOAD_SUCCESS"));
        return CommandResult.SUCCESS;
    }
}
