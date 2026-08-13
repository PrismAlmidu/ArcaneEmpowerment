package net.prismalmidu.arcaneempowerment.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CraftingAltarRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> inputItems;
    private final int width;
    private final int height;

    // Optional: Add custom requirements here later, such as energy costs
    // private final int energyRequirement;

    public CraftingAltarRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> inputItems, int width, int height) {
        this.id = id;
        this.output = output;
        this.inputItems = inputItems;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        // Checks every possible offset position on a 3x3 altar crafting grid
        for (int x = 0; x <= 3 - this.width; ++x) {
            for (int y = 0; y <= 3 - this.height; ++y) {
                if (this.matchesArea(pContainer, x, y, true)) {
                    return true;
                }
                if (this.matchesArea(pContainer, x, y, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the pattern matches a specific area of the Altar's container grid.
     */
    private boolean matchesArea(SimpleContainer pContainer, int offsetX, int offsetY, boolean mirror) {
        for (int slotX = 0; slotX < 3; ++slotX) {
            for (int slotY = 0; slotY < 3; ++slotY) {
                int recipeX = slotX - offsetX;
                int recipeY = slotY - offsetY;
                Ingredient ingredient = Ingredient.EMPTY;

                if (recipeX >= 0 && recipeY >= 0 && recipeX < this.width && recipeY < this.height) {
                    if (mirror) {
                        ingredient = this.inputItems.get(this.width - 1 - recipeX + recipeY * this.width);
                    } else {
                        ingredient = this.inputItems.get(recipeX + recipeY * this.width);
                    }
                }

                // Map 2D coordinates to 1D slot indices (0 to 8) for SimpleContainer
                if (!ingredient.test(pContainer.getItem(slotX + slotY * 3))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= this.width && pHeight >= this.height;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CraftingAltarRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "crafting_altar";
    }

    public static class Serializer implements RecipeSerializer<CraftingAltarRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ArcaneEmpowerment.MOD_ID, "crafting_altar");

        @Override
        public CraftingAltarRecipe fromJson(ResourceLocation id, JsonObject json) {
            // 1. Read pattern lines
            JsonArray patternArray = GsonHelper.getAsJsonArray(json, "pattern");
            String[] pattern = new String[patternArray.size()];
            for (int i = 0; i < pattern.length; i++) {
                pattern[i] = GsonHelper.convertToString(patternArray.get(i), "pattern[" + i + "]");
            }

            int width = pattern[0].length();
            int height = pattern.length;

            if (width > 3 || height > 3) {
                throw new JsonSyntaxException("Crafting Altar recipe pattern dimensions cannot exceed 3x3");
            }
            for (String line : pattern) {
                if (line.length() != width) {
                    throw new JsonSyntaxException("Crafting Altar recipe pattern lines must match the same width length");
                }
            }

            // 2. Read keys character mapping object
            JsonObject keyObject = GsonHelper.getAsJsonObject(json, "key");
            Map<Character, Ingredient> keyMap = new HashMap<>();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : keyObject.entrySet()) {
                if (entry.getKey().length() != 1) {
                    throw new JsonSyntaxException("Recipe key '" + entry.getKey() + "' must be a single character string");
                }
                if (" ".equals(entry.getKey())) {
                    throw new JsonSyntaxException("The space character ' ' is reserved for empty recipe slots");
                }
                keyMap.put(entry.getKey().charAt(0), Ingredient.fromJson(entry.getValue()));
            }
            keyMap.put(' ', Ingredient.EMPTY);

            // 3. Flatten patterned grid rows to sequential NonNullList
            NonNullList<Ingredient> inputs = NonNullList.withSize(width * height, Ingredient.EMPTY);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char c = pattern[y].charAt(x);
                    Ingredient ing = keyMap.get(c);
                    if (ing == null) {
                        throw new JsonSyntaxException("Pattern reference uses an undefined key mapping symbol: '" + c + "'");
                    }
                    inputs.set(x + y * width, ing);
                }
            }

            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            return new CraftingAltarRecipe(id, output, inputs, width, height);
        }

        @Override
        public @Nullable CraftingAltarRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int width = buf.readVarInt();
            int height = buf.readVarInt();

            NonNullList<Ingredient> inputs = NonNullList.withSize(width * height, Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new CraftingAltarRecipe(id, output, inputs, width, height);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CraftingAltarRecipe recipe) {
            buf.writeVarInt(recipe.getWidth());
            buf.writeVarInt(recipe.getHeight());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(null), false);
        }
    }
}