package com.darkgran.smc.play;

public class BareDoor extends ChainBoxObject {
    private boolean closed;

    public BareDoor(LevelStage levelStage, float x, float y, float width, float height, float angle, boolean state) {
        super(levelStage, x, y, width, height, angle);
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
        getChainBody().getBody().getFixtureList().get(0).setSensor(!closed);
    }

    public boolean isClosed() {
        return closed;
    }
}
