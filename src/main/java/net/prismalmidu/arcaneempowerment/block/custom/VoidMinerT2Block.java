package net.prismalmidu.arcaneempowerment.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;
import net.prismalmidu.arcaneempowerment.block.entity.ModBlockEntities;
import net.prismalmidu.arcaneempowerment.block.entity.VoidMinerT2BlockEntity;
import org.jetbrains.annotations.Nullable;

public class VoidMinerT2Block extends BaseEntityBlock {

    public VoidMinerT2Block(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoidMinerT2BlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Ensures the ticking logic runs ONLY on the server side
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.VOID_MINER_T2_BE.get(), VoidMinerT2BlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof VoidMinerT2BlockEntity voidMiner) {

                // 🛠️ TEMPORARY TESTING SHORTCUT:
                // If right-clicking with a Redstone Block, instantly fill the energy storage!
                if (player.getItemInHand(hand).is(net.minecraft.world.item.Items.REDSTONE_BLOCK)) {
                    // We access the capability on the server side to insert power directly
                    voidMiner.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY)
                            .ifPresent(energy -> energy.receiveEnergy(50000, false));

                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("§a[Debug] Energy Filled!§r"), true);
                    return InteractionResult.SUCCESS;
                }

                // Forge Network opens the screen safely across the channel boundary
                NetworkHooks.openScreen((ServerPlayer) player, voidMiner, pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // 1. Ensure the block is actually changing types (not just state properties like lighting/rotation)
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            // 2. Safely verify that this is our specific Void Miner block entity instance
            if (blockEntity instanceof VoidMinerT2BlockEntity voidMiner) {
                // 3. Request the internal item capability storage grid handler instance
                voidMiner.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
                    // 4. Loop through all 9 internal slots and drop any item stacks into the world
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        ItemStack stack = itemHandler.getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                        }
                    }
                });
            }

            // 5. Run the vanilla super method to cleanly delete the actual block entity from the world map
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
