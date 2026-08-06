package net.prismalmidu.arcaneempowerment.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ArcaneEmpowerment.MOD_ID);

    public static final RegistryObject<Item> ARCANIUM_SHARD = ITEMS.register("arcanium_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PHANTASMIUM_SHARD = ITEMS.register("phantasmium_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STELLARIUM_SHARD = ITEMS.register("stellarium_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHRONIUM_SHARD = ITEMS.register("chronium_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VOLSARNIUM_SHARD = ITEMS.register("volsarnium_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LIQUID_MANA_BUCKET = ITEMS.register("liquid_mana_bucket",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
