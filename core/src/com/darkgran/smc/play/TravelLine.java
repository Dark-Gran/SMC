package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class TravelLine {
    private boolean active = true;
    private float x;
    private float y;
    private Vector2 speed;

    public TravelLine(float x, float y, Vector2 speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void update(float x, float y, Vector2 speed) {
        //TODO
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);



        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
