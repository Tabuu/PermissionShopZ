package nl.tabuu.permissionshopz.gui;

import net.milkbowl.vault.economy.Economy;
import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.permissionhandler.IPermissionHandler;
import nl.tabuu.permissionshopz.util.Message;
import nl.tabuu.tabuucore.configuration.ConfigurationManager;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.economy.hook.Vault;
import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryFormUI;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.item.ItemBuilder;
import nl.tabuu.tabuucore.material.SafeMaterial;
import nl.tabuu.tabuucore.util.Dictionary;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class ShopInterface extends InventoryFormUI {

    private IConfiguration _config;
    protected Dictionary _local;

    private Economy _economy;

    protected int _currentPage = 0;
    private int _maxPage;

    private Player _player;
    private IPermissionHandler _permissionHandler;

    public ShopInterface(Player player){
        super("", InventorySize.TWO_ROWS);

        ConfigurationManager manager = PermissionShopZ.getInstance().getConfigurationManager();
        _config = manager.getConfiguration("config");
        _local = manager.getConfiguration("lang").getDictionary("");

        _economy = Vault.getEconomy();
        _player = player;
        _permissionHandler = PermissionShopZ.getInstance().getPermissionHandler();

        _maxPage = PermissionShopZ.getInstance().getPerkManager().getPerks().size() / 9;

        updateTitle();
    }

    @Override
    protected void draw() {
        ItemBuilder
                next =              new ItemBuilder(SafeMaterial.GREEN_STAINED_GLASS_PANE)
                                            .setDisplayName(_local.translate("GUI_PAGE_NEXT")),

                previous =          new ItemBuilder(SafeMaterial.GREEN_STAINED_GLASS_PANE)
                                            .setDisplayName(_local.translate("GUI_PAGE_PREVIOUS")),

                barrier =           new ItemBuilder(SafeMaterial.BARRIER)
                                            .setDisplayName(_local.translate("GUI_PAGE_CLOSE"));

        Style
                nextButtonStyle =       new Style(next.build(), next.build()),
                previousButtonStyle =   new Style(previous.build(), previous.build()),
                exitButtonStyle =       new Style(barrier.build(), barrier.build());

        Button
                nextButton =            new Button(nextButtonStyle, this::nextPage),
                previousButton =        new Button(previousButtonStyle, this::previousPage),
                exitButton =            new Button(exitButtonStyle, this::onCloseButton);

        setElement(new Vector2f(8, 1), nextButton);
        setElement(new Vector2f(0, 1), previousButton);
        setElement(new Vector2f(4, 1), exitButton);

        List<Perk> perks = new ArrayList<>(PermissionShopZ.getInstance().getPerkManager().getPerks());
        for(int i = _currentPage * 9; i < (_currentPage * 9) + 9; i++){
            Vector2f position = new Vector2f(i % 9, 0);

            if(perks.size() < i + 1) {
                setElement(position, new Button(new Style(SafeMaterial.AIR.toItemStack(), SafeMaterial.AIR.toItemStack())));
                continue;
            }

            Perk perk = perks.get(i);
            setElement(position, createPerkItem(_player, perk));
        }
        super.draw();
    }

    protected Button createPerkItem(Player player, Perk perk){
        SafeMaterial unlockedItem = _config.getEnum(SafeMaterial.class, "UnlockedMaterial");

        boolean unlocked = perk.getPermissions().stream().allMatch(node -> _player.hasPermission(node));
        ItemBuilder displayItem = new ItemBuilder(unlocked ? unlockedItem.toItemStack() : perk.getDisplayItem());
        displayItem.setDisplayName(ChatColor.translateAlternateColorCodes('&', perk.getName()));

        if(_config.getBoolean("DisplayPermissionList")){
            for(String node : perk.getPermissions()) {
                String text = "PERK_PERMISSION_NODE";
                if(_player.hasPermission(node))
                    text += "_HAS";

                displayItem.addLore(_local.translate(text, "{NODE}", node));
            }
        }

        if(unlocked)
            displayItem.addLore(_local.translate("GUI_PERK_UNLOCKED"));
        else
            displayItem.addLore(_local.translate("GUI_PERK_PRICE", "{PRICE}", suffixFormat(perk.getCost())));

        Style style = new Style(displayItem.build(), displayItem.build());
        Button button = new Button(style, p -> onPerkClick(player, perk));
        button.setEnabled(!unlocked);

        return button;
    }

    protected void onPerkClick(Player player, Perk perk){
        if(_economy.has(player, perk.getCost())){
            _economy.withdrawPlayer(player, perk.getCost());
            perk.getPermissions().forEach(node -> _permissionHandler.addPermission(player, node));
            Message.send(player, _local.translate("PERK_BUY_SUCCESSFUL", "{PERK_NAME}", perk.getName()));
        }
        else{
            Message.send(player, _local.translate("PERK_BUY_INSUFFICIENTFUNDS", "{PERK_NAME}", perk.getName()));
        }
        updateTitle();
    }

    protected void updateTitle(){
        setTitle(_local.translate("GUI_TITLE", "{PAGE_NUMBER}", (_currentPage + 1) + ""));
        reload();
        draw();
    }

    private void onCloseButton(Player player){
        this.close(player);
    }

    private void nextPage(Player player){
        if(_currentPage < _maxPage)
            _currentPage++;

        updateTitle();
    }

    private void previousPage(Player player){
        if(_currentPage > 0)
            _currentPage--;

        updateTitle();
    }

    private String suffixFormat(double value){
        NavigableMap<Double, String> suffixMap = new TreeMap<>();
        Set<String> suffixList = _config.getConfigurationSection("NumberSuffixes").getKeys(false);

        for(String string : suffixList){
            int zeroCount = Integer.parseInt(string);
            String suffix = _config.getString("NumberSuffixes." + string);
            suffixMap.put(Math.pow(10d, zeroCount), suffix);
        }

        Map.Entry<Double, String> entry = suffixMap.floorEntry(value);

        if(entry == null)
            return value + "";

        Double divider = entry.getKey();
        String suffix = entry.getValue();

        double formattedNumber = value / (divider / 10);
        boolean hasDecimal = formattedNumber < 100 && (formattedNumber / 10d) != (formattedNumber / 10);
        return hasDecimal ? (formattedNumber / 10d) + suffix : (formattedNumber / 10) + suffix;
    }
}
