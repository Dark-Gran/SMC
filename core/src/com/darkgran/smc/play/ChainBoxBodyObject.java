package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class ChainBoxBodyObject extends ChainBodyObject { //for non-sizable box bodies (eg. Wall)
    private float width;
    private float height;

    public ChainBoxBodyObject(LevelStage levelStage, float x, float y, float width, float height, float angle, float restitution, BodyDef.BodyType bodyType) {
        super(levelStage, x, y, (new Vector2[]{new Vector2(-width, -height), new Vector2(-width, +height), new Vector2(+width, +height), new Vector2(+width, -height)}), angle, restitution, bodyType);
        this.height = height;
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

}
