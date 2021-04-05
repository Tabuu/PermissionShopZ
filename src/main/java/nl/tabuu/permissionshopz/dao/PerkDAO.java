package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.Shop;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.string.Serializer;

import java.util.Collection;
import java.util.Objects;

public class PerkDAO extends IntegerTreeMapDAO<Perk> {
    @Override
    public boolean readAll() {
        if(!_data.isEmpty())
            _data.clear();

        IDataHolder data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("perks.json");
        _data.putAll(data.getSerializableMap("Perks", Perk.class, Serializer.INTEGER));
        return true;
    }

    @Override
    public boolean writeAll() {
        IConfiguration data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("perks.json");
        data.setSerializableMap("Perks", _data, Serializer.INTEGER);
        data.save();
        return true;
    }

    @Override
    public boolean isGarbage(Perk perk) {
        if(Objects.isNull(perk)) return true;

        Collection<Shop> shops = PermissionShopZ.getInstance().getShopDao().getAll();
        return shops.stream().noneMatch(shop -> shop.getContents().contains(perk));
    }
}