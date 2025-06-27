package net.royling.lsp.datagenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.royling.lsp.LethalSlashProject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BladeDataProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput.PathProvider pathProvider;
    private final String modId;
    public BladeDataProvider(PackOutput output, String modId) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "slashblade/named_blades");
        this.modId = modId;
    }
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CustomBladeRegistry.BladeDefinition> blades = CustomBladeRegistry.getAll();
        LethalSlashProject.LOGGER.info("Generating blade data for {} blades", blades.size());
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (CustomBladeRegistry.BladeDefinition blade : blades) {
            Path path = pathProvider.json(blade.getId());
            JsonObject json = createBladeJson(blade);
            futures.add(DataProvider.saveStable(cache, json, path)
                    .thenRun(() -> LethalSlashProject.LOGGER.debug("Generated blade JSON: {}", path)));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    private JsonObject createBladeJson(CustomBladeRegistry.BladeDefinition blade) {
        JsonObject json = new JsonObject();

        // 仅当有附魔时才生成附魔部分
        if (!blade.getEnchantments().isEmpty()) {
            JsonArray enchantmentsArray = new JsonArray();
            for (CustomBladeRegistry.BladeDefinition.EnchantmentData ench : blade.getEnchantments()) {
                JsonObject enchJson = new JsonObject();
                enchJson.addProperty("id", ForgeRegistries.ENCHANTMENTS.getKey(ench.getEnchantment()).toString());
                enchJson.addProperty("lvl", ench.getLevel());
                enchantmentsArray.add(enchJson);
            }
            json.add("enchantments", enchantmentsArray);
        }

        // 刀剑名称
        json.addProperty("name", blade.getId().toString());

        // 属性部分
        JsonObject properties = new JsonObject();
        properties.addProperty("attack_base", blade.getBaseAttack());
        properties.addProperty("max_damage", blade.getMaxDamage());

        // 仅当有斩击艺术时才添加
        if (blade.getSlashArt() != null) {
            properties.addProperty("slash_art", blade.getSlashArt().toString());
        }
        if (!blade.getSpecialEffects().isEmpty()) {
            JsonArray specialEffectsArray = new JsonArray();
            for (String effect : blade.getSpecialEffects()) {
                ResourceLocation se = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID,effect);
                specialEffectsArray.add(se.toString());
            }
            properties.add("special_effects", specialEffectsArray);
        }

        // 刀剑类型（同样支持可为空）
        if (!blade.getSwordTypes().isEmpty()) {
            JsonArray swordTypes = new JsonArray();
            for (String type : blade.getSwordTypes()) {
                swordTypes.add(type);
            }
            properties.add("sword_type", swordTypes);
        }

        json.add("properties", properties);

        // 渲染部分
        JsonObject render = new JsonObject();
        render.addProperty("model", blade.getModel().toString());
        render.addProperty("texture", blade.getTexture().toString());
        render.addProperty("summon_sword_color", blade.getSummonSwordColor());
        json.add("render", render);

        return json;
    }
    @Override
    public String getName() {
        return "Blade Data Provider for " + modId;
    }
}
