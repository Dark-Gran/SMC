package com.darkgran.smc.play;

import java.util.ArrayList;

public class LevelInfo {
    private byte id;
    private String intro;
    private ArrayList<CircleInfo> circles;
    private ArrayList<WallInfo> walls;
    private ArrayList<BeamInfo> beams;
    private ArrayList<SwitchInfo> switches;
    private ArrayList<RotatableInfo> rotatables;

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

    public ArrayList<WallInfo> getWalls() {
        return walls;
    }

    public ArrayList<BeamInfo> getBeams() {
        return beams;
    }

    public ArrayList<SwitchInfo> getSwitches() {
        return switches;
    }

    public ArrayList<RotatableInfo> getRotatables() { return rotatables; }
}
