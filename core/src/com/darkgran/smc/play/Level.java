package com.darkgran.smc.play;

import com.darkgran.smc.WorldScreen;
import java.util.ArrayList;

public class Level {
    private final WorldScreen worldScreen;
    private ArrayList<ColoredCircle> circles = new ArrayList<ColoredCircle>();

    public Level(final WorldScreen worldScreen) {
        this.worldScreen = worldScreen;
        System.out.println("Starting Level.");
        circles.add(new ColoredCircle(this, 0f, 0f, 0f, 0f, MainColor.WHITE));
    }

    public WorldScreen getWorldScreen() {
        return worldScreen;
    }

    public void dispose() { }
}
