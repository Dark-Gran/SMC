package com.darkgran.smc.play;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.darkgran.smc.WorldScreen;

public class StandardSwitch extends Actor {
    private final LevelStage levelStage;
    private final ChainBody chainBody;
    private final Switchable[] beams;
    private final Switchable[] rotatables;
    private SwitchType switchType;
    private final Sprite sprite;

    public StandardSwitch(LevelStage levelStage, float x, float y, float width, float height, float angle, Switchable[] beams, Switchable[] rotatables, SwitchType switchType, Texture texture) {
        this.levelStage = levelStage;
        this.switchType = switchType;
        chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, (new Vector2[]{new Vector2(-width, -height), new Vector2(-width, +height), new Vector2(+width, +height), new Vector2(+width, -height)}), 0f, BodyDef.BodyType.StaticBody, null);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
        chainBody.getBody().getFixtureList().get(0).setSensor(true);
        this.setBounds(x-width, y-height, width*2, height*2);
        this.beams = beams;
        this.rotatables = rotatables;
        sprite = new Sprite(texture);
        sprite.setBounds((x-width)*WorldScreen.PPM, (y-height)*WorldScreen.PPM, width*WorldScreen.PPM*2, height*WorldScreen.PPM*2);
    }

    public void updateSprite() {
        sprite.setPosition(getChainBody().getBody().getPosition().x* WorldScreen.PPM-sprite.getWidth()/2, getChainBody().getBody().getPosition().y*WorldScreen.PPM-sprite.getHeight()/2);
        sprite.setRotation((float) (getChainBody().getBody().getAngle() / WorldScreen.DEGREES_TO_RADIANS));
    }

    public void click() {
        for (Switchable beam : beams) {
            switch (switchType) {
                case ACTIVATOR:
                    beam.switchState();
                    break;
                case COLORER:
                    if (beam instanceof Beam) {
                        ((Beam) beam).switchColor();
                    }
                    break;
            }
        }
        for (Switchable rotatable : rotatables) {
            switch (switchType) {
                case ACTIVATOR:
                    rotatable.switchState();
                    break;
            }
        }
    }

    public ChainBody getChainBody() {
        return chainBody;
    }

    public Sprite getSprite() {
        return sprite;
    }

}
