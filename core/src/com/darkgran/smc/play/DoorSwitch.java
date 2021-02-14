package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;

public class DoorSwitch {
    private final LevelStage levelStage;
    private final ChainBody chainBody;
    private final BareDoor[] doors;

    public DoorSwitch(LevelStage levelStage, float x, float y, float width, float height, float angle, BareDoor[] doors) {
        this.levelStage = levelStage;
        this.doors = doors;
        this.chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, width, height);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
    }

    public ChainBody getChainBody() {
        return chainBody;
    }
}
