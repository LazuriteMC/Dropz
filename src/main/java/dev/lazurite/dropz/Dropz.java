package dev.lazurite.dropz;

import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.api.shape.provider.BoundingBoxShapeProvider;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.*;

public class Dropz implements ModInitializer {
	public static final String MODID = "dropz";

	@Override
	public void onInitialize() {
		DynamicEntityRegistry.INSTANCE.register(ItemEntity.class, BoundingBoxShapeProvider::get, 1.0f);
	}
}
