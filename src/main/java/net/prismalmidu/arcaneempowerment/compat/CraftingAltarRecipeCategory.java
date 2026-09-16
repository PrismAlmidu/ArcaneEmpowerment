package net.prismalmidu.arcaneempowerment.compat;


import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.recipe.CraftingAltarRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CraftingAltarRecipeCategory implements IRecipeCategory<CraftingAltarRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(ArcaneEmpowerment.MOD_ID, "crafting_altar");
    public static final ResourceLocation TEXTURE = new ResourceLocation(ArcaneEmpowerment.MOD_ID,
            "textures/gui/crafting_altar_t5.png");

    public static final RecipeType<CraftingAltarRecipe> CRAFTING_ALTAR_TYPE =
            new RecipeType<>(UID, CraftingAltarRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CraftingAltarRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 3, 4, 170, 78);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CRAFTING_ALTAR_T5.get()));
    }

    @Override
    public RecipeType<CraftingAltarRecipe> getRecipeType() {
        return CRAFTING_ALTAR_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Crafting Altar");
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CraftingAltarRecipe recipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 13).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 45, 13).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 63, 13).addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 31).addIngredients(recipe.getIngredients().get(3));
        builder.addSlot(RecipeIngredientRole.INPUT, 45, 31).addIngredients(recipe.getIngredients().get(4));
        builder.addSlot(RecipeIngredientRole.INPUT, 63, 31).addIngredients(recipe.getIngredients().get(5));
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 49).addIngredients(recipe.getIngredients().get(6));
        builder.addSlot(RecipeIngredientRole.INPUT, 45, 49).addIngredients(recipe.getIngredients().get(7));
        builder.addSlot(RecipeIngredientRole.INPUT, 63, 49).addIngredients(recipe.getIngredients().get(8));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 30).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public List<Component> getTooltipStrings(CraftingAltarRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltips = new ArrayList<>();

        // --- Arrow Bounding Box Detection ---
        int arrowMinX = 92;
        int arrowMaxX = 116;
        int arrowMinY = 35;
        int arrowMaxY = 50;

        if (mouseX >= arrowMinX && mouseX <= arrowMaxX && mouseY >= arrowMinY && mouseY <= arrowMaxY) {
            // Retrieve the integer value dynamically parsed from your JSON recipe object
            int energyCost = recipe.getEnergyRequirement();

            // Build a localized and cleanly formatted string
            tooltips.add(Component.translatable("tooltip.arcaneempowerment.jei.energy_cost")
                    .withStyle(ChatFormatting.GOLD));
            tooltips.add(Component.literal(energyCost + " Mana")
                    .withStyle(ChatFormatting.GRAY));
        }

        return tooltips;
    }
}
