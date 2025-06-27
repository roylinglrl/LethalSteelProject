package net.royling.lsp.SpecialEffects;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class SERegistry {
    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECTS = DeferredRegister.create(SpecialEffect.REGISTRY_KEY,
            SlashBlade.MODID);
    public static final RegistryObject<SpecialEffect> HEAT_CUTTING_20  = SPECIAL_EFFECTS.register("heat_cutting",
            HeatCutting::new);
    public static final RegistryObject<SpecialEffect> DUAL_BLADE_MAIN  = SPECIAL_EFFECTS.register("dual_blade_main",
            DualBladeMain::new);
    public static final RegistryObject<SpecialEffect> DUAL_BLADE_OFF  = SPECIAL_EFFECTS.register("dual_blade_off",
            DualBladeoff::new);
    public static final RegistryObject<SpecialEffect> AEONIA  = SPECIAL_EFFECTS.register("aeonia",
            Aeonia::new);
    public static final RegistryObject<SpecialEffect> BLEEDING_CUTTING  = SPECIAL_EFFECTS.register("bleeding_cutting",
            BleedingCutting::new);

}
