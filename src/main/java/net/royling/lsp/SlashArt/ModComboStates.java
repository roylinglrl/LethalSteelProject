package net.royling.lsp.SlashArt;

import mods.flammpfeil.slashblade.SlashBlade;

import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.event.FallHandler;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.Drive;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;



    public class ModComboStates {
        public static final DeferredRegister<ComboState> COMBO_STATE = DeferredRegister.create(ComboState.REGISTRY_KEY,
                SlashBlade.MODID);
        // 获取刀剑颜色
        private static int getBladeColor(LivingEntity entity, int defaultColor) {
            return entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                    .map(ISlashBladeState::getColorCode)
                    .orElse(defaultColor);
        }
        // 播放声音
        private static void playSound(LivingEntity entity, SoundEvent sound, float volume, float pitch) {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
        }
        // 通用斩击方法
        private static void performSlash(LivingEntity entity, float roll, int color, float damage, KnockBacks knockback) {
            if (entity.level().isClientSide()) return;
            AttackManager.doSlash(
                    entity,
                    roll,
                    color,
                    new Vec3(0, 0.5, 0),
                    false,
                    true,
                    damage,
                    knockback
            );
        }
        // 创建随机斩击 (用于天道回返雪月花)
        private static void createRandomSlash(LivingEntity entity, int baseTime, int currentTick) {
            int tickOffset = currentTick - baseTime;
            if (entity.level().isClientSide()) return;

            RandomSource rand = entity.getRandom();
            int colorCode = rand.nextInt(0xFFFFFF + 1);
            float randomYaw = entity.getYRot() + rand.nextFloat() * 180 - 90;

            float visualOffsetX = rand.nextFloat() * 2.0F - 1.5F;
            float visualOffsetZ = 1.0F + (rand.nextFloat() * 0.5F - 0.25F);

            Vec3 lookVec = entity.getLookAngle().normalize();
            Vec3 rightVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();
            Vec3 forwardVec = lookVec.cross(rightVec).normalize();

            Vec3 customParticleSpawnPos = entity.position()
                    .add(0, entity.getBbHeight() / 2.0F, 0)
                    .add(rightVec.scale(visualOffsetX))
                    .add(forwardVec.scale(visualOffsetZ));

            performSlash(entity, randomYaw, colorCode, 0.5F, KnockBacks.toss);

            if (entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        customParticleSpawnPos.x(), customParticleSpawnPos.y(), customParticleSpawnPos.z(),
                        1, 0.0, 0.0, 0.0, 0.0);
            }

            if (tickOffset == 0) {
                playSound(entity, SoundEvents.PLAYER_ATTACK_SWEEP, 0.3F, 1.2F);
            }
        }
        //构造方法 直斩
        private static void performCrossSlash(LivingEntity entity, float roll) {
            if (entity.level().isClientSide()) return;
            // 获取刀剑颜色
            int colorCode = entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                    .map(ISlashBladeState::getColorCode)
                    .orElseGet(() -> 0xFFFFFF); // 默认白色
            // 创建十字斩击
            AttackManager.doSlash(
                    entity,             // 施放者
                    roll, // 旋转角度
                    colorCode,          // 颜色
                    new Vec3(0, 0.5, 0), // 生成位置偏移
                    false,              // 是否静音
                    true,               // 是否暴击
                    1.0f,              // 伤害倍率
                    KnockBacks.toss // 击退效果
            );
        }
        //构造方法 十字波光
        private static void releaseCrossWave(LivingEntity entity) {
            if (entity.level().isClientSide()) return;

            int colorCode = getBladeColor(entity, 0x00BFFF);
            float yRot = entity.getYRot();

            Drive.doSlash(entity, yRot, 60, colorCode, new Vec3(0, 0.5, 0), false, 1.0, KnockBacks.smash, 1.5f);
            Drive.doSlash(entity, yRot + 90, 60, colorCode, new Vec3(0, 0.5, 0), false, 1.0, KnockBacks.smash, 1.5f);
            Drive.doSlash(entity, yRot + 180, 60, colorCode, new Vec3(0, 0.5, 0), false, 1.0, KnockBacks.smash, 1.5f);
            Drive.doSlash(entity, yRot - 90, 60, colorCode, new Vec3(0, 0.5, 0), false, 1.0, KnockBacks.smash, 1.5f);
            if (entity.level() instanceof ServerLevel serverLevel) {
                Vec3 pos = entity.position().add(0, entity.getBbHeight() / 2.0F, 0);
                serverLevel.sendParticles(ParticleTypes.CRIT, pos.x, pos.y, pos.z, 20, 0.5, 0.5, 0.5, 0.1);
            }
            playSound(entity, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 0.9F, 1.0F);
        }
        /**
         * 创建基础幻影剑
         * @param player 施放玩家
         * @param powerLevel 力量附魔等级
         * @param colorCode 颜色代码
         * @param targetPos 目标位置（可为空）
         */
        public static void createSummonedSword(ServerPlayer player, double powerLevel, int colorCode, @Nullable Vec3 targetPos) {
            Level world = player.level();
            // 计算目标位置
            Vec3 finalTargetPos = (targetPos != null) ? targetPos :
                    player.getEyePosition().add(player.getLookAngle().scale(40));
            // 创建幻影剑实体
            EntityAbstractSummonedSword sword = new EntityAbstractSummonedSword(
                    SlashBlade.RegistryEvents.SummonedSword,
                    world
            );
            // 设置位置（玩家两侧交替生成）
            boolean sided = player.getRandom().nextBoolean();
            Vec3 spawnPos = player.getEyePosition()
                    .add(VectorHelper.getVectorForRotation(0.0f, player.getYRot() + 90)
                            .scale(sided ? 1 : -1));
            sword.setPos(spawnPos);
            sword.setOwner(player);
            sword.setColor(colorCode);
            sword.setDamage(powerLevel);
            sword.setRoll(player.getRandom().nextFloat() * 360.0f);
            // 设置发射方向
            Vec3 direction = finalTargetPos.subtract(spawnPos).normalize();
            sword.shoot(direction.x, direction.y, direction.z, 3.0f, 0.0f);
            // 添加到世界
            world.addFreshEntity(sword);
            player.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
        }
        /**
         * 创建圆环幻影剑（围绕玩家）
         * @param player 施放玩家
         * @param powerLevel 力量附魔等级
         * @param colorCode 颜色代码
         * @param count 数量（6或8）
         */
        public static void createSpiralSwords(ServerPlayer player, int powerLevel, int colorCode, int count) {
            Level world = player.level();

            for (int i = 0; i < count; i++) {
                EntitySpiralSwords sword = new EntitySpiralSwords(
                        SlashBlade.RegistryEvents.SpiralSwords,
                        world
                );
                sword.setPos(player.position());
                sword.setOwner(player);
                sword.setColor(colorCode);
                sword.setDamage(powerLevel);
                sword.startRiding(player, true); // 附着在玩家身上
                sword.setDelay(360 / count * i); // 设置旋转偏移

                world.addFreshEntity(sword);
            }
            player.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
        }
        /**
         * 创建烈风环影剑（围绕目标）
         * @param player 施放玩家
         * @param target 目标实体
         * @param powerLevel 力量附魔等级
         * @param colorCode 颜色代码
         * @param count 数量（6或8）
         */
        public static void createStormSwords(ServerPlayer player, Entity target, int powerLevel, int colorCode, int count) {
            Level world = player.level();
            for (int i = 0; i < count; i++) {
                EntityStormSwords sword = new EntityStormSwords(
                        SlashBlade.RegistryEvents.StormSwords,
                        world
                );
                sword.setPos(player.position());
                sword.setOwner(player);
                sword.setColor(colorCode);
                sword.setDamage(powerLevel);
                sword.startRiding(target, true); // 附着在目标身上
                sword.setDelay(360 / count * i); // 设置旋转偏移

                world.addFreshEntity(sword);
            }
            player.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
        }
        /**
         * 创建急袭幻影剑
         * @param player 施放玩家
         * @param powerLevel 力量附魔等级
         * @param colorCode 颜色代码
         * @param count 数量（6或8）
         */
        public static void createBlisteringSwords(ServerPlayer player, int powerLevel, int colorCode, int count) {
            Level world = player.level();
            for (int i = 0; i < count; i++) {
                EntityBlisteringSwords sword = new EntityBlisteringSwords(
                        SlashBlade.RegistryEvents.BlisteringSwords,
                        world
                );
                sword.setPos(player.position());
                sword.setOwner(player);
                sword.setColor(colorCode);
                sword.setDamage(powerLevel);
                sword.startRiding(player, true); // 附着在玩家身上
                sword.setDelay(i); // 设置延迟

                world.addFreshEntity(sword);
            }
            player.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
        }
        /**
         * 创建五月雨幻影剑（从空中降下）
         * @param player 施放玩家
         * @param powerLevel 力量附魔等级
         * @param colorCode 颜色代码
         * @param basePos 基础位置（通常是目标上空）
         * @param count 主剑数量
         * @param multiplier 每把主剑的分裂数量
         */
        public static void createHeavyRainSwords(ServerPlayer player, int powerLevel, int colorCode,
                                                 Vec3 basePos, int count, int multiplier) {
            Level world = player.level();
            // 创建主剑（中心位置）
            EntityHeavyRainSwords mainSword = new EntityHeavyRainSwords(
                    SlashBlade.RegistryEvents.HeavyRainSwords,
                    world
            );

            mainSword.setPos(basePos);
            mainSword.setOwner(player);
            mainSword.setColor(colorCode);
            mainSword.setDamage(powerLevel);
            mainSword.startRiding(player, true);
            mainSword.setXRot(-90); // 垂直向下
            world.addFreshEntity(mainSword);

            // 创建分裂剑
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < multiplier; j++) {
                    EntityHeavyRainSwords sword = new EntityHeavyRainSwords(
                            SlashBlade.RegistryEvents.HeavyRainSwords,
                            world
                    );

                    sword.setOwner(player);
                    sword.setColor(colorCode);
                    sword.setDamage(powerLevel);
                    sword.startRiding(player, true);
                    sword.setDelay(i);
                    sword.setSpread(basePos); // 设置散布位置
                    sword.setXRot(-90); // 垂直向下

                    world.addFreshEntity(sword);
                }
            }
            player.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
        }
        //坠地米字斩
        //region
        public static final RegistryObject<ComboState> GROUND_POUND_START = COMBO_STATE.register("ground_pound_start",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(1000, 1005)
                        .priority(60)
                        .aerial()
                        .nextOfTimeout(entity -> SlashBlade.prefix("ground_pound_impact"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    entity.setDeltaMovement(entity.getDeltaMovement().x, -5.8, entity.getDeltaMovement().z);
                                    playSound(entity, SoundEvents.PLAYER_ATTACK_SWEEP, 0.5F, 0.8F);
                                })
                                .build())
                        .build());
        public static final RegistryObject<ComboState> GROUND_POUND_IMPACT = COMBO_STATE.register("ground_pound_impact",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(1006, 1010) // 假设动画帧范围，非常短暂
                        .priority(70) // 优先级更高
                        .aerial() // 标记为空中技能
                        .next(entity -> SlashBlade.prefix("ground_pound_recovery")) // 冲击后进入恢复阶段
                        .nextOfTimeout(entity -> SlashBlade.prefix("ground_pound_recovery"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    if (entity.onGround()) {
                                        AttackManager.areaAttack(entity, KnockBacks.toss.action, 2.5f, true, true, true); // 6倍伤害乘数
                                        int colorCode = entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                                                .map(ISlashBladeState::getColorCode)
                                                .orElseGet(() -> 16777215);
                                        for (float angle = 0; angle < 360; angle += 45) {
                                            AttackManager.doSlash(
                                                    entity,         // playerIn: 攻击者
                                                    angle,          // roll: 斩击的角度 (绕Z轴旋转)
                                                    colorCode,      // colorCode: 刀光颜色，从刀剑获取
                                                    new Vec3(0, 0, 0), // centerOffset: 刀光相对于玩家的偏移
                                                    false,          // mute: 是否静音
                                                    false,          // critical: 是否暴击
                                                    -2147483647,            // damage: 伤害 这里设置为-21亿只显示效果不造成伤害
                                                    KnockBacks.toss // knockback: 击退效果，这里使用之前定义的 toss
                                            );
                                        }
                                        entity.level().addParticle(ParticleTypes.EXPLOSION, entity.getX(), entity.getY(), entity.getZ(), 5, 0.5, 0.1);
                                        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                                net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                                    }
                                })
                                .build())
                        .addHitEffect((target, user) -> StunManager.setStun(target, 20)) // 命中目标施加短暂眩晕
                        .build());

        public static final RegistryObject<ComboState> GROUND_POUND_RECOVERY = COMBO_STATE.register("ground_pound_recovery",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(1011, 1020) // 假设动画帧范围
                        .priority(50)
                        .next(entity -> SlashBlade.prefix("none")) // 恢复后回到无状态
                        .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(entity -> {
                        })
                        .build());
        //endregion
        // 天字十三杀 (Heaven's Thirteen Slashes)
        //region
        public static final RegistryObject<ComboState> HEAVENS_SLASH_START = COMBO_STATE.register("heavens_slash_start",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(2000, 2005)
                        .priority(60)
                        .next(entity -> SlashBlade.prefix("heavens_slash_attack"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("heavens_slash_attack"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> playSound(entity, SoundEvents.PLAYER_ATTACK_SWEEP, 0.7F, 1.0F))
                                .build())
                        .build());
        public static final RegistryObject<ComboState> HEAVENS_SLASH_ATTACK = COMBO_STATE.register("heavens_slash_attack",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(2006, 2042)
                        .priority(65)
                        .nextOfTimeout(entity -> SlashBlade.prefix("heavens_slash_recovery"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> createRandomSlash(entity, 2006, 0))
                                .put(3, entity -> createRandomSlash(entity, 2006, 3))
                                .put(6, entity -> createRandomSlash(entity, 2006, 6))
                                .put(9, entity -> createRandomSlash(entity, 2006, 9))
                                .put(12, entity -> createRandomSlash(entity, 2006, 12))
                                .put(15, entity -> createRandomSlash(entity, 2006, 15))
                                .put(18, entity -> createRandomSlash(entity, 2006, 18))
                                .put(21, entity -> createRandomSlash(entity, 2006, 21))
                                .put(24, entity -> createRandomSlash(entity, 2006, 24))
                                .put(27, entity -> createRandomSlash(entity, 2006, 27))
                                .put(30, entity -> createRandomSlash(entity, 2006, 30))
                                .put(33, entity -> createRandomSlash(entity, 2006, 33))
                                .put(36, entity -> createRandomSlash(entity, 2006, 36))
                                .build())
                        .build());

        public static final RegistryObject<ComboState> HEAVENS_SLASH_RECOVERY = COMBO_STATE.register("heavens_slash_recovery",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(2043, 2056) // Animation frames for recovery
                        .priority(50)
                        .next(entity -> SlashBlade.prefix("none")) // Return to no state
                        .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(entity -> {
                            // Any recovery actions or animations
                        })
                        .build());
        //endregion
    // 热能波光 (Thermal Wave Beam)
    //region
    public static final RegistryObject<ComboState> THERMAL_WAVE_START = COMBO_STATE.register("thermal_wave_start",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1595, 1600) // 起始动画帧
                    .priority(60)
                    .next(entity -> SlashBlade.prefix("thermal_wave_attack")) // 进入攻击阶段
                    .nextOfTimeout(entity -> SlashBlade.prefix("thermal_wave_attack"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> {
                                // 起始音效和特效
                                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                        SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 0.7F);

                                if (entity.level() instanceof ServerLevel serverLevel) {
                                    Vec3 pos = entity.position().add(0, 1.0, 0);
                                    serverLevel.sendParticles(ParticleTypes.FLAME,
                                            pos.x, pos.y, pos.z,
                                            20,
                                            0.5, 0.5, 0.5,
                                            0.1);
                                }
                            })
                            .build())
                    .build());
    public static final RegistryObject<ComboState> THERMAL_WAVE_ATTACK = COMBO_STATE.register("thermal_wave_attack",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1600, 1638) // 攻击阶段持续20刻 (1秒)
                    .priority(65)
                    .nextOfTimeout(entity -> SlashBlade.prefix("thermal_wave_recovery"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(1, entity -> releaseThermalBeam(entity, 0))   // 第一道剑气 - 0°
                            .put(6, entity -> releaseThermalBeam(entity, 45))  // 第二道剑气 - 45°
                            .put(11, entity -> releaseThermalBeam(entity, 90)) // 第三道剑气 - 90°
                            .put(16, entity -> releaseThermalBeam(entity, 135))// 第四道剑气 - 135°
                            .build())
                    .build());
    public static final RegistryObject<ComboState> THERMAL_WAVE_RECOVERY = COMBO_STATE.register("thermal_wave_recovery",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1638, 1645) // 恢复动画帧
                    .priority(50)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(entity -> {
                        // 恢复动作
                        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 1.0F);
                    })
                    .build());

    // 热能波光释放方法
    private static void releaseThermalBeam(LivingEntity entity, float rotationOffset) {
        if (entity.level().isClientSide()) return;
        int colorCode = getBladeColor(entity, 0xFF4500);
        float rotation = entity.getYRot() + rotationOffset;
        Drive.doSlash(
                entity,
                rotation,
                40,
                new Vec3(0, 0.5, 0),
                false,
                2.0,
                KnockBacks.toss,
                1.8f
        );
        if (entity.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = entity.position()
                    .add(entity.getLookAngle().scale(2.0))
                    .add(0, 1.5, 0);
            serverLevel.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 15, 0.2, 0.2, 0.2, 0.05);
            serverLevel.sendParticles(ParticleTypes.LAVA, pos.x, pos.y, pos.z, 5, 0.3, 0.3, 0.3, 0.0);
        }
        playSound(entity, SoundEvents.FIRECHARGE_USE, 0.8F, 0.9F + entity.getRandom().nextFloat() * 0.2F);
    }
    //endregion
    // 水鸟乱舞 (Waterfowl Dance)
    //region
    public static final RegistryObject<ComboState> WATERFOWL_DANCE_START = COMBO_STATE.register("waterfowl_dance_start",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(2000, 2005) // 起始动画帧
                    .priority(70)
                    .next(entity -> SlashBlade.prefix("waterfowl_dance_wave1"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("waterfowl_dance_wave1")) // 修复：添加超时状态
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> {
                                // 起始音效
                                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                        SoundEvents.TRIDENT_RIPTIDE_2, SoundSource.PLAYERS, 1.0F, 1.2F);

                                // 起始粒子效果
                                if (entity.level() instanceof ServerLevel serverLevel) {
                                    Vec3 pos = entity.position().add(0, 1.0, 0);
                                    serverLevel.sendParticles(ParticleTypes.BUBBLE,
                                            pos.x, pos.y, pos.z,
                                            30,
                                            1.0, 0.5, 1.0,
                                            0.1);
                                }
                            })
                            .build())
                    .build());

    // 第一波浪
    public static final RegistryObject<ComboState> WATERFOWL_DANCE_WAVE1 = COMBO_STATE.register("waterfowl_dance_wave1",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(2006, 2021) // 动画帧不变，仍为15刻的波浪
                    .priority(75)
                    .next(entity -> SlashBlade.prefix("waterfowl_dance_pause1"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("waterfowl_dance_pause1")) // 修复：添加超时状态
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2,entity ->  ModComboStates.moveForward(entity,0.75f))
                            .put(4, entity -> performWaterfowlSlash(entity, 0))   // 第1刀 & **第一次移动** (距离增加)
                            .put(6, entity -> performWaterfowlSlash(entity, 30))  // 第2刀
                            .put(8, entity -> performWaterfowlSlash(entity, 60))  // 第3刀
                            .put(10, entity -> performWaterfowlSlash(entity, 90))  // 第4刀
                            .put(12, entity -> performWaterfowlSlash(entity, 120)) // 第5刀
                            .put(14, entity -> performWaterfowlSlash(entity, 150)) // 第6刀
                            .build())
                    .build());

    // 第一波后暂停
    public static final RegistryObject<ComboState> WATERFOWL_DANCE_PAUSE1 = COMBO_STATE.register("waterfowl_dance_pause1",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(2022, 2032) // 5刻暂停 -> 10刻 (翻倍延长)
                    .priority(60)
                    .next(entity -> SlashBlade.prefix("waterfowl_dance_wave2"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("waterfowl_dance_wave2")) // 修复：添加超时状态
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> {
                                // 暂停音效
                                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                        SoundEvents.WATER_AMBIENT, SoundSource.PLAYERS, 0.5F, 1.5F);
                            })
                            .build())
                    .build());

    // 第二波浪
    public static final RegistryObject<ComboState> WATERFOWL_DANCE_WAVE2 = COMBO_STATE.register("waterfowl_dance_wave2",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(2033, 2048) // 动画帧不变，仍为15刻的波浪
                    .priority(75)
                    .next(entity -> SlashBlade.prefix("waterfowl_dance_pause2"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("waterfowl_dance_pause2")) // 修复：添加超时状态
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, entity ->  ModComboStates.moveForward(entity,0.75f))
                            .put(4, entity -> performWaterfowlSlash(entity, 180))   // 第7刀 & **第二次移动** (距离增加)
                            .put(6, entity -> performWaterfowlSlash(entity, 210))  // 第8刀
                            .put(8, entity -> performWaterfowlSlash(entity, 240))  // 第9刀
                            .put(10, entity -> performWaterfowlSlash(entity, 270))  // 第10刀
                            .put(12, entity -> performWaterfowlSlash(entity, 300)) // 第11刀
                            .put(14, entity -> performWaterfowlSlash(entity, 330)) // 第12刀
                            .build())
                    .build());

    // 第二波后暂停
    public static final RegistryObject<ComboState> WATERFOWL_DANCE_PAUSE2 = COMBO_STATE.register("waterfowl_dance_pause2",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(2049, 2059) // 5刻暂停 -> 10刻 (翻倍延长)
                    .priority(60)
                    .next(entity -> SlashBlade.prefix("waterfowl_dance_wave3"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("waterfowl_dance_wave3")) // 修复：添加超时状态
                    .addTickAction(FallHandler::fallDecrease)
                    .build());

    // 第三波浪
    public static final RegistryObject<ComboState> WATERFOWL_DANCE_WAVE3 = COMBO_STATE.register("waterfowl_dance_wave3",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(2060, 2075) // 动画帧不变，仍为15刻的波浪
                    .priority(75)
                    .next(entity -> SlashBlade.prefix("waterfowl_dance_recovery"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("waterfowl_dance_recovery")) // 修复：添加超时状态
                    .addTickAction(FallHandler::fallDecrease)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, entity -> performWaterfowlSlash(entity, 0))    // 第13刀 & **第三次移动** (距离增加)
                            .put(4, entity -> performWaterfowlSlash(entity, 45))   // 第14刀
                            .put(6, entity -> performWaterfowlSlash(entity, 90))   // 第15刀
                            .put(8, entity -> performWaterfowlSlash(entity, 135))  // 第16刀
                            .put(10, entity -> performWaterfowlSlash(entity, 180))  // 第17刀
                            .put(12, entity -> performWaterfowlSlash(entity, 225)) // 第18刀
                            .build())
                    .build());
    // 恢复状态
    public static final RegistryObject<ComboState> WATERFOWL_DANCE_RECOVERY = COMBO_STATE.register("waterfowl_dance_recovery",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(2076, 2091) // 恢复动画帧不变，仍为15刻
                    .priority(50)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none")) // 修复：添加超时状态
                    .addTickAction(FallHandler::fallDecrease)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> {
                                // 结束音效
                                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                        SoundEvents.TRIDENT_RIPTIDE_3, SoundSource.PLAYERS, 1.0F, 0.8F);

                                // 结束粒子效果
                                if (entity.level() instanceof ServerLevel serverLevel) {
                                    Vec3 pos = entity.position().add(0, 1.0, 0);
                                    serverLevel.sendParticles(ParticleTypes.SPLASH,
                                            pos.x, pos.y, pos.z,
                                            30,
                                            1.0, 0.5, 1.0,
                                            0.2);
                                }
                            })
                            .build())
                    .build());
    // 水鸟乱舞挥砍执行方法
    private static void performWaterfowlSlash(LivingEntity entity, float roll) {
        if (entity.level().isClientSide()) return;
        int colorCode = getBladeColor(entity, 0x1E90FF);
        performSlash(entity, roll, colorCode, 1.0f, KnockBacks.toss);

        if (entity.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = entity.position()
                    .add(entity.getLookAngle().scale(1.5))
                    .add(0, 1.0, 0);
            serverLevel.sendParticles(ParticleTypes.BUBBLE, pos.x, pos.y, pos.z, 10, 0.5, 0.2, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.GLOW, pos.x, pos.y, pos.z, 5, 0.3, 0.3, 0.3, 0.0);
        }
        playSound(entity, SoundEvents.TRIDENT_THROW, 0.5F, 1.0F + entity.getRandom().nextFloat() * 0.3F);
    }

    // 向前移动方法
    private static void moveForward(LivingEntity entity, float speedCount) {
        // 获取玩家视线方向
        Vec3 lookVec = entity.getLookAngle();
        // 设置向前移动的速度
        float speed = 0.8f * (float) 1.5 * speedCount; // 基础速度 0.8f * strength，strength 越大移动越远
        entity.setDeltaMovement(lookVec.x * speed, 0.1, lookVec.z * speed);

        // 确保玩家不会飞起来
        if (!entity.isNoGravity()) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.05, 0));
        }

        // 移动粒子效果
        if (entity.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = entity.position().add(0, 0.1, 0);
            serverLevel.sendParticles(ParticleTypes.SPLASH,
                    pos.x, pos.y, pos.z,
                    5,
                    0.2, 0.0, 0.2,
                    0.05);
        }
    }
        // 向后移动方法
        private static void moveBack(LivingEntity entity, float speedCount) {
            // 获取玩家视线方向
            Vec3 lookVec = entity.getLookAngle();
            // 设置向前移动的速度
            float speed = 0.8f * (float) 1.5 * speedCount; // 基础速度 0.8f * strength，strength 越大移动越远
            entity.setDeltaMovement(-lookVec.x * speed, 0.1, -lookVec.z * speed);

            // 确保玩家不会飞起来
            if (!entity.isNoGravity()) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.05, 0));
            }

            // 移动粒子效果
            if (entity.level() instanceof ServerLevel serverLevel) {
                Vec3 pos = entity.position().add(0, 0.1, 0);
                serverLevel.sendParticles(ParticleTypes.SPLASH,
                        pos.x, pos.y, pos.z,
                        5,
                        0.2, 0.0, 0.2,
                        0.05);
            }
        }
    //endregion
    // 十字斩 (Cross Slash)
    //region
    public static final RegistryObject<ComboState> CROSS_SLASH_START = COMBO_STATE.register("cross_slash_start",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1600, 1605) // 起始动画帧
                    .priority(60)
                    .next(entity -> SlashBlade.prefix("cross_slash_attack")) // 进入攻击阶段
                    .nextOfTimeout(entity -> SlashBlade.prefix("cross_slash_attack"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(1, entity -> {
                                // 起始音效
                                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.7F, 1.2F);
                            })
                            .build())
                    .build());

    public static final RegistryObject<ComboState> CROSS_SLASH_ATTACK = COMBO_STATE.register("cross_slash_attack",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1606, 1615)
                    .priority(65)
                    .next(entity -> SlashBlade.prefix("cross_slash_recovery"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("cross_slash_recovery"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> performSlash(entity, 105, getBladeColor(entity, 0xFFFFFF), 1f, KnockBacks.toss))
                            .put(3, entity -> performSlash(entity, 15, getBladeColor(entity, 0xFFFFFF), 1f, KnockBacks.toss))
                            .build())
                    .build());
    public static final RegistryObject<ComboState> CROSS_SLASH_RECOVERY = COMBO_STATE.register("cross_slash_recovery",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1616, 1620) // 恢复阶段
                    .priority(50)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(FallHandler::fallResist)
                    .build());
    //endregion
    // 十字波光斩 (Cross Wave Slash)
    //region
    public static final RegistryObject<ComboState> CROSS_WAVE_SLASH_START = COMBO_STATE.register("cross_wave_slash_start",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(3000, 3005) // Animation frames for starting
                    .priority(60)
                    .next(entity -> SlashBlade.prefix("cross_wave_slash_attack")) // Transition to attack state
                    .nextOfTimeout(entity -> SlashBlade.prefix("cross_wave_slash_attack"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> {
                                // Play a starting sound
                                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8F, 1.1F);
                            })
                            .build())
                    .build());
    public static final RegistryObject<ComboState> CROSS_WAVE_SLASH_ATTACK = COMBO_STATE.register("cross_wave_slash_attack",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(3006, 3015) // Animation frames for the attack
                    .priority(65)
                    .nextOfTimeout(entity -> SlashBlade.prefix("cross_wave_slash_recovery"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> {
                                // Perform the initial cross slash
                                performCrossSlash(entity, 105); // Horizontal
                                performCrossSlash(entity, 15);  // Vertical (forward)
                            })
                            .put(5, entity -> {
                                // Release the cross-shaped wave projectile
                                releaseCrossWave(entity);
                            })
                            .build())
                    .build());

    public static final RegistryObject<ComboState> CROSS_WAVE_SLASH_RECOVERY = COMBO_STATE.register("cross_wave_slash_recovery",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(3016, 3025) // Animation frames for recovery
                    .priority(50)
                    .next(entity -> SlashBlade.prefix("none")) // Return to no state
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(FallHandler::fallResist)
                    .build());

    //endregion
    // 尸横遍野 (Corpse Piler)
    //region
    public static final RegistryObject<ComboState> CORPSE_PILER_START = COMBO_STATE.register("corpse_piler_start",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1600, 1605) // 起始动画帧
                    .priority(60)
                    .next(entity -> SlashBlade.prefix("corpse_piler_attack1"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("corpse_piler_attack1"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> {
                                // 起始音效
                                playSound(entity, SoundEvents.PLAYER_ATTACK_SWEEP, 0.7F, 1.2F);
                            })
                            .build())
                    .build());

    public static final RegistryObject<ComboState> CORPSE_PILER_ATTACK1 = COMBO_STATE.register("corpse_piler_attack1",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1606, 1625) // 第一次攻击阶段
                    .priority(65)
                    .next(entity -> SlashBlade.prefix("corpse_piler_pause1"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("corpse_piler_pause1"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> performSlash(entity, 150, getBladeColor(entity, 0xFF0000), 0.75f, KnockBacks.toss))
                            .put(3, entity -> performSlash(entity, 30, getBladeColor(entity, 0xFF0000), 0.75f, KnockBacks.toss))
                            .build())
                    .build());

    public static final RegistryObject<ComboState> CORPSE_PILER_PAUSE1 = COMBO_STATE.register("corpse_piler_pause1",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1626, 1630) // 第一次暂停
                    .priority(60)
                    .next(entity -> SlashBlade.prefix("corpse_piler_attack2"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("corpse_piler_attack2"))
                    .addTickAction(FallHandler::fallResist)
                    .build());

    public static final RegistryObject<ComboState> CORPSE_PILER_ATTACK2 = COMBO_STATE.register("corpse_piler_attack2",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1606, 1625) // 第二次攻击阶段
                    .priority(65)
                    .next(entity -> SlashBlade.prefix("corpse_piler_pause2"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("corpse_piler_pause2"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> performSlash(entity, 150, getBladeColor(entity, 0xFF0000), 1.0f, KnockBacks.toss))
                            .put(3, entity -> performSlash(entity, 30, getBladeColor(entity, 0xFF0000), 1.0f, KnockBacks.toss))
                            .build())
                    .build());

    public static final RegistryObject<ComboState> CORPSE_PILER_PAUSE2 = COMBO_STATE.register("corpse_piler_pause2",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1626, 1630) // 第二次暂停
                    .priority(60)
                    .next(entity -> SlashBlade.prefix("corpse_piler_attack3"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("corpse_piler_attack3"))
                    .addTickAction(FallHandler::fallResist)
                    .build());

    public static final RegistryObject<ComboState> CORPSE_PILER_ATTACK3 = COMBO_STATE.register("corpse_piler_attack3",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1606, 1625) // 最终攻击阶段
                    .priority(70) // 最高优先级
                    .next(entity -> SlashBlade.prefix("corpse_piler_recovery"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("corpse_piler_recovery"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, entity -> {
                                // 最终强力斩击
                                float roll = entity.getYRot();
                                performSlash(entity, 25, getBladeColor(entity, 0x990000), 1.5f, KnockBacks.smash);

                                // 添加血腥粒子效果
                                if (entity.level() instanceof ServerLevel serverLevel) {
                                    Vec3 pos = entity.position()
                                            .add(entity.getLookAngle().scale(2.0))
                                            .add(0, 1.0, 0);
                                    serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                                            pos.x, pos.y, pos.z,
                                            20, 1.0, 0.5, 1.0, 0.2);
                                    serverLevel.sendParticles(ParticleTypes.CRIT,
                                            pos.x, pos.y, pos.z,
                                            15, 0.8, 0.3, 0.8, 0.1);
                                }

                                playSound(entity, SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 0.8F);
                            })
                            .build())
                    .build());
    public static final RegistryObject<ComboState> CORPSE_PILER_RECOVERY = COMBO_STATE.register("corpse_piler_recovery",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1676, 1685) // 恢复阶段
                    .priority(50)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(FallHandler::fallResist)
                    .build());
