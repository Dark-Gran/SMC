package com.darkgran.smc.play;

import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class ChainBodyObject {
    private final LevelStage levelStage;
    private ChainBody chainBody;

    public ChainBodyObject(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution) {
        this.levelStage = levelStage;
        createChainBody(levelStage, x, y, vertices, angle, restitution);
    }

    public void createChainBody(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution) {
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
        chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, vertices, restitution, BodyDef.BodyType.StaticBody);
        chainBody.getBody().setTransform(x, y, angle);
    }

    public ChainBody getChainBody() {
        return chainBody;
    }

    protected void setChainBody(ChainBody chainBody) {
        this.chainBody = chainBody;
    }
}
