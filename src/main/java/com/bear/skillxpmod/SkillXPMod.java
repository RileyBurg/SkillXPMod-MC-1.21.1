package com.bear.skillxpmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;


import static com.bear.skillxpmod.SkillValues.*;

public class SkillXPMod implements ModInitializer {

	@Override
	public void onInitialize() {
		registerFishingEvent();
		registerBlockBreakEvents();
		registerCombatEvents();
		registerPlayerJoinEvent();
	}

	private void registerFishingEvent() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer && player.getStackInHand(hand).getItem() == Items.FISHING_ROD) {
				if (player.fishHook != null && player.fishHook.isRemoved() && (player.fishHook.getHookedEntity() != null || world.getRandom().nextFloat() < 0.9f)) {
					SkillData.addSkillXP(serverPlayer, "fishing", 10);
					sendXPMessage(serverPlayer,"fishing", 10, "fishing");
					checkLevelUp(serverPlayer, "fishing");
				}
			}
			return TypedActionResult.pass(player.getStackInHand(hand));
		});
	}

	private void registerBlockBreakEvents() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
				Block block = state.getBlock();
				if (LOG_BLOCKS.contains(block)) {
					SkillData.addSkillXP(serverPlayer, "woodcutting", 5);
					sendXPMessage(serverPlayer,"woodcutting", 5, block.getName().getString());
					checkLevelUp(serverPlayer, "woodcutting");
				} else if (!COOKING_BLOCKS.contains(block)) {
					int xpToAward = MINING_XP_VALUES.getOrDefault(block, 1);
					SkillData.addSkillXP(serverPlayer, "mining", xpToAward);
					sendXPMessage(serverPlayer, "mining", xpToAward, block.getName().getString());
					checkLevelUp(serverPlayer, "mining");
				}
			}
		});
	}

	private void registerCombatEvents() {
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer && entity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
				EntityType<?> entityType = entity.getType();
				int xpToAward = COMBAT_HIT_XP_VALUES.getOrDefault(entityType, 1); // Default 1 XP for unlisted mobs, super easy!
				SkillData.addSkillXP(serverPlayer, "combat", xpToAward);
				sendXPMessage(serverPlayer, "combat", xpToAward, entity.getName().getString());
				checkLevelUp(serverPlayer, "combat");
			}
			return ActionResult.PASS;
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (!entity.getWorld().isClient && entity instanceof LivingEntity && damageSource.getSource() instanceof ServerPlayerEntity serverPlayer) {
				EntityType<?> entityType = entity.getType();
				int xpToAward = COMBAT_KILL_XP_VALUES.getOrDefault(entityType, 10); // Default 10 XP for unlisted mobs, super easy!
				SkillData.addSkillXP(serverPlayer, "combat", xpToAward);
				sendXPMessage(serverPlayer, "combat", xpToAward,entity.getName().getString());
				checkLevelUp(serverPlayer, "combat");
			}
		});
	}

	private  void registerPlayerJoinEvent() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.player;
			SkillData.syncData(player);
		});
	}

	public static int calculateXPForNextLevel(int currentLevel) {
		if (currentLevel == 0) {
			return 100;
		}
		double xpRequired = 100.0;
		for (int i = 1; i <= currentLevel; i++) {
			xpRequired *= 1.4;
		}
		return (int) Math.round(xpRequired);
	}

	public static void sendXPMessage(ServerPlayerEntity player, String skill, int amount, String source) {
		int xp = SkillData.getSkillXP(player, skill);
		int level = SkillData.getSkillLevel(player, skill);
		int xpForNextLevel = calculateXPForNextLevel(level);
		player.sendMessage(Text.literal(skill.substring(0, 1).toUpperCase() + skill.substring(1) +
				" XP: " + xp + "/" + xpForNextLevel + " (+" + amount + " from " + source + ")"), true);
		checkLevelUp(player, skill);
	}

	public static void checkLevelUp(ServerPlayerEntity player, String skill) {
		int xp = SkillData.getSkillXP(player, skill);
		int level = SkillData.getSkillLevel(player, skill);
		int xpForNextLevel = calculateXPForNextLevel(level);
		if (xp >= xpForNextLevel) {
			SkillData.setSkillLevel(player, skill, level + 1);
			player.sendMessage(Text.literal("Congratulations! You've reached " + skill + " level " + (level + 1) + "!"), false);
		}
	}
}