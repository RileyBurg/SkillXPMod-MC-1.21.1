package com.bear.skillxpmod;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class SkillDataStorage extends PersistentState {
    private NbtCompound data = new NbtCompound();
    private static final Type<SkillDataStorage> TYPE = new Type<>(
            SkillDataStorage::new, // Supplier for new instance
            SkillDataStorage::createFromNbt, // Deserializer from NBT
            null // DataFixer, not needed for simple mods
    );

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup lookup) {
        nbt.put("SkillXPData", data);
        return nbt;
    }

    public static SkillDataStorage createFromNbt(NbtCompound tag, WrapperLookup lookup) {
        SkillDataStorage storage = new SkillDataStorage();
        storage.data = tag.getCompound("SkillXPData");
        return storage;
    }

    public static SkillDataStorage get(World world) {
        return world.getServer().getOverworld().getPersistentStateManager()
                .getOrCreate(TYPE, "skillxpmod");
    }

    public NbtCompound getData() {
        return data;
    }

    public void setData(NbtCompound data) {
        this.data = data;
        markDirty();
    }
}