//endregion
    // 燕返 (Swallow Return) 招式状态
    //region
public static final RegistryObject<ComboState> SWALLOW_RETURN_START = COMBO_STATE.register("swallow_return_start",
        () -> ComboState.Builder.newInstance()
                .startAndEnd(1600, 1605) // 起始动画帧
                .priority(65)
                .aerial() // 空中技能
                .next(entity -> SlashBlade.prefix("swallow_return_attack"))
                .nextOfTimeout(entity -> SlashBlade.prefix("swallow_return_attack"))
                .addTickAction(FallHandler::fallResist)
                .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                        .put(0, entity -> {
                            // 升空效果
                            entity.setDeltaMovement(entity.getDeltaMovement().x, 0.5, entity.getDeltaMovement().z);
                            // 升空音效
                            playSound(entity, SoundEvents.ENDER_DRAGON_FLAP, 0.7F, 1.5F);

                            // 升空粒子效果
                            if (entity.level() instanceof ServerLevel serverLevel) {
                                Vec3 pos = entity.position();
                                serverLevel.sendParticles(ParticleTypes.CLOUD,
                                        pos.x, pos.y, pos.z,
                                        15,
                                        0.5, 0.2, 0.5,
                                        0.05);
                            }
                        })
                        .build())
                .build());
    public static final RegistryObject<ComboState> SWALLOW_RETURN_ATTACK = COMBO_STATE.register("swallow_return_attack",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1606, 1620) // 攻击阶段
                    .priority(70)
                    .aerial()
                    .next(entity -> SlashBlade.prefix("swallow_return_recovery"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("swallow_return_recovery"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            // 6次快速斩击，每2刻一次
                            .put(0, entity -> performSwallowSlash(entity, 0))
                            .put(2, entity -> performSwallowSlash(entity, 30))
                            .put(4, entity -> performSwallowSlash(entity, -30))
                            .put(6, entity -> performSwallowSlash(entity, 60))
                            .put(8, entity -> performSwallowSlash(entity, -60))
                            .put(10, entity -> performSwallowSlash(entity, 0))
                            .build())
                    .build());
    public static final RegistryObject<ComboState> SWALLOW_RETURN_RECOVERY = COMBO_STATE.register("swallow_return_recovery",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(1621, 1630) // 恢复阶段
                    .priority(50)
                    .aerial()
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(entity -> {
                        // 缓慢下落
                        entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.8, 0.5, 0.8));
                    })
                    .build());
    // 燕返斩击执行方法
    private static void performSwallowSlash(LivingEntity entity, float angleOffset) {
        if (entity.level().isClientSide()) return;

        // 获取刀剑颜色
        int colorCode = getBladeColor(entity, 0x87CEEB); // 默认天蓝色

        // 计算斩击角度（玩家朝向+偏移）
        float roll = entity.getYRot() + angleOffset;

        // 执行斩击
        performSlash(entity, roll, colorCode, 0.8f, KnockBacks.toss);

        // 向前推进效果
        Vec3 lookVec = entity.getLookAngle().scale(0.3);
        entity.setDeltaMovement(entity.getDeltaMovement().add(lookVec));

        // 粒子效果
        if (entity.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = entity.position()
                    .add(entity.getLookAngle().scale(1.5))
                    .add(0, 1.0, 0);

            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    pos.x, pos.y, pos.z,
                    5,
                    0.5, 0.2, 0.5,
                    0.05);

            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    pos.x, pos.y, pos.z,
                    3,
                    0.3, 0.1, 0.3,
                    0.02);
        }
        // 音效（只在第一击和最后一击播放完整音效）
        if (angleOffset == 0) {
            playSound(entity, SoundEvents.PLAYER_ATTACK_SWEEP, 0.9F, 1.2F);
        } else {
            playSound(entity, SoundEvents.PLAYER_ATTACK_WEAK, 0.6F, 1.8F);
        }
    }
