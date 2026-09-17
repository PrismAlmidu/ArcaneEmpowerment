package net.prismalmidu.arcaneempowerment.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.prismalmidu.arcaneempowerment.block.entity.EldrinGeneratorBlockEntity;
import net.prismalmidu.arcaneempowerment.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class EldrinGeneratorBlock extends BaseEntityBlock {
    public EldrinGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL; // Ensured block renders correctly
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EldrinGeneratorBlockEntity(pos, state);
    }

    // Assign block ownership upon being placed
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && placer instanceof Player player) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof EldrinGeneratorBlockEntity generator) {
                generator.setOwner(player);
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    // Attach the server-side ticking function
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.ELDRIN_GENERATOR_BE.get(), EldrinGeneratorBlockEntity::tick);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof EldrinGeneratorBlockEntity generator) {
                generator.validateStructure(level, pos, state);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof EldrinGeneratorBlockEntity generator) {

                // Normal gameplay behavior when clicking with empty hands or normal blocks
                if (generator.isStructureComplete()) {
                    NetworkHooks.openScreen((ServerPlayer) player, generator, pos);
                } else {
                    player.sendSystemMessage(Component.literal("§cStructure Incomplete! Check the base patterns. Right-click with a Diamond to force-complete for testing.§r"));
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }
}