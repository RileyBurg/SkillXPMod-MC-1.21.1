package com.bear.skillxpmod.mixin;

import com.bear.skillxpmod.SkillData;
import com.bear.skillxpmod.SkillValues;
import com.bear.skillxpmod.SkillXPMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingCraftingMixin {
    @Inject(method = "onTakeOutput", at = @At("TAIL"))
    private void onSmithingTake(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            Item item = stack.getItem();
            Integer xp = SkillValues.SMITHING_XP_VALUES.get(item);
            if (xp != null) {
                SkillData.addSkillXP(serverPlayer, "smithing", xp);
                SkillXPMod.sendXPMessage(serverPlayer, "smithing", xp, item.getName().getString());
                SkillXPMod.checkLevelUp(serverPlayer, "smithing");
            }
        }
    }
}