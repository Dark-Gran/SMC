package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Beam {
    private final LevelStage levelStage;
    private final ChainBody chainBody;
    private ColorType colorType;
    private float width;
    private float height;
    private boolean active;

    public Beam(LevelStage levelStage, float x, float y, float width, float height, float angle, ColorType colorType, boolean state) {
        this.levelStage = levelStage;
        this.colorType = colorType;
        this.width = width;
        this.height = height;
        this.chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, width, height);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
        changeState(state);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        if (active) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(colorType.getColor());
            shapeRenderer.rect(chainBody.getBody().getPosition().x - width, chainBody.getBody().getPosition().y - height, width * 2, height * 2);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.end();
        }
    }

    public void switchState() {
        changeState(!active);
    }

    private void changeState(boolean state) {
        active = state;
        applyState();
    }

    private void applyState() {
        chainBody.getBody().getFixtureList().get(0).setSensor(!active);
    }

    public ChainBody getChainBody() {
        return chainBody;
    }

}
