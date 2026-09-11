package net.prismalmidu.arcaneempowerment.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.block.entity.BeaconT5BlockEntity;

public class BeaconT5Menu extends AbstractContainerMenu {
    private final BeaconT5BlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // Client-side constructor (Called automatically by Forge network pipeline)
    public BeaconT5Menu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(17));
    }

    // Common server-side constructor
    public BeaconT5Menu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.BEACON_T5_MENU.get(), id);
        checkContainerDataCount(data, 17);
        // FIXED: Safe cast check to prevent client-side NullPointerExceptions when chunks are loading
        if (entity instanceof BeaconT5BlockEntity beacon) {
            this.blockEntity = beacon;
        } else {
            this.blockEntity = null;
        }
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots(data);
    }

    // Direct data hooks for our UI rendering later
    public int getEnergy() { return this.data.get(0); }
    public int getSpeed() { return this.data.get(1); }
    public int getStrength() { return this.data.get(2); }
    public int getResistance() { return this.data.get(3); }
    public int getDrainRate() {return this.data.get(4); }
    public int getRegeneration() {return this.data.get(5); }
    public int getSaturation() {return this.data.get(6); }
    public int getHaste() {return this.data.get(7); }
    public int getHealthBoost() {return this.data.get(8); }
    public int getWaterBreathing() {return this.data.get(9); }
    public int getDolphinsGrace() {return this.data.get(10); }
    public int getLuck() {return this.data.get(11); }
    public int getSlowFalling() {return this.data.get(12); }
    public int getNightVision() {return this.data.get(13); }
    public int getJumpBoost() {return this.data.get(14); }
    public int getInvisibility() {return this.data.get(15); }
    public int getFireResistance() {return this.data.get(16); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // No functional item slots in this UI, quickMove can be empty
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.BEACON_T5.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
    }
}
