package net.prismalmidu.arcaneempowerment.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.recipe.ModRecipes;
import net.prismalmidu.arcaneempowerment.recipe.TransmutationRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TransmutationAltarBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private int progress = 0;
    private final int maxProgress = 40;

    public TransmutationAltarBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.TRANSMUTATION_ALTAR_BE.get(), pPos, pState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private boolean checkBlock(Level world, BlockPos pos, Block targetBlock) {
        return world.getBlockState(pos).is(targetBlock);
    }

    private boolean checkFluidSource(Level world, BlockPos pos, Block targetBlock) {
        BlockState state = world.getBlockState(pos);
        net.minecraft.world.level.material.FluidState fluidState = world.getFluidState(pos);

        // Returns true ONLY if the block matches AND it is a source block
        return state.is(targetBlock) && fluidState.isSource();
    }

    public boolean isStructureValid() {
        if (this.level == null) return false;

        BlockPos controllerPos = this.worldPosition;

        return
                // Layer 1 (y = -2)
                checkBlock(this.level, controllerPos.offset(-1, -2, -2), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(0, -2, -2), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(1, -2, -2), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-2, -2, -1), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(2, -2, -1), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-2, -2, 0), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(2, -2, 0), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-2, -2, 1), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(2, -2, 1), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-1, -2, 2), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(0, -2, 2), Blocks.DIORITE) &&
                checkBlock(this.level, controllerPos.offset(1, -2, 2), Blocks.DIORITE) &&


                // Layer 2 (y = -1)
                // Row 1 (z -3)
                checkBlock(this.level, controllerPos.offset(-3, -1, -3), Blocks.POLISHED_ANDESITE) &&
                checkBlock(this.level, controllerPos.offset(-2, -1, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-1, -1, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(0, -1, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(1, -1, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(2, -1, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(3, -1, -3), Blocks.POLISHED_ANDESITE) &&

                // Row 2 (z -2)
                checkBlock(this.level, controllerPos.offset(-3, -1, -2), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-2, -1, -2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkFluidSource(level, controllerPos.offset(-1, -1, -2), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkFluidSource(level, controllerPos.offset(0, -1, -2), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkFluidSource(level, controllerPos.offset(1, -1, -2), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkBlock(this.level, controllerPos.offset(2, -1, -2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(this.level, controllerPos.offset(3, -1, -2), Blocks.POLISHED_DIORITE) &&

                // Row 3 (z -1)
                checkBlock(this.level, controllerPos.offset(-3, -1, -1), Blocks.POLISHED_DIORITE) &&
                checkFluidSource(level, controllerPos.offset(-2, -1, -1), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkBlock(this.level, controllerPos.offset(-1, -1, -1), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(this.level, controllerPos.offset(0, -1, -1), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(this.level, controllerPos.offset(1, -1, -1), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkFluidSource(level, controllerPos.offset(2, -1, -1), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkBlock(this.level, controllerPos.offset(3, -1, -1), Blocks.POLISHED_DIORITE) &&

                // Row 4 (z 0)
                checkBlock(this.level, controllerPos.offset(-3, -1, 0), Blocks.POLISHED_DIORITE) &&
                checkFluidSource(level, controllerPos.offset(-2, -1, 0), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkBlock(this.level, controllerPos.offset(-1, -1, 0), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(this.level, controllerPos.offset(0, -1, 0), Blocks.LAPIS_BLOCK) &&
                checkBlock(this.level, controllerPos.offset(1, -1, 0), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkFluidSource(level, controllerPos.offset(2, -1, 0), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkBlock(this.level, controllerPos.offset(3, -1, 0), Blocks.POLISHED_DIORITE) &&

                // Row 5 (z 1)
                checkBlock(this.level, controllerPos.offset(-3, -1, 1), Blocks.POLISHED_DIORITE) &&
                checkFluidSource(level, controllerPos.offset(-2, -1, 1), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkBlock(this.level, controllerPos.offset(-1, -1, 1), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(this.level, controllerPos.offset(0, -1, 1), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(this.level, controllerPos.offset(1, -1, 1), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkFluidSource(level, controllerPos.offset(2, -1, 1), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkBlock(this.level, controllerPos.offset(3, -1, 1), Blocks.POLISHED_DIORITE) &&

                // Row 6 (z 2)
                checkBlock(this.level, controllerPos.offset(-3, -1, 2), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-2, -1, 2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkFluidSource(level, controllerPos.offset(-1, -1, 2), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkFluidSource(level, controllerPos.offset(0, -1, 2), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkFluidSource(level, controllerPos.offset(1, -1, 2), ModBlocks.LIQUID_MANA_BLOCK.get()) &&
                checkBlock(this.level, controllerPos.offset(2, -1, 2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(this.level, controllerPos.offset(3, -1, 2), Blocks.POLISHED_DIORITE) &&

                // Row 7 (z 3)
                checkBlock(this.level, controllerPos.offset(-3, -1, 3), Blocks.POLISHED_ANDESITE) &&
                checkBlock(this.level, controllerPos.offset(-2, -1, 3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-1, -1, 3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(0, -1, 3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(1, -1, 3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(2, -1, 3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(3, -1, 3), Blocks.POLISHED_ANDESITE) &&

                // Layer 3 (y = 0)
                checkBlock(this.level, controllerPos.offset(-3, 0, -3), Blocks.DIORITE_WALL) &&
                checkBlock(this.level, controllerPos.offset(3, 0, -3), Blocks.DIORITE_WALL) &&
                checkBlock(this.level, controllerPos.offset(-3, 0, 3), Blocks.DIORITE_WALL) &&
                checkBlock(this.level, controllerPos.offset(3, 0, 3), Blocks.DIORITE_WALL) &&

                // Layer 4 (y = 1)
                checkBlock(this.level, controllerPos.offset(-3, 1, -3), Blocks.DIORITE_WALL) &&
                checkBlock(this.level, controllerPos.offset(3, 1, -3), Blocks.DIORITE_WALL) &&
                checkBlock(this.level, controllerPos.offset(-3, 1, 3), Blocks.DIORITE_WALL) &&
                checkBlock(this.level, controllerPos.offset(3, 1, 3), Blocks.DIORITE_WALL) &&

                // Layer 5 (y = 2)
                checkBlock(this.level, controllerPos.offset(-3, 2, -3), Blocks.POLISHED_ANDESITE) &&
                checkBlock(this.level, controllerPos.offset(3, 2, -3), Blocks.POLISHED_ANDESITE) &&
                checkBlock(this.level, controllerPos.offset(-3, 2, 3), Blocks.POLISHED_ANDESITE) &&
                checkBlock(this.level, controllerPos.offset(3, 2, 3), Blocks.POLISHED_ANDESITE) &&

                // Layer 6 (y = 3)
                checkBlock(this.level, controllerPos.offset(-2, 3, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(2, 3, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-3, 3, -2), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(3, 3, -2), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-3, 3, 2), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(3, 3, 2), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-2, 3, 3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(2, 3, 3), Blocks.POLISHED_DIORITE) &&

                // Layer 7 (y = 4)
                checkBlock(this.level, controllerPos.offset(-1, 4, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(0, 4, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(1, 4, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-3, 4, -1), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(3, 4, -1), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-3, 4, 0), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(3, 4, 0), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-3, 4, 1), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(3, 4, 1), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(-1, 4, 3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(0, 4, 3), Blocks.POLISHED_DIORITE) &&
                checkBlock(this.level, controllerPos.offset(1, 4, 3), Blocks.POLISHED_DIORITE);
    }

    // 20% chance to break one random Liquid Mana block from the structure
    private void tryConsumeLiquidMana() {
        if (this.level == null || this.level.isClientSide()) return;

        // Roll a 20% chance (0.20)
        if (this.level.random.nextFloat() < 0.20F) {
            BlockPos controllerPos = this.worldPosition;

            // Array of the relative Diorite positions in your structure
            BlockPos[] manaPositions = new BlockPos[] {
                    controllerPos.offset(-1, -1, -2),
                    controllerPos.offset(0, -1, -2),
                    controllerPos.offset(1, -1, -2),
                    controllerPos.offset(-2, -1, -1),
                    controllerPos.offset(2, -1, -1),
                    controllerPos.offset(-2, -1, 0),
                    controllerPos.offset(2, -1, 0),
                    controllerPos.offset(-2, -1, 1),
                    controllerPos.offset(2, -1, 1),
                    controllerPos.offset(-1, -1, 2),
                    controllerPos.offset(0, -1, 2),
                    controllerPos.offset(1, -1, 2)
            };

            int randomIndex = this.level.random.nextInt(manaPositions.length);
            BlockPos targetPos = manaPositions[randomIndex];

            // --- THE FIXED LOGIC FOR FLUID CONSUMPTION ---
            // 1. Force-set the block state directly to Air (Deletes the liquid instantly)
            // Flag 3 flags a full block update and sends notification packets to nearby clients
            this.level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);

            // 2. Manually play the splash/fizz sound effects at the target coordinates
            this.level.playSound(null, targetPos, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY_LAVA,
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.2F);

            // 3. Force physics recalculation on neighboring blocks
            // This prevents vanilla infinite fluid sources from instantly refilling the consumed pocket!
            this.level.blockUpdated(targetPos, Blocks.AIR);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TransmutationAltarBlockEntity pEntity) {
        if (level.isClientSide())
            return;

        if (pEntity.hasRecipe()) {
            pEntity.progress++;
            setChanged(level, pos, state);
            if (pEntity.progress >= pEntity.maxProgress) {
                pEntity.craftItem();
                pEntity.progress = 0;
            }
        } else {
            pEntity.progress = 0;
        }
    }

    private Optional<TransmutationRecipe> getCurrentRecipe() {
        SimpleContainer inventoryWrapper = new SimpleContainer(this.itemHandler.getSlots());
        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            inventoryWrapper.setItem(i, this.itemHandler.getStackInSlot(i));
        }
        return this.level.getRecipeManager().getRecipeFor(ModRecipes.TRANSMUTATION_TYPE.get(), inventoryWrapper, this.level);
    }

    private boolean hasRecipe() {
        if (!this.isStructureValid()) {
            return false;
        }

        Optional<TransmutationRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack recipeOutput = recipe.get().getResultItem(this.level.registryAccess());
        ItemStack currentOutputSlot = this.itemHandler.getStackInSlot(1);

        if (currentOutputSlot.isEmpty()) return true;

        boolean matchesOutput = currentOutputSlot.is(recipeOutput.getItem());
        boolean hasSpace = (currentOutputSlot.getCount() + recipeOutput.getCount()) <= currentOutputSlot.getMaxStackSize();

        return matchesOutput && hasSpace;
    }

    private void craftItem() {
        Optional<TransmutationRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack recipeOutput = recipe.get().getResultItem(this.level.registryAccess());

        // 1. Consume 1 item from Input Slot (0)
        this.itemHandler.extractItem(0, 1, false);

        // 2. Output result to Output Slot (1)
        ItemStack currentOutput = this.itemHandler.getStackInSlot(1);
        if (currentOutput.isEmpty()) {
            this.itemHandler.setStackInSlot(1, recipeOutput.copy());
        } else {
            currentOutput.grow(recipeOutput.getCount());
        }

        tryConsumeLiquidMana();
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("Inventory", itemHandler.serializeNBT());
        pTag.putInt("Progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("Inventory"));
        progress = pTag.getInt("Progress");
    }
}
