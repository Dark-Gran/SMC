package com.darkgran.smc.play;

import com.darkgran.smc.WorldScreen;
import java.util.ArrayList;

public class Level {
    private final WorldScreen worldScreen;
    private ArrayList<ColoredCircle> circles = new ArrayList<ColoredCircle>();

    public Level(final WorldScreen worldScreen) {
        this.worldScreen = worldScreen;
        System.out.println("Starting Level.");
        circles.add(new ColoredCircle(this, 4.8f, 2.4f, 0.2f, MainColor.WHITE));
        circles.add(new ColoredCircle(this, 4.8f, 2.4f, 0.2f, MainColor.WHITE));
        circles.add(new ColoredCircle(this, 4.8f, 2.4f, 0.2f, MainColor.WHITE));
        circles.add(new ColoredCircle(this, 4.8f, 2.4f, 0.2f, MainColor.WHITE));
        circles.add(new ColoredCircle(this, 4.8f, 2.4f, 0.2f, MainColor.WHITE));
        circles.add(new ColoredCircle(this, 4.8f, 2.4f, 0.2f, MainColor.WHITE));
    }

    public void update() {
        for (ColoredCircle circle : circles) {
            circle.update();
        }
    }

    public WorldScreen getWorldScreen() {
        return worldScreen;
    }

    public void dispose() { }
}
