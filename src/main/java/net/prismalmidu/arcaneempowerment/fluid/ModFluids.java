package net.prismalmidu.arcaneempowerment.fluid;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.item.ModItems;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, ArcaneEmpowerment.MOD_ID);

    public static final RegistryObject<FlowingFluid> SOURCE_LIQUID_MANA = FLUIDS.register("liquid_mana_fluid_source",
            () -> new ForgeFlowingFluid.Source(ModFluids.LIQUID_MANA_FLUID_PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_LIQUID_MANA = FLUIDS.register("liquid_mana_fluid_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.LIQUID_MANA_FLUID_PROPERTIES));

    public static final ForgeFlowingFluid.Properties LIQUID_MANA_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.LIQUID_MANA_FLUID_TYPE, SOURCE_LIQUID_MANA, FLOWING_LIQUID_MANA)
            .slopeFindDistance(2).levelDecreasePerBlock(1).block(ModBlocks.LIQUID_MANA_BLOCK)
            .bucket(ModItems.LIQUID_MANA_BUCKET);

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }

}
