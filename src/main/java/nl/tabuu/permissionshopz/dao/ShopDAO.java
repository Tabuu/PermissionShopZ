package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Shop;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.IDataHolder;

import java.util.Objects;

public class ShopDAO extends StringMapDAO<Shop> {

    @Override
    public boolean readAll() {
        if(!getDataBase().isEmpty())
            getDataBase().clear();

        IDataHolder data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("shops.json");
        getDataBase().putAll(data.getSerializableMap("Shops", Shop.class));
        return true;
    }

    @Override
    public boolean writeAll() {
        IConfiguration data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("shops.json");
        data.setSerializableMap("Shops", getDataBase());
        data.save();
        return true;
    }

    @Override
    public boolean isGarbage(Shop shop) {
        if(Objects.isNull(shop)) return true;

        return shop.getContents().isEmpty();
    }

    @Override
    protected String getUniqueKey(Shop object) {
        return object.getName();
    }
}