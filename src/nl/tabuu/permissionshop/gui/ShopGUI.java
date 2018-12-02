package nl.tabuu.permissionshop.gui;

import net.milkbowl.vault.economy.Economy;
import nl.tabuu.permissionshop.PermissionShop;
import nl.tabuu.tabuucore.configuration.Config;
import nl.tabuu.tabuucore.configuration.LanguageConfig;
import nl.tabuu.tabuucore.gui.GUIClick;
import nl.tabuu.tabuucore.gui.IGUI;
import nl.tabuu.tabuucore.hooks.vault.VaultEconomy;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.packets.titlepackets.TitleAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopGUI implements IGUI {

    private LanguageConfig _langConfig;
    private Config _data, _config;

    private Player _player;
    private Economy _economy;

    private int _currentPage = 1;
    private int _maxPage = 1;

    List<String> _perkIds;

    public ShopGUI(Player player){
        _data = PermissionShop.getConfigManager().getConfig("data");
        _config = PermissionShop.getConfigManager().getConfig("config");
        _langConfig = PermissionShop.getConfigManager().getLanguageConfig("lang");

        _player = player;
        _economy = VaultEconomy.getEconomy();

        updatePerkList();
    }

    private void updatePerkList(){
        _perkIds = new ArrayList<>();
        if(_data.getData().getConfigurationSection("Perks") == null) {
            TitleAPI.sendActionbar(_player, _langConfig.parseText("ERROR_EMPTYSHOP"), 10, 20, 10);
        }
        else{
            _maxPage = (int) Math.ceil(_data.getData().getConfigurationSection("Perks").getKeys(false).size() / 9d);
            _perkIds.addAll(_data.getData().getConfigurationSection("Perks").getKeys(false));
        }
    }

    @Override
    public void onGUIClick(Player player, GUIClick guiClick) {

        ItemStack itemStack = guiClick.getClickedItem();
        int slot = guiClick.getSlot();

        if(itemStack == null)
            return;

        //Navigation
        switch (slot){
            case 13:
                player.closeInventory();
                break;

            case 9:
                if(!itemStack.getType().equals(Material.AIR)){
                    _currentPage--;
                    player.openInventory(this.getInventory());
                }
                break;

            case 17:
                if(!itemStack.getType().equals(Material.AIR)){
                    _currentPage++;
                    player.openInventory(this.getInventory());
                }
                break;
        }

        //Perk click
        if(slot < 9){
            String perkName = _perkIds.get(((_currentPage - 1) * 9) + slot);
            ConfigurationSection perkData = _data.getData().getConfigurationSection("Perks." + perkName);

            List<String> permissionNodes = perkData.getStringList("Permissions");
            double price = perkData.getDouble("Price");

            boolean unlocked = !permissionNodes.stream().anyMatch(permission -> !_player.hasPermission(permission));;

            if(unlocked){
                TitleAPI.sendActionbar(player, _langConfig.parseText("PERK_BUY_UNLOCKED"), 10, 20, 10);
            }
            else if(_economy.has(player, price)){
                _economy.withdrawPlayer(player, price);

                for(String permissionNode : permissionNodes){
                    PermissionShop.getPermissionHandler().addPermission(player, permissionNode);
                }

                TitleAPI.sendActionbar(player, _langConfig.parseText("PERK_BUY_SUCCESSFULL", "{PERK_NAME}", perkName), 10, 20, 10);
                player.openInventory(this.getInventory());
            }
            else{
                TitleAPI.sendActionbar(player, _langConfig.parseText("PERK_BUY_INSUFFICIENTFUNDS"), 10, 20, 10);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 18, _langConfig.parseText("GUI_TITLE", "{PAGE_NUMBER}", _currentPage + ""));

        //Navigation
        ItemBuilder nextButton = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE);
        nextButton.setDisplayName(_langConfig.parseText("GUI_PAGE_NEXT"));

        ItemBuilder previousButton = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE);
        previousButton.setDisplayName(_langConfig.parseText("GUI_PAGE_PREVIOUS"));

        ItemBuilder closeButton = new ItemBuilder(Material.BARRIER);
        closeButton.setDisplayName(_langConfig.parseText("GUI_PAGE_CLOSE"));

        if(_currentPage > 1)
            inventory.setItem(9, previousButton.build());

        if(_currentPage < _maxPage)
            inventory.setItem(17, nextButton.build());

        inventory.setItem(13, closeButton.build());

        //Perk items
        for(int i = (_currentPage - 1) * 9; i < ((_currentPage - 1) * 9) + 9; i++){

            if(i >= _perkIds.size()) break;

            String perkName = _perkIds.get(i);
            ConfigurationSection perkData = _data.getData().getConfigurationSection("Perks." + perkName);

            List<String> permissionNodes = perkData.getStringList("Permissions");
            double price = perkData.getDouble("Price");
            ItemStack displayItem = perkData.getItemStack("DisplayItem");

            boolean unlocked = !permissionNodes.stream().anyMatch(permission -> !_player.hasPermission(permission));

            ItemBuilder perkItem = new ItemBuilder(displayItem);
            if(unlocked){
                String materialString = _config.getData().getString("UnlockedMaterial").toUpperCase();
                try{
                    Material material = Material.valueOf(materialString);
                    perkItem.setMaterial(material);
                }
                catch (IllegalArgumentException e){
                    PermissionShop.getPlugin().getLogger().severe("Could not find material with id \"" + materialString + "\".");
                }
            }

            perkItem.setDisplayName(ChatColor.translateAlternateColorCodes('&', perkName))
                    .clearLore()
                    .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);

            if(_config.getBoolean("DisplayPermissionList")){
                for(String permission : permissionNodes){
                    if(_player.hasPermission(permission))
                        perkItem.addLore(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "- " + permission + ChatColor.RESET + " " + ChatColor.GREEN + "✔");
                    else
                        perkItem.addLore(ChatColor.GRAY + "- " + permission);
                }
                perkItem.addLore("");
            }

            if(unlocked) {
                perkItem.addLore(_langConfig.parseText("GUI_PERK_UNLOCKED"));
            }
            else {
                String priceString;
                if(_config.getBoolean("UseNumberSuffix"))
                    priceString = suffixFormat(price);
                else
                    priceString = price + "";

                perkItem.addLore(_langConfig.parseText("GUI_PERK_PRICE", "{PRICE}", priceString));
            }

            inventory.setItem(i % 9, perkItem.build());
        }
        return inventory;
    }

    private String suffixFormat(double value){
        NavigableMap<Double, String> suffixMap = new TreeMap<>();
        List<String> suffixList = _config.getKeyList("NumberSuffixes");

        for(String string : suffixList){
            int zeroCount = Integer.parseInt(string);
            String suffix = _config.getString("NumberSuffixes." + string);
            suffixMap.put(Math.pow(10d, zeroCount), suffix);
        }

        Map.Entry<Double, String> entry = suffixMap.floorEntry(value);
        Double divider = entry.getKey();
        String suffix = entry.getValue();

        double formattedNumber = value / (divider / 10);
        boolean hasDecimal = formattedNumber < 100 && (formattedNumber / 10d) != (formattedNumber / 10);
        return hasDecimal ? (formattedNumber / 10d) + suffix : (formattedNumber / 10) + suffix;
    }
}
