package dev.lazurite.dropz;

import dev.lazurite.dropz.mixin.common.ItemEntityMixin;
import dev.lazurite.dropz.mixin.common.access.ItemEntityAccess;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.api.event.collision.ElementCollisionEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.entity.item.ItemEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main entrypoint for Dropz. All it really does is register {@link ItemEntity} with
 * Rayon. It also sets up a collision event for {@link ItemEntity}s which allows them
 * to merge when they collide with eachother.
 * @see ItemEntityMixin
 */
public class Dropz implements ModInitializer {
	public static final String modid = "dropz";
	public static final Logger LOGGER = LogManager.getLogger("Dropz");

	@Override
	public void onInitialize() {
		LOGGER.info("Yeet.");
		ElementCollisionEvents.ELEMENT_COLLISION.register(Dropz::onCollision);
	}

	public static void onCollision(PhysicsElement element1, PhysicsElement element2, float impulse) {
		if (element1 instanceof ItemEntity item1 && element2 instanceof ItemEntity item2) {
			final var space = element1.getRigidBody().getSpace();

			if (space.isServer()) {
				((ItemEntityAccess) item1).invokeTryToMerge(item2);
			}
		}
	}
}