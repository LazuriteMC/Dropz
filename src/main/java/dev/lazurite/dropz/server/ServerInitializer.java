package dev.lazurite.dropz.server;

import dev.lazurite.dropz.server.entity.PhysicsDropEntity;
import dev.lazurite.dropz.util.ItemEntityTracker;
import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.physics.shape.BoundingBoxShape;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "dropz";
	public static final String VERSION = "1.0.0";
	public static final String URL = "https://github.com/LazuriteMC/Dropz/releases";

	public static EntityType<Entity> PHYSICS_ITEM_ENTITY;

	@Override
	public void onInitialize() {
		ServerTickEvents.START_WORLD_TICK.register(ItemEntityTracker::tick);

		PHYSICS_ITEM_ENTITY = Registry.register(
				Registry.ENTITY_TYPE,
				new Identifier(MODID, "physics_item_entity"),
				FabricEntityTypeBuilder.create(SpawnGroup.MISC, PhysicsDropEntity::new)
						.dimensions(EntityDimensions.fixed(0.5F, 0.5F))
						.trackRangeBlocks(80)
						.trackedUpdateRate(3)
						.forceTrackedVelocityUpdates(true)
						.build()
		);

		DynamicEntityRegistry.INSTANCE.register(PhysicsDropEntity.class,
				(entity) -> {
//					int id = Item.getRawId(((PhysicsDropEntity) entity).getStack().getItem());
//					System.out.println(id);

					return new BoundingBoxShape(entity.getBoundingBox());
				},
				0.5f);
	}
}
