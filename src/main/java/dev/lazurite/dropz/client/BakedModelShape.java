package dev.lazurite.dropz.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.Pair;
import shadow.com.bulletphysics.collision.shapes.CompoundShape;
import shadow.com.bulletphysics.collision.shapes.ConvexHullShape;
import shadow.com.bulletphysics.linearmath.Transform;
import shadow.com.bulletphysics.util.ObjectArrayList;
import shadow.javax.vecmath.Matrix4f;
import shadow.javax.vecmath.Quat4f;
import shadow.javax.vecmath.Vector3f;

import java.util.Random;
import java.util.function.Predicate;

public class BakedModelShape extends CompoundShape {
    private final BakedModel bakedModel;
    private final BlockState blockState;

    public BakedModelShape(BakedModel bakedModel, BlockState blockState) {
        this.bakedModel = bakedModel;
        this.blockState = blockState;

        if (bakedModel instanceof MultipartBakedModel) {
            MultipartBakedModel multi = (MultipartBakedModel) bakedModel;

            for (Pair<Predicate<BlockState>, BakedModel> component : multi.components) {
                BakedModel inner = component.getRight();

                if (inner instanceof BasicBakedModel) {
                    BasicBakedModel basic = (BasicBakedModel) inner;

                    for (Direction direction : Direction.values()) {
                        for (BakedQuad bakedQuad : basic.faceQuads.get(direction)) {
                            addQuad(bakedQuad);
                        }
                    }
                }
            }
        } else {
            for (Direction direction : Direction.values()) {
                for (BakedQuad quad : bakedModel.getQuads(blockState, direction, new Random())) {
                    addQuad(quad);
                }
            }
        }
    }

    public void addQuad(BakedQuad quad) {
        ObjectArrayList<Vector3f> points = new ObjectArrayList<>();
        int[] v = quad.getVertexData();

        for (int i = 0; i < v.length; i += 8) { // loop 4 times through 32 byte queue
            float x = Math.round(Float.intBitsToFloat(v[i]));
            float y = Math.round(Float.intBitsToFloat(v[i + 1]));
            float z = Math.round(Float.intBitsToFloat(v[i + 2]));
            Vector3f point = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
            points.add(point);
        }

        ConvexHullShape hull = new ConvexHullShape(points);
        hull.setLocalScaling(new Vector3f(0.25f, 0.25f, 0.25f));
        hull.setMargin(0.005f);

        Transform trans = new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), new Vector3f(), 1.0f));
        this.addChildShape(trans, hull);
    }

    public BakedModel getBakedModel() {
        return this.bakedModel;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }
}