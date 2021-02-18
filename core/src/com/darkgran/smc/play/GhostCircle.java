package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.darkgran.smc.WorldScreen;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class GhostCircle {
    private final LevelStage levelStage;
    private final float size;
    private boolean active = false;
    private float ghostTimer = 0;
    private final int spawnTime;
    private final SimpleCounter lock;

    public GhostCircle(LevelStage levelStage, float size, int lockTime, int spawnTime) {
        this.spawnTime = spawnTime;
        this.levelStage = levelStage;
        this.size = size;
        lock = new SimpleCounter(false, lockTime, 0);
    }

    public void update(boolean buttonDown, boolean allowed) {
        if (lock.isEnabled()) {
            lock.update();
        } else if (buttonDown && allowed) {
            active = true;
            if (ghostTimer > spawnTime) {
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
        final int segments = 40;

        float midX = levelStage.getWorldScreen().getMouseInWorld2D().x;
        float midY = levelStage.getWorldScreen().getMouseInWorld2D().y;

        int maxSegment = Math.round(segments / ((spawnTime+1) / ghostTimer));
        float degreeStep = 360 / segments;
        float angle = 0;
        for (int i = 0; i <= segments && i <= maxSegment; i++) {
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
