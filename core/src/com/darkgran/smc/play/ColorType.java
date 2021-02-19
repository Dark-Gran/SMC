package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;

public enum ColorType {
    WHITE(Color.WHITE, 0.04f, 0.05),
    BLUE(Color.BLUE, 0.04f, 0.05),
    GREEN(Color.GREEN, 0.06f, 0.05),
    RED(Color.RED, 0.08f, 0.05);

    private final Color color;
    private final float speed;
    private final double minRadius;

    ColorType(Color color, float speed, double minRadius) {
        this.color = color;
        this.speed = speed;
        this.minRadius = minRadius;
    }

    public Color getColor() {
        return color;
    }

    public float getSpeed() {
        return speed;
    }

    public double getMinRadius() {
        return minRadius;
    }

}
