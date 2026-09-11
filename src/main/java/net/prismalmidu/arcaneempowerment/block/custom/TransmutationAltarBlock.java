package net.prismalmidu.arcaneempowerment.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.prismalmidu.arcaneempowerment.block.entity.ModBlockEntities;
import net.prismalmidu.arcaneempowerment.block.entity.TransmutationAltarBlockEntity;
import net.prismalmidu.arcaneempowerment.recipe.ModRecipes;
import org.jetbrains.annotations.Nullable;

public class TransmutationAltarBlock extends BaseEntityBlock {

    public TransmutationAltarBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.TRANSMUTATION_ALTAR_BE.get().create(pPos, pState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) return null;

        return createTickerHelper(pBlockEntityType, ModBlockEntities.TRANSMUTATION_ALTAR_BE.get(),
                TransmutationAltarBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof TransmutationAltarBlockEntity altar) {
                ItemStack heldItem = pPlayer.getItemInHand(pHand);
                var inventory = altar.getItemHandler();

                // Check if player is trying to put an item into an empty input slot
                if (!heldItem.isEmpty() && inventory.getStackInSlot(0).isEmpty()) {
                    // Quick validation wrapper to check if a JSON recipe exists for this held item
                    SimpleContainer testContainer = new SimpleContainer(1);
                    testContainer.setItem(0, heldItem);
                    var recipeOpt = pLevel.getRecipeManager().getRecipeFor(ModRecipes.TRANSMUTATION_TYPE.get(), testContainer, pLevel);

                    if (recipeOpt.isPresent()) {
                        inventory.setStackInSlot(0, heldItem.copy());
                        pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
                        pLevel.playSound(null, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 0.8F);
                        return InteractionResult.SUCCESS;
                    }
                }

                // Extract logic for output slot (remains unchanged)
                ItemStack outputStack = inventory.getStackInSlot(1);
                if (!outputStack.isEmpty() && heldItem.isEmpty()) {
                    pPlayer.setItemInHand(pHand, outputStack.copy());
                    inventory.setStackInSlot(1, ItemStack.EMPTY);
                    pLevel.playSound(null, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.2F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof TransmutationAltarBlockEntity altar) {

                // Drop everything inside the ItemStackHandler into the world
                var inventory = altar.getItemHandler();
                for (int i = 0; i < inventory.getSlots(); i++) {
                    Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), inventory.getStackInSlot(i));
                }

                // Update comparator redstone signals if any are listening to this block position
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            // Super call handles removing the BlockEntity from the world safely
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }
}