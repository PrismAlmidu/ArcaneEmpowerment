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

    public static final RegistryObject<BlockEntityType<AccumulatorCoreBlockEntity>> ACCUMULATOR_CORE_BE =
            BLOCK_ENTITIES.register("accumulator_core_be", () ->
                    BlockEntityType.Builder.of(AccumulatorCoreBlockEntity::new,
                            ModBlocks.ACCUMULATOR_CORE.get()).build(null));

    public static final RegistryObject<BlockEntityType<VoidMinerT2BlockEntity>> VOID_MINER_T2_BE =
            BLOCK_ENTITIES.register("void_miner_t2_be", () ->
                    BlockEntityType.Builder.of(VoidMinerT2BlockEntity::new,
                            ModBlocks.VOID_MINER_T2.get()).build(null));



    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
