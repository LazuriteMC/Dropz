package dev.lazurite.dropz.server;

import dev.lazurite.dropz.server.entity.PhysicsItemEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "dropz";

	public static final ItemStackType ITEM_STACK_TYPE = new ItemStackType();

	public static EntityType<Entity> PHYSICS_ITEM_ENTITY;

	@Override
	public void onInitialize() {
		TrackedDataHandlerRegistry.register(ITEM_STACK_TYPE);

		PHYSICS_ITEM_ENTITY = Registry.register(
				Registry.ENTITY_TYPE,
				new Identifier(MODID, "physics_item_entity"),
				FabricEntityTypeBuilder.create(SpawnGroup.MISC, PhysicsItemEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.125F)).trackable(80, 3, true).build()
		);
	}
}
