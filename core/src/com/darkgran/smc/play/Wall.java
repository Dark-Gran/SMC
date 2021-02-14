package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Wall {
    private final LevelStage levelStage;
    private final WallBody wallBody;
    private final Sprite sprite;

    public Wall(LevelStage levelStage, float x, float y, float width, float height, Texture texture) {
        this.levelStage = levelStage;
        wallBody = new WallBody(levelStage.getWorldScreen().getWorld(), this, x, y, width, height);
        sprite = new Sprite(texture);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public WallBody getWallBody() {
        return wallBody;
    }
}
