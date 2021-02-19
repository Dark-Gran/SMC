package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CircleActor extends Actor {
    private final LevelStage levelStage;
    private CircleBody circleBody;
    private int radius;

    public CircleActor(final LevelStage levelStage, float x, float y, int radius, BodyDef.BodyType bodyType) {
        this.levelStage = levelStage;
        circleBody = new CircleBody(levelStage.getWorldScreen().getWorld(), this, x, y, radius, bodyType);
        this.radius = radius;
        refreshActorBounds();
    }

    public void drawShape(ShapeRenderer shapeRenderer, Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        int segments = Math.round(getRadius()*0.2f);
        if (segments < 10) { segments = 10; }
        else if (segments > 100) { segments = 50; }
        shapeRenderer.circle(getCircleBody().getBody().getPosition().x, getCircleBody().getBody().getPosition().y, getRadius()/1000f, segments);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();
    }

    public void refreshActorBounds() {
        float actualRadius = radius/1000f;
        this.setBounds(circleBody.getBody().getPosition().x-actualRadius, circleBody.getBody().getPosition().y-actualRadius, actualRadius*2, actualRadius*2);
    }

    public CircleBody getCircleBody() {
        return circleBody;
    }

    public void setCircleBody(CircleBody circleBody) {
        this.circleBody = circleBody;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public LevelStage getLevelStage() {
        return levelStage;
    }
}
