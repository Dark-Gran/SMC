package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.darkgran.smc.WorldScreen;

import static java.lang.Math.*;

public class TravelLine {
    private boolean active = true;
    private float midX;
    private float midY;
    private double radius;
    private Vector2 speed;
    private float x;
    private float y;
    private float targetX;
    private float targetY;

    public TravelLine(float x, float y, double radius, Vector2 speed) {
        this.radius = radius;
        this.midX = x;
        this.midY = y;
        this.speed = speed;
        refreshCoords();
    }

    public void update(float x, float y, double radius, Vector2 speed) {
        this.midX = x;
        this.midY = y;
        this.radius = radius;
        this.speed = speed;
        refreshCoords();
    }

    private void refreshCoords() {
        targetX = midX + speed.x;
        targetY = midY + speed.y;
        double angle = atan2(targetY-midY, targetX-midX);
        x = midX + (float) (radius *  cos(angle));
        y = midY + (float) (radius *  sin(angle));
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.line(x, y, targetX, targetY);
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
