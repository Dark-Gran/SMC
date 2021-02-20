package com.darkgran.smc.play;

public class SwitchInfo extends WallInfo {
    private SwitchType switchType;
    private int[] beams;
    private int[] rotatables;

    public SwitchType getSwitchType() {
        return switchType;
    }

    public int[] getBeams() {
        return beams;
    }

    public int[] getRotatables() {
        return rotatables;
    }
}
