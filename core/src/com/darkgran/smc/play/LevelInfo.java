package com.darkgran.smc.play;

import java.util.ArrayList;

public class LevelInfo {
    private byte id;
    private String intro;
    private ArrayList<CircleInfo> circles;

    public LevelInfo() { }

    public byte getId() {
        return id;
    }

    public String getIntro() {
        return intro;
    }

    public ArrayList<CircleInfo> getCircles() {
        return circles;
    }

}
