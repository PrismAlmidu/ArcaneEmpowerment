package net.prismalmidu.arcaneempowerment.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.recipe.CraftingAltarRecipe;
import net.prismalmidu.arcaneempowerment.recipe.ModRecipes;
import net.prismalmidu.arcaneempowerment.recipe.TransmutationRecipe;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public class JEIArcaneEmpowermentPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ArcaneEmpowerment.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {

        registration.addRecipeCategories(new CraftingAltarRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new TransmutationRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<CraftingAltarRecipe> altarRecipes = recipeManager.getAllRecipesFor(CraftingAltarRecipe.Type.INSTANCE);
        registration.addRecipes(CraftingAltarRecipeCategory.CRAFTING_ALTAR_TYPE, altarRecipes);

        List<TransmutationRecipe> transmutationRecipes = recipeManager.getAllRecipesFor(ModRecipes.TRANSMUTATION_TYPE.get());
        registration.addRecipes(TransmutationRecipeCategory.TRANSMUTATION_TYPE, transmutationRecipes);

    }
}