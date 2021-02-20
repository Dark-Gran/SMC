package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;

public class RotatableTriangle extends RotatableChainObject {

    public RotatableTriangle(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution) {
        super(levelStage, x, y, vertices, angle, restitution);
    }

}
