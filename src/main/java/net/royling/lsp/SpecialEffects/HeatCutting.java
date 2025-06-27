package net.royling.lsp.SpecialEffects;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HeatCutting extends SpecialEffect {
    private static final float BASE_PROBABILITY = 0.4f;   // 40%基础概率
    private static final float BOOSTED_PROBABILITY = 0.8f; // 80%强化概率
    private static final int SOUL_THRESHOLD = 2000;
    public HeatCutting() {
        super(20);
    }
    @SubscribeEvent
    public static void onSlashBladeHit(SlashBladeEvent.HitEvent event){
        if (!event.getSlashBladeState().hasSpecialEffect(SERegistry.HEAT_CUTTING_20.getId())) {
            return;
        }
        if (!(event.getUser() instanceof Player player)) {
            return;
        }
        int level = player.experienceLevel;
        ISlashBladeState state = event.getSlashBladeState();
        if (!SpecialEffect.isEffective(SERegistry.HEAT_CUTTING_20.get(), player.experienceLevel)) {
            return;
        }
        int soul = state.getProudSoulCount();
        float probability = (soul > SOUL_THRESHOLD) ? BOOSTED_PROBABILITY : BASE_PROBABILITY;
        int amplifier = (soul > SOUL_THRESHOLD) ? 1 : 0;  // 0=减速I, 1=减速II
        int duration = (soul > SOUL_THRESHOLD) ? 120 : 80; // 不同持续时间
        event.getTarget().addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                duration,
                amplifier
        ));
        System.out.println("触发热切割SE");
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LAVA_POP, SoundSource.PLAYERS,
                0.5F, 1.2F + player.getRandom().nextFloat() * 0.4F);
    }
}
