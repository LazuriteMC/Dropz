package dev.lazurite.dropz.util;

import net.minecraft.entity.ItemEntity;
import dev.lazurite.dropz.mixin.common.ItemEntityMixin;

/**
 * This interface is what allows access to the
 * following methods in {@link ItemEntityMixin}.
 */
public interface ItemEntityAccess {
    void merge(ItemEntity itemEntity);
    boolean isBlock();
}