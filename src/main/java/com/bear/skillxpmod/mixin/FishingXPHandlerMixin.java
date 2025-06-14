package com.bear.skillxpmod.mixin;

import com.bear.skillxpmod.SkillData;
import com.bear.skillxpmod.SkillXPMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingBobberEntity.class)
public abstract class FishingXPHandlerMixin {

    @Inject(method = "use", at = @At("RETURN"))
    private void onReelIn(CallbackInfoReturnable<Integer> cir) {
        FishingBobberEntity bobber = (FishingBobberEntity) (Object) this;

        // getOwner() returns the player who cast the line
        if (!bobber.getWorld().isClient && bobber.getOwner() instanceof ServerPlayerEntity player) {
            int result = cir.getReturnValue();

            if (result == 1) {
                int xp = 10;
                SkillData.addSkillXP(player, "fishing", xp);
                SkillXPMod.sendXPMessage(player, "fishing", xp, "catch");
                SkillXPMod.checkLevelUp(player, "fishing");
            }
        }
    }
}