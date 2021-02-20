package com.darkgran.smc.play;

public abstract class BareDoor extends ChainBoxBodyObject implements Switchable {
    private boolean closed;

    public BareDoor(LevelStage levelStage, float x, float y, float width, float height, float angle, boolean state, float restitution) {
        super(levelStage, x, y, width, height, angle, restitution);
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
