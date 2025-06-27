package net.royling.lsp.datagenerator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import net.royling.lsp.LethalSlashProject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class SlashBladeRecipeProvider extends RecipeProvider implements IConditionBuilder{
    public static class IngredientDefinition {
        private final String type; // 材料类型 (如 "item", "tag", "slashblade:blade")
        private final String item; // 物品ID
        private BladeRequest request; // 刀类材料的要求 (仅当type为slashblade:blade时使用)
        // 普通物品构造器 (接受字符串ID)
        public IngredientDefinition(String item) {
            this("item", item, null);
        }
        // 普通物品构造器 (接受ItemLike)
        public IngredientDefinition(ItemLike item) {
            this("item", ForgeRegistries.ITEMS.getKey(item.asItem()).toString(), null);
        }
        // 标签构造器
        public IngredientDefinition(TagKey<Item> tag) {
            this("tag", tag.location().toString(), null);
        }
        // 拔刀剑专用构造器 (接受字符串ID和BladeRequest)
        public IngredientDefinition(String item, BladeRequest request) {
            this("slashblade:blade", item, request);
        }
        // 拔刀剑专用构造器 (接受ItemLike和BladeRequest)
        public IngredientDefinition(ItemLike item, BladeRequest request) {
            this("slashblade:blade", ForgeRegistries.ITEMS.getKey(item.asItem()).toString(), request);
        }
        // 完整构造器
        public IngredientDefinition(String type, String item, BladeRequest request) {
            this.type = type;
            this.item = item;
            this.request = request;
        }
        // 转换为JSON
        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("item", item);

            if (request != null && !request.isEmpty()) {
                json.add("request", request.toJson());
            }
            return json;
        }
        // 静态工厂方法，用于更方便地创建 IngredientDefinition
        public static IngredientDefinition of(String item) {
            return new IngredientDefinition(item);
        }
        public static IngredientDefinition of(ItemLike item) {
            return new IngredientDefinition(item);
        }
        public static IngredientDefinition of(TagKey<Item> tag) {
            return new IngredientDefinition(tag);
        }
        public static IngredientDefinition ofBlade(String item, BladeRequest request) {
            return new IngredientDefinition(item, request);
        }

        public static IngredientDefinition ofBlade(ItemLike item, BladeRequest request) {
            return new IngredientDefinition(item, request);
        }
    }
    public static class BladeRequest {
        private String name;
        private Integer kill;
        private Integer proudSoul;
        private Integer refine;
        private List<EnchantmentRequirement> enchantments = new ArrayList<>();
        public BladeRequest setName(String name) {
            this.name = name;
            return this;
        }
        public BladeRequest setKill(int kill) {
            this.kill = kill;
            return this;
        }
        public BladeRequest setProudSoul(int proudSoul) {
            this.proudSoul = proudSoul;
            return this;
        }
        public BladeRequest setRefine(int refine) {
            this.refine = refine;
            return this;
        }
        public BladeRequest addEnchantment(Enchantment enchantment, int level) {
            this.enchantments.add(new EnchantmentRequirement(enchantment, level));
            return this;
        }
        public BladeRequest addEnchantment(String enchantmentId, int level) {
            this.enchantments.add(new EnchantmentRequirement(enchantmentId, level));
            return this;
        }
        public boolean isEmpty() {
            return name == null && kill == null && proudSoul == null &&
                    refine == null && enchantments.isEmpty();
        }
        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            if (name != null) json.addProperty("name", name);
            if (kill != null) json.addProperty("kill", kill);
            if (proudSoul != null) json.addProperty("proud_soul", proudSoul);
            if (refine != null) json.addProperty("refine", refine);
            if (!enchantments.isEmpty()) {
                JsonArray enchantArray = new JsonArray();
                for (EnchantmentRequirement req : enchantments) {
                    enchantArray.add(req.toJson());
                }
                json.add("enchantments", enchantArray);
            }
            return json;
        }
    }
    public static class EnchantmentRequirement {
        private final String enchantmentId;
        private final int level;
        public EnchantmentRequirement(Enchantment enchantment, int level) {
            this(ForgeRegistries.ENCHANTMENTS.getKey(enchantment).toString(), level);
        }
        public EnchantmentRequirement(String enchantmentId, int level) {
            this.enchantmentId = enchantmentId;
            this.level = level;
        }
        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("id", enchantmentId);
            json.addProperty("lvl", level);
            return json;
        }
    }
    public SlashBladeRecipeProvider(PackOutput gen) {
        super(gen);
    }
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        //空气之刃
        createRecipe(consumer, "air_sword")
                .withBlade("slashblade:air_sword")
                .withCategory("equipment")
                .withPattern(
                        "EBE",
                        "BDB",
                        "EBE")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setKill(7)
                                .setProudSoul(7)
                                .setRefine(0)))
                .addKey('B', new IngredientDefinition(Items.COBBLESTONE))
                .addKey('E', new IngredientDefinition(Items.IRON_INGOT))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //观世正宗
        createRecipe(consumer, "kanze_masamune")
                .withBlade("slashblade:kanze_masamune")
                .withCategory("equipment")
                .withPattern(
                        " BE",
                        "BDB",
                        "SB ")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setKill(7)
                                .setProudSoul(7)
                                .setRefine(0)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.DIAMOND))
                .addKey('S', new IngredientDefinition(Items.DIAMOND_SWORD))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //备前刀
        createRecipe(consumer, "bizen_osafune_nagamitsu")
                .withBlade("slashblade:bizen_osafune_nagamitsu")
                .withCategory("equipment")
                .withPattern(
                        " BE",
                        "BDB",
                        "EB ")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(117)
                                .addEnchantment(Enchantments.FIRE_ASPECT,2)
                                .setProudSoul(7777)
                                .setRefine(3)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.DIAMOND))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //物干竿
        createRecipe(consumer, "clothesline")
                .withBlade("slashblade:clothesline")
                .withCategory("equipment")
                .withPattern(
                        " BE",
                        "BDB",
                        "EB ")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(117)
                                .addEnchantment(Enchantments.BANE_OF_ARTHROPODS,2)
                                .setProudSoul(7777)
                                .setRefine(3)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.EMERALD))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //妖刀村正
        createRecipe(consumer, "monster_muramasa")
                .withBlade("slashblade:monster_muramasa")
                .withCategory("equipment")
                .withPattern(
                        " BE",
                        "BDB",
                        "EB ")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:muramasa")
                                .setKill(117)
                                .addEnchantment(Enchantments.FIRE_ASPECT,2)
                                .setProudSoul(7777)
                                .setRefine(10)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.GOLD_BLOCK))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //解构者
        createRecipe(consumer, "dissociator")
                .withBlade("slashblade:dissociator")
                .withCategory("equipment")
                .withPattern(
                        "CBE",
                        "BDB",
                        "EBC")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(1000)
                                .addEnchantment(Enchantments.UNBREAKING,3)
                                .setProudSoul(15000)
                                .setRefine(10)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.REDSTONE_BLOCK))
                .addKey('C', new IngredientDefinition(Items.LAPIS_BLOCK))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //光剑
        createRecipe(consumer, "light_sword")
                .withBlade("slashblade:light_sword")
                .withCategory("equipment")
                .withPattern(
                        "CBE",
                        "BDB",
                        "EBC")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(1000)
                                .addEnchantment(Enchantments.FIRE_ASPECT,2)
                                .addEnchantment(Enchantments.SHARPNESS,2)
                                .setProudSoul(15000)
                                .setRefine(10)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.REDSTONE_LAMP))
                .addKey('C', new IngredientDefinition(Items.TNT))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //童子切安纲
        createRecipe(consumer, "dojikiri_yasutsuna")
                .withBlade("slashblade:dojikiri_yasutsuna")
                .withCategory("equipment")
                .withPattern(
                        " BE",
                        "BDB",
                        "EB")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setKill(800)
                                .addEnchantment(Enchantments.POWER_ARROWS,2)
                                .setProudSoul(2000)
                                .setRefine(5)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_sphere))
                .addKey('E', new IngredientDefinition(Items.GOLD_BLOCK))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //蓝小蛋
        createRecipe(consumer, "egg_lan")
                .withBlade("slashblade:egg_lan")
                .withCategory("equipment")
                .withPattern(
                        "ABC",
                        "BDB",
                        "EBF")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setKill(1777)
                                .addEnchantment(Enchantments.SMITE,4)
                                .setProudSoul(17777)
                                .setRefine(17)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_sphere))
                .addKey('E', new IngredientDefinition(Items.GOLD_BLOCK))
                .addKey('A', new IngredientDefinition(Items.SUGAR_CANE))
                .addKey('F', new IngredientDefinition(Items.POPPY))
                .addKey('C', new IngredientDefinition(Items.ENDER_PEARL))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //玛莲妮亚义手刀
        createRecipe(consumer, "hand_of_malenia")
                .withBlade("slashblade:hand_of_malenia")
                .withCategory("equipment")
                .withPattern(
                        "AAA",
                        "CDE",
                        "GGG")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(2000)
                                .addEnchantment(Enchantments.UNBREAKING,3)
                                .setProudSoul(40000)
                                .setRefine(30)))
                .addKey('G', new IngredientDefinition(SBItems.proudsoul_sphere))
                .addKey('E', new IngredientDefinition(Items.AXOLOTL_BUCKET))
                .addKey('A', new IngredientDefinition(Items.ENDER_EYE))
                .addKey('C', new IngredientDefinition(Items.FEATHER))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //胁差
        createRecipe(consumer, "wakizashi")
                .withBlade("slashblade:wakizashi")
                .withCategory("equipment")
                .withPattern(
                        " BE",
                        "BFB",
                        "SB ")
                .addKey('B', new IngredientDefinition(SBItems.proudsoul))
                .addKey('F', new IngredientDefinition(Items.ENDER_PEARL))
                .addKey('E', new IngredientDefinition(Items.EMERALD))
                .addKey('S', new IngredientDefinition(Items.IRON_INGOT))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //名刀月隐
        createRecipe(consumer, "moonveil")
                .withBlade("slashblade:moonveil")
                .withCategory("equipment")
                .withPattern(
                        "ABE",
                        "BDB",
                        "EBA")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(1333)
                                .addEnchantment(Enchantments.POWER_ARROWS,3)
                                .setProudSoul(13333)
                                .setRefine(3)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.AMETHYST_SHARD))
                .addKey('A', new IngredientDefinition(Items.GLOWSTONE_DUST))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //尸山血海
        createRecipe(consumer, "rivers_of_blood")
                .withBlade("slashblade:rivers_of_blood")
                .withCategory("equipment")
                .withPattern(
                        "ABE",
                        "BDB",
                        "CBF")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(1666)
                                .addEnchantment(Enchantments.POWER_ARROWS,1)
                                .addEnchantment(Enchantments.SHARPNESS,1)
                                .setProudSoul(16666)
                                .setRefine(6)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.BONE_BLOCK))
                .addKey('A', new IngredientDefinition(Items.SOUL_LANTERN))
                .addKey('C', new IngredientDefinition(Items.FERMENTED_SPIDER_EYE))
                .addKey('F', new IngredientDefinition(Items.ROTTEN_FLESH))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //残败大剑
        createRecipe(consumer, "ruined_sword")
                .withBlade("slashblade:ruined_sword")
                .withCategory("equipment")
                .withPattern(
                        "ABC",
                        "BDB",
                        "EBA")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(800)
                                .addEnchantment(Enchantments.SMITE,2)
                                .addEnchantment(Enchantments.FIRE_ASPECT,2)
                                .setProudSoul(8888)
                                .setRefine(6)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.NETHERITE_SCRAP))
                .addKey('A', new IngredientDefinition(Items.BLAZE_ROD))
                .addKey('C', new IngredientDefinition(Items.DIAMOND))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //飒天
        createRecipe(consumer, "ssa_hayate")
                .withBlade("slashblade:ssa_hayate")
                .withCategory("equipment")
                .withPattern(
                        "ABC",
                        "BDB",
                        "EBA")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(1500)
                                .addEnchantment(Enchantments.KNOCKBACK,2)
                                .addEnchantment(Enchantments.UNBREAKING,3)
                                .setProudSoul(10000)
                                .setRefine(10)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.STRING))
                .addKey('A', new IngredientDefinition(Items.APPLE))
                .addKey('C', new IngredientDefinition(Items.FEATHER))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //燎里
        createRecipe(consumer, "ssa_kagari")
                .withBlade("slashblade:ssa_kagari")
                .withCategory("equipment")
                .withPattern(
                        "ABC",
                        "BDB",
                        "EBA")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(1500)
                                .addEnchantment(Enchantments.FIRE_ASPECT,2)
                                .addEnchantment(Enchantments.UNBREAKING,3)
                                .setProudSoul(10000)
                                .setRefine(10)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.REDSTONE))
                .addKey('A', new IngredientDefinition(Items.BLAZE_ROD))
                .addKey('C', new IngredientDefinition(Items.CARROT))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //灵衣
        createRecipe(consumer, "ssa_raye")
                .withBlade("slashblade:ssa_raye")
                .withCategory("equipment")
                .withPattern(
                        "ABC",
                        "BDB",
                        "EBA")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(1500)
                                .addEnchantment(Enchantments.SMITE,2)
                                .addEnchantment(Enchantments.UNBREAKING,3)
                                .setProudSoul(10000)
                                .setRefine(10)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.REDSTONE_BLOCK))
                .addKey('A', new IngredientDefinition(Items.REPEATER))
                .addKey('C', new IngredientDefinition(Items.FLINT))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();
        //露世
        createRecipe(consumer, "ssa_roze")
                .withBlade("slashblade:ssa_roze")
                .withCategory("equipment")
                .withPattern(
                        "ABC",
                        "BDB",
                        "EBA")
                .addKey('D', new IngredientDefinition("slashblade:slashblade",
                        new BladeRequest()
                                .setName("slashblade:kanze_masamune")
                                .setKill(1500)
                                .addEnchantment(Enchantments.BANE_OF_ARTHROPODS,2)
                                .addEnchantment(Enchantments.UNBREAKING,3)
                                .setProudSoul(10000)
                                .setRefine(10)))
                .addKey('B', new IngredientDefinition(SBItems.proudsoul_ingot))
                .addKey('E', new IngredientDefinition(Items.REDSTONE_BLOCK))
                .addKey('A', new IngredientDefinition(Items.COMPARATOR))
                .addKey('C', new IngredientDefinition(Items.FLINT))
                .withResult("slashblade:slashblade")
                .showNotification(true)
                .build();


    }
    public RecipeBuilder createRecipe(Consumer<FinishedRecipe> consumer, String recipeName) {
        return new RecipeBuilder(consumer, ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, recipeName));
    }
    public class RecipeBuilder {
        private final Consumer<FinishedRecipe> consumer;
        private final ResourceLocation recipeId;
        private String blade;
        private String category = "equipment"; // 默认类别
        private final List<String> pattern = new ArrayList<>();
        private final Map<Character, IngredientDefinition> keys = new HashMap<>();
        private String result = "slashblade:slashblade"; // 默认结果
        private boolean showNotification = true;
        public RecipeBuilder(Consumer<FinishedRecipe> consumer, ResourceLocation recipeId) {
            this.consumer = consumer;
            this.recipeId = recipeId;
        }
        public RecipeBuilder withBlade(String blade) {
            this.blade = blade;
            return this;
        }
        public RecipeBuilder withCategory(String category) {
            this.category = category;
            return this;
        }
        public RecipeBuilder withPattern(String... lines) {
            this.pattern.addAll(Arrays.asList(lines));
            return this;
        }
        public RecipeBuilder addKey(char symbol, IngredientDefinition ingredient) {
            this.keys.put(symbol, ingredient);
            return this;
        }
        public RecipeBuilder withResult(String result) {
            this.result = result;
            return this;
        }
        public RecipeBuilder showNotification(boolean show) {
            this.showNotification = show;
            return this;
        }
        public void build() {
            consumer.accept(new FinishedRecipe() {
                @Override
                public void serializeRecipeData(JsonObject json) {
                    json.addProperty("type", "slashblade:shaped_blade");
                    if (blade != null) {
                        json.addProperty("blade", blade);
                    }
                    json.addProperty("category", category);
                    // 构建key对象
                    JsonObject keyJson = new JsonObject();
                    for (Map.Entry<Character, IngredientDefinition> entry : keys.entrySet()) {
                        keyJson.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
                    }
                    json.add("key", keyJson);
                    // 构建pattern
                    JsonArray patternArray = new JsonArray();
                    for (String line : pattern) {
                        patternArray.add(line);
                    }
                    json.add("pattern", patternArray);
                    // 构建结果
                    JsonObject resultJson = new JsonObject();
                    resultJson.addProperty("item", result);
                    json.add("result", resultJson);
                    json.addProperty("show_notification", showNotification);
                }
                @Override
                public @NotNull ResourceLocation getId() {
                    return recipeId;
                }
                @Override
                public JsonObject serializeAdvancement() {
                    return null;
                }
                @Override
                public @NotNull RecipeSerializer<?> getType() {
                    return RecipeSerializer.SHAPED_RECIPE;
                }
                @Override
                public ResourceLocation getAdvancementId() {
                    return null;
                }
            });
        }
    }

}
