package com.darkgran.smc.play;

import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class ChainBodyObject {
    private final LevelStage levelStage;
    private ChainBody chainBody;

    public ChainBodyObject(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution, BodyDef.BodyType bodyType) {
        this.levelStage = levelStage;
        Vector2[] v = new Vector2[vertices.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = vertices[i].cpy();
        }
        createChainBody(levelStage, x, y, v, angle, restitution, bodyType);
    }

    public void createChainBody(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution, BodyDef.BodyType bodyType) {
        float[] polygon = new float[vertices.length*2];
        int i = 0;
        for (Vector2 vertex : vertices) {
            polygon[i] = vertex.x;
            polygon[i+1] = vertex.y;
            i += 2;
        }
        Vector2 centroid = new Vector2();
        GeometryUtils.polygonCentroid(polygon, 0, polygon.length, centroid);
        for (Vector2 vertex : vertices) {
            vertex.x -= centroid.x;
            vertex.y -= centroid.y;
        }
        chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, vertices, restitution, bodyType, centroid);
        chainBody.getBody().setTransform(x, y, angle);
    }

    public ChainBody getChainBody() {
        return chainBody;
    }

    protected void setChainBody(ChainBody chainBody) {
        this.chainBody = chainBody;
    }

}
