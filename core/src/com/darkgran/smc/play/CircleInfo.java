package com.darkgran.smc.play;

public class CircleInfo extends PositionInfo {
    private ColorType type;

    private double radius;
    private float angle;

    public CircleInfo() { }

    public CircleInfo(float x, float y, float angle, double radius, ColorType type) {
        this.type = type;
        this.radius = radius;
        this.angle = angle;
        setX(x);
        setY(y);
    }

    public ColorType getType() {
        return type;
    }

    public double getRadius() {
        return radius;
    }

    public float getAngle() {
        return angle;
    }
}
