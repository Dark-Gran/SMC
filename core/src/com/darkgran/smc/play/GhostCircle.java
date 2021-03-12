package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.darkgran.smc.WorldScreen;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class GhostCircle extends CircleSensor {
    private float ghostTimer = 0;
    private final int spawnTime;
    private final SimpleCounter lock;

    public GhostCircle(LevelStage levelStage, float size, int lockTime, int spawnTime) {
        super(levelStage, size, BodyDef.BodyType.DynamicBody);
        this.spawnTime = spawnTime;
        lock = new SimpleCounter(false, lockTime, 0);
    }

    public void update(boolean buttonDown, boolean allowed) {
        if (lock.isEnabled()) {
            lock.update();
        } else if (allowed) {
            setMouseFollow(buttonDown);
            if (ghostTimer > spawnTime) {
                if (couldBeSpawnedNow() && !buttonDown) {
                    setMouseFollow(false);
                    ghostTimer = 0;
                    getLevelStage().spawnPlayerCircle(getLevelStage().getWorldScreen().getMouseInWorld2D().x, getLevelStage().getWorldScreen().getMouseInWorld2D().y);
                }
            } else if (buttonDown) {
                ghostTimer++;
            } else {
                getLevelStage().removeGhost();
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        Gdx.gl.glLineWidth(3);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(couldBeSpawnedNow() ? Color.WHITE : Color.RED);
        final int segments = 40;

        float midX = getLevelStage().getWorldScreen().getMouseInWorld2D().x;
        float midY = getLevelStage().getWorldScreen().getMouseInWorld2D().y;

        int maxSegment = Math.round(segments / ((spawnTime+1) / ghostTimer));
        float degreeStep = 360 / segments;
        float angle = 0;
        for (int i = 0; i <= segments && i <= maxSegment; i++) {
            float x = midX + (float) (getSize() * sin(angle));
            float y = midY + (float) (getSize() * cos(angle));
            angle += degreeStep*WorldScreen.DEGREES_TO_RADIANS;
            float x2 = midX + (float) (getSize() * sin(angle));
            float y2 = midY + (float) (getSize() * cos(angle));
            shapeRenderer.line(x, y, 0, x2, y2, 0);
        }

        shapeRenderer.end();
        shapeRenderer.setColor(Color.WHITE);
        Gdx.gl.glLineWidth(1);
    }

    private boolean couldBeSpawnedNow() {
        Array<Contact> contacts = getLevelStage().getWorldScreen().getWorld().getContactList();
        for (Contact contact : contacts) {
            if (contact.getFixtureA().getBody() == getCircleBody().getBody() || contact.getFixtureB().getBody() == getCircleBody().getBody()) {
                if (contact.isTouching()) {
                    Body other = getCircleBody().getBody() == contact.getFixtureA().getBody() ? contact.getFixtureB().getBody() : contact.getFixtureA().getBody();
                    if (other.getUserData() instanceof BareDoor) {
                        return !((BareDoor) other.getUserData()).isEnabled();
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isLocked() {
        return lock.isEnabled();
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
