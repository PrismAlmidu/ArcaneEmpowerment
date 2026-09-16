package net.prismalmidu.arcaneempowerment.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.recipe.TransmutationRecipe;

public class TransmutationRecipeCategory implements IRecipeCategory<TransmutationRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(ArcaneEmpowerment.MOD_ID, "transmutation");

    public static final RecipeType<TransmutationRecipe> TRANSMUTATION_TYPE =
            new RecipeType<>(UID, TransmutationRecipe.class);

    private final IDrawable background;
    private final IDrawable slotDrawable;
    private final IDrawable icon;

    public TransmutationRecipeCategory(IGuiHelper helper) {
        // Create an empty canvas window (Width: 120px, Height: 45px)
        // This completely bypasses the need for an underlying custom file texture sheet.
        this.background = helper.createBlankDrawable(120, 45);

        // Grab JEI's native slot square silhouette box (the classic inventory square asset)
        this.slotDrawable = helper.getSlotDrawable();

        // Hook up the block icon tab indicator
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.TRANSMUTATION_ALTAR.get()));
    }

    @Override
    public RecipeType<TransmutationRecipe> getRecipeType() {
        return TRANSMUTATION_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.arcaneempowerment.transmutation_altar");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TransmutationRecipe recipe, IFocusGroup focuses) {
        // Arrange inputs and outputs neatly within your 120x45 blank boundary

        // Input Item (X: 10, Y: 15)
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 15)
                .addIngredients(recipe.getIngredient())
                .setBackground(slotDrawable, -1, -1); // Offsets slightly to render the square frame cleanly

        // Output Transmuted Product (X: 90, Y: 15)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 15)
                .addItemStack(recipe.getResultItem(null))
                .setBackground(slotDrawable, -1, -1);
    }

    @Override
    public void draw(TransmutationRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, net.minecraft.client.gui.GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Use Minecraft's native recipe book/furnace texture location for the arrow graphic
        ResourceLocation vanillaGui = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

        // Render a flat static arrow pointing right between your input and output slots
        // Parameters: (Texture path, screenX, screenY, textureU, textureV, width, height)
        // 79, 34 targets the default furnace arrow on the vanilla sheet
        guiGraphics.blit(vanillaGui, 55, 16, 79, 34, 24, 17);
    }
}