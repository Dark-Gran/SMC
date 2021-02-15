package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Beam extends BareDoor {
    private ColorType type;
    private float width;
    private float height;

    public Beam(LevelStage levelStage, float x, float y, float width, float height, float angle, ColorType colorType, boolean state) {
        super(levelStage, x, y, width, height, angle, state, 0f);
        this.type = colorType;
        this.width = width;
        this.height = height;
    }

    public void switchColor() {
        switch (type) {
            case WHITE:
                setType(ColorType.BLUE);
                break;
            case BLUE:
                setType(ColorType.GREEN);
                break;
            case GREEN:
                setType(ColorType.WHITE);
                break;
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        if (isClosed()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(type.getColor().r, type.getColor().g, type.getColor().b, 0.7f);
            shapeRenderer.rect(getChainBody().getBody().getPosition().x - width, getChainBody().getBody().getPosition().y - height, width * 2, height * 2);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.end();
        }
    }

    public ColorType getColorType() {
        return type;
    }

    public void setType(ColorType type) {
        this.type = type;
    }
}
