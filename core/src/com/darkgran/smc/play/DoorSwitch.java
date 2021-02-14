package com.darkgran.smc.play;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class DoorSwitch extends Actor {
    private final LevelStage levelStage;
    private final ChainBody chainBody;
    private final BareDoor[] doors;

    public DoorSwitch(LevelStage levelStage, float x, float y, float width, float height, float angle, BareDoor[] doors) {
        this.levelStage = levelStage;
        chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, width, height);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
        chainBody.getBody().getFixtureList().get(0).setSensor(true);
        this.setBounds(x-width, y-height, width*2, height*2);
        this.doors = doors;
    }

    public void click() {
        for (BareDoor door : doors) {
            door.switchState();
        }
    }

    public ChainBody getChainBody() {
        return chainBody;
    }
}