//endregion
// 间隙月影 (Gap Moon Shadow)
//region
public static final RegistryObject<ComboState> GAP_MOON_SHADOW_START = COMBO_STATE.register("gap_moon_shadow_start",
        () -> ComboState.Builder.newInstance()
                .startAndEnd(1600,1605) // 起始动画帧
                .priority(60)
                .next(entity -> SlashBlade.prefix("gap_moon_shadow_attack")) // 进入攻击阶段
                .nextOfTimeout(entity -> SlashBlade.prefix("gap_moon_shadow_attack"))
                .addTickAction(FallHandler::fallResist)
                .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                        .put(0, entity -> {
                            // 起始音效（刀出鞘的声音）
                            playSound(entity, SoundEvents.PLAYER_ATTACK_SWEEP, 0.8F, 0.9F);
                            // 起始粒子效果（月光粒子）
                            if (entity.level() instanceof ServerLevel serverLevel) {
                                Vec3 pos = entity.position().add(0, 1.0, 0);
                                serverLevel.sendParticles(ParticleTypes.GLOW,
                                        pos.x, pos.y, pos.z,
                                        20,
                                        0.5, 0.5, 0.5,
                                        0.1);
                            }
                        })
                        .build())
                .build());

        public static final RegistryObject<ComboState> GAP_MOON_SHADOW_ATTACK = COMBO_STATE.register("gap_moon_shadow_attack",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(1606, 1610) // 攻击阶段
                        .priority(65)
                        .next(entity -> SlashBlade.prefix("gap_moon_shadow_recovery"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("gap_moon_shadow_recovery"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    performSlash(entity, 100, getBladeColor(entity, 0x99CCFF), 2.0f, KnockBacks.smash);
                                    playSound(entity, SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.8F);
                                    // 斩击粒子效果
                                    if (entity.level() instanceof ServerLevel serverLevel) {
                                        Vec3 pos = entity.position()
                                                .add(entity.getLookAngle().scale(1.5))
                                                .add(0, 1.0, 0);
                                        serverLevel.sendParticles(ParticleTypes.CRIT,
                                                pos.x, pos.y, pos.z,
                                                30,
                                                1.0, 0.5, 1.0,
                                                0.1);
                                    }
                                })
                                .build())
                        .build());
        public static final RegistryObject<ComboState> GAP_MOON_SHADOW_RECOVERY = COMBO_STATE.register("gap_moon_shadow_recovery",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(1611, 1620) // 恢复阶段
                        .priority(50)
                        .next(entity -> SlashBlade.prefix("none"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    // 恢复音效
                                    playSound(entity, SoundEvents.PLAYER_ATTACK_WEAK, 0.5F, 1.0F);
                                })
                                .build())
                        .build());
