package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.BodyDef;

public class PlayerCircle extends CircleActor { //TODO sprite

    public PlayerCircle(LevelStage levelStage, float x, float y, float radius) {
        super(levelStage, x, y, radius, BodyDef.BodyType.StaticBody);
    }

}
