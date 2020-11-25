package dev.lazurite.dropz.util;

import dev.lazurite.dropz.server.entity.PhysicsItemEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class ItemEntityTracker {
    private static final List<Entry> entries = new ArrayList<>();

    public static void tick(ServerWorld world) {
        List<Entry> yeetus = new ArrayList<>();

        entries.forEach(entry -> {
            if (entry.age > entry.ttl) {
                yeetus.add(entry);
            }

            entry.age++;
        });

        yeetus.forEach(entries::remove);
    }

    public static int size() {
        return entries.size();
    }

    public static void add(PhysicsItemEntity physicsItemEntity, ItemEntity itemEntity, int ttl) {
        entries.add(new Entry(physicsItemEntity, itemEntity, ttl));
    }

    public static void add(PhysicsItemEntity physicsItemEntity, ItemEntity itemEntity) {
        add(physicsItemEntity, itemEntity, 64);
    }

    public static PhysicsItemEntity get(ItemEntity itemEntity) {
        for (Entry entry : entries) {
            if (entry.getItemEntity().equals(itemEntity)) {
                return entry.getPhysicsItemEntity();
            }
        }

        return null;
    }

    public static PhysicsItemEntity get(int id) {
        for (Entry entry : entries) {
            if (entry.getItemEntity().getEntityId() == id) {
                return entry.getPhysicsItemEntity();
            }
        }

        return null;
    }

    public static class Entry {
        private final PhysicsItemEntity physicsItemEntity;
        private final ItemEntity itemEntity;
        private final int ttl;
        private int age;

        public Entry(PhysicsItemEntity physicsItemEntity, ItemEntity itemEntity, int ttl) {
            this.physicsItemEntity = physicsItemEntity;
            this.itemEntity = itemEntity;
            this.ttl = ttl;
        }

        public PhysicsItemEntity getPhysicsItemEntity() {
            return this.physicsItemEntity;
        }

        public ItemEntity getItemEntity() {
            return this.itemEntity;
        }
    }
}
