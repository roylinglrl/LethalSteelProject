package net.royling.lsp;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.royling.lsp.SlashArt.ModComboStates;
import net.royling.lsp.SlashArt.SARegistries;
import net.royling.lsp.SpecialEffects.SERegistry;
import net.royling.lsp.datagenerator.ModBlades;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LethalSlashProject.MOD_ID)
public class LethalSlashProject
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "lethal_slash_project";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public LethalSlashProject(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        ModBlades.registerBlades();
        modEventBus.addListener(this::commonSetup);
        SARegistries.SLASH_ARTS.register(modEventBus);
        SERegistry.SPECIAL_EFFECTS.register(modEventBus);
        ModComboStates.COMBO_STATE.register(modEventBus);
    }
    private void commonSetup(final FMLCommonSetupEvent event)
    {

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
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