//endregion
// 切腹 (Seppuku)
//region
public static final RegistryObject<ComboState> SEPPUKU_START = COMBO_STATE.register("seppuku_start",
        () -> ComboState.Builder.newInstance()
                .startAndEnd(7000, 7005) // 起始动画帧
                .priority(60)
                .next(entity -> SlashBlade.prefix("seppuku_attack")) // 进入攻击阶段
                .nextOfTimeout(entity -> SlashBlade.prefix("seppuku_attack"))
                .addTickAction(FallHandler::fallResist)
                .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                        .put(0, entity -> {
                            // 起始音效（刀出鞘的声音）
                            playSound(entity, SoundEvents.PLAYER_ATTACK_SWEEP, 0.7F, 0.7F);

                            // 起始粒子效果（血雾）
                            if (entity.level() instanceof ServerLevel serverLevel) {
                                Vec3 pos = entity.position().add(0, 1.0, 0);
                                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                                        pos.x, pos.y, pos.z,
                                        10,
                                        0.5, 0.5, 0.5,
                                        0.1);
                            }
                        })
                        .build())
                .build());

        public static final RegistryObject<ComboState> SEPPUKU_ATTACK = COMBO_STATE.register("seppuku_attack",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(7006, 7010) // 攻击阶段
                        .priority(65)
                        .next(entity -> SlashBlade.prefix("seppuku_recovery"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("seppuku_recovery"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    if (entity.level().isClientSide()) return;
                                    // 计算伤害（最大生命值的15%）
                                    float maxHealth = entity.getMaxHealth();
                                    float damageAmount = maxHealth * 0.15f;
                                    // 确保不会死亡（至少保留1点生命值）
                                    if (entity.getHealth() - damageAmount <= 1.0f) {
                                        damageAmount = entity.getHealth() - 1.0f;
                                    }
                                    // 对自己造成伤害
                                    if (damageAmount > 0) {
                                        entity.hurt(entity.damageSources().magic(), damageAmount);

                                        // 伤害粒子效果
                                        if (entity.level() instanceof ServerLevel serverLevel) {
                                            Vec3 pos = entity.position().add(0, 1.0, 0);
                                            serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                                                    pos.x, pos.y, pos.z,
                                                    20,
                                                    0.7, 0.7, 0.7,
                                                    0.2);
                                            serverLevel.sendParticles(ParticleTypes.CRIT,
                                                    pos.x, pos.y, pos.z,
                                                    15,
                                                    0.5, 0.5, 0.5,
                                                    0.1);
                                        }
                                        // 播放受伤音效
                                        playSound(entity, SoundEvents.PLAYER_HURT, 1.0F, 0.8F);
                                    }
                                    // 添加力量效果（60秒 = 1200刻）
                                    entity.addEffect(new MobEffectInstance(
                                            MobEffects.DAMAGE_BOOST,
                                            1200, // 60秒
                                            0,    // 等级I
                                            false, // 无粒子
                                            true   // 显示图标
                                    ));
                                    // 播放力量效果音效
                                    playSound(entity, SoundEvents.BEACON_ACTIVATE, 0.7F, 1.0F);
                                    // 添加力量粒子效果
                                    if (entity.level() instanceof ServerLevel serverLevel) {
                                        Vec3 pos = entity.position().add(0, 1.0, 0);
                                        serverLevel.sendParticles(ParticleTypes.HEART,
                                                pos.x, pos.y, pos.z,
                                                5,
                                                0.5, 0.5, 0.5,
                                                0.1);
                                    }
                                })
                                .build())
                        .build());
        public static final RegistryObject<ComboState> SEPPUKU_RECOVERY = COMBO_STATE.register("seppuku_recovery",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(7011, 7020) // 恢复阶段
                        .priority(50)
                        .next(entity -> SlashBlade.prefix("none"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    // 恢复音效
                                    playSound(entity, SoundEvents.PLAYER_BREATH, 0.5F, 1.0F);
                                })
                                .build())
                        .build());
