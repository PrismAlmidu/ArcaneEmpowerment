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

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }


    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
