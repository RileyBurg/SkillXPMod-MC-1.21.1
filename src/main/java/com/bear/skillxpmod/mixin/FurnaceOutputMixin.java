package com.bear.skillxpmod.mixin;

import com.bear.skillxpmod.SkillData;
import com.bear.skillxpmod.SkillXPMod;
import com.bear.skillxpmod.SkillValues;
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

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        this.cachedPlayer = player;

        if (!player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            int amount = stack.getCount();
            Item item = stack.getItem();

            awardCookingXP(serverPlayer, item, amount);
            awardSmeltingXP(serverPlayer, item, amount);
        }
    }

    @Inject(method = "takeStack", at = @At("RETURN"))
    private void onShiftClickTakeStack(int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (cachedPlayer == null || cachedPlayer.getWorld().isClient || !(cachedPlayer instanceof ServerPlayerEntity serverPlayer)) return;

        ItemStack stack = cir.getReturnValue();
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            int count = stack.getCount();

            awardCookingXP(serverPlayer, item, count);
            awardSmeltingXP(serverPlayer, item, count);
        }
    }

    @Unique
    private void awardCookingXP(ServerPlayerEntity player, Item item, int amount) {
        Integer xpPerItem = SkillValues.COOKING_XP_VALUES.get(item);
        if (xpPerItem != null) {
            int totalXP = xpPerItem * amount;
            SkillData.addSkillXP(player, "cooking", totalXP);

            int xp = SkillData.getSkillXP(player, "cooking");
            int level = SkillData.getSkillLevel(player, "cooking");
            int xpForNextLevel = SkillXPMod.calculateXPForNextLevel(level);

            player.sendMessage(Text.literal("Cooking XP: " + xp + "/" + xpForNextLevel +
                    " (+" + totalXP + " from " + amount + "x " + item.getName().getString() + ")"), true);

            SkillXPMod.checkLevelUp(player, "cooking");
        }
    }

    @Unique
    private void awardSmeltingXP(ServerPlayerEntity player, Item item, int amount) {
        Integer xpPerItem = SkillValues.SMITHING_XP_VALUES.get(item);
        if (xpPerItem != null) {
            int totalXP = xpPerItem * amount;
            SkillData.addSkillXP(player, "smithing", totalXP);
            SkillXPMod.sendXPMessage(player, "smithing", totalXP, item.getName().getString());
            SkillXPMod.checkLevelUp(player, "smithing");
        }
    }
}
