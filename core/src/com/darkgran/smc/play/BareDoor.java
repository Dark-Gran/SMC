package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;

public class BareDoor {
    private final LevelStage levelStage;
    private final ChainBody chainBody;
    private boolean closed;

    public BareDoor(LevelStage levelStage, float x, float y, float width, float height, float angle, boolean state) {
        this.levelStage = levelStage;
        this.chainBody = new ChainBody(levelStage.getWorldScreen().getWorld(), this, width, height);
        chainBody.getBody().setTransform(new Vector2(x, y), angle);
        changeState(state);
    }

    public void switchState() {
        changeState(!closed);
    }

    private void changeState(boolean state) {
        closed = state;
        applyState();
    }

    private void applyState() {
        chainBody.getBody().getFixtureList().get(0).setSensor(!closed);
    }

    public ChainBody getChainBody() {
        return chainBody;
    }

    public boolean isClosed() {
        return closed;
    }
}
