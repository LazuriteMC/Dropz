package dev.lazurite.dropz.mixin.common.access;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemEntity.class)
public interface ItemEntityAccess {
    @Invoker void invokeTryMerge(ItemEntity other);
}
