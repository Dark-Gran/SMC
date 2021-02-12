package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.smc.WorldScreen;
import java.util.ArrayList;

public class LevelStage extends Stage {
    public static final float MIN_RADIUS = 0.05f; //for "not merging away" circles
    public static final float CHANGE_UP = 0.01f;
    private final WorldScreen worldScreen;
    private final ArrayList<ColoredCircle> circles = new ArrayList<ColoredCircle>();
    private ColoredCircle lastTouch;
    private final float colorPower;
    private final InputAdapter generalInputProcessor = new InputAdapter() {
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            lastTouch = null;
            return false;
        }
    };

    public LevelStage(final WorldScreen worldScreen, Viewport viewport) {
        super(viewport);
        this.worldScreen = worldScreen;
        System.out.println("Starting Level.");
        circles.add(new ColoredCircle(this, 2f, 0f, 0.05f, 30, ColorType.WHITE));
        circles.add(new ColoredCircle(this, 3f, 1f, 0.2f, 70, ColorType.WHITE));
        circles.add(new ColoredCircle(this, 4f, 2f, 0.2f, 210, ColorType.WHITE));
        circles.add(new ColoredCircle(this, 5f, 3f, 0.05f, 45, ColorType.WHITE));
        circles.add(new ColoredCircle(this, 6f, 4f, 0.2f, 180, ColorType.WHITE));
        circles.add(new ColoredCircle(this, 7f, 5f, 0.2f, 10, ColorType.WHITE));
        colorPower = 0.9f;
        setupActors();
        worldScreen.getSmc().getInputMultiplexer().addProcessor(generalInputProcessor);
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
                        lastTouch = (ColoredCircle) event.getTarget();
                    }
                    return true;
                }
            });
        }
    }

    public void update() {
        for (ColoredCircle circle : circles) {
            if (!circle.isGone()) {
                circle.update();
            } else {
                worldScreen.getCorpses().add(circle);
            }
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && lastTouch != null) {
            distributedSizeChange(lastTouch);
        }
    }

    private void distributedSizeChange(ColoredCircle chosenCircle) {
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
                            circle.setRadius(circle.getRadius()-changeDown);
                        } else {
                            spareChange = changeDown - (circle.getRadius()-LevelStage.MIN_RADIUS);
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

    public ArrayList<ColoredCircle> getCircles() {
        return circles;
    }

    public void dispose() { }
}