//endregion
// 冰雷剑 (Ice Thunder Sword)
//region
public static final RegistryObject<ComboState> ICE_THUNDER_SWORD_START = COMBO_STATE.register("ice_thunder_sword_start",
        () -> ComboState.Builder.newInstance()
                .startAndEnd(1600, 1605) // 起始动画帧
                .priority(60)
                .next(entity -> SlashBlade.prefix("ice_thunder_sword_attack"))
                .nextOfTimeout(entity -> SlashBlade.prefix("ice_thunder_sword_attack"))
                .addTickAction(FallHandler::fallResist)
                .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                        .put(0, entity -> {
                            // 起始音效（冰与雷的混合音效）
                            playSound(entity, SoundEvents.GLASS_BREAK, 0.5F, 1.5F);
                            playSound(entity, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.3F, 1.8F);
                            // 起始粒子效果（冰雾和闪电粒子）
                            if (entity.level() instanceof ServerLevel serverLevel) {
                                Vec3 pos = entity.position().add(0, 1.0, 0);
                                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                                        pos.x, pos.y, pos.z,
                                        20,
                                        0.5, 0.5, 0.5,
                                        0.1);
                                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                        pos.x, pos.y, pos.z,
                                        10,
                                        0.3, 0.3, 0.3,
                                        0.05);
                            }
                        })
                        .build())
                .build());
        public static final RegistryObject<ComboState> ICE_THUNDER_SWORD_ATTACK = COMBO_STATE.register("ice_thunder_sword_attack",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(1606, 1615) // 攻击阶段
                        .priority(65)
                        .next(entity -> SlashBlade.prefix("ice_thunder_sword_recovery"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("ice_thunder_sword_recovery"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    // 执行冰属性劈砍
                                    int iceColor = 0x00BFFF; // 冰蓝色
                                    performSlash(entity, 105, iceColor, 1.8f, KnockBacks.smash);
                                    // 冰属性粒子效果
                                    if (entity.level() instanceof ServerLevel serverLevel) {
                                        Vec3 pos = entity.position()
                                                .add(entity.getLookAngle().scale(2.0))
                                                .add(0, 1.0, 0);
                                        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                                                pos.x, pos.y, pos.z,
                                                30,
                                                1.0, 0.5, 1.0,
                                                0.1);
                                        serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL,
                                                pos.x, pos.y, pos.z,
                                                20,
                                                0.8, 0.3, 0.8,
                                                0.05);
                                    }
                                    // 冰属性音效
                                    playSound(entity, SoundEvents.GLASS_BREAK, 0.8F, 1.2F);
                                })
                                .put(3, entity -> {
                                    // 尝试获取锁定目标
                                    if (entity.level().isClientSide()) return;
                                    getLockedTarget(entity).ifPresent(target -> {
                                        // 在锁定目标位置召唤闪电
                                        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(entity.level());
                                        if (lightning != null) {
                                            lightning.moveTo(target.getX(), target.getY(), target.getZ());
                                            lightning.setVisualOnly(false); // 实际造成伤害
                                            entity.level().addFreshEntity(lightning);
                                            // 闪电特效
                                            if (entity.level() instanceof ServerLevel serverLevel) {
                                                Vec3 pos = target.position().add(0, 1.0, 0);
                                                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                                        pos.x, pos.y, pos.z,
                                                        30,
                                                        0.5, 1.0, 0.5,
                                                        0.2);
                                                serverLevel.sendParticles(ParticleTypes.FIREWORK,
                                                        pos.x, pos.y, pos.z,
                                                        20,
                                                        0.3, 0.3, 0.3,
                                                        0.1);
                                            }
                                            playSound(entity, SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 0.9F);
                                            playSound(entity, SoundEvents.LIGHTNING_BOLT_IMPACT, 0.8F, 1.0F);
                                            // 对目标施加短暂缓慢效果（冰雷组合效果）
                                            target.addEffect(new MobEffectInstance(
                                                    MobEffects.MOVEMENT_SLOWDOWN,
                                                    60, // 3秒
                                                    1,   // 等级II
                                                    false,
                                                    true
                                            ));
                                        }
                                    });
                                })
                                .build())
                        .build());
        public static final RegistryObject<ComboState> ICE_THUNDER_SWORD_RECOVERY = COMBO_STATE.register("ice_thunder_sword_recovery",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(1616, 1625) // 恢复阶段
                        .priority(50)
                        .next(entity -> SlashBlade.prefix("none"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    // 恢复音效（冰晶消散）
                                    playSound(entity, SoundEvents.FIRE_EXTINGUISH, 0.5F, 1.5F);
                                })
                                .build())
                        .build());
        // 获取玩家锁定的目标（假设使用SlashBlade的锁定机制）
        private static Optional<LivingEntity> getLockedTarget(LivingEntity entity) {
            if (entity instanceof Player player) {
                // 从主手刀获取锁定目标
                ItemStack mainHandItem = player.getMainHandItem();
                if (mainHandItem.getItem() instanceof ItemSlashBlade) {
                    // The key is to ensure the map function correctly returns an Optional<LivingEntity>
                    return mainHandItem.getCapability(ItemSlashBlade.BLADESTATE)
                            .map(bladeState -> { // Renamed 'state' to 'bladeState' for clarity
                                Entity target = bladeState.getTargetEntity(player.level());
                                if (target instanceof LivingEntity livingTarget) {
                                    return Optional.of(livingTarget); // Returns Optional<LivingEntity>
                                }
                                return Optional.<LivingEntity>empty(); // Explicitly cast to Optional<LivingEntity>
                            })
                            .orElse(Optional.empty()); // If capability not present, return Optional<LivingEntity>
                }
            }
            return Optional.empty(); // If not a player or not SlashBlade, return Optional<LivingEntity>
        }
