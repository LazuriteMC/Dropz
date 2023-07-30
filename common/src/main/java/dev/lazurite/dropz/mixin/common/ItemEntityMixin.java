package dev.lazurite.dropz.mixin.common;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.dropz.Dropz;
import dev.lazurite.dropz.util.Config;
import dev.lazurite.dropz.physics.ShapeGenerator;
import dev.lazurite.dropz.util.ItemStorage;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.transporter.api.pattern.Pattern;
import dev.lazurite.transporter.impl.Transporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects the {@link EntityPhysicsElement} interface into {@link ItemEntity}.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements EntityPhysicsElement, ItemStorage {

    @Unique private final EntityRigidBody rigidBody = new EntityRigidBody(this);
    @Unique private Pattern prevPattern;
    @Unique private Item prevItem = Items.AIR;
    @Unique private BoundingBox box;

    @Shadow public abstract ItemStack getItem();

    private ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public BoundingBox getBox() {
        return this.box;
    }

    @Override
    public boolean skipVanillaEntityCollisions() {
        return true;
    }

    @Override @Nullable
    public EntityRigidBody getRigidBody() {
        return Config.dropzEnabled ? this.rigidBody : null;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("RETURN"))
    public void init$RETURN(EntityType<? extends ItemEntity> type, Level level, CallbackInfo info) {
        if (!Config.dropzEnabled) return;
        this.rigidBody.setMass(2.5f);

        if (!Config.doBuoyancy) {
            this.rigidBody.setBuoyancyType(ElementRigidBody.BuoyancyType.NONE);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V", at = @At("RETURN"))
    public void init$SetDeltaMovement(Level level, double d, double e, double f, ItemStack itemStack, double g, double h, double i, CallbackInfo ci) {
        if (!Config.dropzEnabled) return;
        float modifier = 1.0f;
        this.rigidBody.setLinearVelocity(new Vector3f((float) g * 20f * modifier, (float) h * 20f * modifier, (float) i * 20f * modifier));
    }

    /**
     * Prevent items from merging with one another except when they collide via collision event.
     * @see Dropz#onCollision
     */
    @Inject(method = "isMergable", at = @At("HEAD"), cancellable = true)
    public void mergeWithNeighbors$HEAD(CallbackInfoReturnable<Boolean> info) {
        if (!Config.dropzEnabled) return;
        info.setReturnValue(false);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER))
    public void tick$tick(CallbackInfo info) {
        if (!Config.dropzEnabled) return;

        var thisEntity = (ItemEntity) (Object) this;

        if (!thisEntity.level().isClientSide && getItem().getItem() instanceof BlockItem) {
            var pattern = Transporter.getPatternBuffer().getItem(Item.getId(getItem().getItem()));

            // Pattern has changed, create a new shape
            if (pattern != prevPattern) {
                this.prevPattern = pattern;
                this.box = ShapeGenerator.create(thisEntity);

                if (this.box != null) {
                    rigidBody.setCollisionShape(MinecraftShape.convex(this.box));
                }
            }
        }

        if (!getItem().getItem().equals(prevItem)) {
            this.prevItem = getItem().getItem();

            // Item has changed, create a new shape
            this.box = ShapeGenerator.create(thisEntity);

            if (this.box != null) {
                this.rigidBody.setCollisionShape(MinecraftShape.convex(this.box));
            }
        }
    }

}
