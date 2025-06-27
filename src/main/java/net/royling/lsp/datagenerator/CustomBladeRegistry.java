package net.royling.lsp.datagenerator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class CustomBladeRegistry {
    private static final List<BladeDefinition> BLADES = new ArrayList<>();
    public static void register(BladeDefinition blade) {
        BLADES.add(blade);
    }
    public static List<BladeDefinition> getAll() {
        return BLADES;
    }
    public static class BladeDefinition {
        private final ResourceLocation id;
        private final ResourceLocation model;
        private final ResourceLocation texture;
        private final int summonSwordColor;
        private final float baseAttack;
        private final int maxDamage;
        private final ResourceLocation slashArt; // 现在可为null
        private final List<String> swordTypes;
        private final List<EnchantmentData> enchantments;
        private final List<String> specialEffects;

        public BladeDefinition(ResourceLocation id, ResourceLocation model, ResourceLocation texture,
                               int summonSwordColor, float baseAttack, int maxDamage,
                               ResourceLocation slashArt, // 现在可为null
                               List<String> swordTypes,
                               List<EnchantmentData> enchantments,
                               List<String> specialEffects) {
            this.id = id;
            this.model = model;
            this.texture = texture;
            this.summonSwordColor = summonSwordColor;
            this.baseAttack = baseAttack;
            this.maxDamage = maxDamage;
            this.slashArt = slashArt; // 允许传入null
            this.swordTypes = swordTypes;
            this.enchantments = enchantments;
            this.specialEffects = specialEffects;

        }
        // Getters
        public ResourceLocation getId() { return id; }
        public ResourceLocation getModel() { return model; }
        public ResourceLocation getTexture() { return texture; }
        public int getSummonSwordColor() { return summonSwordColor; }
        public float getBaseAttack() { return baseAttack; }
        public int getMaxDamage() { return maxDamage; }
        public ResourceLocation getSlashArt() {
            return slashArt; // 可能返回null
        }        public List<String> getSwordTypes() { return swordTypes; }
        public List<EnchantmentData> getEnchantments() { return enchantments; }
        public List<String> getSpecialEffects() { return specialEffects; }

        public static class EnchantmentData {
            private final Enchantment enchantment;
            private final int level;

            public EnchantmentData(Enchantment enchantment, int level) {
                this.enchantment = enchantment;
                this.level = level;
            }

            public Enchantment getEnchantment() { return enchantment; }
            public int getLevel() { return level; }
        }
    }
}
