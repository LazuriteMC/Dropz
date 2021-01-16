package dev.lazurite.dropz;

import dev.lazurite.dropz.util.ItemEntityAccess;
import dev.lazurite.rayon.api.event.EntityBodyCollisionEvent;
import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.api.shape.provider.BoundingBoxShapeProvider;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.*;
import net.minecraft.world.World;

public class Dropz implements ModInitializer {
	public static final String MODID = "dropz";

	@Override
	public void onInitialize() {
		DynamicEntityRegistry.INSTANCE.register(ItemEntity.class, BoundingBoxShapeProvider::get, 2.5f);
		EntityBodyCollisionEvent.ENTITY_COLLISION.register((body1, body2) -> {
			if (body1.getEntity() instanceof ItemEntity && body2.getEntity() instanceof ItemEntity) {
				ItemEntity item1 = (ItemEntity) body1.getEntity();
				ItemEntity item2 = (ItemEntity) body1.getEntity();
				World world = item1.getEntityWorld();

				if (!world.isClient() && item1.canMerge() && item2.canMerge()) {
					((ItemEntityAccess) item1).merge(item2);
				}
			}
		});
	}
}
