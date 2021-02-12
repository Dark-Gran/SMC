package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.smc.WorldScreen;
import java.util.ArrayList;
import java.util.logging.Level;

public class LevelStage extends Stage {
    public static final float MIN_RADIUS = 0.05f;
    private final WorldScreen worldScreen;
    private final ArrayList<ColoredCircle> circles = new ArrayList<ColoredCircle>();
    private ColoredCircle lastTouch;
    private final float colorPower;

    public LevelStage(final WorldScreen worldScreen, Viewport viewport) {
        super(viewport);
        this.worldScreen = worldScreen;
        System.out.println("Starting Level.");
        circles.add(new ColoredCircle(this, 2f, 1f, 0.05f, 0, MainColor.WHITE));
        circles.add(new ColoredCircle(this, 5f, 2f, 0.2f, 45, MainColor.WHITE));
        colorPower = 0.25f;
        setupActors();
    }

    private void setupActors() {
        for (ColoredCircle circle :circles) {
            this.addActor(circle);
            circle.addListener(new ClickListener() //TODO DEBUG
            {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
                {
                    if (event.getTarget() instanceof ColoredCircle) {
                        lastTouch = (ColoredCircle) event.getTarget();
                    }
                    return false;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (event.getTarget() == lastTouch) {
                        lastTouch = null;
                    }
                }
            });
        }
    }

    public void update() {
        for (ColoredCircle circle : circles) {
            circle.update();
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && lastTouch != null) {
            attemptCircleSizeChange(lastTouch);
        }
    }

    private void attemptCircleSizeChange(ColoredCircle chosenCircle) {
        final float CHANGE_UP = 0.01f;
        final float MIN_CHANGE_DOWN = 0.001f;
        if (circles.size() > 1) {
            float maxRadius = colorPower - (circles.size()-1)*MIN_RADIUS;
            if (chosenCircle.getRadius()+CHANGE_UP <= maxRadius) {
                ArrayList<ColoredCircle> eligibles = new ArrayList<>();
                float changeSpace = 0f;
                for (ColoredCircle circle : circles) {
                    if (circle != chosenCircle && circle.getRadius()-MIN_CHANGE_DOWN >= LevelStage.MIN_RADIUS) {
                        eligibles.add(circle);
                        changeSpace += circle.getRadius()-LevelStage.MIN_RADIUS;
                    }
                }
                if (changeSpace >= CHANGE_UP && eligibles.size() > 0) {
                    float changeDown = CHANGE_UP/eligibles.size();
                    float spareChange = 0f;
                    for (int i = 0; i < eligibles.size(); i++) {
                        ColoredCircle circle = eligibles.get(i);
                        if (circle.getRadius()-changeDown >= LevelStage.MIN_RADIUS) {
                            circle.setRadius(eligibles.get(i).getRadius()-changeDown);
                        } else {
                            spareChange = changeDown - (eligibles.get(i).getRadius()-LevelStage.MIN_RADIUS);
                            circle.setRadius(LevelStage.MIN_RADIUS);
                        }
                        if (spareChange > 0) {
                            changeDown += spareChange / (eligibles.size()-i+1);
                        } else {
                            spareChange = 0;
                        }
                    }
                    chosenCircle.setRadius(chosenCircle.getRadius()+CHANGE_UP);
                }
            }

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
