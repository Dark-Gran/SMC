package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.darkgran.smc.WorldScreen;

public class PlayerCircle extends CircleActor implements Spriter {
    private final Sprite sprite;

    public PlayerCircle(LevelStage levelStage, float x, float y, float radius, Texture tex) {
        super(levelStage, x, y, radius, BodyDef.BodyType.StaticBody);
        sprite = new Sprite(tex);
        updateSprite();
    }

    @Override
    public void updateSprite() {
        sprite.setPosition(getCircleBody().getBody().getPosition().x* WorldScreen.PPM-sprite.getWidth()/2, getCircleBody().getBody().getPosition().y*WorldScreen.PPM-sprite.getHeight()/2);
        sprite.setRotation((float) (getCircleBody().getBody().getAngle() / WorldScreen.DEGREES_TO_RADIANS));
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }
}
