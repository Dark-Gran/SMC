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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class LevelStage extends Stage {
    public static final float MIN_RADIUS = 0.05f; //for "not merging away" circles
    public static final float CHANGE_UP = 0.01f;
    private final WorldScreen worldScreen;
    private final HashMap<ColorType, ArrayList<ColoredCircle>> circles = new HashMap<>();
    private final EnumMap<ColorType, Float> colorPowers = new EnumMap<>(ColorType.class);
    private ColoredCircle lastTouch;
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
        ArrayList<ColoredCircle> coloredCircles = new ArrayList<>();
        coloredCircles.add(new ColoredCircle(this, 1.5f, 5.5f, 0.05f, 70, ColorType.BLUE));
        coloredCircles.add(new ColoredCircle(this, 4.5f, 5f, 0.1f, 30, ColorType.BLUE));
        coloredCircles.add(new ColoredCircle(this, 2.5f, 4f, 0.1f, 45, ColorType.BLUE));
        coloredCircles.add(new ColoredCircle(this, 6.5f, 3f, 0.05f, 130, ColorType.BLUE));
        coloredCircles.add(new ColoredCircle(this, 4.5f, 2f, 0.1f, 10, ColorType.BLUE));
        coloredCircles.add(new ColoredCircle(this, 8f, 1f, 0.1f, 180, ColorType.BLUE));
        coloredCircles.add(new ColoredCircle(this, 3.5f, 0f, 0.1f, 320, ColorType.BLUE));
        circles.put(ColorType.BLUE, coloredCircles);
        colorPowers.put(ColorType.BLUE, 0.6f);
        coloredCircles = new ArrayList<>();
        coloredCircles.add(new ColoredCircle(this, 2f, 0f, 0.05f, 30, ColorType.WHITE));
        coloredCircles.add(new ColoredCircle(this, 3f, 1f, 0.1f, 70, ColorType.WHITE));
        coloredCircles.add(new ColoredCircle(this, 4f, 2f, 0.1f, 130, ColorType.WHITE));
        coloredCircles.add(new ColoredCircle(this, 5f, 3f, 0.05f, 45, ColorType.WHITE));
        coloredCircles.add(new ColoredCircle(this, 6f, 4f, 0.1f, 180, ColorType.WHITE));
        coloredCircles.add(new ColoredCircle(this, 7f, 5f, 0.1f, 10, ColorType.WHITE));
        coloredCircles.add(new ColoredCircle(this, 6f, 5f, 0.1f, 160, ColorType.WHITE));
        circles.put(ColorType.WHITE, coloredCircles);
        colorPowers.put(ColorType.WHITE, 0.6f);
        setupActors();
        worldScreen.getSmc().getInputMultiplexer().addProcessor(generalInputProcessor);
    }

    private void setupActors() {
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            for (ColoredCircle circle : entry.getValue()) {
                this.addActor(circle);
                circle.addListener(new ClickListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if (event.getTarget() instanceof ColoredCircle) {
                            lastTouch = (ColoredCircle) event.getTarget();
                        }
                        return true;
                    }
                });
            }
        }
    }

    public void update() {
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            for (ColoredCircle circle : entry.getValue()) {
                if (!circle.isGone()) {
                    circle.update();
                } else {
                    worldScreen.getCorpses().add(circle); //TODO sync?
                }
            }
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && lastTouch != null) {
            distributedSizeChange(lastTouch);
        }
    }

    private void distributedSizeChange(ColoredCircle chosenCircle) {
        if (circles.get(chosenCircle.getColorType()) != null) {
            final float MIN_CHANGE_DOWN = 0.001f;
            ArrayList<ColoredCircle> coloredCircles = circles.get(chosenCircle.getColorType());
            if (coloredCircles.size() > 1) {
                float maxRadius = colorPowers.get(chosenCircle.getColorType()) - (coloredCircles.size() - 1) * MIN_RADIUS;
                if (chosenCircle.getRadius() + CHANGE_UP <= maxRadius) {
                    ArrayList<ColoredCircle> eligibles = new ArrayList<>();
                    float changeSpace = 0f;
                    for (ColoredCircle circle : coloredCircles) {
                        if (circle != chosenCircle && circle.getColorType() == chosenCircle.getColorType() && circle.getRadius() - MIN_CHANGE_DOWN >= LevelStage.MIN_RADIUS) {
                            eligibles.add(circle);
                            changeSpace += circle.getRadius() - LevelStage.MIN_RADIUS;
                        }
                    }
                    if (changeSpace >= CHANGE_UP && eligibles.size() > 0) {
                        float changeDown = CHANGE_UP / eligibles.size();
                        float spareChange = 0f;
                        for (int i = 0; i < eligibles.size(); i++) {
                            ColoredCircle circle = eligibles.get(i);
                            if (circle.getRadius() - changeDown >= LevelStage.MIN_RADIUS) {
                                circle.setRadius(circle.getRadius() - changeDown);
                            } else {
                                spareChange = changeDown - (circle.getRadius() - LevelStage.MIN_RADIUS);
                                circle.setRadius(LevelStage.MIN_RADIUS);
                            }
                            if (spareChange > 0) {
                                changeDown += spareChange / (eligibles.size() - i + 1);
                            } else {
                                spareChange = 0;
                            }
                        }
                        chosenCircle.setRadius(chosenCircle.getRadius() + CHANGE_UP);
                    }
                }
            }
        }
    }

    public void drawShapes(ShapeRenderer shapeRenderer) {
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            for (ColoredCircle circle : entry.getValue()) {
                circle.drawShapes(shapeRenderer);
            }
        }
    }

    public void removeCircle(ColoredCircle coloredCircle) {
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            if (entry.getValue().contains(coloredCircle)) {
                entry.getValue().remove(coloredCircle);
                break;
            }
        }
    }

    public WorldScreen getWorldScreen() {
        return worldScreen;
    }

    public void dispose() { }
}
