package net.royling.lsp.SpecialEffects;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.royling.lsp.ModCap.ModCapabilities;

@Mod.EventBusSubscriber
public class DualBladeMain extends SpecialEffect {
    public DualBladeMain() {
        super(5);
    }
    @SubscribeEvent
    public static void onSlashBladeHit(SlashBladeEvent.HitEvent event) {
        // 检查主手武器是否有DualBladeMain效果
        if (!event.getSlashBladeState().hasSpecialEffect(SERegistry.DUAL_BLADE_MAIN.getId())) {
            return;
        }
        if (!(event.getUser() instanceof Player player)) {
            return;
        }
        // 检查副手武器是否有DualBladeoff效果
        ItemStack offhandItem = player.getOffhandItem();
        if (!(offhandItem.getItem() instanceof ItemSlashBlade)) {
            return;
        }
        ISlashBladeState offhandState = offhandItem.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (!offhandState.hasSpecialEffect(SERegistry.DUAL_BLADE_OFF.getId())) {
            return;
        }
        // 根据力量buff决定出血计数
        int bleedingCount = player.hasEffect(MobEffects.DAMAGE_BOOST) ? 45 : 30;
        // 应用出血效果
        LivingEntity target = event.getTarget();
        target.invulnerableTime = 0;
        target.getCapability(ModCapabilities.BLOOD_ACCUMULATION_CAPABILITY).ifPresent(bloodAccum -> {
            bloodAccum.addAccumulation(bleedingCount);
            bloodAccum.setLastAttackerUUID(player.getUUID());
        });
    }
}
