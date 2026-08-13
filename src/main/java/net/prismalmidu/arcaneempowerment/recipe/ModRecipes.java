package net.prismalmidu.arcaneempowerment.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ArcaneEmpowerment.MOD_ID);

    public static final RegistryObject<RecipeSerializer<CraftingAltarRecipe>> CRAFTING_ALTAR_SERIALIZER =
            SERIALIZERS.register("crafting_altar", () -> CraftingAltarRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
