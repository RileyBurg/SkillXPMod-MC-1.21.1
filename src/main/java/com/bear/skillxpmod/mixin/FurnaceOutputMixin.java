package com.bear.skillxpmod.mixin;

import com.bear.skillxpmod.SkillData;
import com.bear.skillxpmod.SkillXPMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceOutputSlot.class)
public abstract class FurnaceOutputMixin {

    @Unique
    private PlayerEntity cachedPlayer = null;

    // Called on normal take (click-and-drag)
    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void cachePlayer(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        this.cachedPlayer = player;
        awardCookingXP(player, stack);
    }

    // Called when shift-clicking
    @Inject(method = "takeStack", at = @At("RETURN"), cancellable = true)
    private void onShiftClickTakeStack(int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (this.cachedPlayer != null) return; // Already handled in onTakeItem

        ItemStack stack = cir.getReturnValue();
        if (!stack.isEmpty()) {
            awardCookingXP(this.cachedPlayer, stack);
        }
    }

    @Unique
    private void awardCookingXP(PlayerEntity player, ItemStack stack) {
        if (player == null || player.getWorld().isClient) return;

        Item item = stack.getItem();
        int amount = stack.getCount();

        if (SkillXPMod.COOKING_XP_VALUES.containsKey(item)) {
            int xpPerItem = SkillXPMod.COOKING_XP_VALUES.get(item);
            int totalXP = xpPerItem * amount;

            SkillData.addSkillXP((ServerPlayerEntity) player, "cooking", totalXP);
            int xp = SkillData.getSkillXP(player, "cooking");
            int level = SkillData.getSkillLevel(player, "cooking");
            int xpForNextLevel = SkillXPMod.calculateXPForNextLevel(level);

            player.sendMessage(Text.literal("Cooking XP: " + xp + "/" + xpForNextLevel +
                    " (+" + totalXP + " from " + amount + "x " + item.getName().getString() + ")"), true);

            SkillXPMod.checkLevelUp((ServerPlayerEntity) player, "cooking");
        }
    }
}
