package nl.tabuu.permissionshopz.command;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.PerkManager;
import nl.tabuu.permissionshopz.gui.ShopEditInterface;
import nl.tabuu.permissionshopz.gui.ShopInterface;
import nl.tabuu.permissionshopz.util.Message;
import nl.tabuu.tabuucore.command.Command;
import nl.tabuu.tabuucore.command.CommandResult;
import nl.tabuu.tabuucore.command.SenderType;
import nl.tabuu.tabuucore.command.argument.ArgumentConverter;
import nl.tabuu.tabuucore.command.argument.ArgumentType;
import nl.tabuu.tabuucore.command.argument.converter.OrderedArgumentConverter;
import nl.tabuu.tabuucore.util.BukkitUtils;
import nl.tabuu.tabuucore.util.Dictionary;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PermissionShopCommand extends Command {
    private Dictionary _local;
    private PerkManager _manager;

    public PermissionShopCommand() {
        super("permissionshopz");

        _local = PermissionShopZ.getInstance().getLocal();
        _manager = PermissionShopZ.getInstance().getPerkManager();

        setRequiredSenderType(SenderType.PLAYER);

        addSubCommand("add", new PermissionShopAddCommand(this));
        addSubCommand("remove", new PermissionShopRemoveCommand(this));
        addSubCommand("reload", new PermissionShopReloadCommand(this));
    }

    @Override
    protected CommandResult onCommand(CommandSender sender, List<Optional<?>> arguments) {
        Player player = (Player) sender;
        new ShopInterface(player).open(player);
        return CommandResult.SUCCESS;
    }

    class PermissionShopAddCommand extends Command {
        private PermissionShopAddCommand(Command parent) {
            super("permissionshopz add", parent);

            ArgumentConverter converter = new OrderedArgumentConverter()
                    .setSequence(ArgumentType.STRING, ArgumentType.DOUBLE, ArgumentType.STRING)
                    .setParameter(ArgumentType.STRING);

           	setRequiredSenderType(SenderType.PLAYER);
           	setArgumentConverter(converter);
        }

        @Override
        protected CommandResult onCommand(CommandSender sender, List<Optional<?>> arguments) {
            Player player = (Player) sender;

            if(!arguments.stream().allMatch(Optional::isPresent)) return CommandResult.WRONG_SYNTAX;

            String name = (String) arguments.get(0).get();
            double cost = (Double) arguments.get(1).get();
            List<String> nodes = new ArrayList<>();

            for (int i = 2; i < arguments.size(); i++)
                nodes.add((String) arguments.get(i).get());

            ItemStack itemStack = BukkitUtils.getItemInMainHand(player);

            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                Message.send(player, _local.translate("ERROR_INVALIDITEM"));
                return CommandResult.SUCCESS;
            }

            _manager.createPerk(name, cost, itemStack, nodes.stream().toArray(String[]::new));
            Message.send(player, _local.translate("PERK_ADD_SUCCESS", "{PERK_NAME}", name));

            return CommandResult.SUCCESS;
        }
    }

    class PermissionShopRemoveCommand extends Command {
        private PermissionShopRemoveCommand(Command parent) {
            super("permissionshopz remove", parent);

            setRequiredSenderType(SenderType.PLAYER);
        }

        @Override
        protected CommandResult onCommand(CommandSender sender, List<Optional<?>> arguments) {
            Player player = (Player) sender;
            new ShopEditInterface(player).open(player);
            return CommandResult.SUCCESS;
        }
    }

    class PermissionShopReloadCommand extends Command {
        private PermissionShopReloadCommand(Command parent) {
            super("permissionshopz reload", parent);
        }

        @Override
        protected CommandResult onCommand(CommandSender sender, List<Optional<?>> arguments) {
            PermissionShopZ.getInstance().reload();
            sender.sendMessage(_local.translate("PLUGIN_RELOAD_SUCCESS"));
            return CommandResult.SUCCESS;
        }
    }
}
