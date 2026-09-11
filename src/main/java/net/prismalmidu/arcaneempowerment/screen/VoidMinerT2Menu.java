package net.prismalmidu.arcaneempowerment.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.prismalmidu.arcaneempowerment.block.entity.VoidMinerT2BlockEntity;

public class VoidMinerT2Menu extends AbstractContainerMenu {
    public final VoidMinerT2BlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // 🌟 Explicit 32-bit integer slots that handle full uncompressed integer values perfectly
    private final DataSlot currentEnergySlot = DataSlot.standalone();
    private final DataSlot maxEnergySlot = DataSlot.standalone();

    // Client-side initialization constructor called by Forge
    public VoidMinerT2Menu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(5));
    }

    // Actual server-side constructor
    public VoidMinerT2Menu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.VOID_MINER_T2_MENU.get(), containerId);
        checkContainerDataCount(data, 5);
        this.blockEntity = (VoidMinerT2BlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Add the block entity's internal 9 slots layout
        if (this.blockEntity != null) {
            this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                int startX = 8;
                int startY = 18;
                for (int slot = 0; slot < 9; slot++) {
                    this.addSlot(new SlotItemHandler(handler, slot, startX + (slot * 18), startY));
                }
            });
        }

        // Track the 2 progress variables
        this.addDataSlots(data);

        // 🌟 Track the 2 energy variables safely as full 32-bit data streams
        this.addDataSlot(currentEnergySlot);
        this.addDataSlot(maxEnergySlot);
    }

    // Server updates the data slots dynamically on tick updates
    @Override
    public void broadcastChanges() {
        // 1. ALWAYS update the data slot values FIRST
        if (this.blockEntity != null) {
            this.blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> {
                this.currentEnergySlot.set(energyStorage.getEnergyStored());
                this.maxEnergySlot.set(energyStorage.getMaxEnergyStored());
            });
        }

        // 2. CALL SUPER LAST so Minecraft detects the changes we just made and sends the packet!
        super.broadcastChanges();
    }

    // Clean getters pulling data straight from our uncompressed tracked data slots
    public int getEnergy() {
        return this.currentEnergySlot.get();
    }

    public int getMaxEnergy() {
        return this.maxEnergySlot.get();
    }

    public int getSpeedMods() { return this.data.get(2); }
    public int getEfficiencyMods() { return this.data.get(3); }
    public int getProductionMods() { return this.data.get(4); }

    // Required helper indicators for progress bars
    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 26; // Width of your arrow texture asset
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, blockEntity.getBlockState().getBlock());
    }

    // Safe implementation of Shift-Click quick inventory transfers
    private static final int VANILLA_SLOT_COUNT = 36;
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        if (index < VANILLA_SLOT_COUNT) {
            // Transfer from Player Inventory to Block Entity Slots
            if (!moveItemStackTo(sourceStack, VANILLA_SLOT_COUNT, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < slots.size()) {
            // Transfer from Block Entity Slots back to Player Inventory
            if (!moveItemStackTo(sourceStack, 0, VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        if (sourceStack.getCount() == copyStack.getCount()) return ItemStack.EMPTY;
        sourceSlot.onTake(playerIn, sourceStack);
        return copyStack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
