package net.prismalmidu.arcaneempowerment.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.prismalmidu.arcaneempowerment.block.entity.CollectorCrystalBlockEntity;
import net.prismalmidu.arcaneempowerment.block.entity.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CollectorCrystalBlock extends BaseEntityBlock {

    public CollectorCrystalBlock(Properties pProperties) {
        super(pProperties);
    }

    // 1. Tells Minecraft to render this block as a normal 3D model rather than being invisible
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    // 2. Links the block entity instance to the physical world block
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new CollectorCrystalBlockEntity(pPos, pState);
    }

    // 3. Hooks up the server-side fluid production ticker
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        // We only tick on the server side
        if (pLevel.isClientSide()) {
            return null;
        }

        // Utility method from BaseEntityBlock that matches and verifies our registered block entity type
        return createTickerHelper(pBlockEntityType, ModBlockEntities.COLLECTOR_CRYSTAL_BE.get(),
                CollectorCrystalBlockEntity::tick);
    }

    // 4. Handles the bucket right-click interactions automatically via FluidUtil
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos,
                                          @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CollectorCrystalBlockEntity crystal) {
                // Fallback to opening GUI if not holding an interaction fluid container
                if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, crystal.getCapability(ForgeCapabilities.FLUID_HANDLER, pHit.getDirection()).orElse(null))) {
                    return InteractionResult.CONSUME;
                }

                net.minecraftforge.network.NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) pPlayer, crystal, pPos);
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
}