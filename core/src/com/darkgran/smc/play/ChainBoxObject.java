package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class ChainBoxObject {
    private final LevelStage levelStage;
    private final ChainBody chainBody;
    private float width;
    private float height;

    public ChainBoxObject(LevelStage levelStage, float x, float y, float width, float height, float angle, float restitution) {
        this.levelStage = levelStage;
        this.width = width;
        this.height = height;
        chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, width, height, restitution, BodyDef.BodyType.StaticBody);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
    }

    public ChainBody getChainBody() {
        return chainBody;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
