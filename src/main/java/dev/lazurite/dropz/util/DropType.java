package dev.lazurite.dropz.util;

import dev.lazurite.dropz.mixin.client.ItemEntityRendererMixin;
import dev.lazurite.dropz.mixin.common.ItemEntityMixin;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

/**
 * Mainly for handling bounding box information for specific types of items and blocks.
 * Most items and blocks will fall into the {@link DropType#ITEM} or {@link DropType#BLOCK}
 * category. The rest are handled based on the output of {@link DropType#get(ItemStack)}.
 * @see ItemEntityRendererMixin
 * @see ItemEntityMixin
 */
public enum DropType {
    ITEM(new AABB(-0.25, -0.25, -0.03, 0.25, 0.25, 0.03), 1.0f, 0.1125f),
    BLOCK(new AABB(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15), 2.0f, 0.1875f),

    /* Special */
    DOOR(new AABB(-0.15, -0.25, -0.04, 0.15, 0.25, 0.06), 2.5f, 0.125f),
    HEAD(new AABB(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15), 2.0f, 0.07f),
    DRAGON(new AABB(-0.2, -0.2, -0.4, 0.2, 0.2, 0.4), 3.0f, 0.0875f),
    BED(new AABB(-0.15, -0.1, -0.25, 0.15, 0.1, 0.25), 3.0f, 0.0f),
    SLAB(new AABB(-0.15, -0.075, -0.15, 0.15, 0.075, 0.15), 1.0f, 0.125f),
    FENCE(new AABB(-0.085, -0.15, -0.15, 0.085, 0.15, 0.15), 1.5f, 0.1875f),
    TRAP(new AABB(-0.15, -0.05, -0.15, 0.15, 0.05, 0.15), 1.5f, 0.0875f),
    PLATE(new AABB(-0.15, -0.05, -0.15, 0.15, 0.05, 0.15), 1.5f, 0.07f),
    BUTTON(new AABB(-0.075, -0.05, -0.05, 0.075, 0.05, 0.05), 0.25f, 0.1875f);

    private final AABB aabb;
    private final float mass;
    private final float offset;

    DropType(AABB aabb, float mass, float offset) {
        this.aabb = aabb;
        this.mass = mass;
        this.offset = offset;
    }

    public AABB getAabb() {
        return this.aabb;
    }

    public float getMass() {
        return this.mass;
    }

    public float getOffset() {
        return this.offset;
    }

    public static DropType get(ItemStack stack) {
        Block block = Registry.BLOCK.get(Registry.ITEM.getKey(stack.getItem()));

        if (block != Blocks.AIR && !block.isPossibleToRespawnInThis()) {
            String key = block.getDescriptionId();

            if (key.contains("fence") || key.contains("wall")) {
                return FENCE;
            } else if (key.contains("slab") || key.contains("daylight")) {
                return SLAB;
            } else if (key.contains("trap")) {
                return TRAP;
            } else if (key.contains("door")) {
                return DOOR;
            } else if (key.contains("bed") && !key.contains("bedrock")) {
                return BED;
            } else if (key.contains("lantern") || key.contains("campfire") || key.contains("bell") || key.contains("iron_bars") || key.contains("chain") || key.contains("pane") || key.contains("hopper") || key.contains("cobweb")) {
                return ITEM;
            } else {
                return BLOCK;
            }
        } else {
            String key = stack.getItem().getDescriptionId();

            if (key.contains("pressure") || key.contains("carpet") || key.equals("minecraft:snow")) {
                return PLATE;
            } else if (key.contains("button")) {
                return BUTTON;
            } else if (key.contains("chorus") || key.contains("scaffolding")) {
                return BLOCK;
            } else if (key.contains("dragon")) {
                return DRAGON;
            } else if (key.contains("head")) {
                return HEAD;
            } else {
                return ITEM;
            }
        }
    }
}
