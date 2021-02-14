package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.darkgran.smc.WorldScreen;

public class Wall {
    private final LevelStage levelStage;
    private final WallBody wallBody;
    private final Sprite sprite;

    public Wall(LevelStage levelStage, float x, float y, float width, float height, Texture texture) {
        this.levelStage = levelStage;
        wallBody = new WallBody(levelStage.getWorldScreen().getWorld(), this, x, y, width, height);
        wallBody.getBody().setTransform(wallBody.getBody().getPosition(), (float) Math.PI/2);
        sprite = new Sprite(texture);
        updateSprite();
    }

    public void updateSprite() {
        sprite.setPosition(wallBody.getBody().getPosition().x* WorldScreen.PPM-sprite.getWidth()/2, wallBody.getBody().getPosition().y*WorldScreen.PPM-sprite.getHeight()/2);
        sprite.setRotation((float) (wallBody.getBody().getAngle() / WorldScreen.DEGREES_TO_RADIANS));
    }

    public Sprite getSprite() {
        return sprite;
    }

    public WallBody getWallBody() {
        return wallBody;
    }
}
