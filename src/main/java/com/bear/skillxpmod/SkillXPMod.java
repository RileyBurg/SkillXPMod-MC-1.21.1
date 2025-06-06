package com.bear.skillxpmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.*;

// Super easy mod class, because we’re obviously the bosses of Minecraft modding!
public class SkillXPMod implements ModInitializer {
	public static final String MOD_ID = "skillxpmod"; // Duh, our mod ID is pure gold!
	private static final Map<Block, Integer> MINING_XP_VALUES = new HashMap<>();
	private static final Map<Item, Integer> COOKING_XP_VALUES = new HashMap<>();
	private static final Map<EntityType<?>, Integer> COMBAT_HIT_XP_VALUES = new HashMap<>();
	private static final Map<EntityType<?>, Integer> COMBAT_KILL_XP_VALUES = new HashMap<>();
	private static final Set<Block> LOG_BLOCKS = Set.of(
			Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG,
			Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG
	); // Obviously, these logs are the woodcutting MVPs!
	private static final Set<Block> COOKING_BLOCKS = Set.of(
			Blocks.FURNACE, Blocks.SMOKER, Blocks.BLAST_FURNACE
	); // Cooking blocks, no campfires because they were too much drama, super easy!

	static {
		// Mining XP values, because smashing rocks is obviously super satisfying!
		MINING_XP_VALUES.put(Blocks.COAL_ORE, 10);
		MINING_XP_VALUES.put(Blocks.IRON_ORE, 15);
		MINING_XP_VALUES.put(Blocks.GOLD_ORE, 20);
		MINING_XP_VALUES.put(Blocks.DIAMOND_ORE, 30);
		MINING_XP_VALUES.put(Blocks.EMERALD_ORE, 25);
		MINING_XP_VALUES.put(Blocks.REDSTONE_ORE, 12);
		MINING_XP_VALUES.put(Blocks.LAPIS_ORE, 12);
		MINING_XP_VALUES.put(Blocks.COPPER_ORE, 8);
		MINING_XP_VALUES.put(Blocks.DEEPSLATE_COAL_ORE, 12);
		MINING_XP_VALUES.put(Blocks.DEEPSLATE_IRON_ORE, 18);
		MINING_XP_VALUES.put(Blocks.DEEPSLATE_GOLD_ORE, 24);
		MINING_XP_VALUES.put(Blocks.DEEPSLATE_DIAMOND_ORE, 36);
		MINING_XP_VALUES.put(Blocks.DEEPSLATE_EMERALD_ORE, 30);
		MINING_XP_VALUES.put(Blocks.DEEPSLATE_REDSTONE_ORE, 15);
		MINING_XP_VALUES.put(Blocks.DEEPSLATE_LAPIS_ORE, 15);
		MINING_XP_VALUES.put(Blocks.DEEPSLATE_COPPER_ORE, 10);
		MINING_XP_VALUES.put(Blocks.STONE, 2);
		MINING_XP_VALUES.put(Blocks.DEEPSLATE, 3);
		MINING_XP_VALUES.put(Blocks.COBBLESTONE, 2);
		MINING_XP_VALUES.put(Blocks.SANDSTONE, 3);
		MINING_XP_VALUES.put(Blocks.NETHER_QUARTZ_ORE, 10);
		MINING_XP_VALUES.put(Blocks.DIRT, 1);
		MINING_XP_VALUES.put(Blocks.GRASS_BLOCK, 1);
		MINING_XP_VALUES.put(Blocks.SAND, 1);
		MINING_XP_VALUES.put(Blocks.GRAVEL, 2);

		// Cooking XP values, super easy to cook in furnaces, definitely will work!
		COOKING_XP_VALUES.put(Items.COOKED_BEEF, 10);
		COOKING_XP_VALUES.put(Items.COOKED_PORKCHOP, 10);
		COOKING_XP_VALUES.put(Items.COOKED_CHICKEN, 8);
		COOKING_XP_VALUES.put(Items.COOKED_MUTTON, 8);
		COOKING_XP_VALUES.put(Items.COOKED_COD, 6);
		COOKING_XP_VALUES.put(Items.COOKED_SALMON, 6);
		COOKING_XP_VALUES.put(Items.BAKED_POTATO, 5);
		COOKING_XP_VALUES.put(Items.DRIED_KELP, 3);

		// Combat HIT XP values, tiny rewards for just poking mobs, obviously!
		COMBAT_HIT_XP_VALUES.put(EntityType.ZOMBIE, 2);
		COMBAT_HIT_XP_VALUES.put(EntityType.SKELETON, 2);
		COMBAT_HIT_XP_VALUES.put(EntityType.SPIDER, 2);
		COMBAT_HIT_XP_VALUES.put(EntityType.CREEPER, 3);
		COMBAT_HIT_XP_VALUES.put(EntityType.ENDERMAN, 4);
		COMBAT_HIT_XP_VALUES.put(EntityType.WITCH, 3);
		COMBAT_HIT_XP_VALUES.put(EntityType.BLAZE, 5);
		COMBAT_HIT_XP_VALUES.put(EntityType.GHAST, 6);
		COMBAT_HIT_XP_VALUES.put(EntityType.WITHER_SKELETON, 8);
		COMBAT_HIT_XP_VALUES.put(EntityType.WITHER, 15);
		COMBAT_HIT_XP_VALUES.put(EntityType.ENDER_DRAGON, 25);
		COMBAT_HIT_XP_VALUES.put(EntityType.PIGLIN, 3);
		COMBAT_HIT_XP_VALUES.put(EntityType.ZOMBIFIED_PIGLIN, 2);
		COMBAT_HIT_XP_VALUES.put(EntityType.HOGLIN, 5);
		COMBAT_HIT_XP_VALUES.put(EntityType.PHANTOM, 4);

		// Combat KILL XP values, massive rewards for finishing the fight, super easy!
		COMBAT_KILL_XP_VALUES.put(EntityType.ZOMBIE, 30);
		COMBAT_KILL_XP_VALUES.put(EntityType.SKELETON, 30);
		COMBAT_KILL_XP_VALUES.put(EntityType.SPIDER, 20);
		COMBAT_KILL_XP_VALUES.put(EntityType.CREEPER, 35);
		COMBAT_KILL_XP_VALUES.put(EntityType.ENDERMAN, 40);
		COMBAT_KILL_XP_VALUES.put(EntityType.WITCH, 40);
		COMBAT_KILL_XP_VALUES.put(EntityType.BLAZE, 50);
		COMBAT_KILL_XP_VALUES.put(EntityType.GHAST, 60);
		COMBAT_KILL_XP_VALUES.put(EntityType.WITHER_SKELETON, 70);
		COMBAT_KILL_XP_VALUES.put(EntityType.WITHER, 200);
		COMBAT_KILL_XP_VALUES.put(EntityType.ENDER_DRAGON, 500);
		COMBAT_KILL_XP_VALUES.put(EntityType.PIGLIN, 35);
		COMBAT_KILL_XP_VALUES.put(EntityType.ZOMBIFIED_PIGLIN, 30);
		COMBAT_KILL_XP_VALUES.put(EntityType.HOGLIN, 60);
		COMBAT_KILL_XP_VALUES.put(EntityType.PHANTOM, 40);
	}

