package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.smc.WorldScreen;
import java.util.ArrayList;

public class LevelStage extends Stage {
    private final WorldScreen worldScreen;
    private ArrayList<ColoredCircle> circles = new ArrayList<ColoredCircle>();

    public LevelStage(final WorldScreen worldScreen, Viewport viewport) {
        super(viewport);
        this.worldScreen = worldScreen;
        System.out.println("Starting Level.");
        circles.add(new ColoredCircle(this, 2f, 1f, 0.2f, 0, MainColor.WHITE));
        circles.add(new ColoredCircle(this, 5f, 2f, 0.2f, 45, MainColor.WHITE));
        circles.add(new ColoredCircle(this, 8f, 3f, 0.2f, 200, MainColor.WHITE));
        setupActors();
    }

    private void setupActors() {
        for (ColoredCircle circle :circles) {
            this.addActor(circle);
            circle.addListener(new ClickListener()
            {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
                {
                    if (event.getTarget() instanceof ColoredCircle) {
                        ((ColoredCircle) event.getTarget()).click();
                    }
                    return true;
                }
            });
        }
    }

    public void update() {
        for (ColoredCircle circle : circles) {
            circle.update();
        }
    }

    public void drawShapes(ShapeRenderer shapeRenderer) {
        for (ColoredCircle circle : circles) {
            circle.drawShapes(shapeRenderer);
        }
    }

    public WorldScreen getWorldScreen() {
        return worldScreen;
    }

    public void dispose() { }
}
