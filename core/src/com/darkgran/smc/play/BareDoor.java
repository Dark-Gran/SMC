package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class BareDoor extends ChainBoxBodyObject implements Switchable {
    private boolean closed;

    public BareDoor(LevelStage levelStage, float x, float y, float width, float height, float angle, boolean state, float restitution, BodyDef.BodyType bodyType) {
        super(levelStage, x, y, width, height, angle, restitution, bodyType);
        setEnabled(state);
    }

    public void switchState() {
        setEnabled(!closed);
    }

    public void setEnabled(boolean state) {
        closed = state;
        applyState();
    }

    private void applyState() {
        getChainBody().getBody().getFixtureList().get(0).setSensor(!closed);
    }

    public boolean isEnabled() {
        return closed;
    }
}
