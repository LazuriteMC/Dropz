package dev.lazurite.dropz;

import dev.lazurite.dropz.util.Config;
import dev.lazurite.dropz.mixin.common.access.ItemEntityAccess;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.api.event.collision.ElementCollisionEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.entity.item.ItemEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Dropz implements ModInitializer {
	public static final String modid = "dropz";
	public static final Logger LOGGER = LogManager.getLogger("Dropz");

	@Override
	public void onInitialize() {
		LOGGER.info("Yeet.");
		Config.getInstance().load();
		ElementCollisionEvents.ELEMENT_COLLISION.register(Dropz::onCollision);
	}

	public static void onCollision(PhysicsElement element1, PhysicsElement element2, float impulse) {
		if (Config.getInstance().doItemCombination && element1 instanceof ItemEntity item1 && element2 instanceof ItemEntity item2) {
			final var space = element1.getRigidBody().getSpace();

			if (space.isServer()) {
				((ItemEntityAccess) item1).invokeTryToMerge(item2);
			}
		}
	}
}