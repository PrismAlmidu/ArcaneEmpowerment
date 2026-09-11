package net.prismalmidu.arcaneempowerment.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.prismalmidu.arcaneempowerment.fluid.ModFluids;
import net.prismalmidu.arcaneempowerment.screen.CollectorCrystalMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CollectorCrystalBlockEntity extends BlockEntity implements MenuProvider {

    private final FluidTank fluidTank = new FluidTank(64000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.SOURCE_LIQUID_MANA.get();
        }
    };

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            int fluidAmount = fluidTank.getFluidAmount();
            switch (index) {
                case 0 -> {
                    return fluidAmount & 0xFFFF;
                }        // Lower 16 bits
                case 1 -> {
                    return (fluidAmount >> 16) & 0xFFFF;
                } // Upper 16 bits
                case 2 -> {
                    return fluidTank.getCapacity() & 0xFFFF;
                }
                case 3 -> {
                    return (fluidTank.getCapacity() >> 16) & 0xFFFF;
                }
                default -> {
                    return 0;
                }
            }
        }

        @Override
        public void set(int index, int value) {
            // Screen handles values read-only from here, logic can remain empty or basic
        }

        @Override
        public int getCount() {
            return 4; // Total tracked elements
        }
    };

        @Override
        public Component getDisplayName() {
            return Component.translatable("block.arcaneempowerment.collector_crystal");
        }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new CollectorCrystalMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidTank);

    // Tracks whether the 3x3 multiblock platform underneath is valid
    private boolean isComplete = false;
    // Timer to prevent checking the world blocks every single tick (reduces lag)
    private int checkTicker = 0;

    public CollectorCrystalBlockEntity(BlockPos pPos, BlockState pState) {
        super (ModBlockEntities.COLLECTOR_CRYSTAL_BE.get(), pPos, pState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CollectorCrystalBlockEntity be) {
        if (level.isClientSide()) {
            return;
        }

        // 1. Validate structure every 20 ticks
        be.checkTicker++;
        if (be.checkTicker >= 20) {
            be.checkTicker = 0;
            be.validateStructure(level, pos, state);
        }

        // 2. Multiblock checks
        if (be.isComplete) {
            // Produce 10 mB of custom fluid every tick
            be.fluidTank.fill(new FluidStack(ModFluids.SOURCE_LIQUID_MANA.get(), 10), IFluidHandler.FluidAction.EXECUTE);

            // Automatically push fluid out to adjacent blocks (pipes/tanks)
            if (!be.fluidTank.isEmpty()) {
                be.pushFluidToNeighbors(level, pos);
            }

            // NEW: If the tank has 1,000 mB or more, attempt to spill it into a nearby air space
            if (be.fluidTank.getFluidAmount() >= 1000) {
                be.trySpillFluidToAir(level, pos);
            }
        }
    }

    // Your validation method integration
    public void validateStructure(Level world, BlockPos controllerPos, BlockState currentState) {
        // Explicitly check all 9 relative positions 1 block below (y = -1)
        boolean isValid =
                // Layer 1 (y = -4)
                checkBlock(world, controllerPos.offset(-1, -4, -1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -4, -1),  Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(1, -4, -1),  Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(-1, -4, 0), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -4, 0),  Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(1, -4, 0),  Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(-1, -4, 1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -4, 1),  Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(1, -4, 1),  Blocks.DIORITE) &&

                // Layer 2 (y = -3)
                checkBlock(world, controllerPos.offset(-2, -3, -2),  Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(-1, -3, -2),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -3, -2),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -3, -2),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -3, -2),  Blocks.POLISHED_ANDESITE) &&

                checkBlock(world, controllerPos.offset(-2, -3, -1),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -3, -1),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&

                checkBlock(world, controllerPos.offset(-2, -3, 0),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -3, 0),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -3, 0),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&

                checkBlock(world, controllerPos.offset(-2, -3, 1),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -3, 1),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&

                checkBlock(world, controllerPos.offset(-2, -3, 2),  Blocks.POLISHED_ANDESITE) &&
                checkBlock(world, controllerPos.offset(-1, -3, 2),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -3, 2),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -3, 2),  Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -3, 2),  Blocks.POLISHED_ANDESITE) &&

                // Layer 3 (y = -2)
                checkBlock(world, controllerPos.offset(-2, -2, -2),   Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -2, -2),   Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -2, 0),   Blocks.DIORITE_WALL) &&
                checkBlock(world, controllerPos.offset(-2, -2, 2),   Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -2, 2),   Blocks.CHISELED_POLISHED_BLACKSTONE) &&

                checkBlock(world, controllerPos.offset(0, -1, 0),   Blocks.POLISHED_ANDESITE);

        // If the status changed, mark it to save data
        if (this.isComplete != isValid) {
            this.isComplete = isValid;
            setChanged();
        }
    }

    // Helper method to check if a block at a specific position matches the target block type
    private boolean checkBlock(Level world, BlockPos pos, Block targetBlock) {
        return world.getBlockState(pos).is(targetBlock);
    }

    // Define offsets relative to the crystal where it is allowed to place fluid blocks
    private static final BlockPos[] FLUID_PLACEMENT_OFFSETS = {
            new BlockPos(-1, -3, -1),
            new BlockPos(0, -3, -1),
            new BlockPos(1, -3, -1),
            new BlockPos(-1, -3, 0),
            new BlockPos(1, -3, 0),
            new BlockPos(-1, -3, 1),
            new BlockPos(0, -3, 1),
            new BlockPos(1, -3, 1)
    };

    private void trySpillFluidToAir(Level level, BlockPos pos) {
        // 1. Create a list to store all currently available positions
        List<BlockPos> availablePositions = new ArrayList<>();

        // 2. Scan the configured offsets and gather all that contain air or existing mana
        for (BlockPos offset : FLUID_PLACEMENT_OFFSETS) {
            BlockPos targetPos = pos.offset(offset);
            BlockState state = level.getBlockState(targetPos);
            FluidState fluidState = level.getFluidState(targetPos);

            // Match air, or check if it's our flowing mana (allowing us to "fill it up")
            if (state.isAir() || fluidState.is(ModFluids.FLOWING_LIQUID_MANA.get())) {
                // OPTIONAL: Skip if it's already a full SOURCE block to avoid wasting tank fluid
                if (fluidState.is(ModFluids.SOURCE_LIQUID_MANA.get()) && fluidState.isSource()) {
                    continue;
                }
                availablePositions.add(targetPos);
            }
        }

        // 3. If there is at least one space available, choose one randomly
        if (!availablePositions.isEmpty()) {
            int randomIndex = level.random.nextInt(availablePositions.size());
            BlockPos chosenPos = availablePositions.get(randomIndex);

            // Get the physical block state of your custom SOURCE fluid
            BlockState fluidBlockState = ModFluids.SOURCE_LIQUID_MANA.get().defaultFluidState().createLegacyBlock();

            if (!fluidBlockState.isAir()) {
                // 4. Drain the internal tank and place the block
                this.fluidTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                level.setBlockAndUpdate(chosenPos, fluidBlockState);
            }
        }
    }

    private void pushFluidToNeighbors(Level level, BlockPos pos) {
        // Cycle through all 6 sides (North, South, East, West, Up, Down)
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);

            if (neighborBE != null) {
                // Look for a fluid handler capability on the adjacent block face
                neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(neighborHandler -> {

                    // Determine maximum fluid we want to push this tick (e.g., up to 1000 mB)
                    int maxSimulatedDrain = 1000;

                    // Simulate drawing fluid from our tank
                    FluidStack simulatedDrain = this.fluidTank.drain(maxSimulatedDrain, IFluidHandler.FluidAction.SIMULATE);

                    if (!simulatedDrain.isEmpty()) {
                        // Simulate filling the neighboring block
                        int acceptedAmount = neighborHandler.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);

                        if (acceptedAmount > 0) {
                            // Execute the actual transfer
                            FluidStack realDrain = this.fluidTank.drain(acceptedAmount, IFluidHandler.FluidAction.EXECUTE);
                            neighborHandler.fill(realDrain, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                });
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
            return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("fluid", this.fluidTank.writeToNBT(new CompoundTag()));
        pTag.putBoolean("isComplete", this.isComplete);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.fluidTank.readFromNBT(pTag.getCompound("fluid"));
        this.isComplete = pTag.getBoolean("isComplete");
    }
}
