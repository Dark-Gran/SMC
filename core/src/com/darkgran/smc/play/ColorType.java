package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;

public enum ColorType {
    WHITE(Color.WHITE, 0.08f),
    BLUE(Color.BLUE, 0.08f),
    GREEN(Color.GREEN, 0.12f),
    RED(Color.RED, 0.16f);

    private final Color color;
    private final float speed;

    ColorType(Color color, float speed) {
        this.color = color;
        this.speed = speed;
    }

    public Color getColor() {
        return color;
    }

    public float getSpeed() {
        return speed;
    }
}
