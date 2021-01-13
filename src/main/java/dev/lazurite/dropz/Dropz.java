package dev.lazurite.dropz;

import dev.lazurite.dropz.shape.ItemShapeProvider;
import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.*;

public class Dropz implements ModInitializer {
	public static final String MODID = "dropz";

	@Override
	public void onInitialize() {
		DynamicEntityRegistry.INSTANCE.register(ItemEntity.class, ItemShapeProvider::get, 1.0f);
	}
}
