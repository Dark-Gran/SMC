package com.darkgran.smc.play;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.darkgran.smc.WorldScreen;

public class DoorSwitch extends Actor {
    private final LevelStage levelStage;
    private final ChainBody chainBody;
    private final BareDoor[] doors;
    private SwitchType switchType;
    private final Sprite sprite;

    public DoorSwitch(LevelStage levelStage, float x, float y, float width, float height, float angle, BareDoor[] doors, SwitchType switchType, Texture texture) {
        this.levelStage = levelStage;
        this.switchType = switchType;
        chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, width, height, 0f);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
        chainBody.getBody().getFixtureList().get(0).setSensor(true);
        this.setBounds(x-width, y-height, width*2, height*2);
        this.doors = doors;
        sprite = new Sprite(texture);
        sprite.setBounds((x-width)*WorldScreen.PPM, (y-height)*WorldScreen.PPM, width*WorldScreen.PPM*2, height*WorldScreen.PPM*2);
    }

    public void updateSprite() {
        sprite.setPosition(getChainBody().getBody().getPosition().x* WorldScreen.PPM-sprite.getWidth()/2, getChainBody().getBody().getPosition().y*WorldScreen.PPM-sprite.getHeight()/2);
        sprite.setRotation((float) (getChainBody().getBody().getAngle() / WorldScreen.DEGREES_TO_RADIANS));
    }

    public void click() {
        for (BareDoor door : doors) {
            switch (switchType) {
                case ACTIVATOR:
                    door.switchState();
                    break;
                case COLORER:
                    if (door instanceof Beam) {
                        ((Beam) door).switchColor();
                    }
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
