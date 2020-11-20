package dev.lazurite.dropz.util;

import dev.lazurite.api.physics.network.tracker.generic.GenericType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class ItemStackType implements GenericType<ItemStack> {
    @Override
    public void toTag(CompoundTag compoundTag, String s, ItemStack itemStack) {
        itemStack.toTag(compoundTag);
    }

    @Override
    public ItemStack fromTag(CompoundTag compoundTag, String s) {
        return ItemStack.fromTag(compoundTag);
    }

    @Override
    public Class<ItemStack> getClassType() {
        return ItemStack.class;
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, ItemStack itemStack) {
        packetByteBuf.writeItemStack(itemStack);
    }

    @Override
    public ItemStack read(PacketByteBuf packetByteBuf) {
        return packetByteBuf.readItemStack();
    }

    @Override
    public ItemStack copy(ItemStack itemStack) {
        return itemStack.copy();
    }
}
