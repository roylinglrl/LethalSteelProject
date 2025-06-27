package net.royling.lsp.datagenerator;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public class ModBlades {
    public static final ResourceLocation GROUND_POUNDER = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "egg_lan");
    public static final ResourceLocation KANZE_MASAMUNE = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "kanze_masamune");
    public static final ResourceLocation MONSTER_MURAMASA = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "monster_muramasa");
    public static final ResourceLocation BON = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "bizen_osafune_nagamitsu");
    public static final ResourceLocation LIGHT_SWORD = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "light_sword");
    public static final ResourceLocation RUINED_SWORD = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "ruined_sword");
    public static final ResourceLocation AIR_SWORD = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "air_sword");
    public static final ResourceLocation CLOTHESLINE = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "clothesline");
    public static final ResourceLocation WAKIZASHI = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "wakizashi");
    public static final ResourceLocation DISSOCIATOR = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "dissociator");
    public static final ResourceLocation SSA_RAYE = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "ssa_raye");
    public static final ResourceLocation SSA_ROZY = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "ssa_roze");
    public static final ResourceLocation SSA_KAGARI = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "ssa_kagari");
    public static final ResourceLocation SSA_HAYATE = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "ssa_hayate");
    public static final ResourceLocation HAND_OF_MALENIA = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "hand_of_malenia");
    public static final ResourceLocation RIVERS_OF_BLOOD = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "rivers_of_blood");
    public static final ResourceLocation DOJIKIRI = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "dojikiri_yasutsuna");
    public static final ResourceLocation MOONVEIL = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "moonveil");

    public static void registerBlades() {
        //region
        // 蓝小蛋
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        GROUND_POUNDER,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/egg_lan/egg_lan.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/egg_lan/egg_lan.png"),
                        0xEA67DD,
                        18f,
                        75,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "ground_pound"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.SMITE, 4)
                        ),
                        List.of()
                )
        );

        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        KANZE_MASAMUNE,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/kanze_masamune.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/kanze_masamune.png"),
                        0x2751B1,
                        4.5f,
                        55,
                        null,
                        List.of("none"),
                        List.of(),
                        List.of()
                )
        );
        //空氣劍
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        AIR_SWORD,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/air_sword/air_sword.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/air_sword/air_sword.png"),
                        0x00275101,
                        3.5f,
                        666,
                        null,
                        List.of("none"),
                        List.of(),
                        List.of()
                )
        );
        //妖刀村正
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        MONSTER_MURAMASA,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/kanze_masamune.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/monster_muramasa.png"),
                        0x2D98B1,
                        6.5f,
                        85,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "drive_horizontal"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.FIRE_ASPECT, 2),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.KNOCKBACK, 2)
                        ),
                        List.of()
                )
        );
        //備前長船長光
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        BON,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/kanze_masamune.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/bizen_osafune_nagamitsu.png"),
                        0xE50B00,
                        8.0f,
                        85,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "swallow_return"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.FIRE_ASPECT, 2),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.SHARPNESS, 4),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.KNOCKBACK, 2)
                        ),
                        List.of()
                )
        );
        //名刀月隐
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        MOONVEIL,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/kanze_masamune.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/moonveil.png"),
                        0x22E5D4,
                        8.0f,
                        85,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "transient_moonlight"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.POWER_ARROWS, 5)
                        ),
                        List.of()
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        LIGHT_SWORD,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/light_sword/light_sword.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/light_sword/light_sword.png"),
                        0xE50B00,
                        8.5f,
                        185,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "thermal_wave_beam"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.FIRE_ASPECT, 4),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.SHARPNESS, 10)
                        ),
                        List.of("heat_cutting")
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        RUINED_SWORD,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/ruined_sword/ruined_sword.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/ruined_sword/ruined_sword.png"),
                        0xDC7CE5,
                        10f,
                        265,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "heavens_thirteen_slashes"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.SMITE, 8),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.FIRE_ASPECT, 3)
                        ),
                        List.of()
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        CLOTHESLINE,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/clothesline/clothesline.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/clothesline/clothesline.png"),
                        0xE4E4E4,
                        8.5f,
                        145,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "cross_slash"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.SMITE, 4),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.UNBREAKING, 3)
                        ),
                        List.of("dual_blade_main")
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        WAKIZASHI,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/wakizashi/wakizashi.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/wakizashi/wakizashi.png"),
                        0xE4E4E4,
                        4.5f,
                        25,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID,"seppuku"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.UNBREAKING, 3)
                        ),
                        List.of("dual_blade_off")
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        DISSOCIATOR,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/dissociator/dissociator_simple.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/dissociator/dissociator_simple.png"),
                        0xE4AB40,
                        8.5f,
                        265,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "cross_wave_slash"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.UNBREAKING, 3),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.SMITE, 3)
                        ),
                        List.of()
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        SSA_RAYE,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/raye/raye.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/raye/raye2k.png"),
                        0x6E0E0F,
                        7.5f,
                        465,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "heavens_thirteen_slashes"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.UNBREAKING, 3),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.SMITE, 3)
                        ),
                        List.of()
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        SSA_ROZY,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/roze/roze.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/roze/roze.png"),
                        0x000000,
                        7.5f,
                        465,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "ice_thunder_sword"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.UNBREAKING, 3),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.BANE_OF_ARTHROPODS, 3)
                        ),
                        List.of()
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        SSA_KAGARI,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kagari/kagari.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kagari/kagari.png"),
                        0xFF9040,
                        7.5f,
                        465,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "blaze_reborn"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.UNBREAKING, 3),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.FIRE_ASPECT, 3)
                        ),
                        List.of()
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        SSA_HAYATE,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/hayate/hayate.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/hayate/hayate.png"),
                        0x66FF66,
                        7.5f,
                        465,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "storm_bias"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.UNBREAKING, 3),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.KNOCKBACK, 3)
                        ),
                        List.of()
                )
        );
        //endregion
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        HAND_OF_MALENIA,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/hand_of_malenia/hand_of_malenia.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/hand_of_malenia/hand_of_malenia.png"),
                        0xC2AF4B,
                        9.5f,
                        333,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "waterfowl_dance"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.UNBREAKING, 6)
                        ),
                        List.of("aeonia")
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        RIVERS_OF_BLOOD,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/kanze_masamune.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/kanze_masamune/rivers_of_blood.png"),
                        0xE50B00,
                        9.5f,
                        147,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "corpse_piler"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.SHARPNESS, 4),
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.POWER_ARROWS, 2)
                        ),
                        List.of("bleeding_cutting")
                )
        );
        CustomBladeRegistry.register(
                new CustomBladeRegistry.BladeDefinition(
                        DOJIKIRI,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/dojikiri_yasutsuna/dojikiri_yasutsuna.obj"),
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/dojikiri_yasutsuna/dojikiri_yasutsuna.png"),
                        0x6AE5E5,
                        7.5f,
                        147,
                        ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "corpse_piler"),
                        List.of("bewitched"),
                        List.of(
                                new CustomBladeRegistry.BladeDefinition.EnchantmentData(Enchantments.POWER_ARROWS, 2)
                        ),
                        List.of()
                )
        );

    }
}
