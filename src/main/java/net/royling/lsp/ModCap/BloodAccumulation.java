package net.royling.lsp.ModCap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class BloodAccumulation implements IBloodAccumulation, INBTSerializable<CompoundTag> {
    private int accumulation = 0;
    private UUID lastAttackerUUID; // 存储最后一个攻击者的UUID

    @Override
    public int getAccumulation() {
        return accumulation;
    }

    @Override
    public void setAccumulation(int amount) {
        this.accumulation = amount;
    }

    @Override
    public void addAccumulation(int amount) {
        this.accumulation += amount;
    }

    @Override
    public void clearAccumulation() {
        this.accumulation = 0;
        this.lastAttackerUUID = null; // 清空时也清空攻击者
    }

    @Override
    public void setLastAttackerUUID(UUID uuid) {
        this.lastAttackerUUID = uuid;
    }

    @Override
    public UUID getLastAttackerUUID() {
        return lastAttackerUUID;
    }

    // 序列化和反序列化数据，以便保存和加载
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("accumulation", accumulation);
        if (lastAttackerUUID != null) {
            tag.putUUID("lastAttackerUUID", lastAttackerUUID);
        }
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.accumulation = nbt.getInt("accumulation");
        this.lastAttackerUUID = nbt.contains("lastAttackerUUID")
                ? nbt.getUUID("lastAttackerUUID")
                : null; // 显式重置
    }
}