//endregion

        // 烈火再燃 (Blazing Rekindle)
//region
        public static final RegistryObject<ComboState> BLAZING_REKINDLE_START = COMBO_STATE.register("blazing_rekindle_start",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(1500, 1505) // 起始动画帧
                        .priority(60)
                        .next(entity -> SlashBlade.prefix("blazing_rekindle_dash")) // 进入冲刺阶段
                        .nextOfTimeout(entity -> SlashBlade.prefix("blazing_rekindle_dash"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    // 起始音效（火焰点燃）
                                    playSound(entity, SoundEvents.FLINTANDSTEEL_USE, 0.8F, 1.2F);

                                    // 起始粒子效果（火焰粒子）
                                    if (entity.level() instanceof ServerLevel serverLevel) {
                                        Vec3 pos = entity.position().add(0, 1.0, 0);
                                        serverLevel.sendParticles(ParticleTypes.FLAME,
                                                pos.x, pos.y, pos.z,
                                                20,
                                                0.5, 0.5, 0.5,
                                                0.1);
                                        serverLevel.sendParticles(ParticleTypes.SMOKE,
                                                pos.x, pos.y, pos.z,
                                                10,
                                                0.3, 0.3, 0.3,
                                                0.05);
                                    }
                                })
                                .build())
                        .build());

        public static final RegistryObject<ComboState> BLAZING_REKINDLE_DASH = COMBO_STATE.register("blazing_rekindle_dash",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(1, 33) // 冲刺阶段
                        .priority(65)
                        .next(entity -> SlashBlade.prefix("blazing_rekindle_recovery"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("blazing_rekindle_recovery"))
                        .addTickAction(FallHandler::resetState)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(1, entity -> {
                                    // 向前冲刺
                                    Vec3 lookVec = entity.getLookAngle();
                                    ModComboStates.moveForward(entity,0.5f);
                                    entity.hurtMarked = true; // 确保移动同步到客户端
                                    // 冲刺粒子效果
                                    if (entity.level() instanceof ServerLevel serverLevel) {
                                        Vec3 pos = entity.position().add(0, 0.5, 0);
                                        serverLevel.sendParticles(ParticleTypes.FLAME,
                                                pos.x, pos.y, pos.z,
                                                15,
                                                0.3, 0.1, 0.3,
                                                0.05);
                                        serverLevel.sendParticles(ParticleTypes.SMOKE,
                                                pos.x, pos.y, pos.z,
                                                8,
                                                0.2, 0.1, 0.2,
                                                0.03);
                                    }

                                    // 冲刺音效
                                    playSound(entity, SoundEvents.FIRE_AMBIENT, 0.6F, 1.5F);
                                })
                                .put(6,entity -> {
                                    AttackManager.areaAttack(
                                            entity,
                                            target -> { // beforeHit Consumer
                                                target.setSecondsOnFire(4); // 命中目标燃烧4秒
                                                if (entity.level() instanceof ServerLevel serverLevel) {
                                                    serverLevel.sendParticles(ParticleTypes.LAVA,
                                                            target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ(),
                                                            5, 0.2, 0.2, 0.2, 0.01);
                                                }
                                            }, // 不使用 beforeHit 回调
                                            1.5f,
                                            true,
                                            true,
                                            false
                                    );
                                    if (entity.level() instanceof ServerLevel serverLevel) {
                                        Vec3 pos = entity.position()
                                                .add(entity.getLookAngle().scale(2.0))
                                                .add(0, 1.0, 0);
                                        serverLevel.sendParticles(ParticleTypes.FLAME,
                                                pos.x, pos.y, pos.z,
                                                30,
                                                1.0, 0.5, 1.0,
                                                0.1);
                                        serverLevel.sendParticles(ParticleTypes.LAVA,
                                                pos.x, pos.y, pos.z,
                                                10,
                                                0.5, 0.3, 0.5,
                                                0.05);
                                    }
                                    // 刺击音效
                                    playSound(entity, SoundEvents.FIRECHARGE_USE, 0.9F, 1.0F);
                                })
                                .build())
                        .build());
        public static final RegistryObject<ComboState> BLAZING_REKINDLE_RECOVERY = COMBO_STATE.register("blazing_rekindle_recovery",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(33, 55) // 恢复阶段
                        .priority(50)
                        .next(entity -> SlashBlade.prefix("none"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    // 恢复音效（火焰熄灭）
                                    playSound(entity, SoundEvents.LAVA_EXTINGUISH, 0.5F, 1.0F);

                                    // 火焰残留粒子
                                    if (entity.level() instanceof ServerLevel serverLevel) {
                                        Vec3 pos = entity.position().add(0, 1.0, 0);
                                        serverLevel.sendParticles(ParticleTypes.SMOKE,
                                                pos.x, pos.y, pos.z,
                                                15,
                                                0.5, 0.5, 0.5,
                                                0.1);
                                    }
                                })
                                .build())
                        .build());
