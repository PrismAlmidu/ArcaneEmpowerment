package net.prismalmidu.arcaneempowerment.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ArcaneEmpowerment.MOD_ID);

    public static final RegistryObject<BlockEntityType<CraftingAltarT1BlockEntity>> CRAFTING_ALTAR_T1_BE =
            BLOCK_ENTITIES.register("crafting_altar_t1_be", () ->
                    BlockEntityType.Builder.of(CraftingAltarT1BlockEntity::new,
                            ModBlocks.CRAFTING_ALTAR_T1.get()).build(null));
    public static final RegistryObject<BlockEntityType<CraftingAltarT2BlockEntity>> CRAFTING_ALTAR_T2_BE =
            BLOCK_ENTITIES.register("crafting_altar_t2_be", () ->
                    BlockEntityType.Builder.of(CraftingAltarT2BlockEntity::new,
                            ModBlocks.CRAFTING_ALTAR_T2.get()).build(null));
    public static final RegistryObject<BlockEntityType<CraftingAltarT3BlockEntity>> CRAFTING_ALTAR_T3_BE =
            BLOCK_ENTITIES.register("crafting_altar_t3_be", () ->
                    BlockEntityType.Builder.of(CraftingAltarT3BlockEntity::new,
                            ModBlocks.CRAFTING_ALTAR_T3.get()).build(null));
    public static final RegistryObject<BlockEntityType<CraftingAltarT4BlockEntity>> CRAFTING_ALTAR_T4_BE =
            BLOCK_ENTITIES.register("crafting_altar_t4_be", () ->
                    BlockEntityType.Builder.of(CraftingAltarT4BlockEntity::new,
                            ModBlocks.CRAFTING_ALTAR_T4.get()).build(null));
    public static final RegistryObject<BlockEntityType<CraftingAltarT5BlockEntity>> CRAFTING_ALTAR_T5_BE =
            BLOCK_ENTITIES.register("crafting_altar_t5_be", () ->
                    BlockEntityType.Builder.of(CraftingAltarT5BlockEntity::new,
                            ModBlocks.CRAFTING_ALTAR_T5.get()).build(null));

    public static final RegistryObject<BlockEntityType<AccumulatorCoreBlockEntity>> ACCUMULATOR_CORE_BE =
            BLOCK_ENTITIES.register("accumulator_core_be", () ->
                    BlockEntityType.Builder.of(AccumulatorCoreBlockEntity::new,
                            ModBlocks.ACCUMULATOR_CORE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TransmutationAltarBlockEntity>> TRANSMUTATION_ALTAR_BE =
            BLOCK_ENTITIES.register("transmutation_altar_be", () ->
                    BlockEntityType.Builder.of(TransmutationAltarBlockEntity::new,
                            ModBlocks.TRANSMUTATION_ALTAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CollectorCrystalBlockEntity>> COLLECTOR_CRYSTAL_BE =
            BLOCK_ENTITIES.register("collector_crystal_be", () ->
                    BlockEntityType.Builder.of(CollectorCrystalBlockEntity::new,
                            ModBlocks.COLLECTOR_CRYSTAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<PerkShrineBlockEntity>> PERK_SHRINE_BE =
            BLOCK_ENTITIES.register("perk_shrine_be", () ->
                    BlockEntityType.Builder.of(PerkShrineBlockEntity::new,
                            ModBlocks.PERK_SHRINE.get()).build(null));

    public static final RegistryObject<BlockEntityType<VoidMinerT2BlockEntity>> VOID_MINER_T2_BE =
            BLOCK_ENTITIES.register("void_miner_t2_be", () ->
                    BlockEntityType.Builder.of(VoidMinerT2BlockEntity::new,
                            ModBlocks.VOID_MINER_T2.get()).build(null));
    public static final RegistryObject<BlockEntityType<VoidMinerT3BlockEntity>> VOID_MINER_T3_BE =
            BLOCK_ENTITIES.register("void_miner_t3_be", () ->
                    BlockEntityType.Builder.of(VoidMinerT3BlockEntity::new,
                            ModBlocks.VOID_MINER_T3.get()).build(null));
    public static final RegistryObject<BlockEntityType<VoidMinerT4BlockEntity>> VOID_MINER_T4_BE =
            BLOCK_ENTITIES.register("void_miner_t4_be", () ->
                    BlockEntityType.Builder.of(VoidMinerT4BlockEntity::new,
                            ModBlocks.VOID_MINER_T4.get()).build(null));
    public static final RegistryObject<BlockEntityType<VoidMinerT5BlockEntity>> VOID_MINER_T5_BE =
            BLOCK_ENTITIES.register("void_miner_t5_be", () ->
                    BlockEntityType.Builder.of(VoidMinerT5BlockEntity::new,
                            ModBlocks.VOID_MINER_T5.get()).build(null));

    public static final RegistryObject<BlockEntityType<BeaconT3BlockEntity>> BEACON_T3_BE =
            BLOCK_ENTITIES.register("beacon_t3_be", () ->
                    BlockEntityType.Builder.of(BeaconT3BlockEntity::new,
                            ModBlocks.BEACON_T3.get()).build(null));
    public static final RegistryObject<BlockEntityType<BeaconT4BlockEntity>> BEACON_T4_BE =
            BLOCK_ENTITIES.register("beacon_t4_be", () ->
                    BlockEntityType.Builder.of(BeaconT4BlockEntity::new,
                            ModBlocks.BEACON_T4.get()).build(null));
    public static final RegistryObject<BlockEntityType<BeaconT5BlockEntity>> BEACON_T5_BE =
            BLOCK_ENTITIES.register("beacon_t5_be", () ->
                    BlockEntityType.Builder.of(BeaconT5BlockEntity::new,
                            ModBlocks.BEACON_T5.get()).build(null));



    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
