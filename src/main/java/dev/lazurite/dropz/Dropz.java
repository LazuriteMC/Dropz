package dev.lazurite.dropz;

import dev.lazurite.dropz.mixin.common.ItemEntityMixin;
import dev.lazurite.dropz.util.storage.ItemEntityStorage;
import dev.lazurite.rayon.api.event.EntityBodyCollisionEvent;
import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.api.shape.provider.BoundingBoxShapeProvider;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main entrypoint for Dropz. All it really does is register {@link ItemEntity} with
 * Rayon. It also sets up a collision event for {@link ItemEntity}s which allows them
 * to merge when they collide with eachother.
 * @see ItemEntityStorage
 * @see ItemEntityMixin
 */
public class Dropz implements ModInitializer {
	public static final String MODID = "dropz";
	public static final Logger LOGGER = LogManager.getLogger("Dropz");

	@Override
	public void onInitialize() {
		LOGGER.info("Yeet.");
		DynamicEntityRegistry.INSTANCE.register(ItemEntity.class, BoundingBoxShapeProvider::get, 2.5f);
		EntityBodyCollisionEvent.ENTITY_COLLISION.register(ItemEntityStorage::onCollide);
	}
}
