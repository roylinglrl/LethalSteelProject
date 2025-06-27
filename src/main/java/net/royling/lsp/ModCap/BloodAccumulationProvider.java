package net.royling.lsp.ModCap;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BloodAccumulationProvider implements ICapabilityProvider {
    private final BloodAccumulation bloodAccumulation = new BloodAccumulation();
    private final LazyOptional<IBloodAccumulation> optional = LazyOptional.of(() -> bloodAccumulation);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ModCapabilities.BLOOD_ACCUMULATION_CAPABILITY) { // 使用你稍后注册的CAPABILITY对象
            return optional.cast();
        }
        return LazyOptional.empty();
    }
}
