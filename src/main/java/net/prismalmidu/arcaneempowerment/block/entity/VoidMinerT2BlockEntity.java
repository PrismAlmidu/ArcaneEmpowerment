package net.prismalmidu.arcaneempowerment.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.screen.VoidMinerT2Menu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VoidMinerT2BlockEntity extends BlockEntity implements MenuProvider {

    // 1. Structure Logic & Modifier Counts
    private boolean isComplete = false;
    private int structureCheckTick = 0; // Performance optimization counter

    private int speedModifiers = 0;
    private int efficiencyModifiers = 0;
    private int productionModifiers = 0;

    // 2. Base Configuration Constants (Default values when no modifiers are present)
    private static final int BASE_MAX_PROGRESS = 100;    // 5 seconds
    private static final int BASE_ENERGY_COST = 20;     // 20 FE/tick

    // 3. Dynamic Variables modified by the framework blocks
    private int maxProgress = BASE_MAX_PROGRESS;
    private int energyCostPerTick = BASE_ENERGY_COST;
    private double doubleProductionChance = 0.0;        // 0% to 100% (0.0 to 1.0)

    // 2. The block checker helper method
    private boolean checkBlock(Level world, BlockPos targetPos, Block expectedBlock) {
        return world.getBlockState(targetPos).is(expectedBlock);
    }
    private boolean checkBlockOrModifier(Level world, BlockPos targetPos, Block defaultBlock) {
        BlockState state = world.getBlockState(targetPos);
        Block actualBlock = state.getBlock();

        // If it matches the standard default structural block, it's valid!
        if (state.is(defaultBlock)) {
            return true;
        }

        // If it's a Speed Modifier
        if (actualBlock == ModBlocks.MODIFIER_MINERSPEED_BLOCK.get()) {
            this.speedModifiers++;
            return true;
        }

        // If it's an Efficiency Modifier
        if (actualBlock == ModBlocks.MODIFIER_MINEREFFICIENCY_BLOCK.get()) {
            this.efficiencyModifiers++;
            return true;
        }

        // If it's a Production Modifier
        if (actualBlock == ModBlocks.MODIFIER_MINERPRODUCTION_BLOCK.get()) {
            this.productionModifiers++;
            return true;
        }

        // Not a structural block or a valid modifier
        return false;
    }

    // 3. Your multiblock validation method
    public void validateStructure(Level world, BlockPos controllerPos, BlockState currentState) {
        // Reset counters before every fresh scan
        this.speedModifiers = 0;
        this.efficiencyModifiers = 0;
        this.productionModifiers = 0;

        boolean isValid =
                // Row 1 (z = -5)
                checkBlock(world, controllerPos.offset(-2, -6, -5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-1, -6, -5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(0, -6, -5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -6, -5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(2, -6, -5), Blocks.STONE_BRICKS) &&

                // Row 2 (z = -4)
                checkBlock(world, controllerPos.offset(-4, -6, -4), Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(-3, -6, -4), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-2, -6, -4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -6, -4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -6, -4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -6, -4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -6, -4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -6, -4), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(4, -6, -4), Blocks.POLISHED_ANDESITE) &&

                // Row 3 (z = -3)
                checkBlock(world, controllerPos.offset(-4, -6, -3), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-3, -6, -3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -6, -3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -6, -3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -6, -3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -6, -3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -6, -3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -6, -3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -6, -3), Blocks.STONE_BRICKS) &&

                // Row 4 (z = -2)
                checkBlock(world, controllerPos.offset(-5, -6, -2), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-4, -6, -2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -6, -2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -6, -2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -6, -2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -6, -2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -6, -2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -6, -2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -6, -2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -6, -2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -6, -2), Blocks.STONE_BRICKS) &&

                // Row 5 (z = -1)
                checkBlock(world, controllerPos.offset(-5, -6, -1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-4, -6, -1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -6, -1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -6, -1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -6, -1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -6, -1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -6, -1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -6, -1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -6, -1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -6, -1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -6, -1), Blocks.STONE_BRICKS) &&

                // Row 6 (z = 0)
                checkBlock(world, controllerPos.offset(-5, -6, 0), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-4, -6, 0), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -6, 0), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -6, 0), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -6, 0), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -6, 0), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -6, 0), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -6, 0), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -6, 0), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -6, 0), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -6, 0), Blocks.STONE_BRICKS) &&

                // Row 7 (z = 1)
                checkBlock(world, controllerPos.offset(-5, -6, 1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-4, -6, 1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -6, 1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -6, 1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -6, 1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -6, 1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -6, 1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -6, 1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -6, 1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -6, 1), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -6, 1), Blocks.STONE_BRICKS) &&

                // Row 8 (z = 2)
                checkBlock(world, controllerPos.offset(-5, -6, 2), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-4, -6, 2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -6, 2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -6, 2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -6, 2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -6, 2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -6, 2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -6, 2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -6, 2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -6, 2), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -6, 2), Blocks.STONE_BRICKS) &&

                // Row 9 (z = 3)
                checkBlock(world, controllerPos.offset(-4, -6, 3), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-3, -6, 3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -6, 3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -6, 3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -6, 3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -6, 3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -6, 3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -6, 3), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -6, 3), Blocks.STONE_BRICKS) &&

                // Row 10 (z = 4)
                checkBlock(world, controllerPos.offset(-4, -6, 4), Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(-3, -6, 4), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-2, -6, 4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -6, 4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -6, 4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -6, 4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -6, 4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -6, 4), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(4, -6, 4), Blocks.POLISHED_ANDESITE) &&

                // Row 11 (z = 5)
                checkBlock(world, controllerPos.offset(-2, -6, 5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-1, -6, 5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(0, -6, 5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -6, 5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(2, -6, 5), Blocks.STONE_BRICKS) &&

                // Layer 2 (y = -5)
                checkBlock(world, controllerPos.offset(-2, -5, -5), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -5, -5), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -5, -2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -5, -2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -5, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-5, -5, 2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -5, 2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -5, 5), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -5, 5), Blocks.CHISELED_POLISHED_BLACKSTONE) &&

                // Layer 3 (y = -4)
                checkBlockOrModifier(world, controllerPos.offset(-2, -4, -5), ModBlocks.MODIFIER_TEMPLATE_BLOCK.get()) &&
                checkBlockOrModifier(world, controllerPos.offset(2, -4, -5), ModBlocks.MODIFIER_TEMPLATE_BLOCK.get()) &&
                checkBlockOrModifier(world, controllerPos.offset(-5, -4, -2), ModBlocks.MODIFIER_TEMPLATE_BLOCK.get()) &&
                checkBlockOrModifier(world, controllerPos.offset(5, -4, -2), ModBlocks.MODIFIER_TEMPLATE_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -4, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlockOrModifier(world, controllerPos.offset(-5, -4, 2), ModBlocks.MODIFIER_TEMPLATE_BLOCK.get()) &&
                checkBlockOrModifier(world, controllerPos.offset(5, -4, 2), ModBlocks.MODIFIER_TEMPLATE_BLOCK.get()) &&
                checkBlockOrModifier(world, controllerPos.offset(-2, -4, 5), ModBlocks.MODIFIER_TEMPLATE_BLOCK.get()) &&
                checkBlockOrModifier(world, controllerPos.offset(2, -4, 5), ModBlocks.MODIFIER_TEMPLATE_BLOCK.get()) &&

                // Layer 4 (y = -3)
                checkBlock(world, controllerPos.offset(-2, -3, -5), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -3, -5), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -3, -2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -3, -2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -3, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-5, -3, 2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -3, 2), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -3, 5), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -3, 5), Blocks.CHISELED_POLISHED_BLACKSTONE) &&

                // Layer 5 (y = -2)
                checkBlock(world, controllerPos.offset(-1, -2, -5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(0, -2, -5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -2, -5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-5, -2, -1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(5, -2, -1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-5, -2, 0), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(0, -2, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -2, 0), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-5, -2, 1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(5, -2, 1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-1, -2, 5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(0, -2, 5), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -2, 5), Blocks.STONE_BRICKS) &&

                // Layer 6 (y = -1)
                checkBlock(world, controllerPos.offset(0, -1, -5), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-5, -1, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -1, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -1, 5), ModBlocks.ARCANIUM_BLOCK.get()) &&

                // Layer 7 (y = 0)
                checkBlock(world, controllerPos.offset(0, 0, -4), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, 0, -3), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-4, 0, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, 0, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, 0, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, 0, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, 0, 3), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, 0, 4), ModBlocks.ARCANIUM_BLOCK.get()) &&

                // Layer 8 (y = +1)
                checkBlock(world, controllerPos.offset(0, 1, -2), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, 1, -1), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, 1, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, 1, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, 1, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, 1, 0), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, 1, 1), ModBlocks.ARCANIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, 1, 2), ModBlocks.ARCANIUM_BLOCK.get());

        this.isComplete = isValid;

        if (this.isComplete) {
            // 📈 SPEED MODIFIER MATH: Each one reduces time taken by 10 ticks (0.5s). Cannot go below 20 ticks (1s).
            this.maxProgress = Math.max(20, BASE_MAX_PROGRESS - (this.speedModifiers * 10));

            // 🎁 PRODUCTION MODIFIER MATH: Each one adds a 15% chance to clone the output block.
            this.doubleProductionChance = Math.min(1.0, this.productionModifiers * 0.15);

            // ⚡ BALANCED ENERGY MATH:
            // - Start with the BASE_ENERGY_COST (20)
            // - Add 8 FE/tick per Speed Modifier
            // - Add 12 FE/tick per Production Modifier
            // - Subtract 3 FE/tick per Efficiency Modifier
            int penaltyCost = (this.speedModifiers * 8) + (this.productionModifiers * 12);
            int savingsCost = (this.efficiencyModifiers * 3);

            // Enforce a strict safe minimum floor cost of 5 FE so it never runs completely for free or negative power
            this.energyCostPerTick = Math.max(5, BASE_ENERGY_COST + penaltyCost - savingsCost);

        } else {
            // Reset to defaults if structure is broken
            this.maxProgress = BASE_MAX_PROGRESS;
            this.energyCostPerTick = BASE_ENERGY_COST;
            this.doubleProductionChance = 0.0;
        }

        setChanged();
    }

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> VoidMinerT2BlockEntity.this.progress;
                case 1 -> VoidMinerT2BlockEntity.this.maxProgress;
                case 2 -> VoidMinerT2BlockEntity.this.speedModifiers;       // NEW
                case 3 -> VoidMinerT2BlockEntity.this.efficiencyModifiers;  // NEW
                case 4 -> VoidMinerT2BlockEntity.this.productionModifiers;  // NEW
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> VoidMinerT2BlockEntity.this.progress = value;
                case 1 -> VoidMinerT2BlockEntity.this.maxProgress = value;
                // Client catches and stores the counts locally for the Screen class
                case 2 -> VoidMinerT2BlockEntity.this.speedModifiers = value;
                case 3 -> VoidMinerT2BlockEntity.this.efficiencyModifiers = value;
                case 4 -> VoidMinerT2BlockEntity.this.productionModifiers = value;
            }
        }

        @Override
        public int getCount() {
            return 5; // Updated size from 2 to 5
        }
    };

    // 1. Define Capabilities
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(this, 50000, 1000);

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    // 2. Logic fields
    private int progress = 0;

    private static final ResourceLocation VOID_MINER_T2_LOOT =
            new ResourceLocation("arcaneempowerment", "gameplay/void_miner_t2");

    public VoidMinerT2BlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOID_MINER_T2_BE.get(), pos, state);
    }

    // 3. Tick Execution (Server-Side only)
    public static void tick(Level level, BlockPos pos, BlockState state, VoidMinerT2BlockEntity be) {
        if (level.isClientSide) return;

        // Force structure check for testing
        be.structureCheckTick++;
        if (be.structureCheckTick >= 20) {
            be.validateStructure(level, pos, state);
            be.structureCheckTick = 0;
        }

        // Bypassing abstract `.extractEnergy()` checks:
        // Read the absolute variable field directly from the underlying EnergyStorage object!
        int currentEnergy = be.energyStorage.getEnergyStored();

        if (be.isComplete && currentEnergy >= be.energyCostPerTick) {

            // 🌟 FORCE DIRECT IN-PLACE EXTRACTION:
            // We bypass Forge's restriction layers and manually rewrite the inner data field
            int nextEnergy = Math.max(0, currentEnergy - be.energyCostPerTick);
            be.energyStorage.setEnergy(nextEnergy);

            be.progress++;
            be.setChanged();

            // Push an immediate update state to your Open Menu Container fields
            level.sendBlockUpdated(pos, state, state, 3);

            if (be.progress >= be.maxProgress) {
                be.produceBlock(level);
                be.progress = 0;
            }
        } else {
            if (be.progress > 0) {
                be.progress = Math.max(0, be.progress - 2);
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private void produceBlock(Level level) {
        // 1. Safe check and conversion to ServerLevel (required for server-side loot generation)
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return;

        // 2. Fetch the updated loot table data accessor registry
        LootTable table = serverLevel.getServer().getLootData().getLootTable(VOID_MINER_T2_LOOT);

        // 3. Construct 1.20.1 LootParams context mapping
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition))
                .create(LootContextParamSets.EMPTY);

        // 4. Generate random items from your JSON configuration
        List<ItemStack> generatedDrops = table.getRandomItems(params);

        RandomSource random = level.getRandom();
        boolean triggerDouble = random.nextDouble() < this.doubleProductionChance;

        // 5. Route item stacks to inventory slots
        for (ItemStack drop : generatedDrops) {
            ItemStack producedStack = drop.copy();

            if (triggerDouble) {
                producedStack.setCount(Math.min(producedStack.getMaxStackSize(), producedStack.getCount() * 2));
            }

            ItemStack remaining = ItemHandlerHelper.insertItemStacked(this.itemHandler, producedStack, false);

            if (!remaining.isEmpty()) {
                BlockPos targetPos = this.worldPosition.below();
                BlockEntity targetBE = level.getBlockEntity(targetPos);
                if (targetBE != null) {
                    targetBE.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(neighborHandler -> {
                        ItemHandlerHelper.insertItemStacked(neighborHandler, remaining, false);
                    });
                }
            }
        }
    }

    // 4. Capabilities lifecycle management
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyItemHandler = LazyOptional.of(() -> itemHandler);
        this.lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    // 5. Saving and Loading NBT Data
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        // Load original systems
        this.itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
        this.energyStorage.deserializeNBT(nbt.get("Energy"));
        this.progress = nbt.getInt("Progress");
        this.isComplete = nbt.getBoolean("IsComplete");

        // 🌟 LOAD NEW MODIFIER VARIABLES
        this.speedModifiers = nbt.getInt("SpeedModifiers");
        this.efficiencyModifiers = nbt.getInt("EfficiencyModifiers");
        this.productionModifiers = nbt.getInt("ProductionModifiers");

        this.maxProgress = nbt.getInt("MaxProgress");
        this.energyCostPerTick = nbt.getInt("EnergyCostPerTick");
        this.doubleProductionChance = nbt.getDouble("DoubleProductionChance");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        // Save original systems
        nbt.put("Inventory", this.itemHandler.serializeNBT());
        nbt.put("Energy", this.energyStorage.serializeNBT());
        nbt.putInt("Progress", this.progress);
        nbt.putBoolean("IsComplete", this.isComplete);

        // 🌟 SAVE NEW MODIFIER VARIABLES
        // We use exact unique string keys to retrieve these cleanly on load
        nbt.putInt("SpeedModifiers", this.speedModifiers);
        nbt.putInt("EfficiencyModifiers", this.efficiencyModifiers);
        nbt.putInt("ProductionModifiers", this.productionModifiers);

        nbt.putInt("MaxProgress", this.maxProgress);
        nbt.putInt("EnergyCostPerTick", this.energyCostPerTick);
        nbt.putDouble("DoubleProductionChance", this.doubleProductionChance);
    }

    // Helper Inner-class for handling Energy with custom callbacks
    private static class CustomEnergyStorage extends EnergyStorage {
        private final VoidMinerT2BlockEntity blockEntity;

        public CustomEnergyStorage(VoidMinerT2BlockEntity blockEntity, int capacity, int maxReceive) {
            // FIX: Set maxExtract to capacity so internal code can consume energy!
            super(capacity, maxReceive, capacity);
            this.blockEntity = blockEntity;
        }

        // 🌟 SAFETY LOCK: Returns false to prevent external cables/pipes from pulling power out
        @Override
        public boolean canExtract() {
            return false;
        }

        // 🌟 ADD THIS: Allows direct variable modification safely
        public void setEnergy(int energy) {
            this.energy = energy;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int rc = super.receiveEnergy(maxReceive, simulate);
            if (rc > 0 && !simulate) blockEntity.setChanged();
            return rc;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int ext = super.extractEnergy(maxExtract, simulate);
            if (ext > 0 && !simulate) blockEntity.setChanged();
            return ext;
        }
    } // FIX 2: Removed duplicate dangling overrides that were breaking the class block structure here!

    @Override
    public Component getDisplayName() {
        return Component.translatable("Void Miner T2");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new VoidMinerT2Menu(id, inventory, this, this.data);
    }
    // 🌟 FORCE NETWORKING SYNC 1: Sends server data to client when block updates
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag); // Pack your inventory, energy, and progress into the packet
        return tag;
    }

    // 🌟 FORCE NETWORKING SYNC 2: Prepares the packet container for shipping
    @Nullable
    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    // 🌟 FORCE NETWORKING SYNC 3: Unpacks server data on the client machine
    @Override
    public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (pkt.getTag() != null) {
            load(pkt.getTag()); // Extract the synced energy and progress
        }
    }

}
