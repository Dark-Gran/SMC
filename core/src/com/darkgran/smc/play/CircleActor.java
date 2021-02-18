package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CircleActor extends Actor {
    private final LevelStage levelStage;
    private CircleBody circleBody;
    private float radius;

    public CircleActor(final LevelStage levelStage, float x, float y, float radius, BodyDef.BodyType bodyType) {
        this.levelStage = levelStage;
        circleBody = new CircleBody(levelStage.getWorldScreen().getWorld(), this, x, y, radius, bodyType);
        this.radius = radius;
        refreshActorBounds();
    }

    public void drawCircleShape(ShapeRenderer shapeRenderer, Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        int segments = Math.round(getRadius()*200);
        if (segments < 10) { segments = 10; }
        else if (segments > 100) { segments = 50; }
        shapeRenderer.circle(getCircleBody().getBody().getPosition().x, getCircleBody().getBody().getPosition().y, getRadius(), segments);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();
    }

    public void refreshActorBounds() {
        this.setBounds(circleBody.getBody().getPosition().x-radius, circleBody.getBody().getPosition().y-radius, radius*2, radius*2);
    }

    public CircleBody getCircleBody() {
        return circleBody;
    }

    public void setCircleBody(CircleBody circleBody) {
        this.circleBody = circleBody;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public LevelStage getLevelStage() {
        return levelStage;
    }
}
