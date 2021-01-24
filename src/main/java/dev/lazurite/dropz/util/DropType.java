package dev.lazurite.dropz.util;

import dev.lazurite.dropz.mixin.client.ItemEntityRendererMixin;
import dev.lazurite.dropz.mixin.common.ItemEntityMixin;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;

/**
 * Mainly for handling bounding box information for specific types of items and blocks.
 * Most items and blocks will fall into the {@link DropType#ITEM} or {@link DropType#BLOCK}
 * category. The rest are handled based on the output of {@link DropType#get(ItemStack)}.
 * @see ItemEntityRendererMixin
 * @see ItemEntityMixin
 */
public enum DropType {
    ITEM(new Box(-0.25, -0.25, -0.05, 0.25, 0.25, 0.05), 1.0f, 0.1125f),
    BLOCK(new Box(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15), 2.0f, 0.1875f),

    /* Special */
    DOOR(new Box(-0.15, -0.25, -0.04, 0.15, 0.25, 0.06), 2.5f, 0.125f),
    HEAD(new Box(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15), 2.0f, 0.07f),
    DRAGON(new Box(-0.2, -0.2, -0.4, 0.2, 0.2, 0.4), 3.0f, 0.0875f),
    BED(new Box(-0.15, -0.1, -0.25, 0.15, 0.1, 0.25), 3.0f, 0.0f),
    SLAB(new Box(-0.15, -0.075, -0.15, 0.15, 0.075, 0.15), 1.0f, 0.125f),
    FENCE(new Box(-0.085, -0.15, -0.15, 0.085, 0.15, 0.15), 1.5f, 0.1875f),
    TRAP(new Box(-0.15, -0.05, -0.15, 0.15, 0.05, 0.15), 1.5f, 0.0875f),
    PLATE(new Box(-0.15, -0.05, -0.15, 0.15, 0.05, 0.15), 1.5f, 0.07f),
    BUTTON(new Box(-0.075, -0.05, -0.05, 0.075, 0.05, 0.05), 0.25f, 0.1875f);

    private final Box box;
    private final float mass;
    private final float offset;

    DropType(Box box, float mass, float offset) {
        this.box = box;
        this.mass = mass;
        this.offset = offset;
    }

    public Box getBox() {
        return this.box;
    }

    public float getMass() {
        return this.mass;
    }

    public float getOffset() {
        return this.offset;
    }

    public static DropType get(ItemStack stack) {
        Block block = Registry.BLOCK.get(Registry.ITEM.getId(stack.getItem()));

        if (block != Blocks.AIR && !block.canMobSpawnInside()) {
            String key = block.getTranslationKey();

            if (key.contains("fence") || key.contains("wall")) {
                return FENCE;
            } else if (key.contains("slab") || key.contains("sensor")) {
                return SLAB;
            } else if (key.contains("trap")) {
                return TRAP;
            } else if (key.contains("door")) {
                return DOOR;
            } else if (key.contains("bed")) {
                return BED;
            } else if (key.contains("lantern") || key.contains("campfire") || key.contains("bell") || key.contains("iron_bars") || key.contains("chain") || key.contains("pane") || key.contains("hopper") || key.contains("cobweb")) {
                return ITEM;
            } else {
                return BLOCK;
            }
        } else {
            String key = stack.getItem().getTranslationKey();

            if (key.contains("plate") || key.contains("carpet") || key.equals("minecraft:snow")) {
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
