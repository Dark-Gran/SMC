package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.darkgran.smc.WorldScreen;

public abstract class RotatableChainObject extends ChainBodyObject implements Switchable {
    private float angle;

    public RotatableChainObject(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution) {
        super(levelStage, x, y, vertices, angle, restitution, BodyDef.BodyType.KinematicBody);
        this.angle = angle;
    }

    public void update() {
        if (!closeEnough()) {
            getChainBody().getBody().setAngularVelocity(-1f);
        } else {
            getChainBody().getBody().setAngularVelocity(0f);
            getChainBody().getBody().setTransform(getChainBody().getBody().getPosition(), angle);
        }
    }

    public boolean closeEnough() {
        boolean ce = false;
        double maxDif = WorldScreen.DEGREES_TO_RADIANS;
        ce = getChainBody().getBody().getAngle() >= angle-maxDif && getChainBody().getBody().getAngle() <= angle+maxDif;
        return ce;
    }

    @Override
    public void switchState() {
        if (closeEnough()) {
            angle -= (float) (90* WorldScreen.DEGREES_TO_RADIANS);
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void setEnabled(boolean enabled) { }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
