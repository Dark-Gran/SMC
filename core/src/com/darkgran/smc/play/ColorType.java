package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;

public enum ColorType {
    WHITE(Color.WHITE, 0.04f),
    BLUE(Color.BLUE, 0.04f),
    GREEN(Color.GREEN, 0.06f),
    RED(Color.RED, 0.08f);

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
