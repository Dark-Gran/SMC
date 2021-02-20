package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;

public abstract class ChainBoxBodyObject extends ChainBodyObject { //for non-sizable box bodies (eg. Wall)
    private float width;
    private float height;

    public ChainBoxBodyObject(LevelStage levelStage, float x, float y, float width, float height, float angle, float restitution) {
        super(levelStage, x, y, (new Vector2[]{new Vector2(-width, -height), new Vector2(-width, +height), new Vector2(+width, +height), new Vector2(+width, -height)}), angle, restitution);
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
