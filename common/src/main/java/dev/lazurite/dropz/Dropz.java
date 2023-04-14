package dev.lazurite.dropz;

import dev.lazurite.dropz.util.Config;
import dev.lazurite.dropz.mixin.common.access.ItemEntityAccess;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.api.event.collision.ElementCollisionEvents;
import net.minecraft.world.entity.item.ItemEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Dropz {

	public static final String MODID = "dropz";
	public static final Logger LOGGER = LogManager.getLogger("Dropz");

	public static void initialize() {
		LOGGER.info("Yeet.");
		ElementCollisionEvents.ELEMENT_COLLISION.register(Dropz::onCollision);
	}

	public static void onCollision(PhysicsElement element1, PhysicsElement element2, float impulse) {
		if (Config.doItemCombination && element1 instanceof ItemEntity item1 && element2 instanceof ItemEntity item2) {
			var space = element1.getRigidBody().getSpace();

			if (space.isServer()) {
				space.getWorkerThread().getParentExecutor().execute(() -> {
					((ItemEntityAccess) item1).invokeTryToMerge(item2);
				});
			}
		}
	}
}