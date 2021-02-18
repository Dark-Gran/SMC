package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
            if (ghostTimer > 20) {
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

        /*float x = worldScreen.getMouseInWorld2D().x;
        float y = worldScreen.getMouseInWorld2D().y;

        float angle = 2 * MathUtils.PI / segments;
        float cos = MathUtils.cos(angle);
        float sin = MathUtils.sin(angle);
        float cx = radius, cy = 0;

        for (int i = 0; i < 40; i++) {

        }*/

        shapeRenderer.circle(levelStage.getWorldScreen().getMouseInWorld2D().x, levelStage.getWorldScreen().getMouseInWorld2D().y, size, 40);


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
