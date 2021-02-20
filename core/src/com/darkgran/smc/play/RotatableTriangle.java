package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;

public class RotatableTriangle extends RotatableChainObject {

    public RotatableTriangle(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, boolean selfSwitch) {
        super(levelStage, x, y, vertices, angle, 0f, selfSwitch);
    }

}
