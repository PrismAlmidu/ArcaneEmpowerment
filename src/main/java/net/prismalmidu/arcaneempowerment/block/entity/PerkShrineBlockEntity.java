package net.prismalmidu.arcaneempowerment.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.prismalmidu.arcaneempowerment.capability.PlayerPerksProvider;
import net.prismalmidu.arcaneempowerment.networking.ClientboundSyncPerksPacket;
import net.prismalmidu.arcaneempowerment.networking.ModMessages;
import net.prismalmidu.arcaneempowerment.screen.PerkShrineMenu;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class PerkShrineBlockEntity extends BlockEntity implements MenuProvider {

    private final Set<String> unlockedPerks = new HashSet<>();

    public PerkShrineBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.PERK_SHRINE_BE.get(), pPos, pState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcaneempowerment.perk_shrine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PerkShrineMenu(pContainerId, pPlayerInventory, this);
    }

    public void tryUnlockPerkOnServer(ServerPlayer player, String perkId) {
        player.getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(perks -> {
            // 1. Force a live check of the blocks right before a purchase occurs
            validateStructure(this.level, this.worldPosition, this.getBlockState());

            // 2. Reject the transaction if the base blocks don't match your layout
            if (!this.isComplete) {
                player.displayClientMessage(Component.literal("§cThe Shrine's multi-block structure is incomplete!"), true);
                return;
            }

            if (perks.hasPerk(perkId)) return;

            // NEW CHECK: Costs 1 custom Perk Point per node block purchase
            if (perks.getPerkPoints() < 1) {
                player.displayClientMessage(Component.literal("§cYou do not have any spendable Perk Points! Eat special perk foods to claim more."), true);
                return;
            }

            // Spend point safely
            perks.consumePerkPoints(1);
            perks.unlockPerk(perkId);

            player.displayClientMessage(Component.literal("§aUnlocked perk: " + perkId + "!"), true);

            // Sync changes down to client capability store
            CompoundTag tag = new CompoundTag();
            perks.saveNBT(tag);
            ModMessages.sendToPlayer(new ClientboundSyncPerksPacket(tag), player);
        });
    }

    private boolean isComplete = false;

    public boolean isStructureComplete() {
        return this.isComplete;
    }

    public void validateStructure(Level world, BlockPos controllerPos, BlockState currentState) {
        boolean isValid =
                // Layer 1 (y = -1)
                // Row 1 (z = -9)
                checkBlock(world, controllerPos.offset(-9, -1, -9), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-8, -1, -9),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, -9),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(7, -1, -9),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(8, -1, -9),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(9, -1, -9),  Blocks.POLISHED_DIORITE) &&

                // Row 2 (z = -8)
                checkBlock(world, controllerPos.offset(-9, -1, -8), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-8, -1, -8),  Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-6, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-5, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-4, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-3, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-2, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(1, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(2, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(3, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(4, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(5, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(6, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(7, -1, -8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(8, -1, -8),  Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(9, -1, -8),  Blocks.POLISHED_DIORITE) &&

                // Row 3 (z = -7)
                checkBlock(world, controllerPos.offset(-9, -1, -7), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-8, -1, -7),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, -7),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(9, -1, -7),  Blocks.POLISHED_DIORITE) &&

                // Row 4 (z = -6)
                checkBlock(world, controllerPos.offset(-8, -1, -6),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, -6),  Blocks.POLISHED_DIORITE) &&

                // Row 5 (z = -5)
                checkBlock(world, controllerPos.offset(-8, -1, -5),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, -5),  Blocks.POLISHED_DIORITE) &&

                // Row 6 (z = -4)
                checkBlock(world, controllerPos.offset(-8, -1, -4),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, -4),  Blocks.POLISHED_DIORITE) &&

                // Row 7 (z = -3)
                checkBlock(world, controllerPos.offset(-8, -1, -3),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, -3),  Blocks.POLISHED_DIORITE) &&

                // Row 8 (z = -2)
                checkBlock(world, controllerPos.offset(-8, -1, -2),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, -2),  Blocks.POLISHED_DIORITE) &&

                // Row 9 (z = -1)
                checkBlock(world, controllerPos.offset(-8, -1, -1),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, -1),  Blocks.POLISHED_DIORITE) &&

                // Row 10 (z = 0)
                checkBlock(world, controllerPos.offset(-8, -1, 0),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, 0),  Blocks.POLISHED_DIORITE) &&

                // Row 11 (z = 1)
                checkBlock(world, controllerPos.offset(-8, -1, 1),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, 1),  Blocks.POLISHED_DIORITE) &&

                // Row 12 (z = 2)
                checkBlock(world, controllerPos.offset(-8, -1, 2),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, 2),  Blocks.POLISHED_DIORITE) &&

                // Row 13 (z = 3)
                checkBlock(world, controllerPos.offset(-8, -1, 3),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, 3),  Blocks.POLISHED_DIORITE) &&

                // Row 14 (z = 4)
                checkBlock(world, controllerPos.offset(-8, -1, 4),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, 4),  Blocks.POLISHED_DIORITE) &&

                // Row 15 (z = 5)
                checkBlock(world, controllerPos.offset(-8, -1, 5),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, 5),  Blocks.POLISHED_DIORITE) &&

                // Row 16 (z = 6)
                checkBlock(world, controllerPos.offset(-8, -1, 6),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, 6),  Blocks.POLISHED_DIORITE) &&

                // Row 17 (z = 7)
                checkBlock(world, controllerPos.offset(-9, -1, 7),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-8, -1, 7),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -1, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, -1, 7),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(9, -1, 7),  Blocks.POLISHED_DIORITE) &&

                // Row 18 (z = 8)
                checkBlock(world, controllerPos.offset(-9, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-8, -1, 8),  Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-6, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-5, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-4, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(1, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(2, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(3, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(4, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(5, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(6, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(7, -1, 8),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(8, -1, 8),  Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(9, -1, 8),  Blocks.POLISHED_DIORITE) &&

                // Row 19 (z = 9)
                checkBlock(world, controllerPos.offset(-9, -1, 9),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-8, -1, 9),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-7, -1, 9),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(7, -1, 9),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(8, -1, 9),  Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(9, -1, 9),  Blocks.POLISHED_DIORITE) &&

                // Layer 2 (y = 0)
                checkBlock(world, controllerPos.offset(-8, 0, -8),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, 0, -8),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-8, 0, 8),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(8, 0, 8),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&

                // Layer 3 (y = 1)
                checkBlock(world, controllerPos.offset(-8, 1, -8),  Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(8, 1, -8),  Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(-8, 1, 8),  Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(8, 1, 8),  Blocks.DIORITE_WALL) &&

                // Layer 4 (y = 2)
                checkBlock(world, controllerPos.offset(-8, 2, -8),  Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(8, 2, -8),  Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(-8, 2, 8),  Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(8, 2, 8),  Blocks.DIORITE_WALL) &&

                // Layer 5 (y = 3)
                checkBlock(world, controllerPos.offset(-8, 3, -8),  Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(8, 3, -8),  Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(-8, 3, 8),  Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(8, 3, 8),  Blocks.DIORITE_WALL) &&

                // Layer 6 (y = 4)
                checkBlock(world, controllerPos.offset(-8, 4, -8),  Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(8, 4, -8),  Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(-8, 4, 8),  Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(8, 4, 8),  Blocks.POLISHED_ANDESITE);

        this.isComplete = isValid;
        setChanged();
    }

    // Helper method to safely compare blocks in the world
    private boolean checkBlock(Level world, BlockPos pos, Block targetBlock) {
        return world.getBlockState(pos).is(targetBlock);
    }

    public boolean hasPerk(String perkId) {
        return this.unlockedPerks.contains(perkId);
    }

    // --- 3. HARD DRIVE PERSISTENT STORAGE (NBT WRITING) ---
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        // Create an NBT list wrapper
        ListTag perkList = new ListTag();
        for (String perk : this.unlockedPerks) {
            perkList.add(StringTag.valueOf(perk));
        }

        // Put list array into the root tag file
        pTag.put("PurchasedPerks", perkList);

        pTag.putBoolean("IsStructureComplete", this.isComplete);
    }

    // --- 4. HARD DRIVE LOAD LOGIC (NBT READING) ---
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.unlockedPerks.clear();

        if (pTag.contains("PurchasedPerks", Tag.TAG_LIST)) {
            ListTag perkList = pTag.getList("PurchasedPerks", Tag.TAG_STRING);
            for (int i = 0; i < perkList.size(); i++) {
                this.unlockedPerks.add(perkList.getString(i));
            }
        }
        this.isComplete = pTag.getBoolean("IsStructureComplete");
    }

    // --- 5. NETWORK LIVE SYNCHRONIZATION OVERLAYS ---
    // These methods ensure that when a player opens or looks at the block, the server instantly sends the data to the client's memory.

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
