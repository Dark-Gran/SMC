package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.BodyDef;

public class CircleSensor {
    private final LevelStage levelStage;
    private boolean mouseFollow = false;
    private final float size;
    private final CircleBody circleBody;

    public CircleSensor(LevelStage levelStage, float size, BodyDef.BodyType bodyType) {
        this.levelStage = levelStage;
        this.size = size;
        circleBody = new CircleBody(levelStage.getWorldScreen().getWorld(), this, 0, 0, size, bodyType);
        circleBody.getBody().getFixtureList().get(0).setSensor(true);
    }

    public void updateBody() {
        if (mouseFollow) {
            circleBody.getBody().setTransform(levelStage.getWorldScreen().getMouseInWorld2D().x, levelStage.getWorldScreen().getMouseInWorld2D().y, 0);
        }
    }

    public void setMouseFollow(boolean active) {
        this.mouseFollow = active;
    }

    public boolean isMouseFollow() {
        return mouseFollow;
    }

    public LevelStage getLevelStage() {
        return levelStage;
    }

    public float getSize() {
        return size;
    }

    public CircleBody getCircleBody() {
        return circleBody;
    }
}
