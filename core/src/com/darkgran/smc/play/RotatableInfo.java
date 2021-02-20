package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;

public class RotatableInfo extends PositionInfo {
    private ColorType type;
    private PolygonType polygon;
    private float angle;
    private Vector2[] vertices;
    private boolean selfSwitch;

    public float getAngle() {
        return angle;
    }

    public Vector2[] getVertices() {
        return vertices;
    }

    public ColorType getType() {
        return type;
    }

    public PolygonType getPolygon() {
        return polygon;
    }

    public boolean isSelfSwitch() {
        return selfSwitch;
    }
}
