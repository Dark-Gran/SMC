package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class ChainBodyObject {
    private final LevelStage levelStage;
    private final ChainBody chainBody;

    public ChainBodyObject(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution) {
        this.levelStage = levelStage;
        chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, x, y, vertices, restitution, BodyDef.BodyType.StaticBody);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
    }

    public ChainBody getChainBody() {
        return chainBody;
    }

}
