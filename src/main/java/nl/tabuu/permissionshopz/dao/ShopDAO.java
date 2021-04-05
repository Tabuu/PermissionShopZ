package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Shop;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.serialization.string.AbstractStringSerializer;
import nl.tabuu.tabuucore.serialization.string.Serializer;

import java.util.Objects;

public class ShopDAO extends StringMapDAO<Shop> implements IConfigurationDAO<String, Shop>{

    @Override
    public String getConfigurationKey() {
        return "Shops";
    }

    @Override
    public IConfiguration getConfiguration() {
        return PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("shops.json");
    }

    @Override
    public AbstractStringSerializer<String> getKeySerializer() {
        return Serializer.STRING;
    }

    @Override
    protected String getUniqueKey(Shop object) {
        return object.getName();
    }

    @Override
    public boolean isGarbage(Shop shop) {
        if(Objects.isNull(shop)) return true;

        return shop.getContents().isEmpty();
    }
}