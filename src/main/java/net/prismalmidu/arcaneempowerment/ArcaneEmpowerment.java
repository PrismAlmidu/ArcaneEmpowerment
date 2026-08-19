package net.prismalmidu.arcaneempowerment;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.block.entity.ModBlockEntities;
import net.prismalmidu.arcaneempowerment.fluid.ModFluidTypes;
import net.prismalmidu.arcaneempowerment.fluid.ModFluids;
import net.prismalmidu.arcaneempowerment.item.ModCreativeModeTabs;
import net.prismalmidu.arcaneempowerment.item.ModItems;
import net.prismalmidu.arcaneempowerment.recipe.ModRecipes;
import net.prismalmidu.arcaneempowerment.screen.CraftingAltarT1Screen;
import net.prismalmidu.arcaneempowerment.screen.CraftingAltarT2Screen;
import net.prismalmidu.arcaneempowerment.screen.ModMenuTypes;
import net.prismalmidu.arcaneempowerment.screen.VoidMinerT2Screen;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ArcaneEmpowerment.MOD_ID)
public class ArcaneEmpowerment
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "arcaneempowerment";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public ArcaneEmpowerment(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            MenuScreens.register(ModMenuTypes.CRAFTING_ALTAR_T1_MENU.get(), CraftingAltarT1Screen::new);
            MenuScreens.register(ModMenuTypes.CRAFTING_ALTAR_T2_MENU.get(), CraftingAltarT2Screen::new);

            MenuScreens.register(ModMenuTypes.VOID_MINER_T2_MENU.get(), VoidMinerT2Screen::new);

            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_LIQUID_MANA.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_LIQUID_MANA.get(), RenderType.translucent());
        }
    }
}
