package com.darkgran.smc.play;

import java.util.ArrayList;

public class LevelInfo {
    private byte id;
    private ArrayList<CircleInfo> circles;

    public LevelInfo() { }

    public byte getId() {
        return id;
    }

    public ArrayList<CircleInfo> getCircles() {
        return circles;
    }

}
