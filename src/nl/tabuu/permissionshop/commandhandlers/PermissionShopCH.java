package nl.tabuu.permissionshop.commandhandlers;

import nl.tabuu.permissionshop.PermissionShop;
import nl.tabuu.permissionshop.gui.ShopGUI;
import nl.tabuu.permissionshop.gui.ShopRemoveGUI;
import nl.tabuu.tabuucore.configuration.Config;
import nl.tabuu.tabuucore.configuration.LanguageConfig;
import nl.tabuu.tabuucore.packets.titlepackets.TitleAPI;
import nl.tabuu.tabuucore.utils.BukkitUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class PermissionShopCH implements CommandExecutor{

	Plugin _plugin;
	LanguageConfig _langConfig;
	Config _data;
	
	public PermissionShopCH() {
		_plugin = PermissionShop.getPlugin();
		_langConfig = PermissionShop.getConfigManager().getLanguageConfig("lang");
		_data = PermissionShop.getConfigManager().getConfig("data");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)){
			sender.sendMessage(_langConfig.parseText("ERROR_NOTAPLAYER"));
			return true;
		}

		Player player = (Player) sender;

		if(args.length == 0){
			player.openInventory(new ShopGUI(player).getInventory());
			return true;
		}

		boolean hasPermission = false;
		String correctSyntax = "";
		switch (args[0]){
			case "add":
				hasPermission = player.hasPermission("permissionshop.admin");

				if(args.length < 4)
					correctSyntax = "/pshop add <name> <price> <permission> [<permission> <permission> ...]";
				break;

			case "remove":
				hasPermission = player.hasPermission("permissionshop.admin");

				if(args.length > 1)
					correctSyntax = "/pshop remove";
				break;

			case "reload":
				hasPermission = player.hasPermission("permissionshop.admin");

				if(args.length != 1)
					correctSyntax = "/pshop reload";
				break;
		}

		if(!hasPermission){
			player.sendMessage(_langConfig.parseText("ERROR_NOPERMISSION"));
			return true;
		}
		else if(correctSyntax != ""){
			player.sendMessage(_langConfig.parseText("ERROR_WRONGSYNTAX", "{SYNTAX}", correctSyntax));
			return true;
		}

		switch (args[0]){
			case "add":
				if(!NumberUtils.isNumber(args[2])){
					player.sendMessage(_langConfig.parseText("ERROR_NOTANUMBER", "{NUMBER}", args[2]));
					return true;
				}

				String perkName = args[1].replace('_', ' ');
				double price = Double.parseDouble(args[2]);
				List<String> permissionNodes = Arrays.asList(Arrays.copyOfRange(args, 3, args.length));
				ItemStack displayItem = BukkitUtils.getItemInMainHand(player);

				if(_data.getData().get("Perks." + perkName) != null) {
					TitleAPI.sendActionbar(player, _langConfig.parseText("PERK_ADD_ALREADYEXISTS", "{PERK_NAME}", perkName), 10, 20, 10);
				}
				else if (displayItem == null || displayItem.getType().equals(Material.AIR)){
					TitleAPI.sendActionbar(player, _langConfig.parseText("ERROR_INVALIDITEM"), 10 ,20, 10);
				}
				else{
					ConfigurationSection newPerk = _data.getData().createSection("Perks." + perkName);
					newPerk.set("Price", price);
					newPerk.set("Permissions", permissionNodes);
					newPerk.set("DisplayItem", displayItem);

					_data.save();

					TitleAPI.sendActionbar(player, _langConfig.parseText("PERK_ADD_SUCCESS", "{PERK_NAME}", perkName), 10 ,20, 10);
				}
				break;

			case "remove":
				player.openInventory(new ShopRemoveGUI(player).getInventory());
				break;

			case "reload":
				PermissionShop.getConfigManager().reloadAll();
				TitleAPI.sendActionbar(player, _langConfig.parseText("PLUGIN_RELOAD_SUCCESS"), 10 ,20, 10);
				break;
		}
		return true;
	}
}
