package net.prismalmidu.arcaneempowerment.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.block.custom.AccumulatorCoreBlock;
import net.prismalmidu.arcaneempowerment.item.ModItems;
import net.prismalmidu.arcaneempowerment.recipe.CraftingAltarRecipe;
import net.prismalmidu.arcaneempowerment.screen.CraftingAltarT1Menu;
import net.prismalmidu.arcaneempowerment.util.ModEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CraftingAltarT1BlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0, 1, 2, 3, 4, 5, 6, 7, 8 -> true;
                case 9 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int INPUT_SLOT_4 = 3;
    private static final int INPUT_SLOT_5 = 4;
    private static final int INPUT_SLOT_6 = 5;
    private static final int INPUT_SLOT_7 = 6;
    private static final int INPUT_SLOT_8 = 7;
    private static final int INPUT_SLOT_9 = 8;
    private static final int OUTPUT_SLOT = 9;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();

    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(512, 256) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                if (getLevel() != null) {
                    getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }
        };
    }

    // NEW FIELDS FOR ACCUMULATOR CORE DETECTION
    private int scanTimer = 0;        // Keeps track of ticks to optimize scanning performance
    private int activeCoresCount = 0; // Caches the total number of valid accumulator cores found (capped at 8)
    private float energyCounter = 0.0f;

    public CraftingAltarT1BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CRAFTING_ALTAR_T1_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CraftingAltarT1BlockEntity.this.progress;
                    case 1 -> CraftingAltarT1BlockEntity.this.maxProgress;
                    // Split current energy into lower 16 bits
                    case 2 -> CraftingAltarT1BlockEntity.this.ENERGY_STORAGE.getEnergyStored() & 0xFFFF;
                    // Split current energy into upper 16 bits
                    case 3 -> (CraftingAltarT1BlockEntity.this.ENERGY_STORAGE.getEnergyStored() >> 16) & 0xFFFF;
                    case 4 -> CraftingAltarT1BlockEntity.this.activeCoresCount;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CraftingAltarT1BlockEntity.this.progress = pValue;
                    case 1 -> CraftingAltarT1BlockEntity.this.maxProgress = pValue;
                    // Client-side synchronization buffer updates (Handled via network syncing packets)
                    case 2, 3 -> { }
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    public IEnergyStorage getEnergyStorage() {
        return this.ENERGY_STORAGE;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        if (this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, inventory);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcaneempowerment.crafting_altar_t1");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CraftingAltarT1Menu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("energy", ENERGY_STORAGE.getEnergyStored());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        ENERGY_STORAGE.setEnergy(pTag.getInt("energy"));
    }

    /**
     * Ticking logic for the Altar. Checks for structures periodically and increments energy.
     */
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!pLevel.isClientSide()) {
            // OPTIMIZATION: Only search the massive 16-block radius (35,937 blocks) once per second
            this.scanTimer++;
            if (this.scanTimer >= 20) {
                this.activeCoresCount = countAccumulatorCores(pLevel, pPos, 16);
                this.scanTimer = 0;
            }
        }

        // Handle energy generation per tick using the cached cores modifier
        fillUpOnEnergy();

        if (isOutputSlotEmptyOrReceivable() && hasRecipe()) {
            craftItem();
        }
    }

    /**
     * Scans a cubic radius centered around the altar for Accumulator Cores with a blockstate value of 2.
     * Stops checking immediately once the 8 core target is reached.
     */
    private int countAccumulatorCores(Level world, BlockPos centerPos, int radius) {
        int count = 0;
        Iterable<BlockPos> scanArea = BlockPos.betweenClosed(
                centerPos.offset(-radius, -radius, -radius),
                centerPos.offset(radius, radius, radius)
        );

        for (BlockPos targetPos : scanArea) {
            BlockState state = world.getBlockState(targetPos);

            if (state.is(ModBlocks.ACCUMULATOR_CORE.get())) {
                if (state.hasProperty(AccumulatorCoreBlock.STATE) && state.getValue(AccumulatorCoreBlock.STATE) == 2) {
                    count++;
                    if (count >= 8) {
                        return 8; // Performance win: break out early if we found all 8 cores
                    }
                }
            }
        }
        return count;
    }

    /**
     * Generates internal FE capacity using ambient or multiblock amplified generation values.
     */
    private void fillUpOnEnergy() {
        if (ENERGY_STORAGE.getEnergyStored() < ENERGY_STORAGE.getMaxEnergyStored()) {
            // Base generation is 0.4f, amplified by +0.1f per active core found
            float currentGenerationRate = 0.4f + (0.1f * this.activeCoresCount);
            energyCounter += currentGenerationRate;

            if (energyCounter >= 1.0f) {
                int energyToAdd = (int) energyCounter;
                ENERGY_STORAGE.receiveEnergy(energyToAdd, false);
                energyCounter -= energyToAdd;
            }
        } else {
            energyCounter = 0.0f;
        }
    }

    private void craftItem() {
        Optional<CraftingAltarRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack resultItem = recipe.get().getResultItem(getLevel().registryAccess());
        this.ENERGY_STORAGE.extractEnergy(recipe.get().getEnergyRequirement(), false);

        this.itemHandler.extractItem(INPUT_SLOT_1, 1, false);
        this.itemHandler.extractItem(INPUT_SLOT_2, 1, false);
        this.itemHandler.extractItem(INPUT_SLOT_3, 1, false);
        this.itemHandler.extractItem(INPUT_SLOT_4, 1, false);
        this.itemHandler.extractItem(INPUT_SLOT_5, 1, false);
        this.itemHandler.extractItem(INPUT_SLOT_6, 1, false);
        this.itemHandler.extractItem(INPUT_SLOT_7, 1, false);
        this.itemHandler.extractItem(INPUT_SLOT_8, 1, false);
        this.itemHandler.extractItem(INPUT_SLOT_9, 1, false);

        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(resultItem.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + resultItem.getCount()));

        setChanged();
    }

    private boolean hasRecipe() {
        Optional<CraftingAltarRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack resultItem = recipe.get().getResultItem(getLevel().registryAccess());

        return canInsertAmountIntoOutputSlot(resultItem.getCount())
                && canInsertItemIntoOutputSlot(resultItem.getItem()) && hasEnoughEnergyToCraft();
    }

    private boolean hasEnoughEnergyToCraft() {
        Optional<CraftingAltarRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }

        // Dynamic cost check against the active JSON recipe requirements
        return this.ENERGY_STORAGE.getEnergyStored() >= recipe.get().getEnergyRequirement();
    }

    private Optional<CraftingAltarRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for(int i = 0; i < this.itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(CraftingAltarRecipe.Type.INSTANCE, inventory, level);
    }

    private boolean canInsertItemIntoOutputSlot(@NotNull Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize() >=
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count;

    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }


    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }
}