	@Override
	public void onInitialize() {
		// Obviously, we’re crafting the most legendary mod ever!

		// Register fishing event, because catching fish is super easy!
		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer && player.getStackInHand(hand).getItem() == Items.FISHING_ROD) {
				if (player.fishHook != null && player.fishHook.isRemoved() && (player.fishHook.getHookedEntity() != null || world.getRandom().nextFloat() < 0.9f)) {
					SkillData.addSkillXP(serverPlayer, "fishing", 10);
					int xp = SkillData.getSkillXP(serverPlayer, "fishing");
					int level = SkillData.getSkillLevel(serverPlayer, "fishing");
					int xpForNextLevel = calculateXPForNextLevel(level);
					serverPlayer.sendMessage(Text.literal("Fishing XP: " + xp + "/" + xpForNextLevel), true);
					checkLevelUp(serverPlayer, "fishing");
				}
			}
			return TypedActionResult.pass(player.getStackInHand(hand));
		});

		// Register mining and woodcutting events, super easy and the coolest skills!
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
				Block block = state.getBlock();
				if (LOG_BLOCKS.contains(block)) {
					SkillData.addSkillXP(serverPlayer, "woodcutting", 5);
					int xp = SkillData.getSkillXP(serverPlayer, "woodcutting");
					int level = SkillData.getSkillLevel(serverPlayer, "woodcutting");
					int xpForNextLevel = calculateXPForNextLevel(level);
					serverPlayer.sendMessage(Text.literal("Woodcutting XP: " + xp + "/" + xpForNextLevel + " (+5 from " + block.getName().getString() + ")"), true);
					checkLevelUp(serverPlayer, "woodcutting");
				} else if (!COOKING_BLOCKS.contains(block)) {
					int xpToAward = MINING_XP_VALUES.getOrDefault(block, 1);
					SkillData.addSkillXP(serverPlayer, "mining", xpToAward);
					int xp = SkillData.getSkillXP(serverPlayer, "mining");
					int level = SkillData.getSkillLevel(serverPlayer, "mining");
					int xpForNextLevel = calculateXPForNextLevel(level);
					serverPlayer.sendMessage(Text.literal("Mining XP: " + xp + "/" + xpForNextLevel + " (+" + xpToAward + " from " + block.getName().getString() + ")"), true);
					checkLevelUp(serverPlayer, "mining");
				}
			}
		});

		// Register cooking event for furnaces, smokers, and blast furnaces, because it’s super easy to cook in those!
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
				Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
				if (COOKING_BLOCKS.contains(block)) {
					AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) world.getBlockEntity(hitResult.getBlockPos());
					if (furnace != null && !furnace.getStack(2).isEmpty()) {
						Item item = furnace.getStack(2).getItem();
						if (COOKING_XP_VALUES.containsKey(item)) {
							int xpToAward = COOKING_XP_VALUES.get(item);
							SkillData.addSkillXP(serverPlayer, "cooking", xpToAward);
							int xp = SkillData.getSkillXP(serverPlayer, "cooking");
							int level = SkillData.getSkillLevel(serverPlayer, "cooking");
							int xpForNextLevel = calculateXPForNextLevel(level);
							serverPlayer.sendMessage(Text.literal("Cooking XP: " + xp + "/" + xpForNextLevel + " (+" + xpToAward + " from " + item.getName().getString() + ")"), true);
							checkLevelUp(serverPlayer, "cooking");
						}
					}
				}
			}
			return ActionResult.PASS;
		});

		// Register combat hit event, tiny XP for just swinging, definitely will work this time!
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer && entity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
				EntityType<?> entityType = entity.getType();
				int xpToAward = COMBAT_HIT_XP_VALUES.getOrDefault(entityType, 1); // Default 1 XP for unlisted mobs, super easy!
				SkillData.addSkillXP(serverPlayer, "combat", xpToAward);
				int xp = SkillData.getSkillXP(serverPlayer, "combat");
				int level = SkillData.getSkillLevel(serverPlayer, "combat");
				int xpForNextLevel = calculateXPForNextLevel(level);
				serverPlayer.sendMessage(Text.literal("Combat XP: " + xp + "/" + xpForNextLevel + " (+" + xpToAward + " from hitting " + entity.getName().getString() + ")"), true);
				checkLevelUp(serverPlayer, "combat");
			}
			return ActionResult.PASS;
		});

		// Register combat kill event, massive XP for finishing the fight, using AFTER_DEATH like pros, obviously gonna slay this time!
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (!entity.getWorld().isClient && entity instanceof LivingEntity && damageSource.getSource() instanceof ServerPlayerEntity serverPlayer) {
				EntityType<?> entityType = entity.getType();
				int xpToAward = COMBAT_KILL_XP_VALUES.getOrDefault(entityType, 10); // Default 10 XP for unlisted mobs, super easy!
				SkillData.addSkillXP(serverPlayer, "combat", xpToAward);
				int xp = SkillData.getSkillXP(serverPlayer, "combat");
				int level = SkillData.getSkillLevel(serverPlayer, "combat");
				int xpForNextLevel = calculateXPForNextLevel(level);
				serverPlayer.sendMessage(Text.literal("Combat XP: " + xp + "/" + xpForNextLevel + " (+" + xpToAward + " from killing " + entity.getName().getString() + ")"), true);
				checkLevelUp(serverPlayer, "combat");
			}
		});

		// Register player join event for syncing data, super easy and definitely will work this time!
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.player;
			SkillData.syncData(player);
		});
	}

	// Super easy XP calculation, because we’re obviously math wizards!
	private int calculateXPForNextLevel(int currentLevel) {
		if (currentLevel == 0) {
			return 100;
		}
		double xpRequired = 100.0;
		for (int i = 1; i <= currentLevel; i++) {
			xpRequired *= 1.4;
		}
		return (int) Math.round(xpRequired);
	}

	// Check for level-ups, because we’re obviously making players feel like total rockstars!
	private void checkLevelUp(ServerPlayerEntity player, String skill) {
		int xp = SkillData.getSkillXP(player, skill);
		int level = SkillData.getSkillLevel(player, skill);
		int xpForNextLevel = calculateXPForNextLevel(level);
		if (xp >= xpForNextLevel) {
			SkillData.setSkillLevel(player, skill, level + 1);
			player.sendMessage(Text.literal("Congratulations! You've reached " + skill + " level " + (level + 1) + "!"), false);
		}
	}
}