package com.darkgran.smc.play;

public class ColoredCircle {
    private final CircleBody circleBody;
    private MainColor color;

    public ColoredCircle(final Level level, float x, float y, float width, float height, MainColor color) {
        circleBody = new CircleBody(level.getWorldScreen().getWorld());
    }

}
