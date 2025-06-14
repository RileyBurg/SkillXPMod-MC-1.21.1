package com.bear.skillxpmod.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FishingBobberEntity.class)
public interface FishingBobberEntityAccessor {
    @Accessor("CAUGHT_FISH")
    static TrackedData<Boolean> getCaughtFish() {
        throw new UnsupportedOperationException();
    }
}
