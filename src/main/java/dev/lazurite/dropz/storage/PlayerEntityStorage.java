package dev.lazurite.dropz.storage;

import dev.lazurite.dropz.mixin.common.PlayerEntityMixin;

/**
 * This interface allows access to the player's
 * internally stored yeet multiplier.
 * @see PlayerEntityMixin
 */
public interface PlayerEntityStorage {
    void setYeetMultiplier(float yeetMultiplier);
    float getYeetMultiplier();
}
