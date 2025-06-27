package net.royling.lsp.ModCap;

public interface IBloodAccumulation {
    int getAccumulation();
    void setAccumulation(int amount);
    void addAccumulation(int amount);
    void clearAccumulation();
    // 添加一个方法来存储最后一个造成出血的实体UUID
    void setLastAttackerUUID(java.util.UUID uuid);
    java.util.UUID getLastAttackerUUID();
}
