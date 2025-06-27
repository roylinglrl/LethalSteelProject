package net.royling.lsp.SpecialEffects;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Aeonia extends SpecialEffect {
    private static final int SOUL_THRESHOLD = 1500;
    private static final int KILL_COUNT_THRESHOLD = 1000;
    private static final String HEAL_COOLDOWN_TAG = "AeoniaHealCooldown"; // 用于NBT标签的键
    private static final long COOLDOWN_TICKS = 1;

    public Aeonia() {
        super(30); // 需求等级30
    }

    @SubscribeEvent
    public static void onSlashBladeHit(SlashBladeEvent.HitEvent event) {
        if (!event.getSlashBladeState().hasSpecialEffect(SERegistry.AEONIA.getId())) {
            return;
        }
        if (!(event.getUser() instanceof Player player)) {
            return;
        }
        if (!SpecialEffect.isEffective(SERegistry.AEONIA.get(), player.experienceLevel)) {
            return;
        }
        // 获取玩家的持久化数据
        CompoundTag persistentData = player.getPersistentData();
        long lastHealTick = persistentData.getLong(HEAL_COOLDOWN_TAG);
        // 如果当前游戏刻减去上次回复的时间小于冷却时间，则表示在冷却中，不进行回复
        if (player.level().getGameTime() - lastHealTick < COOLDOWN_TICKS) {
            return;
        }
        // 默认恢复1点生命
        int healAmount = 1;

        ISlashBladeState state = event.getSlashBladeState();
        int proudSoul = state.getProudSoulCount();
        int killCount = state.getKillCount();

        if (proudSoul > SOUL_THRESHOLD) {
            healAmount += 1; // 耀魂值超过1500，额外恢复1点
        }
        if (killCount > KILL_COUNT_THRESHOLD) {
            healAmount += 1; // 杀敌数超过1000，额外恢复1点
        }
        // 为玩家恢复生命值
        player.heal(healAmount);
        // 更新上次回复的时间到当前游戏刻，并存入玩家的持久化数据
        persistentData.putLong(HEAL_COOLDOWN_TAG, player.level().getGameTime());
    }
}
