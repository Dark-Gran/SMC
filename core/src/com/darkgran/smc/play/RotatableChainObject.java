package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;

public abstract class RotatableChainObject extends ChainBodyObject {
    private final boolean selfSwitch;
    private float angle;

    public RotatableChainObject(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution, boolean selfSwitch) {
        super(levelStage, x, y, vertices, angle, restitution);
        this.selfSwitch = selfSwitch;
        this.angle = angle;
    }

    public void rotate(float angle) {
        this.angle = angle;
        getChainBody().getBody().setTransform(getChainBody().getBody().getPosition(), angle);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
