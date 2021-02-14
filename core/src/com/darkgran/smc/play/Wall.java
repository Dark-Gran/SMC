package com.darkgran.smc.play;

public class Wall {
    private final LevelStage levelStage;
    private final WallBody wallBody;

    public Wall(LevelStage levelStage, float x, float y, float width, float height) {
        this.levelStage = levelStage;
        wallBody = new WallBody(levelStage.getWorldScreen().getWorld(), this, x, y, width, height);
    }
}
