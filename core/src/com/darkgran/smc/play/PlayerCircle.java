package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PlayerCircle extends Actor {
    private final LevelStage levelStage;
    private CircleBody body;
    private float radius;

    public PlayerCircle(LevelStage levelStage, float x, float y, float radius) {
        this.levelStage = levelStage;
        body = new CircleBody(levelStage.getWorldScreen().getWorld(), this, x, y, radius, BodyDef.BodyType.StaticBody);
        refreshActorBounds();
        this.radius = radius;
    }

    public void drawShapes(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        int segments = Math.round(radius*200);
        if (segments < 10) { segments = 10; }
        else if (segments > 100) { segments = 50; }
        shapeRenderer.circle(body.getBody().getPosition().x, body.getBody().getPosition().y, radius, segments);
        shapeRenderer.end();
    }

    private void refreshActorBounds() {
        this.setBounds(body.getBody().getPosition().x-radius, body.getBody().getPosition().y-radius, radius*2, radius*2);
    }

    public CircleBody getBody() {
        return body;
    }
}
