package dev.lazurite.dropz.util;

import dev.lazurite.dropz.mixin.common.PlayerEntityMixin;

/**
 * This interface allows access to the player's
 * internally stored yeet multiplier.
 * @see PlayerEntityMixin
 */
public interface PlayerEntityAccess {
    void setYeetMultiplier(float yeetMultiplier);
    float getYeetMultiplier();
}
