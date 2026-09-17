package net.prismalmidu.arcaneempowerment.block.entity;

import com.mna.api.affinity.Affinity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.prismalmidu.arcaneempowerment.screen.EldrinGeneratorMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EldrinGeneratorBlockEntity extends BlockEntity implements MenuProvider {

    // 1. Internal Storages
    private final EnergyStorage energyStorage = new EnergyStorage(50000, 1000, 1000);
    private final FluidTank fluidTank = new FluidTank(4000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    // 2. Capabilities
    private LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return energyStorage.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0; // 🛑 Block external extraction completely
        }

        @Override
        public int getEnergyStored() {
            return energyStorage.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return energyStorage.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return false; // 🛑 Tell cables this block cannot be drained
        }

        @Override
        public boolean canReceive() {
            return energyStorage.canReceive();
        }
    });


    private LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> new IFluidHandler() {
        @Override
        public int getTanks() {
            return fluidTank.getTanks();
        }

        @Override
        public @NotNull net.minecraftforge.fluids.FluidStack getFluidInTank(int tank) {
            return fluidTank.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return fluidTank.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull net.minecraftforge.fluids.FluidStack stack) {
            return fluidTank.isFluidValid(tank, stack);
        }

        @Override
        public int fill(net.minecraftforge.fluids.FluidStack resource, FluidAction action) {
            return fluidTank.fill(resource, action); // Allow insertion
        }

        @Override
        public @NotNull net.minecraftforge.fluids.FluidStack drain(net.minecraftforge.fluids.FluidStack resource, FluidAction action) {
            return net.minecraftforge.fluids.FluidStack.EMPTY; // 🛑 Block targeted drain
        }

        @Override
        public @NotNull net.minecraftforge.fluids.FluidStack drain(int maxDrain, FluidAction action) {
            return net.minecraftforge.fluids.FluidStack.EMPTY; // 🛑 Block generic drain
        }
    });

    // 3. Modifiers, Tracking & Multiblock State
    private UUID ownerUUID = null;
    private boolean isComplete = false;
    private int structureCheckCooldown = 0; // Performance optimization

    private static final int FE_PER_TICK = 100;
    private static final int FLUID_PER_TICK = 10;
    private static final float ELDRIN_POWER_PER_AFFINITY = 0.2f;

    // Shared data tracking array for the UI (6 Affinities + Energy + Fluid amount)
    // 0: Energy, 1: Fluid, 2: Arcane Gen (x100), 3: Earth Gen (x100), etc.
    protected final ContainerData data;

    public EldrinGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELDRIN_GENERATOR_BE.get(), pos, state);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0: return energyStorage.getEnergyStored();
                    case 1: return fluidTank.getFluidAmount();
                    // We multiply floats by 100 because ContainerData only supports integers
                    case 2: return isComplete && energyStorage.getEnergyStored() >= FE_PER_TICK ? (int)(ELDRIN_POWER_PER_AFFINITY * 100) : 0; // Arcane
                    case 3: return isComplete && energyStorage.getEnergyStored() >= FE_PER_TICK ? (int)(ELDRIN_POWER_PER_AFFINITY * 100) : 0; // Earth
                    case 4: return isComplete && energyStorage.getEnergyStored() >= FE_PER_TICK ? (int)(ELDRIN_POWER_PER_AFFINITY * 100) : 0; // Ender
                    case 5: return isComplete && energyStorage.getEnergyStored() >= FE_PER_TICK ? (int)(ELDRIN_POWER_PER_AFFINITY * 100) : 0; // Fire
                    case 6: return isComplete && energyStorage.getEnergyStored() >= FE_PER_TICK ? (int)(ELDRIN_POWER_PER_AFFINITY * 100) : 0; // Water
                    case 7: return isComplete && energyStorage.getEnergyStored() >= FE_PER_TICK ? (int)(ELDRIN_POWER_PER_AFFINITY * 100) : 0; // Wind
                    default: return 0;
                }
            }

            @Override
            public void set(int index, int value) {
                // The client doesn't need to write data back to the server for this generator
            }

            @Override
            public int getCount() {
                return 8; // Total tracked integers
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Eldrin Generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new EldrinGeneratorMenu(id, inventory, this, this.data);
    }

    // Add simple public getters for your Menu to inspect internal states directly
    public EnergyStorage getEnergyStorage() { return this.energyStorage; }
    public FluidTank getFluidTank() { return this.fluidTank; }

    public void setOwner(Player player) {
        this.ownerUUID = player.getUUID();
        setChanged();
    }

    // --- Multiblock Structure Methods ---

    public void validateStructure(Level world, BlockPos controllerPos, BlockState currentState) {
        boolean isValid =
                checkBlock(world, controllerPos.offset(-1, -1, -4), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -1, -4), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(1, -1, -4), Blocks.POLISHED_DIORITE) &&

                checkBlock(world, controllerPos.offset(-2, -1, -3), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -3), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(0, -1, -3), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -1, -3), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(2, -1, -3), Blocks.POLISHED_DIORITE) &&

                checkBlock(world, controllerPos.offset(-3, -1, -2), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-2, -1, -2), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-1, -1, -2), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -1, -2), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -1, -2), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(2, -1, -2), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(3, -1, -2), Blocks.POLISHED_DIORITE) &&

                checkBlock(world, controllerPos.offset(-4, -1, -1), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-3, -1, -1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-2, -1, -1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(-1, -1, -1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -1, -1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -1, -1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(2, -1, -1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(3, -1, -1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(4, -1, -1), Blocks.POLISHED_DIORITE) &&

                checkBlock(world, controllerPos.offset(-4, -1, 0), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 0), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-2, -1, 0), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-1, -1, 0), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(0, -1, 0), Blocks.CHISELED_POLISHED_BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -1, 0), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(2, -1, 0), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(3, -1, 0), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(4, -1, 0), Blocks.POLISHED_DIORITE) &&

                checkBlock(world, controllerPos.offset(-4, -1, 1), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-3, -1, 1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-2, -1, 1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -1, 1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -1, 1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(2, -1, 1), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(3, -1, 1), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(4, -1, 1), Blocks.POLISHED_DIORITE) &&

                checkBlock(world, controllerPos.offset(-3, -1, 2), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-2, -1, 2), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(-1, -1, 2), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -1, 2), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -1, 2), Blocks.DIORITE) &&
                checkBlock(world, controllerPos.offset(2, -1, 2), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(3, -1, 2), Blocks.POLISHED_DIORITE) &&

                checkBlock(world, controllerPos.offset(-2, -1, 3), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(-1, -1, 3), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(0, -1, 3), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(1, -1, 3), Blocks.STONE_BRICKS) &&
                checkBlock(world, controllerPos.offset(2, -1, 3), Blocks.POLISHED_DIORITE) &&

                checkBlock(world, controllerPos.offset(-1, -1, 4), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(0, -1, 4), Blocks.POLISHED_DIORITE) &&
                checkBlock(world, controllerPos.offset(1, -1, 4), Blocks.POLISHED_DIORITE);

        if (this.isComplete != isValid) {
            this.isComplete = isValid;
            setChanged();

            // FORCE pipes to immediately update their connections when the structure breaks or forms
            updateCapabilityConnections();
        }
    }

    private boolean checkBlock(Level world, BlockPos pos, Block targetBlock) {
        return world.getBlockState(pos).is(targetBlock);
    }

    public boolean isStructureComplete() {
        return this.isComplete;
    }

    // --- COMPATIBILITY ENGINE SERVER TICKING LOGIC ---
    public static void tick(Level level, BlockPos pos, BlockState state, EldrinGeneratorBlockEntity be) {
        if (level.isClientSide) return;

        // Run validation check once every 20 ticks (1 second) to save CPU cycles
        be.structureCheckCooldown--;
        if (be.structureCheckCooldown <= 0) {
            be.validateStructure(level, pos, state);
            be.structureCheckCooldown = 20;
        }

        // Exit loop if structure layout is unverified (Diamond test bypass sets this to true)
        if (!be.isComplete) return;

        if (level.getServer() != null && be.ownerUUID != null) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(be.ownerUUID);

            if (player != null) {
                Affinity[] coreAffinities = new Affinity[]{
                        Affinity.ARCANE, Affinity.EARTH, Affinity.ENDER,
                        Affinity.FIRE, Affinity.WATER, Affinity.WIND
                };

                // RESOURCE DRAIN GUARD: Validates structural requirements are fully satisfied
                if (be.energyStorage.getEnergyStored() >= FE_PER_TICK && be.fluidTank.getFluidAmount() >= FLUID_PER_TICK) {

                    try {
                        // 1. Target the World level rather than individual entity capabilities
                        Level world = level;

                        // 2. Reflectively search for M&A's official WorldMagicProvider Capability attachment definition
                        Class<?> magicProviderClass = Class.forName("com.mna.capabilities.worlddata.WorldMagicProvider");
                        java.lang.reflect.Field magicCapabilityField = magicProviderClass.getField("MAGIC");
                        net.minecraftforge.common.capabilities.Capability<?> magicCap =
                                (net.minecraftforge.common.capabilities.Capability<?>) magicCapabilityField.get(null);

                        world.getCapability(magicCap).ifPresent(magicInstance -> {
                            try {
                                // 3. Grab the internal Wellspring Node Registry manager data sheet
                                java.lang.reflect.Method getRegistryMethod = magicInstance.getClass().getMethod("getWellspringRegistry");
                                Object wellspringRegistry = getRegistryMethod.invoke(magicInstance);

                                if (wellspringRegistry != null) {

                                    // 4. Force inject power for each elemental category directly into the master registry database
                                    // Target Signature: insertPower(UUID player, Level level, Affinity affinity, float amount)
                                    java.lang.reflect.Method insertPowerMethod = wellspringRegistry.getClass().getMethod(
                                            "insertPower", java.util.UUID.class, net.minecraft.world.level.Level.class, Affinity.class, float.class
                                    );

                                    // Deduct machine storage resources safely inside the confirmed capability thread
                                    be.energyStorage.extractEnergy(FE_PER_TICK, false);
                                    be.fluidTank.drain(FLUID_PER_TICK, IFluidHandler.FluidAction.EXECUTE);

                                    for (Affinity aff : coreAffinities) {
                                        // Execute power addition mapped right under your UUID
                                        insertPowerMethod.invoke(wellspringRegistry, be.ownerUUID, world, aff, ELDRIN_POWER_PER_AFFINITY);
                                    }

                                    // 5. Broadcast a general block refresh update packet across the tracking loops
                                    be.setChanged();
                                    world.sendBlockUpdated(pos, state, state, 3);
                                }
                            } catch (Exception innerReflectiveException) {
                                innerReflectiveException.printStackTrace();
                            }
                        });

                    } catch (Exception globalReflectiveException) {
                        // Hard fallback log if paths inside the main mod build mismatch
                        globalReflectiveException.printStackTrace();
                    }
                }
            }
        }
    }

    // 5. Save & Load Data
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.put("Fluid", fluidTank.writeToNBT(new CompoundTag()));
        tag.putBoolean("StructureComplete", isComplete); // Save structure state
        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.extractEnergy(energyStorage.getEnergyStored(), false);
        energyStorage.receiveEnergy(tag.getInt("Energy"), false);
        fluidTank.readFromNBT(tag.getCompound("Fluid"));
        this.isComplete = tag.getBoolean("StructureComplete"); // Load structure state
        if (tag.hasUUID("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
    }

    // 6. Capability Exposing
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // OPTIONAL: Keep this guard if you want to block external input until the structure is fully built
        if (!this.isComplete) {
            return super.getCapability(cap, side);
        }

        // Expose Forge Energy capability to incoming cables
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }

        // Expose Fluid Handler capability to incoming pipes
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
        fluidOptional.invalidate();
    }


    public void updateCapabilityConnections() {
        if (this.level != null) {
            this.energyOptional.invalidate();
            this.fluidOptional.invalidate();

            // Re-assign the input-only wrappers
            this.energyOptional = LazyOptional.of(() -> new IEnergyStorage() {
                @Override public int receiveEnergy(int maxReceive, boolean simulate) { return energyStorage.receiveEnergy(maxReceive, simulate); }
                @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
                @Override public int getEnergyStored() { return energyStorage.getEnergyStored(); }
                @Override public int getMaxEnergyStored() { return energyStorage.getMaxEnergyStored(); }
                @Override public boolean canExtract() { return false; }
                @Override public boolean canReceive() { return energyStorage.canReceive(); }
            });

            this.fluidOptional = LazyOptional.of(() -> new IFluidHandler() {
                @Override public int getTanks() { return fluidTank.getTanks(); }
                @Override public @NotNull net.minecraftforge.fluids.FluidStack getFluidInTank(int tank) { return fluidTank.getFluidInTank(tank); }
                @Override public int getTankCapacity(int tank) { return fluidTank.getTankCapacity(tank); }
                @Override public boolean isFluidValid(int tank, @NotNull net.minecraftforge.fluids.FluidStack stack) { return fluidTank.isFluidValid(tank, stack); }
                @Override public int fill(net.minecraftforge.fluids.FluidStack resource, FluidAction action) { return fluidTank.fill(resource, action); }
                @Override public @NotNull net.minecraftforge.fluids.FluidStack drain(net.minecraftforge.fluids.FluidStack resource, FluidAction action) { return net.minecraftforge.fluids.FluidStack.EMPTY; }
                @Override public @NotNull net.minecraftforge.fluids.FluidStack drain(int maxDrain, FluidAction action) { return net.minecraftforge.fluids.FluidStack.EMPTY; }
            });

            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        }
    }
}