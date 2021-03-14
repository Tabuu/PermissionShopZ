package nl.tabuu.permissionshopz.command;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.PerkManager;
import nl.tabuu.permissionshopz.gui.PerkEditInterface;
import nl.tabuu.permissionshopz.gui.ShopEditInterface;
import nl.tabuu.permissionshopz.gui.ShopInterface;
import nl.tabuu.tabuucore.command.CommandResult;
import nl.tabuu.tabuucore.command.register.ICommandListener;
import nl.tabuu.tabuucore.command.register.annotation.ChildCommand;
import nl.tabuu.tabuucore.command.register.annotation.CommandExecutor;
import nl.tabuu.tabuucore.util.Dictionary;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PermissionShopCommand implements ICommandListener {

    private final Dictionary _local;
    private final PerkManager _manager;

    public PermissionShopCommand() {
        _local = PermissionShopZ.getInstance().getLocal();
        _manager = PermissionShopZ.getInstance().getPerkManager();
    }

    @CommandExecutor(
            value = "permissionshopz",
            children = {
                    @ChildCommand(label = "add", method = "shopAdd"),
                    @ChildCommand(label = "edit", method = "shopEdit"),
                    @ChildCommand(label = "reload", method = "reload")
            }
    )
    private CommandResult shop(Player player, List<?> arguments) {
        new ShopInterface(player).open(player);
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz add")
    private CommandResult shopAdd(Player player, List<?> arguments) {
        Perk perk = _manager.createDefaultPerk();
        PerkEditInterface edit = new PerkEditInterface(perk);
        edit.open(player);

        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz edit")
    private CommandResult shopEdit(Player player, List<?> arguments) {
        new ShopEditInterface(player).open(player);
        return CommandResult.SUCCESS;
    }

    @CommandExecutor("permissionshopz reload")
    private CommandResult reload(CommandSender sender, List<?> arguments) {
        PermissionShopZ.getInstance().reload();
        sender.sendMessage(_local.translate("RELOAD_SUCCESS"));
        return CommandResult.SUCCESS;
    }
}