package com.bear.skillxpmod.mixin;

import com.bear.skillxpmod.SkillData;
import com.bear.skillxpmod.SkillValues;
import com.bear.skillxpmod.SkillXPMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin {

    @Inject(method = "onTakeItem", at = @At("TAIL"))
    private void onTakeCraftedItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            Item craftedItem = stack.getItem();

            if (SkillValues.SMITHING_XP_VALUES.containsKey(craftedItem)) {
                int xp = SkillValues.SMITHING_XP_VALUES.get(craftedItem);
                SkillData.addSkillXP(serverPlayer, "smithing", xp);
                SkillXPMod.sendXPMessage(serverPlayer, "smithing", xp, craftedItem.getName().getString());
                SkillXPMod.checkLevelUp(serverPlayer, "smithing");
            }
        }
    }
}
