package com.bear.skillxpmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public class SkillXPMod implements ModInitializer {
	public static final String MOD_ID = "skillxpmod";

	@Override
	public void onInitialize() {
		// Register fishing event
		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer && player.getStackInHand(hand).getItem() == Items.FISHING_ROD) {
				// Check for successful fishing (bobber removed with loot)
				if (player.fishHook != null && player.fishHook.isRemoved() && (player.fishHook.getHookedEntity() != null || world.getRandom().nextFloat() < 0.9f)) {
					SkillData.addSkillXP(serverPlayer, "fishing", 10);
					int xp = SkillData.getSkillXP(serverPlayer, "fishing");
					int level = SkillData.getSkillLevel(serverPlayer, "fishing");
					int xpForNextLevel = level * 100;
					serverPlayer.sendMessage(Text.literal("Fishing XP: " + xp + "/" + xpForNextLevel), true);
					checkLevelUp(serverPlayer, "fishing");
				}
			}
			return TypedActionResult.pass(player.getStackInHand(hand));
		});

		// Register mining event
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
				SkillData.addSkillXP(serverPlayer, "mining", 5);
				int xp = SkillData.getSkillXP(serverPlayer, "mining");
				int level = SkillData.getSkillLevel(serverPlayer, "mining");
				int xpForNextLevel = level * 100;
				serverPlayer.sendMessage(Text.literal("Mining XP: " + xp + "/" + xpForNextLevel), true);
				checkLevelUp(serverPlayer, "mining");
			}
		});

		// Register combat event
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer && entity.isAlive()) {
				SkillData.addSkillXP(serverPlayer, "combat", 15);
				int xp = SkillData.getSkillXP(serverPlayer, "combat");
				int level = SkillData.getSkillLevel(serverPlayer, "combat");
				int xpForNextLevel = level * 100;
				serverPlayer.sendMessage(Text.literal("Combat XP: " + xp + "/" + xpForNextLevel), true);
				checkLevelUp(serverPlayer, "combat");
			}
			return ActionResult.PASS;
		});

		// Register player join event for syncing data
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.player;
			SkillData.syncData(player);
		});
	}

	private void checkLevelUp(ServerPlayerEntity player, String skill) {
		int xp = SkillData.getSkillXP(player, skill);
		int level = SkillData.getSkillLevel(player, skill);
		int xpForNextLevel = level * 100;
		if (xp >= xpForNextLevel) {
			SkillData.setSkillLevel(player, skill, level + 1);
			player.sendMessage(Text.literal("Congratulations! You've reached " + skill + " level " + (level + 1) + "!"), false);
		}
	}
}