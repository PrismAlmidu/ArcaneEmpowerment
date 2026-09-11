package net.prismalmidu.arcaneempowerment.screen;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
           DeferredRegister.create(ForgeRegistries.MENU_TYPES, ArcaneEmpowerment.MOD_ID);

    public static final RegistryObject<MenuType<CraftingAltarT1Menu>> CRAFTING_ALTAR_T1_MENU =
            registerMenuType("crafting_altar_t1_menu", CraftingAltarT1Menu::new);
    public static final RegistryObject<MenuType<CraftingAltarT2Menu>> CRAFTING_ALTAR_T2_MENU =
            registerMenuType("crafting_altar_t2_menu", CraftingAltarT2Menu::new);
    public static final RegistryObject<MenuType<CraftingAltarT3Menu>> CRAFTING_ALTAR_T3_MENU =
            registerMenuType("crafting_altar_t3_menu", CraftingAltarT3Menu::new);
    public static final RegistryObject<MenuType<CraftingAltarT4Menu>> CRAFTING_ALTAR_T4_MENU =
            registerMenuType("crafting_altar_t4_menu", CraftingAltarT4Menu::new);
    public static final RegistryObject<MenuType<CraftingAltarT5Menu>> CRAFTING_ALTAR_T5_MENU =
            registerMenuType("crafting_altar_t5_menu", CraftingAltarT5Menu::new);

    public static final RegistryObject<MenuType<VoidMinerT2Menu>> VOID_MINER_T2_MENU =
            registerMenuType("void_miner_t2_menu", VoidMinerT2Menu::new);
    public static final RegistryObject<MenuType<VoidMinerT3Menu>> VOID_MINER_T3_MENU =
            registerMenuType("void_miner_t3_menu", VoidMinerT3Menu::new);
    public static final RegistryObject<MenuType<VoidMinerT4Menu>> VOID_MINER_T4_MENU =
            registerMenuType("void_miner_t4_menu", VoidMinerT4Menu::new);
    public static final RegistryObject<MenuType<VoidMinerT5Menu>> VOID_MINER_T5_MENU =
            registerMenuType("void_miner_t5_menu", VoidMinerT5Menu::new);

    public static final RegistryObject<MenuType<BeaconT3Menu>> BEACON_T3_MENU =
            registerMenuType("beacon_t3_menu", BeaconT3Menu::new);
    public static final RegistryObject<MenuType<BeaconT4Menu>> BEACON_T4_MENU =
            registerMenuType("beacon_t4_menu", BeaconT4Menu::new);
    public static final RegistryObject<MenuType<BeaconT5Menu>> BEACON_T5_MENU =
            registerMenuType("beacon_t5_menu", BeaconT5Menu::new);

    public static final RegistryObject<MenuType<CollectorCrystalMenu>> COLLECTOR_CRYSTAL_MENU =
            registerMenuType("collector_crystal_menu", CollectorCrystalMenu::new);

    public static final RegistryObject<MenuType<PerkShrineMenu>> PERK_SHRINE_MENU =
            registerMenuType("perk_shrine_menu", PerkShrineMenu::new);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }


    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
