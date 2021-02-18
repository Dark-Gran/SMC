package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.darkgran.smc.WorldScreen;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDeclHelper;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class GhostCircle {
    private final LevelStage levelStage;
    private final float size;
    private boolean active = false;
    private float ghostTimer = 0;
    private final SimpleCounter lock;

    public GhostCircle(LevelStage levelStage, float size, int timerCap) {
        this.levelStage = levelStage;
        this.size = size;
        lock = new SimpleCounter(false, timerCap, 0);
    }

    public void update(boolean buttonDown, boolean allowed) {
        if (lock.isEnabled()) {
            lock.update();
        } else if (buttonDown && allowed) {
            active = true;
            if (ghostTimer > 20000) {
                ghostTimer = 0;
                active = false;
                levelStage.spawnPlayerCircle(levelStage.getWorldScreen().getMouseInWorld2D().x, levelStage.getWorldScreen().getMouseInWorld2D().y);
            } else {
                ghostTimer++;
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {

        Gdx.gl.glLineWidth(3);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        float midX = levelStage.getWorldScreen().getMouseInWorld2D().x;
        float midY = levelStage.getWorldScreen().getMouseInWorld2D().y;

        float angle = 0;
        int segments = 40;
        float degreeStep = 360 / segments;
        for (int i = 0; i <= segments; i++) {
            float x = midX + (float) (size * sin(angle));
            float y = midY + (float) (size * cos(angle));
            angle += degreeStep*WorldScreen.DEGREES_TO_RADIANS;
            float x2 = midX + (float) (size * sin(angle));
            float y2 = midY + (float) (size * cos(angle));
            shapeRenderer.line(x, y, 0, x2, y2, 0);
        }

        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public boolean isLocked() {
        return lock.isEnabled();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public SimpleCounter getLock() {
        return lock;
    }

    public float getGhostTimer() {
        return ghostTimer;
    }

    public void setGhostTimer(float ghostTimer) {
        this.ghostTimer = ghostTimer;
    }
}