//endregion
// 暴风偏向 (Storm Bias)
//region
public static final RegistryObject<ComboState> STORM_BIAS_START = COMBO_STATE.register("storm_bias_start",
        () -> ComboState.Builder.newInstance()
                .startAndEnd(10000, 10020) // 蓄力阶段（20刻 = 1秒）
                .priority(60)
                .next(entity -> SlashBlade.prefix("storm_bias_attack")) // 进入攻击阶段
                .nextOfTimeout(entity -> SlashBlade.prefix("storm_bias_attack"))
                .addTickAction(FallHandler::fallResist)
                .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                        .put(0, entity -> {
                            // 蓄力开始音效
                            playSound(entity, SoundEvents.DRAGON_FIREBALL_EXPLODE, 0.5F, 0.7F);
                            // 初始粒子效果
                            if (entity.level() instanceof ServerLevel serverLevel) {
                                Vec3 pos = entity.position().add(0, 1.5, 0);
                                serverLevel.sendParticles(ParticleTypes.CLOUD,
                                        pos.x, pos.y, pos.z,
                                        10,
                                        0.3, 0.3, 0.3,
                                        0.05);
                            }
                        })
                        .put(10, entity -> {
                            // 蓄力中音效
                            playSound(entity, SoundEvents.DRAGON_FIREBALL_EXPLODE, 0.7F, 0.8F);
                            // 蓄力粒子效果
                            if (entity.level() instanceof ServerLevel serverLevel) {
                                Vec3 pos = entity.position().add(0, 1.5, 0);
                                serverLevel.sendParticles(ParticleTypes.CLOUD,
                                        pos.x, pos.y, pos.z,
                                        20,
                                        0.5, 0.5, 0.5,
                                        0.1);
                            }
                        })
                        .put(19, entity -> {
                            // 蓄力完成音效
                            playSound(entity, SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 0.9F);
                            // 强力粒子效果
                            if (entity.level() instanceof ServerLevel serverLevel) {
                                Vec3 pos = entity.position().add(0, 1.5, 0);
                                serverLevel.sendParticles(ParticleTypes.CLOUD,
                                        pos.x, pos.y, pos.z,
                                        30,
                                        0.7, 0.7, 0.7,
                                        0.15);
                            }
                        })
                        .build())
                .build());
        public static final RegistryObject<ComboState> STORM_BIAS_ATTACK = COMBO_STATE.register("storm_bias_attack",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(10021, 10025) // 攻击阶段
                        .priority(70)
                        .next(entity -> SlashBlade.prefix("storm_bias_recovery"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("storm_bias_recovery"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    moveBack(entity,0.6f);
                                    // 2. 发射毁灭波炮弹
                                    if (!entity.level().isClientSide()) {
                                        int colorCode = getBladeColor(entity, 0xFFFFFF);
                                        // 扇形发射7枚幻影剑
                                        float baseYaw = entity.getYRot();
                                        float spread = 20.0f; // 扇形角度
                                        int count = 7; // 数量
                                        for (int i = 0; i < count; i++) {
                                            // 计算角度偏移 (-30° 到 +30°)
                                            float angle = baseYaw + (spread * (i / (float) (count - 1)) - spread / 2);
                                            // 计算目标位置
                                            Vec3 lookVec = VectorHelper.getVectorForRotation(0, angle);
                                            Vec3 targetPos = entity.position().add(lookVec.scale(40));
                                            // 创建幻影剑
                                            double attackDamage = entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
                                            double strength = entity.getMainHandItem().getEnchantmentLevel(Enchantments.POWER_ARROWS) * 1.5;
                                            createSummonedSword((ServerPlayer) entity, attackDamage+strength, colorCode, targetPos);
                                            // 发射音效
                                            playSound(entity, SoundEvents.WITHER_SHOOT, 0.3F, 0.3F);
                                        }
                                    }
                                    // 3. 反冲粒子效果
                                    if (entity.level() instanceof ServerLevel serverLevel) {
                                        Vec3 pos = entity.position().add(0, 1.0, 0);
                                        serverLevel.sendParticles(ParticleTypes.CLOUD,
                                                pos.x, pos.y, pos.z,
                                                20,
                                                0.5, 0.5, 0.5,
                                                0.2);
                                    }
                                })
                                .build())
                        .build());

        public static final RegistryObject<ComboState> STORM_BIAS_RECOVERY = COMBO_STATE.register("storm_bias_recovery",
                () -> ComboState.Builder.newInstance()
                        .startAndEnd(10026, 10035) // 恢复阶段
                        .priority(50)
                        .next(entity -> SlashBlade.prefix("none"))
                        .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                        .addTickAction(FallHandler::fallResist)
                        .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                                .put(0, entity -> {
                                    // 恢复音效（风停）
                                    playSound(entity, SoundEvents.DRAGON_FIREBALL_EXPLODE, 0.5F, 1.0F);
                                })
                                .build())
                        .build());
//endregion

}
