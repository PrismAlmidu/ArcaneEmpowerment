package net.prismalmidu.arcaneempowerment.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;

public class ModRecipes {
    // 1. Serializers register (for parsing text)
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ArcaneEmpowerment.MOD_ID);

    // 2. TYPES Register (for identifying recipes in-world)
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ArcaneEmpowerment.MOD_ID);

    // --- Registered Recipes ---
    public static final RegistryObject<RecipeSerializer<CraftingAltarRecipe>> CRAFTING_ALTAR_SERIALIZER =
            SERIALIZERS.register("crafting_altar", () -> CraftingAltarRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<?>> TRANSMUTATION_SERIALIZER =
            SERIALIZERS.register("transmutation", () -> TransmutationRecipe.Serializer.INSTANCE);

    // FIX: Added the missing RecipeType definition that TransmutationRecipe is trying to find!
    public static final RegistryObject<RecipeType<TransmutationRecipe>> TRANSMUTATION_TYPE =
            TYPES.register("transmutation", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "transmutation";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus); // Crucial: Registers the RecipeType registry event bus listener
    }
}
