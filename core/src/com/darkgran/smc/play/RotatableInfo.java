package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;

public class RotatableInfo extends PositionInfo {
    private float angle;
    private Vector2[] vertices;

    public float getAngle() {
        return angle;
    }

    public Vector2[] getVertices() {
        return vertices;
    }
}
