package net.royling.lsp.SlashArt;


import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SARegistries {

    public static final DeferredRegister<SlashArts> SLASH_ARTS =
            DeferredRegister.create(SlashArts.REGISTRY_KEY, SlashBlade.MODID);
    public static final RegistryObject<SlashArts> GROUND_POUND = SLASH_ARTS.register(
            "ground_pound",
            () -> new SlashArts((entity) -> ModComboStates.GROUND_POUND_START.getId())
    );

    public static final RegistryObject<SlashArts> HEAVENS_THIRTEEN_SLASHES = SLASH_ARTS.register(
            "heavens_thirteen_slashes",
            () -> new SlashArts((entity) -> ModComboStates.HEAVENS_SLASH_START.getId())
    );
    public static final RegistryObject<SlashArts> THERMAL_WAVE_BEAM = SLASH_ARTS.register(
            "thermal_wave_beam",
            () -> new SlashArts((entity) -> ModComboStates.THERMAL_WAVE_START.getId())
    );
    public static final RegistryObject<SlashArts> WATERFOWL_DANCE = SLASH_ARTS.register(
            "waterfowl_dance",
            () -> new SlashArts((entity) -> ModComboStates.WATERFOWL_DANCE_START.getId())
    );
    public static final RegistryObject<SlashArts> CROSS_SLASH = SLASH_ARTS.register(
            "cross_slash",
            () -> new SlashArts((entity) -> ModComboStates.CROSS_SLASH_START.getId())
    );
    public static final RegistryObject<SlashArts> CROSS_WAVE_SLASH = SLASH_ARTS.register(
            "cross_wave_slash",
            () -> new SlashArts((entity) -> ModComboStates.CROSS_WAVE_SLASH_START.getId())
    );
    public static final RegistryObject<SlashArts> CORPSE_PILER = SLASH_ARTS.register(
            "corpse_piler",
            () -> new SlashArts((entity) -> ModComboStates.CORPSE_PILER_START.getId())
    );
    public static final RegistryObject<SlashArts> SWALLOW_RETURN = SLASH_ARTS.register(
            "swallow_return",
            () -> new SlashArts((entity) -> ModComboStates.SWALLOW_RETURN_START.getId())
    );
    public static final RegistryObject<SlashArts> TRANSIENT_MOONLIGHT = SLASH_ARTS.register(
            "transient_moonlight",
            () -> new SlashArts((entity) -> ModComboStates.GAP_MOON_SHADOW_START.getId())
    );
    public static final RegistryObject<SlashArts> SEPPUKU = SLASH_ARTS.register(
            "seppuku",
            () -> new SlashArts((entity) -> ModComboStates.SEPPUKU_START.getId())
    );
    public static final RegistryObject<SlashArts> ICE_THUNDER_SWORD = SLASH_ARTS.register(
            "ice_thunder_sword",
            () -> new SlashArts((entity) -> ModComboStates.ICE_THUNDER_SWORD_START.getId())
    );
    public static final RegistryObject<SlashArts> BLAZE_REBORN = SLASH_ARTS.register(
            "blaze_reborn",
            () -> new SlashArts((entity) -> ModComboStates.BLAZING_REKINDLE_START.getId())
    );
    public static final RegistryObject<SlashArts> STORM_BIAS = SLASH_ARTS.register(
            "storm_bias",
            () -> new SlashArts((entity) -> ModComboStates.STORM_BIAS_START.getId())
    );
}
