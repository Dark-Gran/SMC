package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;

public abstract class RotatableChainObject extends ChainBodyObject implements Switchable {
    private float angle;

    public RotatableChainObject(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution) {
        super(levelStage, x, y, vertices, angle, restitution);
        this.angle = angle;
    }

    @Override
    public void switchState() {
        rotate(90);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            rotate(90);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
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
