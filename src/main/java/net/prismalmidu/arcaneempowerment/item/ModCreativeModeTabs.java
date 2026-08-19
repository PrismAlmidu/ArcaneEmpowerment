package net.prismalmidu.arcaneempowerment.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArcaneEmpowerment.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ARCANEEMPOWERMENT_TAB = CREATIVE_MODE_TABS.register("arcaneempowerment_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ARCANIUM_SHARD.get()))
                    .title(Component.translatable("creativetab.arcaneempowerment_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.ARCANIUM_SHARD.get());
                        pOutput.accept(ModItems.PHANTASMIUM_SHARD.get());
                        pOutput.accept(ModItems.STELLARIUM_SHARD.get());
                        pOutput.accept(ModItems.CHRONIUM_SHARD.get());
                        pOutput.accept(ModItems.VOLSARNIUM_SHARD.get());
                        pOutput.accept(ModItems.LIQUID_MANA_BUCKET.get());

                        pOutput.accept(ModBlocks.ARCANIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.PHANTASMIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.STELLARIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.CHRONIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.VOLSARNIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_TEMPLATE_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_DOLPHINSGRACE_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_FIRERESISTANCE_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_FLIGHT_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_HASTE_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_HEALTHBOOST_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_INVISIBILITY_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_JUMP_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_KNOWLEDGE_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_LUCK_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_MANAREGEN_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_MINEREFFICIENCY_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_MINERPRODUCTION_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_MINERSPEED_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_NIGHTVISION_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_REGENERATION_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_RESISTANCE_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_SATURATION_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_SLOWFALL_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_SPEED_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_SPELLHASTE_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_SPELLLONGEVITY_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_SPELLMIGHT_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_SPELLPRESERVING_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_STRENGTH_BLOCK.get());
                        pOutput.accept(ModBlocks.MODIFIER_WATERBREATHING_BLOCK.get());

                        pOutput.accept(ModBlocks.CRAFTING_ALTAR_T1.get());
                        pOutput.accept(ModBlocks.CRAFTING_ALTAR_T2.get());

                        pOutput.accept(ModBlocks.ACCUMULATOR_CORE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
