package net.royling.lsp.ModCap;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.royling.lsp.LethalSlashProject;

@Mod.EventBusSubscriber(modid = LethalSlashProject.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCapabilities {
    // 声明 Capability 实例
    public static Capability<IBloodAccumulation> BLOOD_ACCUMULATION_CAPABILITY = CapabilityManager.get(new CapabilityToken<IBloodAccumulation>() {});
    // 在注册事件中注册 Capability
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IBloodAccumulation.class);
    }
}
