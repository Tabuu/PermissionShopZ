package nl.tabuu.permissionshopz.dao;

import nl.tabuu.permissionshopz.PermissionShopZ;
import nl.tabuu.permissionshopz.data.Perk;
import nl.tabuu.permissionshopz.data.Shop;
import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tabuucore.serialization.string.AbstractStringSerializer;
import nl.tabuu.tabuucore.serialization.string.Serializer;

import java.util.Collection;
import java.util.Objects;

public class PerkDAO extends IntegerMapDAO<Perk> implements IConfigurationDAO<Integer, Perk> {

    @Override
    public String getConfigurationKey() {
        return "Perks";
    }

    @Override
    public IConfiguration getConfiguration() {
        return PermissionShopZ.getInstance().getConfigurationManager().getConfiguration("perks.json");
    }

    @Override
    public AbstractStringSerializer<Integer> getKeySerializer() {
        return Serializer.INTEGER;
    }

    @Override
    public boolean isGarbage(Perk perk) {
        if (Objects.isNull(perk)) return true;

        Collection<Shop> shops = PermissionShopZ.getInstance().getShopDao().getAll();
        return shops.stream().noneMatch(shop -> shop.getContents().contains(perk));
    }
}