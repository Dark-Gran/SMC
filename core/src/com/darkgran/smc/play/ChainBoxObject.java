package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class ChainBoxObject {
    private final LevelStage levelStage;
    private final ChainBody chainBody;

    public ChainBoxObject(LevelStage levelStage, float x, float y, float width, float height, float angle, float restitution) {
        this.levelStage = levelStage;
        chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, width, height, restitution, BodyDef.BodyType.StaticBody);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
    }

    public ChainBody getChainBody() {
        return chainBody;
    }

}
