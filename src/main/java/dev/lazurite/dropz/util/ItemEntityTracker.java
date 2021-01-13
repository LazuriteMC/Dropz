package dev.lazurite.dropz.util;

import dev.lazurite.dropz.server.entity.PhysicsDropEntity;
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

    public static void add(PhysicsDropEntity physicsItemEntity, ItemEntity itemEntity, int ttl) {
        entries.add(new Entry(physicsItemEntity, itemEntity, ttl));
    }

    public static void add(PhysicsDropEntity physicsItemEntity, ItemEntity itemEntity) {
        add(physicsItemEntity, itemEntity, 64);
    }

    public static PhysicsDropEntity get(ItemEntity itemEntity) {
        for (Entry entry : entries) {
            if (entry.getItemEntity().equals(itemEntity)) {
                return entry.getPhysicsItemEntity();
            }
        }

        return null;
    }

    public static PhysicsDropEntity get(int id) {
        for (Entry entry : entries) {
            if (entry.getItemEntity().getEntityId() == id) {
                return entry.getPhysicsItemEntity();
            }
        }

        return null;
    }

    public static class Entry {
        private final PhysicsDropEntity physicsItemEntity;
        private final ItemEntity itemEntity;
        private final int ttl;
        private int age;

        public Entry(PhysicsDropEntity physicsItemEntity, ItemEntity itemEntity, int ttl) {
            this.physicsItemEntity = physicsItemEntity;
            this.itemEntity = itemEntity;
            this.ttl = ttl;
        }

        public PhysicsDropEntity getPhysicsItemEntity() {
            return this.physicsItemEntity;
        }

        public ItemEntity getItemEntity() {
            return this.itemEntity;
        }
    }
}
