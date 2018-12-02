package nl.tabuu.permissionshop;

import me.lucko.luckperms.api.LuckPermsApi;
import nl.tabuu.permissionshop.bstats.Metrics;
import nl.tabuu.permissionshop.commandhandlers.PermissionShopCH;
import nl.tabuu.permissionshop.permissionhandler.*;
import nl.tabuu.tabuucore.autoupdater.AutoUpdater;
import nl.tabuu.tabuucore.autoupdater.UpdateProviderException;
import nl.tabuu.tabuucore.autoupdater.providers.SpigotUpdateProvider;
import nl.tabuu.tabuucore.configuration.ConfigManager;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionShop extends JavaPlugin {
	
	private static JavaPlugin _plugin;
	private static ConfigManager _configManager;
	private static IPermissionHandler _permissionHandler;

	@Override
	public void onEnable() {
		_plugin = this;
		_configManager = new ConfigManager(this)
								.addConfig("config")
								.addConfig("lang")
								.addConfig("data");

		setupPermissionHandler();

		this.getCommand("permissionshop").setExecutor(new PermissionShopCH());

		new Metrics(this);
	}

	private void setupPermissionHandler(){
		switch (_configManager.getConfig("config").getString("PermissionManager").toUpperCase()){
			case "GROUP_MANAGER":
				try{
					GroupManager groupManager = (GroupManager)this.getServer().getPluginManager().getPlugin("GroupManager");
					_permissionHandler = new PermissionHandler_GroupManager(groupManager);
				}
				catch(Exception e){
					this.getLogger().severe("GroupManager was not found! Please edit the config.yml");
					this.getServer().getPluginManager().disablePlugin(this);
				}
				break;

			case "PERMISSIONS_EX":
				if(this.getServer().getPluginManager().getPlugin("PermissionsEx") != null){
					_permissionHandler = new PermissionHandler_PEX();
				}
				else{
					this.getLogger().severe("PermissionsEx was not found! Please edit the config.yml");
					this.getServer().getPluginManager().disablePlugin(this);
				}
				break;

			case "LUCK_PERMS":
				RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
				if(provider != null){
					_permissionHandler = new PermissionHandler_LuckPerms();
				}
				else{
					this.getLogger().severe("LuckPerms provider was not found! Please edit the config.yml");
					this.getServer().getPluginManager().disablePlugin(this);
				}
				break;

			case "CUSTOM":
				_permissionHandler = new PermissionHandler_CUSTOM();
				break;

			default:
				this.getLogger().severe("No valid permission manager found! Please edit the config.yml");
				this.getServer().getPluginManager().disablePlugin(this);
				break;
		}
	}

	@Override
	public void onDisable() {
		this.getLogger().info("Disabling PermissionShop");
	}
	
	public static JavaPlugin getPlugin() {
		return _plugin;
	}

	public static ConfigManager getConfigManager(){
		return _configManager;
	}

	public static IPermissionHandler getPermissionHandler(){
		return _permissionHandler;
	}
}
