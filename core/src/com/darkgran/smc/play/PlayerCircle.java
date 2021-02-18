package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class PlayerCircle extends CircleActor {

    public PlayerCircle(LevelStage levelStage, float x, float y, float radius) {
        super(levelStage, x, y, radius, BodyDef.BodyType.StaticBody);
    }

    @Override
    public void drawShapes(ShapeRenderer shapeRenderer, Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        int segments = Math.round(getRadius()*200);
        if (segments < 10) { segments = 10; }
        else if (segments > 100) { segments = 50; }
        shapeRenderer.circle(getCircleBody().getBody().getPosition().x, getCircleBody().getBody().getPosition().y, getRadius(), segments);
        shapeRenderer.end();
    }

}
