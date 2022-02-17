package dev.lazurite.dropz.util;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.dropz.mixin.common.ItemEntityMixin;

/**
 * We use this to retreive the real bounding box of the item
 * instead of the entity's bounding box.
 * @see ItemEntityMixin
 */
public interface ItemStorage {
    BoundingBox getBox();
}
