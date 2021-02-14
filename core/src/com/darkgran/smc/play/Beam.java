package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Beam extends BareDoor {
    private ColorType colorType;
    private float width;
    private float height;

    public Beam(LevelStage levelStage, float x, float y, float width, float height, float angle, ColorType colorType, boolean state) {
        super(levelStage, x, y, width, height, angle, state);
        this.colorType = colorType;
        this.width = width;
        this.height = height;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        if (isClosed()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(colorType.getColor());
            shapeRenderer.rect(getChainBody().getBody().getPosition().x - width, getChainBody().getBody().getPosition().y - height, width * 2, height * 2);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.end();
        }
    }

}
