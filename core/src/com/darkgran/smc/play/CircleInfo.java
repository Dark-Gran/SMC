package com.darkgran.smc.play;

public class CircleInfo extends PositionInfo {
    private ColorType type;

    private float radius;
    private float angle;

    public CircleInfo() { }

    public CircleInfo(float x, float y, float angle, float radius, ColorType type) {
        this.type = type;
        this.radius = radius;
        this.angle = angle;
        setX(x);
        setY(y);
    }

    public ColorType getType() {
        return type;
    }

    public float getRadius() {
        return radius;
    }

    public float getAngle() {
        return angle;
    }
}
