package net.royling.lsp.SpecialEffects;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.royling.lsp.ModCap.ModCapabilities;

@Mod.EventBusSubscriber
public class BleedingCutting extends SpecialEffect {
    public BleedingCutting() {
        super(20);
    }
    @SubscribeEvent
    public static void onSlashBladeHit(SlashBladeEvent.HitEvent event){
        if (!event.getSlashBladeState().hasSpecialEffect(SERegistry.BLEEDING_CUTTING.getId())) {
            return;
        }
        if (!(event.getUser() instanceof Player player)) {
            return;
        }
        ISlashBladeState state = event.getSlashBladeState();
        if (!SpecialEffect.isEffective(SERegistry.BLEEDING_CUTTING.get(), player.experienceLevel)) {
            return;
        }
        int soul = state.getProudSoulCount();
        int bleedingCount = (soul > 2000) ? 45 : 30;
        LivingEntity target = event.getTarget();
        target.invulnerableTime=0;
        if(target.getCapability(ModCapabilities.BLOOD_ACCUMULATION_CAPABILITY).isPresent()) {
            target.getCapability(ModCapabilities.BLOOD_ACCUMULATION_CAPABILITY).ifPresent(bloodAccum -> {
                bloodAccum.addAccumulation(bleedingCount);
                bloodAccum.setLastAttackerUUID(event.getUser().getUUID());
            });
        }
    }
}
