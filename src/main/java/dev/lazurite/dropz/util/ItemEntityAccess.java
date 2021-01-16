package dev.lazurite.dropz.util;

import net.minecraft.entity.ItemEntity;

public interface ItemEntityAccess {
    void merge(ItemEntity itemEntity);
    boolean isBlock();
}
