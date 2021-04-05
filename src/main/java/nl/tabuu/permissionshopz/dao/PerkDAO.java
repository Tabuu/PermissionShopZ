package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.Shop;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.configuration.IDataHolder;
import nl.tabuu.tabuucore.serialization.string.Serializer;

import java.util.Collection;
import java.util.Objects;

public class PerkDAO extends IntegerMapDAO<Perk> {
    @Override
    public boolean readAll() {
        if(!getDataBase().isEmpty())
            getDataBase().clear();

        IDataHolder data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("perks.json");
        getDataBase().putAll(data.getSerializableMap("Perks", Perk.class, Serializer.INTEGER));
        return true;
    }

    @Override
    public boolean writeAll() {
        IConfiguration data = PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("perks.json");
        data.setSerializableMap("Perks", getDataBase(), Serializer.INTEGER);
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