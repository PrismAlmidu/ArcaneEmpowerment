package net.prismalmidu.arcaneempowerment.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.fluid.ModFluids;
import net.prismalmidu.arcaneempowerment.item.ModItems;
import net.prismalmidu.arcaneempowerment.block.custom.CraftingAltarT1Block;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ArcaneEmpowerment.MOD_ID);

    public static final RegistryObject<Block> ARCANIUM_BLOCK = registerBlock("arcanium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> PHANTASMIUM_BLOCK = registerBlock("phantasmium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> STELLARIUM_BLOCK = registerBlock("stellarium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> CHRONIUM_BLOCK = registerBlock("chronium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> VOLSARNIUM_BLOCK = registerBlock("volsarnium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_TEMPLATE_BLOCK = registerBlock("modifier_template_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_DOLPHINSGRACE_BLOCK = registerBlock("modifier_dolphinsgrace_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_FIRERESISTANCE_BLOCK = registerBlock("modifier_fireresistance_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_FLIGHT_BLOCK = registerBlock("modifier_flight_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_HASTE_BLOCK = registerBlock("modifier_haste_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_HEALTHBOOST_BLOCK = registerBlock("modifier_healthboost_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_INVISIBILITY_BLOCK = registerBlock("modifier_invisibility_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_JUMP_BLOCK = registerBlock("modifier_jump_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_KNOWLEDGE_BLOCK = registerBlock("modifier_knowledge_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_LUCK_BLOCK = registerBlock("modifier_luck_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_MANAREGEN_BLOCK = registerBlock("modifier_manaregen_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_MINEREFFICIENCY_BLOCK = registerBlock("modifier_minerefficiency_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_MINERPRODUCTION_BLOCK = registerBlock("modifier_minerproduction_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_MINERSPEED_BLOCK = registerBlock("modifier_minerspeed_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_NIGHTVISION_BLOCK = registerBlock("modifier_nightvision_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_REGENERATION_BLOCK = registerBlock("modifier_regeneration_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_RESISTANCE_BLOCK = registerBlock("modifier_resistance_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_SATURATION_BLOCK = registerBlock("modifier_saturation_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_SLOWFALL_BLOCK = registerBlock("modifier_slowfall_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_SPEED_BLOCK = registerBlock("modifier_speed_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_SPELLHASTE_BLOCK = registerBlock("modifier_spellhaste_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_SPELLLONGEVITY_BLOCK = registerBlock("modifier_spelllongevity_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_SPELLMIGHT_BLOCK = registerBlock("modifier_spellmight_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_SPELLPRESERVING_BLOCK = registerBlock("modifier_spellpreserving_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_STRENGTH_BLOCK = registerBlock("modifier_strength_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MODIFIER_WATERBREATHING_BLOCK = registerBlock("modifier_waterbreathing_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> CRAFTING_ALTAR_T1 = registerBlock("crafting_altar_t1",
            () -> new CraftingAltarT1Block(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE).noOcclusion()));

    public static final RegistryObject<LiquidBlock> LIQUID_MANA_BLOCK = BLOCKS.register("liquid_mana_block",
            () -> new LiquidBlock(ModFluids.SOURCE_LIQUID_MANA, BlockBehaviour.Properties.copy(Blocks.WATER)
                    .noLootTable()
                    .lightLevel((state) -> 8)
            ));



    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void  register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
