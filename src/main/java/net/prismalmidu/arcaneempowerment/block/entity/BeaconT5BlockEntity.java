package net.prismalmidu.arcaneempowerment.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.screen.BeaconT5Menu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;

public class BeaconT5BlockEntity extends BlockEntity implements MenuProvider {

    // FIXED: Changed type from 'EnergyStorage' to 'CustomEnergy' so .setEnergy() can be recognized
    private final CustomEnergy energyStorage = new CustomEnergy(10000, 100, 100);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);
    private UUID ownerUuid;

    // 1. BASE AND MODIFIER ENERGY COSTS (Configurable values)
    private static final int BASE_ENERGY_COST = 5;
    private static final int SPEED_COST_PER_BLOCK = 3;
    private static final int STRENGTH_COST_PER_BLOCK = 4;
    private static final int RESISTANCE_COST_PER_BLOCK = 4;
    private static final int REGENERATION_COST_PER_BLOCK = 5;
    private static final int SATURATION_COST_PER_BLOCK = 5;
    private static final int HASTE_COST_PER_BLOCK = 2;
    private static final int HEALTH_BOOST_COST_PER_BLOCK = 4;
    private static final int WATER_BREATHING_COST_PER_BLOCK = 3;
    private static final int DOLPHINS_GRACE_COST_PER_BLOCK = 3;
    private static final int LUCK_COST_PER_BLOCK = 3;
    private static final int SLOW_FALLING_COST_PER_BLOCK = 2;
    private static final int NIGHT_VISION_COST_PER_BLOCK = 2;
    private static final int JUMP_BOOST_COST_PER_BLOCK = 2;
    private static final int INVISIBILITY_COST_PER_BLOCK = 2;
    private static final int FIRE_RESISTANCE_COST_PER_BLOCK = 2;


    // Structure tracking fields
    private boolean isComplete = false;

    private int speedLevel = 0;
    private int strengthLevel = 0;
    private int resistanceLevel = 0;
    private int regenerationLevel = 0;
    private int saturationLevel = 0;
    private int hasteLevel = 0;
    private int healthboostLevel = 0;
    private int waterbreathingLevel = 0;
    private int dolphinsgraceLevel = 0;
    private int luckLevel = 0;
    private int slowfallingLevel = 0;
    private int nightvisionLevel = 0;
    private int jumpboostLevel = 0;
    private int invisibilityLevel = 0;
    private int fireresistanceLevel = 0;
    private int structureCheckCooldown = 0;

    // 2. Variable tracking fields for total cost
    private int currentEnergyCost = BASE_ENERGY_COST;

    public BeaconT5BlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BEACON_T5_BE.get(), pos, state);
    }

    public void setOwnerUuid(UUID uuid) {
        this.ownerUuid = uuid;
        setChanged();
    }

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energyStorage.getEnergyStored();
                case 1 -> speedLevel;
                case 2 -> strengthLevel;
                case 3 -> resistanceLevel;
                case 4 -> getCurrentEnergyCost();
                case 5 -> regenerationLevel;
                case 6 -> saturationLevel;
                case 7 -> hasteLevel;
                case 8 -> healthboostLevel;
                case 9 -> waterbreathingLevel;
                case 10 -> dolphinsgraceLevel;
                case 11 -> luckLevel;
                case 12 -> slowfallingLevel;
                case 13 -> nightvisionLevel;
                case 14 -> jumpboostLevel;
                case 15 -> invisibilityLevel;
                case 16 -> fireresistanceLevel;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energyStorage.setEnergy(value);
                case 1 -> speedLevel = value;
                case 2 -> strengthLevel = value;
                case 3 -> resistanceLevel = value;
                case 4 -> currentEnergyCost = value;
                case 5 -> regenerationLevel = value;
                case 6 -> saturationLevel = value;
                case 7 -> hasteLevel = value;
                case 8 -> healthboostLevel = value;
                case 9 -> waterbreathingLevel = value;
                case 10 -> dolphinsgraceLevel = value;
                case 11 -> luckLevel = value;
                case 12 -> slowfallingLevel = value;
                case 13 -> nightvisionLevel = value;
                case 14 -> jumpboostLevel = value;
                case 15 -> invisibilityLevel = value;
                case 16 -> fireresistanceLevel = value;
            }
        }

        @Override
        public int getCount() {
            return 17; // We are tracking 4 integer variables total
        }
    };

    // Concrete requirements for MenuProvider
    @Override
    public Component getDisplayName() {
        return Component.translatable("Beacon Tier 3");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BeaconT5Menu(id, inventory, this, this.data);
    }


    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        // FIXED: The structural cooldown countdown is moved to the absolute top of the method.
        // It will now countdown every tick, completely immune to early return statements below!
        if (this.structureCheckCooldown > 0) {
            this.structureCheckCooldown--;
        }

        if (this.structureCheckCooldown <= 0) {
            validateStructure(level, pos, state);
            this.structureCheckCooldown = 20; // Reset countdown gate
        }

        // Safety verification check: if the owner string data hasn't loaded yet, break out
        if (ownerUuid == null) return;

        // Verify structure completion state before applying effects or drawing power
        if (!this.isStructureComplete()) return;

        // Quit early if no modifier levels are currently active
        if (this.speedLevel == 0 && this.strengthLevel == 0 && this.resistanceLevel == 0 && this.regenerationLevel == 0 && this.saturationLevel == 0 && this.hasteLevel == 0 && this.healthboostLevel == 0 && this.waterbreathingLevel == 0 && this.dolphinsgraceLevel == 0 && this.luckLevel == 0 && this.slowfallingLevel == 0 && this.nightvisionLevel == 0 && this.jumpboostLevel == 0 && invisibilityLevel == 0 && this.fireresistanceLevel == 0) return;

        // 3. Extract the dynamically calculated cost instead of a static value
        if (energyStorage.extractEnergy(this.currentEnergyCost, true) >= this.currentEnergyCost) {
            if (level.getServer() != null) {
                net.minecraft.server.level.ServerPlayer serverPlayer = level.getServer().getPlayerList().getPlayer(ownerUuid);

                if (serverPlayer != null && serverPlayer.isAlive()) {
                    energyStorage.extractEnergy(this.currentEnergyCost, false);

                    if (speedLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, speedLevel - 1, true, true));
                    }
                    if (strengthLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, strengthLevel - 1, true, true));
                    }
                    if (resistanceLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, resistanceLevel - 1, true, true));
                    }
                    if (regenerationLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, regenerationLevel - 1, true, true));
                    }
                    if (saturationLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.SATURATION, 200, saturationLevel - 1, true, true));
                    }
                    if (hasteLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, hasteLevel - 1, true, true));
                    }
                    if (healthboostLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 200, healthboostLevel - 1, true, true));
                    }
                    if (waterbreathingLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, waterbreathingLevel - 1, true, true));
                    }
                    if (dolphinsgraceLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 200, dolphinsgraceLevel - 1, true, true));
                    }
                    if (luckLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.LUCK, 200, luckLevel - 1, true, true));
                    }
                    if (slowfallingLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, slowfallingLevel - 1, true, true));
                    }
                    if (nightvisionLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, nightvisionLevel - 1, true, true));
                    }
                    if (jumpboostLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, jumpboostLevel - 1, true, true));
                    }
                    if (invisibilityLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, invisibilityLevel - 1, true, true));
                    }
                    if (fireresistanceLevel > 0) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, fireresistanceLevel - 1, true, true));
                    }

                    setChanged();
                }
            }
        }
    }

    // YOUR VALIDATION METHOD: Placed inside the tick lifecycle
    public void validateStructure(Level world, BlockPos controllerPos, BlockState currentState) {
        // 1. Reset counters to 0 before scanning
        ModifierCounter counter = new ModifierCounter(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, BASE_ENERGY_COST);

        boolean isValid =
                //Layer 1 (y =-7)
                // Row 1 (z = -7)
                checkBlock(world, controllerPos.offset(-7, -7, -7), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -7, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -7, -7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -7, -7),  Blocks.BLACKSTONE) &&

                // Row 2 (z = -6)
                checkBlock(world, controllerPos.offset(-7, -7, -6), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -7, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -7, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -7, -6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -7, -6),  Blocks.BLACKSTONE) &&

                // Row 3 (z = -5)
                checkBlock(world, controllerPos.offset(-6, -7, -5), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -7, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, -5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -7, -5),  Blocks.BLACKSTONE) &&

                // Row 4 (z = -4)
                checkBlock(world, controllerPos.offset(-6, -7, -4), Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, -4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -7, -4),  Blocks.BLACKSTONE) &&

                // Row 5 (z = -3)
                checkBlock(world, controllerPos.offset(-5, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, -3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, -3),  Blocks.BLACKSTONE) &&

                // Row 6 (z = -2)
                checkBlock(world, controllerPos.offset(-5, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, -2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, -2),  Blocks.BLACKSTONE) &&

                // Row 7 (z = -1)
                checkBlock(world, controllerPos.offset(-4, -7, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -7, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -7, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -7, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, -1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, -1),  Blocks.BLACKSTONE) &&

                // Row 8 (z = 0)
                checkBlock(world, controllerPos.offset(-4, -7, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -7, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -7, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -7, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, 0),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, 0),  Blocks.BLACKSTONE) &&

                // Row 9 (z = 1)
                checkBlock(world, controllerPos.offset(-4, -7, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -7, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -7, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -7, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, 1),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, 1),  Blocks.BLACKSTONE) &&

                // Row 10 (z = 2)
                checkBlock(world, controllerPos.offset(-5, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, 2),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, 2),  Blocks.BLACKSTONE) &&

                // Row 11 (z = 3)
                checkBlock(world, controllerPos.offset(-5, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, 3),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, 3),  Blocks.BLACKSTONE) &&

                // Row 12 (z = 4)
                checkBlock(world, controllerPos.offset(-6, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-1, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(0, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(1, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, 4),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -7, 4),  Blocks.BLACKSTONE) &&

                // Row 13 (z = 5)
                checkBlock(world, controllerPos.offset(-6, -7, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -7, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-3, -7, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-2, -7, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(2, -7, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(3, -7, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, 5),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -7, 5),  Blocks.BLACKSTONE) &&

                // Row 14 (z = 6)
                checkBlock(world, controllerPos.offset(-7, -7, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -7, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-5, -7, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-4, -7, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(4, -7, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(5, -7, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -7, 6),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -7, 6),  Blocks.BLACKSTONE) &&

                // Row 15 (z = 7)
                checkBlock(world, controllerPos.offset(-7, -7, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(-6, -7, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(6, -7, 7),  Blocks.BLACKSTONE) &&
                checkBlock(world, controllerPos.offset(7, -7, 7),  Blocks.BLACKSTONE) &&

                // Layer 2 (y = -6)
                //Row 1 (z = -7)
                checkBlockOrModifier(world, controllerPos.offset(-7, -6, -7), counter) &&
                checkBlockOrModifier(world, controllerPos.offset(7, -6, -7), counter) &&

                //Row 2 (z = -6)
                checkBlock(world, controllerPos.offset(-6, -6, -6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-5, -6, -6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -6, -6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(6, -6, -6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 3 (z = -5)
                checkBlock(world, controllerPos.offset(-6, -6, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-5, -6, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-4, -6, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -6, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -6, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -6, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -6, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(6, -6, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 4 (z = -4)
                checkBlock(world, controllerPos.offset(-5, -6, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -6, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -6, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -6, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -6, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -6, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -6, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -6, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 5 (z = -3)
                checkBlock(world, controllerPos.offset(-5, -6, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-4, -6, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -6, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -6, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -6, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -6, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -6, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 6 (z = -2)
                checkBlock(world, controllerPos.offset(-4, -6, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -6, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 7 (z = -1)
                checkBlock(world, controllerPos.offset(-4, -6, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -6, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -6, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -6, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 8 (z = 0)
                checkBlock(world, controllerPos.offset(-3, -6, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -6, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 9 (z = 1)
                checkBlock(world, controllerPos.offset(-4, -6, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -6, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -6, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -6, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 10 (z = 2)
                checkBlock(world, controllerPos.offset(-4, -6, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -6, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 11 (z = 3)
                checkBlock(world, controllerPos.offset(-5, -6, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-4, -6, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -6, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -6, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -6, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -6, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -6, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 12 (z = 4)
                checkBlock(world, controllerPos.offset(-5, -6, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -6, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -6, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -6, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -6, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -6, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -6, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -6, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 13 (z = 5)
                checkBlock(world, controllerPos.offset(-6, -6, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-5, -6, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-4, -6, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -6, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -6, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -6, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -6, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(6, -6, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 14 (z = 6)
                checkBlock(world, controllerPos.offset(-6, -6, 6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-5, -6, 6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -6, 6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(6, -6, 6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 15 (z = 7)
                checkBlockOrModifier(world, controllerPos.offset(-7, -6, 7), counter) &&
                checkBlockOrModifier(world, controllerPos.offset(7, -6, 7), counter) &&

                //Layer 3 (y = -5)
                //Row 1 (z = -6)
                checkBlock(world, controllerPos.offset(-6, -5, -6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(6, -5, -6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 2 (z = -5)
                checkBlock(world, controllerPos.offset(-5, -5, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-4, -5, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -5, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -5, -5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 3 (z = -4)
                checkBlock(world, controllerPos.offset(-5, -5, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-4, -5, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -5, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -5, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -5, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -5, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -5, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -5, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 4 (z = -3)
                checkBlock(world, controllerPos.offset(-4, -5, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -5, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -5, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -5, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -5, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -5, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -5, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 5 (z = -2)
                checkBlock(world, controllerPos.offset(-4, -5, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -5, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -5, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -5, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 6 (z = -1)
                checkBlock(world, controllerPos.offset(-3, -5, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -5, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 7 (z = 0)
                checkBlock(world, controllerPos.offset(-3, -5, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -5, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 8 (z = 1)
                checkBlock(world, controllerPos.offset(-3, -5, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -5, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 9 (z = 2)
                checkBlock(world, controllerPos.offset(-4, -5, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -5, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -5, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -5, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 10 (z = 3)
                checkBlock(world, controllerPos.offset(-4, -5, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -5, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -5, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -5, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -5, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -5, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -5, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 11 (z = 4)
                checkBlock(world, controllerPos.offset(-5, -5, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-4, -5, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -5, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -5, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -5, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -5, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -5, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -5, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 12 (z = 5)
                checkBlock(world, controllerPos.offset(-5, -5, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-4, -5, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -5, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(5, -5, 5), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 13 (z = 6)
                checkBlock(world, controllerPos.offset(-6, -5, 6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(6, -5, 6), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Layer 4 (y = -4)
                //Row 1 (z = -5)
                checkBlockOrModifier(world, controllerPos.offset(-5, -4, -5), counter) &&
                checkBlockOrModifier(world, controllerPos.offset(5, -4, -5), counter) &&

                //Row 2 (z = -4)
                checkBlock(world, controllerPos.offset(-4, -4, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -4, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -4, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -4, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 3 (z = -3)
                checkBlock(world, controllerPos.offset(-4, -4, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -4, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -4, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -4, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -4, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -4, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -4, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -4, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 4 (z = -2)
                checkBlock(world, controllerPos.offset(-3, -4, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -4, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -4, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -4, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -4, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 5 (z = -1)
                checkBlock(world, controllerPos.offset(-3, -4, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -4, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -4, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -4, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 6 (z = 0)
                checkBlock(world, controllerPos.offset(-2, -4, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -4, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 7 (z = 1)
                checkBlock(world, controllerPos.offset(-3, -4, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -4, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -4, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -4, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 8 (z = 2)
                checkBlock(world, controllerPos.offset(-3, -4, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -4, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -4, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -4, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -4, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 9 (z = 3)
                checkBlock(world, controllerPos.offset(-4, -4, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -4, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -4, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -4, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -4, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -4, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -4, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -4, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 10 (z = 4)
                checkBlock(world, controllerPos.offset(-4, -4, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-3, -4, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -4, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -4, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 11 (z = 5)
                checkBlockOrModifier(world, controllerPos.offset(-5, -4, 5), counter) &&
                checkBlockOrModifier(world, controllerPos.offset(5, -4, 5), counter) &&

                //Layer 5 (y = -3)
                //Row 1 (z = -4)
                checkBlock(world, controllerPos.offset(-4, -3, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -3, -4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 2 (z = -3)
                checkBlock(world, controllerPos.offset(-3, -3, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -3, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -3, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -3, -3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 3 (z = -2)
                checkBlock(world, controllerPos.offset(-3, -3, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -3, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -3, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -3, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -3, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -3, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -3, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 4 (z = -1)
                checkBlock(world, controllerPos.offset(-2, -3, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -3, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 5 (z = 0)
                checkBlock(world, controllerPos.offset(-2, -3, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -3, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 6 (z = 1)
                checkBlock(world, controllerPos.offset(-2, -3, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -3, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 7 (z = 2)
                checkBlock(world, controllerPos.offset(-3, -3, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -3, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -3, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -3, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -3, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -3, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -3, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 8 (z = 3)
                checkBlock(world, controllerPos.offset(-3, -3, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-2, -3, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -3, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(3, -3, 3), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 9 (z = 4)
                checkBlock(world, controllerPos.offset(-4, -3, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(4, -3, 4), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Layer 6 (y = -2)
                //Row 1 (z = -3)
                checkBlockOrModifier(world, controllerPos.offset(-3, -2, -3), counter) &&
                checkBlockOrModifier(world, controllerPos.offset(3, -2, -3), counter) &&

                //Row 2 (z = -2)
                checkBlock(world, controllerPos.offset(-2, -2, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -2, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -2, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -2, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 3 (z = -1)
                checkBlock(world, controllerPos.offset(-2, -2, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -2, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -2, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -2, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -2, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 4 (z = 0)
                checkBlock(world, controllerPos.offset(-1, -2, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -2, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 5 (z = 1)
                checkBlock(world, controllerPos.offset(-2, -2, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -2, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -2, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -2, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -2, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 6 (z = 2)
                checkBlock(world, controllerPos.offset(-2, -2, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(-1, -2, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -2, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -2, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 7 (z = 3)
                checkBlockOrModifier(world, controllerPos.offset(-3, -2, 3), counter) &&
                checkBlockOrModifier(world, controllerPos.offset(3, -2, 3), counter) &&

                //Layer 7 (y = -1)
                //Row 1 (z = -2)
                checkBlock(world, controllerPos.offset(-2, -1, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -1, -2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 2 (z = -1)
                checkBlock(world, controllerPos.offset(-1, -1, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -1, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -1, -1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 3 (z = 0)
                checkBlock(world, controllerPos.offset(-1, -1, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -1, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -1, 0), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 4 (z = 1)
                checkBlock(world, controllerPos.offset(-1, -1, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(0, -1, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(1, -1, 1), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Row 5 (z = 2)
                checkBlock(world, controllerPos.offset(-2, -1, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&
                checkBlock(world, controllerPos.offset(2, -1, 2), ModBlocks.VOLSARNIUM_BLOCK.get()) &&

                //Layer 8 (y =0)
                checkBlockOrModifier(world, controllerPos.offset(-1, 0, -1), counter) &&
                checkBlockOrModifier(world, controllerPos.offset(1, 0, -1), counter) &&
                checkBlockOrModifier(world, controllerPos.offset(-1, 0, 1), counter) &&
                checkBlockOrModifier(world, controllerPos.offset(1, 0, 1), counter);


        // 1. Assign the structural completion flag universally up front
        this.isComplete = isValid;

        // 2. Update your potion effect fields based on the validity state
        if (isValid) {
            this.currentEnergyCost = counter.totalCost;

            // Both duplicate lines are deleted, removing all warnings cleanly!
            if (this.speedLevel != counter.speed || this.strengthLevel != counter.strength || this.resistanceLevel != counter.resistance || this.regenerationLevel != counter.regeneration || this.saturationLevel != counter.saturation || this.hasteLevel != counter.haste || this.healthboostLevel != counter.healthboost || this.waterbreathingLevel != counter.waterbreathing || this.dolphinsgraceLevel != counter.dolphinsgrace || this.luckLevel != counter.luck || this.slowfallingLevel != counter.slowfalling || this.nightvisionLevel != counter.nightvision || this.jumpboostLevel != counter.jumpboost || this.invisibilityLevel != counter.invisibility || this.fireresistanceLevel != counter.fireresistance) {
                this.speedLevel = counter.speed;
                this.strengthLevel = counter.strength;
                this.resistanceLevel = counter.resistance;
                this.regenerationLevel = counter.regeneration;
                this.saturationLevel = counter.saturation;
                this.hasteLevel = counter.haste;
                this.healthboostLevel = counter.healthboost;
                this.waterbreathingLevel = counter.waterbreathing;
                this.dolphinsgraceLevel = counter.dolphinsgrace;
                this.luckLevel = counter.luck;
                this.slowfallingLevel = counter.slowfalling;
                this.nightvisionLevel = counter.nightvision;
                this.jumpboostLevel = counter.jumpboost;
                this.invisibilityLevel = counter.invisibility;
                this.fireresistanceLevel = counter.fireresistance;

                setChanged();
                world.sendBlockUpdated(controllerPos, currentState, currentState, 3);
            }
        } else {

            this.currentEnergyCost = BASE_ENERGY_COST; // Reset to default on structure breakdown
            // If the structure configuration fails entirely, remove all potion levels
            if (this.speedLevel != 0 || this.strengthLevel != 0 || this.resistanceLevel != 0) {
                this.speedLevel = 0;
                this.strengthLevel = 0;
                this.resistanceLevel = 0;
                this.regenerationLevel = 0;
                this.saturationLevel = 0;
                this.hasteLevel = 0;
                this.healthboostLevel = 0;
                this.waterbreathingLevel = 0;
                this.dolphinsgraceLevel = 0;
                this.luckLevel = 0;
                this.slowfallingLevel = 0;
                this.nightvisionLevel = 0;
                this.jumpboostLevel = 0;
                this.invisibilityLevel = 0;
                this.fireresistanceLevel = 0;
                setChanged();
                world.sendBlockUpdated(controllerPos, currentState, currentState, 3);
            }
        }
    }

    public boolean isStructureComplete() {
        return this.isComplete;
    }

    // Helper class to pass primitive counts by reference so they can be modified inside the method
    private static class ModifierCounter {
        int speed;
        int strength;
        int resistance;
        int regeneration;
        int saturation;
        int haste;
        int healthboost;
        int waterbreathing;
        int dolphinsgrace;
        int luck;
        int slowfalling;
        int nightvision;
        int jumpboost;
        int invisibility;
        int fireresistance;
        int totalCost;

        public ModifierCounter(int speed, int strength, int resistance, int regeneration, int saturation, int haste, int healthboost, int waterbreathing, int dolphinsgrace, int luck, int slowfalling, int nightvision, int jumpboost, int invisibility, int fireresistance, int baseCost) {
            this.speed = speed;
            this.strength = strength;
            this.resistance = resistance;
            this.regeneration = regeneration;
            this.saturation = saturation;
            this.haste = haste;
            this.healthboost = healthboost;
            this.waterbreathing = waterbreathing;
            this.dolphinsgrace = dolphinsgrace;
            this.luck = luck;
            this.slowfalling = slowfalling;
            this.nightvision = nightvision;
            this.jumpboost = jumpboost;
            this.invisibility = invisibility;
            this.fireresistance = fireresistance;
            this.totalCost = baseCost;
        }
    }

    private boolean checkBlockOrModifier(Level world, BlockPos pos, ModifierCounter counter) {
        BlockState state = world.getBlockState(pos);

        // Check if the block is any of your acceptable dynamic modifier blocks
        if (state.is(ModBlocks.MODIFIER_TEMPLATE_BLOCK.get())) {
            return true; // Valid block position!
        } else if (state.is(ModBlocks.MODIFIER_SPEED_BLOCK.get())) {
            counter.speed++;
            counter.totalCost += SPEED_COST_PER_BLOCK;
            return true; // Valid block position!
        } else if (state.is(ModBlocks.MODIFIER_STRENGTH_BLOCK.get())) {
            counter.strength++;
            counter.totalCost += STRENGTH_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_RESISTANCE_BLOCK.get())) {
            counter.resistance++;
            counter.totalCost += RESISTANCE_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_REGENERATION_BLOCK.get())) {
            counter.regeneration++;
            counter.totalCost += REGENERATION_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_SATURATION_BLOCK.get())) {
            counter.saturation++;
            counter.totalCost += SATURATION_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_HASTE_BLOCK.get())) {
            counter.haste++;
            counter.totalCost += HASTE_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_HEALTHBOOST_BLOCK.get())) {
            counter.healthboost++;
            counter.totalCost += HEALTH_BOOST_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_WATERBREATHING_BLOCK.get())) {
            counter.waterbreathing++;
            counter.totalCost += WATER_BREATHING_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_DOLPHINSGRACE_BLOCK.get())) {
            counter.dolphinsgrace++;
            counter.totalCost += DOLPHINS_GRACE_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_LUCK_BLOCK.get())) {
            counter.luck++;
            counter.totalCost += LUCK_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_SLOWFALL_BLOCK.get())) {
            counter.slowfalling++;
            counter.totalCost += SLOW_FALLING_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_NIGHTVISION_BLOCK.get())) {
            counter.nightvision++;
            counter.totalCost += NIGHT_VISION_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_JUMP_BLOCK.get())) {
            counter.jumpboost++;
            counter.totalCost += JUMP_BOOST_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_INVISIBILITY_BLOCK.get())) {
            counter.invisibility++;
            counter.totalCost += INVISIBILITY_COST_PER_BLOCK;
            return true;
        } else if (state.is(ModBlocks.MODIFIER_FIRERESISTANCE_BLOCK.get())) {
            counter.fireresistance++;
            counter.totalCost += FIRE_RESISTANCE_COST_PER_BLOCK;
            return true;
        }

        // Return false if the block placed there doesn't match any allowed modifier
        return false;
    }

    // 8. Add a getter so your Menu and Screens can read the active cost
    public int getCurrentEnergyCost() {
        return this.isStructureComplete() ? this.currentEnergyCost : BASE_ENERGY_COST;
    }

    // Helper method required by your validation code
    private boolean checkBlock(Level world, BlockPos pos, Block targetBlock) {
        return world.getBlockState(pos).is(targetBlock);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.putBoolean("IsStructureComplete", isComplete);
        tag.putInt("SpeedLevel", this.speedLevel);
        tag.putInt("StrengthLevel", this.strengthLevel);
        tag.putInt("ResistanceLevel", this.resistanceLevel);
        tag.putInt("RegenerationLevel", this.regenerationLevel);
        tag.putInt("SaturationLevel", this.saturationLevel);
        tag.putInt("HasteLevel", this.hasteLevel);
        tag.putInt("HealthBoostLevel", this.healthboostLevel);
        tag.putInt("WaterbreathingLevel", this.waterbreathingLevel);
        tag.putInt("DolphinsGraceLevel", this.dolphinsgraceLevel);
        tag.putInt("LuckLevel", this.luckLevel);
        tag.putInt("SlowFallingLevel", this.slowfallingLevel);
        tag.putInt("NightVisionLevel", this.nightvisionLevel);
        tag.putInt("JumpBoostLevel", this.jumpboostLevel);
        tag.putInt("InvisibilityLevel", this.invisibilityLevel);
        tag.putInt("FireResistanceLevel", this.resistanceLevel);

        // FIXED: Explicitly use standard string formatting to guarantee clean identification
        if (ownerUuid != null) {
            tag.putString("OwnerUUIDString", ownerUuid.toString());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) {
            this.energyStorage.setEnergy(tag.getInt("Energy"));
        }
        if (tag.contains("IsStructureComplete")) {
            this.isComplete = tag.getBoolean("IsStructureComplete");
        }
        if (tag.contains("SpeedLevel")) {
            this.speedLevel = tag.getInt("SpeedLevel");
        }
        if (tag.contains("StrengthLevel")) {
            this.strengthLevel = tag.getInt("StrengthLevel");
        }
        if (tag.contains("ResistanceLevel")) {
            this.resistanceLevel = tag.getInt("ResistanceLevel");
        }
        if (tag.contains("RegenerationLevel")) {
            this.regenerationLevel = tag.getInt("RegenerationLevel");
        }
        if (tag.contains("SaturationLevel")) {
            this.saturationLevel = tag.getInt("SaturationLevel");
        }
        if (tag.contains("HasteLevel")) {
            this.hasteLevel = tag.getInt("HasteLevel");
        }
        if (tag.contains("HealthBoostLevel")) {
            this.healthboostLevel = tag.getInt("HealthBoostLevel");
        }
        if (tag.contains("WaterbreathingLevel")) {
            this.waterbreathingLevel = tag.getInt("WaterbreathingLevel");
        }
        if (tag.contains("DolphinsGraceLevel")) {
            this.dolphinsgraceLevel = tag.getInt("DolphinsGraceLevel");
        }
        if (tag.contains("LuckLevel")) {
            this.luckLevel = tag.getInt("LuckLevel");
        }
        if (tag.contains("SlowFallingLevel")) {
            this.slowfallingLevel = tag.getInt("SlowFallingLevel");
        }
        if (tag.contains("NightVisionLevel")) {
            this.nightvisionLevel = tag.getInt("NightVisionLevel");
        }
        if (tag.contains("JumpBoostLevel")) {
            this.jumpboostLevel = tag.getInt("JumpBoostLevel");
        }
        if (tag.contains("InvisibilityLevel")) {
            this.invisibilityLevel = tag.getInt("InvisibilityLevel");
        }
        if (tag.contains("FireResistanceLevel")) {
            this.fireresistanceLevel = tag.getInt("FireResistanceLevel");
        }

        // FIXED: Safely retrieve the string data back into a valid UUID object
        if (tag.contains("OwnerUUIDString")) {
            this.ownerUuid = UUID.fromString(tag.getString("OwnerUUIDString"));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
    }

    private static class CustomEnergy extends EnergyStorage {
        public CustomEnergy(int capacity, int maxReceive, int maxExtract) {
            super(capacity, maxReceive, maxExtract);
        }

        public void setEnergy(int energy) {
            this.energy = Math.max(0, Math.min(this.capacity, energy));
        }
    }
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag); // Pack everything up for network sync
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            load(tag); // Unpack network sync data on client
        }
    }
}
