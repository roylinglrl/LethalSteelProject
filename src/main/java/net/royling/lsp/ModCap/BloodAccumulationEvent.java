package net.royling.lsp.ModCap;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.royling.lsp.LethalSlashProject;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = LethalSlashProject.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BloodAccumulationEvent {
    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity && !event.getObject().getCapability(ModCapabilities.BLOOD_ACCUMULATION_CAPABILITY).isPresent()) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(LethalSlashProject.MOD_ID, "blood_accumulation"), new BloodAccumulationProvider());
        }
    }
    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if (event.isWasDeath()) { // 确保是死亡后重生，而不是简单的尺寸改变
            event.getOriginal().getCapability(ModCapabilities.BLOOD_ACCUMULATION_CAPABILITY).ifPresent(oldCap -> {
                event.getEntity().getCapability(ModCapabilities.BLOOD_ACCUMULATION_CAPABILITY).ifPresent(newCap -> {
                    newCap.setAccumulation(oldCap.getAccumulation());
                    newCap.setLastAttackerUUID(oldCap.getLastAttackerUUID()); // 复制攻击者UUID
                });
            });
        }
    }
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        event.getEntity().getCapability(ModCapabilities.BLOOD_ACCUMULATION_CAPABILITY).ifPresent(IBloodAccumulation::clearAccumulation);
    }
    private static final Vector3f BLOOD_COLOR = new Vector3f(0.7f, 0.0f, 0.0f);
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof LivingEntity && !event.getEntity().level().isClientSide()) {
            LivingEntity entity = event.getEntity();
            entity.getCapability(ModCapabilities.BLOOD_ACCUMULATION_CAPABILITY).ifPresent(bloodAccum -> {

                if (bloodAccum.getAccumulation() >= 120 && entity.invulnerableTime == 0) {
                    float maxHealth = entity.getMaxHealth();
                    float bleedDamage = maxHealth * 0.15F + 8.0F;
                    UUID lastAttackerUUID = bloodAccum.getLastAttackerUUID();
                    Entity lastAttacker = null;
                    if (lastAttackerUUID != null) {
                        if(entity.level() instanceof ServerLevel serverLevel){
                           lastAttacker = serverLevel.getEntity(lastAttackerUUID);
                        }
                    }
                    if (lastAttacker != null) {
                        DamageSource source = lastAttacker.damageSources().generic();
                        entity.hurt(source, bleedDamage);
                        entity.invulnerableTime=0;
                    }
                    else {
                        DamageSource source = entity.damageSources().generic();
                        entity.hurt(source, bleedDamage);
                        entity.invulnerableTime=0;
                    }
                    if (entity.level() instanceof ServerLevel serverLevel) {
                        double x = entity.getX();
                        double y = entity.getY() + entity.getBbHeight() * 0.5;
                        double z = entity.getZ();
                        // 创建红石粒子选项 (深红色血液)
                        DustParticleOptions bloodParticle = new DustParticleOptions(
                                BLOOD_COLOR, // RGB颜色 (0.7, 0.0, 0.0) = 深红色
                                1.0f         // 粒子大小
                        );
                        // 核心喷血效果 (使用红石粒子)
                        int particleCount = Math.min(bloodAccum.getAccumulation() / 5, 60);
                        serverLevel.sendParticles(
                                bloodParticle, // 使用自定义的红石粒子
                                x, y, z,
                                particleCount,
                                entity.getBbWidth() * 0.7,
                                entity.getBbHeight() * 0.3,
                                entity.getBbWidth() * 0.7,
                                0.15
                        );
                        // 血液滴落效果 (使用红石粒子)
                        serverLevel.sendParticles(
                                bloodParticle,
                                x, y - entity.getBbHeight() * 0.25, z,
                                20,
                                entity.getBbWidth() * 0.4,
                                0.1,
                                entity.getBbWidth() * 0.4,
                                0.05
                        );
                    }
                    bloodAccum.clearAccumulation();
                }
                int decayRate = 1;
                if(entity.tickCount%5==0) {
                    if (bloodAccum.getAccumulation() > 0) {
                        bloodAccum.addAccumulation(-decayRate);
                    }
                }
            });
        }
    }
}
