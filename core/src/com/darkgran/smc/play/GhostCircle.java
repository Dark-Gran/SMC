package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
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
    private final CircleBody circleBody;
    private boolean spawnable = true;

    public GhostCircle(LevelStage levelStage, float size, int lockTime, int spawnTime) {
        this.spawnTime = spawnTime;
        this.levelStage = levelStage;
        this.size = size;
        lock = new SimpleCounter(false, lockTime, 0);
        circleBody = new CircleBody(levelStage.getWorldScreen().getWorld(), this, -4, -4, LevelStage.PC_SIZE, BodyDef.BodyType.DynamicBody);
        circleBody.getBody().getFixtureList().get(0).setSensor(true);
    }

    public void update(boolean buttonDown, boolean allowed) {
        if (lock.isEnabled()) {
            lock.update();
        } else if (buttonDown && allowed) {
            active = true;
            if (ghostTimer > spawnTime) {
                if (couldBeSpawnedNow()) {
                    active = false;
                    ghostTimer = 0;
                    levelStage.spawnPlayerCircle(levelStage.getWorldScreen().getMouseInWorld2D().x, levelStage.getWorldScreen().getMouseInWorld2D().y);
                }
            } else {
                ghostTimer++;
            }
        } else {
            active = false;
        }
    }

    public void updateBody() {
        if (active) {
            circleBody.getBody().setTransform(levelStage.getWorldScreen().getMouseInWorld2D().x, levelStage.getWorldScreen().getMouseInWorld2D().y, 0);
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        Gdx.gl.glLineWidth(3);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(couldBeSpawnedNow() ? Color.WHITE : Color.RED);
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
        shapeRenderer.setColor(Color.WHITE);
        Gdx.gl.glLineWidth(1);
    }

    private boolean couldBeSpawnedNow() {
        Array<Contact> contacts = levelStage.getWorldScreen().getWorld().getContactList();
        for (Contact contact : contacts) {
            if (contact.getFixtureA().getBody() == circleBody.getBody() || contact.getFixtureB().getBody() == circleBody.getBody()) {
                if (contact.isTouching()) {
                    return false;
                }
            }
        }
        return true;
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

    public CircleBody getCircleBody() {
        return circleBody;
    }
}
