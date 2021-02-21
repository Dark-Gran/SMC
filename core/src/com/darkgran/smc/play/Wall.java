package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.darkgran.smc.WorldScreen;

public class Wall extends ChainBoxBodyObject implements Spriter {
    private final ColorType colorType;
    private final Sprite sprite;

    public Wall(LevelStage levelStage, float x, float y, float width, float height, float angle, ColorType colorType, Texture texture) {
        super(levelStage, x, y, width, height, angle, 0f, BodyDef.BodyType.StaticBody);
        this.colorType = colorType;
        sprite = new Sprite(texture);
        updateSprite();
    }

    @Override
    public void updateSprite() {
        sprite.setPosition(getChainBody().getBody().getPosition().x* WorldScreen.PPM-sprite.getWidth()/2, getChainBody().getBody().getPosition().y*WorldScreen.PPM-sprite.getHeight()/2);
        sprite.setRotation((float) (getChainBody().getBody().getAngle() / WorldScreen.DEGREES_TO_RADIANS));
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    public ColorType getColorType() {
        return colorType;
    }
}
