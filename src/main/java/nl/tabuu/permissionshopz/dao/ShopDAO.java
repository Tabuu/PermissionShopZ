package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Shop;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.string.Serializer;

import java.util.Objects;

public class ShopDAO extends IntegerTreeMapDAO<Shop> {
    @Override
    public boolean readAll() {
        if(!_data.isEmpty())
            _data.clear();

        IDataHolder data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("shops.json");
        _data.putAll(data.getSerializableMap("Shops", Shop.class, Serializer.INTEGER));
        return true;
    }

    @Override
    public boolean writeAll() {
        IConfiguration data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("shops.json");
        data.setSerializableMap("Shops", _data, Serializer.INTEGER);
        data.save();
        return true;
    }

    @Override
    public boolean isGarbage(Shop shop) {
        if(Objects.isNull(shop)) return true;

        return shop.getContents().isEmpty();
    }
}