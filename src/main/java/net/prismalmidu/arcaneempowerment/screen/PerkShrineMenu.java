package net.prismalmidu.arcaneempowerment.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.block.entity.PerkShrineBlockEntity;

public class PerkShrineMenu extends AbstractContainerMenu {
    private final PerkShrineBlockEntity blockEntity;

    public PerkShrineMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, (PerkShrineBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public PerkShrineMenu(int pContainerId, Inventory inv, PerkShrineBlockEntity entity) {
        super(ModMenuTypes.PERK_SHRINE_MENU.get(), pContainerId);
        this.blockEntity = entity;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        boolean distanceValid = stillValid(
                ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                pPlayer,
                ModBlocks.PERK_SHRINE.get()
        );

        return distanceValid && this.blockEntity.isStructureComplete();
    }

    // =========================================================================
    // VERIFY THIS METHOD EXISTS AND IS PUBLIC
    // =========================================================================
    public PerkShrineBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
    // =========================================================================
}