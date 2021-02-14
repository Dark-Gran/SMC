package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.darkgran.smc.WorldScreen;

public class Wall {
    private final LevelStage levelStage;
    private final ChainBody chainBody;
    private final Sprite sprite;

    public Wall(LevelStage levelStage, float x, float y, float width, float height, float angle, Texture texture) {
        this.levelStage = levelStage;
        chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, width, height);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
        sprite = new Sprite(texture);
        updateSprite();
    }

    public void updateSprite() {
        sprite.setPosition(chainBody.getBody().getPosition().x* WorldScreen.PPM-sprite.getWidth()/2, chainBody.getBody().getPosition().y*WorldScreen.PPM-sprite.getHeight()/2);
        sprite.setRotation((float) (chainBody.getBody().getAngle() / WorldScreen.DEGREES_TO_RADIANS));
    }

    public Sprite getSprite() {
        return sprite;
    }

    public ChainBody getChainBody() {
        return chainBody;
    }
}
