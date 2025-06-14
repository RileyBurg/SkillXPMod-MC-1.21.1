package com.bear.skillxpmod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.*;

public class SkillValues {
    public static final String MOD_ID = "skillxpmod";
    public static final Map<Block, Integer> MINING_XP_VALUES = new HashMap<>();
    public static final Map<Item, Integer> COOKING_XP_VALUES = new HashMap<>();
    public static final Map<EntityType<?>, Integer> COMBAT_HIT_XP_VALUES = new HashMap<>();
    public static final Map<EntityType<?>, Integer> COMBAT_KILL_XP_VALUES = new HashMap<>();
    public static final Map<Item, Integer> SMITHING_XP_VALUES = new HashMap<>();

    public static final Set<Block> LOG_BLOCKS = Set.of(
            Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG,
            Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG
    );

    public static final Set<Block> COOKING_BLOCKS = Set.of(
            Blocks.FURNACE, Blocks.SMOKER, Blocks.BLAST_FURNACE
    );

    static {
        SMITHING_XP_VALUES.put(Items.IRON_INGOT, 15);           // From smelting iron ore or raw iron
        SMITHING_XP_VALUES.put(Items.COPPER_INGOT, 10);         // From copper ore or raw copper
        SMITHING_XP_VALUES.put(Items.GOLD_INGOT, 20);           // From gold ore or raw gold
        SMITHING_XP_VALUES.put(Items.NETHERITE_SCRAP, 50);      // From ancient debris
        SMITHING_XP_VALUES.put(Items.NETHERITE_INGOT, 0);       // Crafting only, not smelted
        SMITHING_XP_VALUES.put(Items.STONE, 5);                 // From cobblestone
        SMITHING_XP_VALUES.put(Items.SMOOTH_STONE, 7);          // From stone
        SMITHING_XP_VALUES.put(Items.GLASS, 4);                 // From sand
        SMITHING_XP_VALUES.put(Items.BRICK, 6);                 // From clay ball
        SMITHING_XP_VALUES.put(Items.CRACKED_STONE_BRICKS, 5); // From stone bricks

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

        COOKING_XP_VALUES.put(Items.COOKED_BEEF, 10);
        COOKING_XP_VALUES.put(Items.COOKED_PORKCHOP, 10);
        COOKING_XP_VALUES.put(Items.COOKED_CHICKEN, 8);
        COOKING_XP_VALUES.put(Items.COOKED_MUTTON, 8);
        COOKING_XP_VALUES.put(Items.COOKED_COD, 6);
        COOKING_XP_VALUES.put(Items.COOKED_SALMON, 6);
        COOKING_XP_VALUES.put(Items.BAKED_POTATO, 5);
        COOKING_XP_VALUES.put(Items.DRIED_KELP, 3);

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
}