package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.BodyDef;

public class SimSensor extends CircleSensor {
    public SimSensor(LevelStage levelStage, float size, BodyDef.BodyType bodyType) {
        super(levelStage, size, bodyType);
    }
}
