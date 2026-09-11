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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.prismalmidu.arcaneempowerment.block.entity.BeaconT4BlockEntity;
import net.prismalmidu.arcaneempowerment.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class BeaconT4Block extends Block implements EntityBlock {

    public BeaconT4Block(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BeaconT4BlockEntity(pPos, pState);
    }

    // 2. Automatically links the server-side tick method to the Minecraft game loop
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Only run ticking on the server-side
        if (level.isClientSide()) {
            return null;
        }

        // Compare types directly: if the world's BE type matches our registry type, return the ticker
        if (type == ModBlockEntities.BEACON_T4_BE.get()) {
            return (lvl, pos, st, blockEntity) -> {
                if (blockEntity instanceof BeaconT4BlockEntity beacon) {
                    beacon.tick(lvl, pos, st);
                }
            };
        }

        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && placer instanceof Player player) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BeaconT4BlockEntity beaconEntity) {
                beaconEntity.setOwnerUuid(player.getUUID());
                beaconEntity.setChanged(); // Forces server-side NBT memory serialization save immediately!
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BeaconT4BlockEntity beaconEntity) {

                // 1. UTILITY CHEAT CODE FIRST: Let players add energy even if the structure is broken!
                if (player.isCrouching() && player.getItemInHand(hand).isEmpty()) {
                    beaconEntity.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY)
                            .ifPresent(energy -> {
                                int energyAdded = energy.receiveEnergy(1000, false);
                                player.sendSystemMessage(Component.literal("§aAdded " + energyAdded + " FE. Current Energy: " + energy.getEnergyStored() + " FE"));
                            });
                    return InteractionResult.SUCCESS;
                }

                // 2. INSTANT SCAN FORCE: Force an instant validation on click so players never hit lag delays
                beaconEntity.validateStructure(level, pos, state);

                // 3. INTERCEPT INTERACTION: Check the freshly updated completion status
                if (!beaconEntity.isStructureComplete()) {
                    player.sendSystemMessage(Component.literal("§cStructure is incomplete or invalid!"));
                    return InteractionResult.CONSUME;
                }

                // 4. OPEN MENU: Fire screen transmission bridge
                NetworkHooks.openScreen((ServerPlayer) player, beaconEntity, pos);
            } else {
                throw new IllegalStateException("Our NamedContainerProvider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
