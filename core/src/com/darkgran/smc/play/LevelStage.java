package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.smc.SaveMeCircles;
import com.darkgran.smc.WorldScreen;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class LevelStage extends Stage {
    public static final float MIN_RADIUS = 0.05f; //for "not merging away" circles
    public static final float COMFORT_RADIUS = 0.25f;
    public static final float ACTUAL_MIN_RADIUS = 0.001f;
    public static final float RADIUS_CHANGE = 0.01f;
    public static final float MIN_RADIUS_CHANGE = 0.001f;
    public static final LevelLibrary LEVEL_LIBRARY = new LevelLibrary();
    private final WorldScreen worldScreen;
    private final Stage UIStage;
    private final HashMap<ColorType, ArrayList<ColoredCircle>> circles = new HashMap<>();
    private final HashMap<CircleInfo, Boolean> circlesToAdd = new HashMap<>();
    private final EnumMap<ColorType, Float> colorPowers = new EnumMap<>(ColorType.class);
    private final ArrayList<Wall> walls = new ArrayList<>();
    private final ArrayList<Beam> beams = new ArrayList<>();
    private final ArrayList<DoorSwitch> switches = new ArrayList<>();
    private ColoredCircle lastTouch;
    private int currentLevel = -1;
    private boolean completed = false;
    private float timer = 0;
    private float frameCounter = 0;
    private int seconds = 0;
    private String introMessage;
    private final Texture wallTex = new Texture("images/wall.png");
    private final Texture wallTexB = new Texture("images/wallB.png");
    private final Texture wallTexG = new Texture("images/wallG.png");
    private final Texture soTex = new Texture("images/switch_over.png");

    public LevelStage(final WorldScreen worldScreen, final Stage UIStage, Viewport viewport) {
        super(viewport);
        this.worldScreen = worldScreen;
        this.UIStage = UIStage;
        LEVEL_LIBRARY.loadLocal("content/levels.json");
    }

    public void loadLevel(int levelNum) {
        if (levelNum >= 0) {
            timer = 0;
            frameCounter = 0;
            seconds = 0;
            completed = false;
            lastTouch = null;
            System.out.println("Launching Level: " + levelNum);
            currentLevel = levelNum;
            LevelInfo levelInfo = LEVEL_LIBRARY.getLevel(levelNum);
            if (levelInfo != null) {
                //Circles
                ArrayList<ColoredCircle> whites = new ArrayList<>();
                float whitePower = 0f;
                ArrayList<ColoredCircle> blues = new ArrayList<>();
                float bluePower = 0f;
                ArrayList<ColoredCircle> greens = new ArrayList<>();
                float greenPower = 0f;
                ArrayList<ColoredCircle> reds = new ArrayList<>();
                float redPower = 0f;
                for (CircleInfo circleInfo : levelInfo.getCircles()) {
                    switch (circleInfo.getType()) {
                        case WHITE:
                            whites.add(new ColoredCircle(this, circleInfo.getX(), circleInfo.getY(), circleInfo.getRadius(), circleInfo.getAngle(), ColorType.WHITE));
                            whitePower += Math.max(circleInfo.getRadius(), LevelStage.MIN_RADIUS);
                            break;
                        case BLUE:
                            blues.add(new ColoredCircle(this, circleInfo.getX(), circleInfo.getY(), circleInfo.getRadius(), circleInfo.getAngle(), ColorType.BLUE));
                            bluePower += Math.max(circleInfo.getRadius(), LevelStage.MIN_RADIUS);
                            break;
                        case GREEN:
                            greens.add(new ColoredCircle(this, circleInfo.getX(), circleInfo.getY(), circleInfo.getRadius(), circleInfo.getAngle(), ColorType.GREEN));
                            greenPower += Math.max(circleInfo.getRadius(), LevelStage.MIN_RADIUS);
                            break;
                        case RED:
                            reds.add(new ColoredCircle(this, circleInfo.getX(), circleInfo.getY(), circleInfo.getRadius(), circleInfo.getAngle(), ColorType.RED));
                            redPower += Math.max(circleInfo.getRadius(), LevelStage.MIN_RADIUS);
                            break;
                    }
                }
                if (whitePower > 0f && whites.size() > 0) {
                    circles.put(ColorType.WHITE, whites);
                    colorPowers.put(ColorType.WHITE, whitePower);
                }
                if (bluePower > 0f && blues.size() > 0) {
                    circles.put(ColorType.BLUE, blues);
                    colorPowers.put(ColorType.BLUE, bluePower);
                }
                if (greenPower > 0f && greens.size() > 0) {
                    circles.put(ColorType.GREEN, greens);
                    colorPowers.put(ColorType.GREEN, greenPower);
                }
                if (redPower > 0f && reds.size() > 0) {
                    circles.put(ColorType.RED, reds);
                    colorPowers.put(ColorType.RED, redPower);
                }
                setupActors();
                //Obstacles
                for (WallInfo wallInfo : levelInfo.getWalls()) {
                    Texture tex = wallTex;
                    if (wallInfo.getType() == ColorType.BLUE) {
                        tex = wallTexB;
                    } else if (wallInfo.getType() == ColorType.GREEN) {
                        tex = wallTexG;
                    }
                    walls.add(new Wall(this, wallInfo.getX(), wallInfo.getY(), wallInfo.getWidth()/2, wallInfo.getHeight()/2, (float) (wallInfo.getAngle()*WorldScreen.DEGREES_TO_RADIANS), wallInfo.getType(), tex));
                }
                for (BeamInfo beamInfo : levelInfo.getBeams()) {
                    beams.add(new Beam(this, beamInfo.getX(), beamInfo.getY(), beamInfo.getWidth()/2, beamInfo.getHeight()/2, beamInfo.getAngle(), beamInfo.getColorType(), beamInfo.isActive()));
                }
                for (SwitchInfo switchInfo : levelInfo.getSwitches()) {
                    BareDoor[] doors = new BareDoor[switchInfo.getBeams().length];
                    for (int i = 0; i < doors.length; i++) {
                        if (beams.get(switchInfo.getBeams()[i]) != null) {
                            doors[i] = beams.get(switchInfo.getBeams()[i]);
                        }
                    }
                    DoorSwitch doorSwitch = new DoorSwitch(this, switchInfo.getX(), switchInfo.getY(), switchInfo.getWidth()/2, switchInfo.getHeight()/2, switchInfo.getAngle(), doors, switchInfo.getSwitchType(), soTex);
                    switches.add(doorSwitch);
                    this.addActor(doorSwitch);
                    doorSwitch.addListener(new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            if (event.getTarget() instanceof DoorSwitch) {
                                ((DoorSwitch) event.getTarget()).click();
                            }
                            return true;
                        }
                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                            lastTouch = null;
                        }
                    });
                }
                //Finish
                introMessage = levelInfo.getIntro();
            } else {
                System.out.println("Level-Loading Error!");
            }
        }
    }

    public void freshCircle(CircleInfo circleInfo, boolean additive) {
        circlesToAdd.put(circleInfo, additive);
    }

    public void addCircle(CircleInfo circleInfo, boolean additive) {
        if (circleInfo.getRadius() >= ACTUAL_MIN_RADIUS) {
            ColoredCircle circle = new ColoredCircle(this, circleInfo.getX(), circleInfo.getY(), ACTUAL_MIN_RADIUS, circleInfo.getAngle(), circleInfo.getType());
            circle.setFreshShard(true);
            circle.addToGrow(circleInfo.getRadius()-ACTUAL_MIN_RADIUS);
            circle.setLockedFromInteractions(true);
            circle.setUnbreakable(true);
            circles.get(circle.getColorType()).add(circle);
            if (additive) { colorPowers.put(circle.getColorType(), colorPowers.get(circle.getColorType())+circle.getRadius()); }
            addActor(circle);
            addCircleClicks(circle);
        } else {
            System.out.println("Circle Spawn Error: Radius < Minimum!");
        }
    }

    private void setupActors() {
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            for (ColoredCircle circle : entry.getValue()) {
                this.addActor(circle);
                addCircleClicks(circle);
            }
        }
    }

    private void addCircleClicks(ColoredCircle circle) {
        circle.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.getTarget() instanceof ColoredCircle) {
                    lastTouch = (ColoredCircle) event.getTarget();
                }
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                lastTouch = null;
            }
        });
    }

    public void switchLevel(boolean forward) {
        int newID = forward ? currentLevel+1 : currentLevel-1;
        if (LEVEL_LIBRARY.levelExists(newID)) {
            disableContinue();
            clearLevel();
            loadLevel(newID);
        }
    }

    public void reloadLevel() {
        disableContinue();
        clearLevel();
        loadLevel(currentLevel);
    }

    private void clearLevel() {
        introMessage = null;
        lastTouch = null;
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            for (ColoredCircle circle : entry.getValue()) {
                if (circle.getListeners().size > 0) {
                    circle.removeListener(circle.getListeners().get(0));
                }
                circle.remove();
                worldScreen.getWorld().destroyBody(circle.getCircleBody().getBody());
            }
        }
        circles.clear();
        colorPowers.clear();
        circlesToAdd.clear();
        for (Wall wall : walls) {
            worldScreen.getWorld().destroyBody(wall.getChainBody().getBody());
        }
        walls.clear();
        for (Beam beam : beams) {
            worldScreen.getWorld().destroyBody(beam.getChainBody().getBody());
        }
        beams.clear();
        for (DoorSwitch doorSwitch : switches) {
            if (doorSwitch.getListeners().size > 0) {
                doorSwitch.removeListener(doorSwitch.getListeners().get(0));
            }
            doorSwitch.remove();
            worldScreen.getWorld().destroyBody(doorSwitch.getChainBody().getBody());
        }
        switches.clear();
    }

    public void removeCircle(ColoredCircle coloredCircle) {
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            if (entry.getValue().contains(coloredCircle)) {
                entry.getValue().remove(coloredCircle);
                break;
            }
        }
    }

    private boolean checkCompletion() {
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            if (entry.getValue().size() > 1) {
                return false;
            }
        }
        return true;
    }

    private void disableContinue() {
        worldScreen.getContinueButton().remove();
        worldScreen.getContinueButton().removeListener(worldScreen.getContinueButton().getClickListener());
    }

    private void enableContinue() {
        UIStage.addActor(worldScreen.getContinueButton());
        worldScreen.getContinueButton().addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                switchLevel(true);
                disableContinue();
            }
        });
    }

    public void update() {
        if (checkCompletion() && !completed) {
            completed = true;
            enableContinue();
        }
        //Circle Input
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && lastTouch != null && !completed) {
            if (lastTouch.isDisabled()) {
                lastTouch = null;
            } else {
                distributedSizeChange(lastTouch);
            }
        }
        //Updates
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            for (ColoredCircle circle : entry.getValue()) {
                if (!circle.isGone()) {
                    circle.update();
                } else {
                    worldScreen.getCorpses().add(circle);
                }
            }
        }
        for (Wall wall : walls) {
            wall.updateSprite();
        }
        for (DoorSwitch doorSwitch : switches) {
            doorSwitch.updateSprite();
        }
        //New Circles
        if (circlesToAdd.size() > 0) {
            for (Map.Entry<CircleInfo, Boolean> entry : circlesToAdd.entrySet()) {
                addCircle(entry.getKey(), entry.getValue());
            }
            circlesToAdd.clear();
        }
    }

    private void distributedSizeChange(ColoredCircle chosenCircle) {
        if (circles.get(chosenCircle.getColorType()) != null) {
            ArrayList<ColoredCircle> coloredCircles = circles.get(chosenCircle.getColorType());
            if (coloredCircles.size() > 1) {
                float maxRadius = colorPowers.get(chosenCircle.getColorType()) - (coloredCircles.size() - 1) * MIN_RADIUS;
                if (chosenCircle.getRadius() + RADIUS_CHANGE <= maxRadius) {
                    ArrayList<ColoredCircle> eligibles = new ArrayList<>();
                    float changeSpace = 0f;
                    for (ColoredCircle circle : coloredCircles) {
                        if (circle != chosenCircle && circle.getColorType() == chosenCircle.getColorType() && circle.getRadius() - MIN_RADIUS_CHANGE >= LevelStage.MIN_RADIUS) {
                            eligibles.add(circle);
                            changeSpace += circle.getRadius() - LevelStage.MIN_RADIUS;
                        }
                    }
                    if (changeSpace >= RADIUS_CHANGE && eligibles.size() > 0) {
                        float changeDown = RADIUS_CHANGE / eligibles.size();
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
                        chosenCircle.setRadius(chosenCircle.getRadius() + RADIUS_CHANGE);
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
        for (Beam beam : beams) {
            beam.draw(shapeRenderer);
        }
    }

    public void tickTock() {
        timer += Gdx.graphics.getRawDeltaTime();
        frameCounter++;
        if (timer >= 1 && !completed) {
            timer -= 1;
            seconds++;
        }
    }

    public void drawSprites(SpriteBatch batch) {
        //Intro
        if (frameCounter < 150) {
            drawLevelIntro(batch, frameCounter);
        }
        //Timer
        if (currentLevel != 0) {
            drawText(worldScreen.getFont(), batch, String.valueOf(seconds), SaveMeCircles.SW * 9 / 10, SaveMeCircles.SH / 7.5f, Color.WHITE);
        }
        //Obstacles
        for (Wall wall : walls) {
            wall.getSprite().draw(batch);
        }
        for (DoorSwitch doorSwitch :switches) {
            doorSwitch.getSprite().draw(batch);
        }
    }

    private void drawLevelIntro(SpriteBatch batch, float time) {
        if (introMessage != null) {
            GlyphLayout layout = new GlyphLayout();
            layout.setText(worldScreen.getFont(), introMessage);
            float alpha = 1;
            if (time > 100) {
                alpha = ((150 - time) * 2) / 100;
            }
            drawText(worldScreen.getFont(), batch, introMessage, SaveMeCircles.SW/2-layout.width/2, (SaveMeCircles.SH / 5), new Color(1, 1, 1, alpha));
        }
    }

    public void drawText(BitmapFont font, SpriteBatch batch, String txt, float x, float y, Color color) {
        font.setColor(color);
        font.draw(batch, txt, x, y);
    }

    public void dispose() {
        disableContinue();
        wallTex.dispose();
        soTex.dispose();
    }

    public WorldScreen getWorldScreen() {
        return worldScreen;
    }

    public ColoredCircle getLastTouch() {
        return lastTouch;
    }

    public void setLastTouch(ColoredCircle lastTouch) {
        this.lastTouch = lastTouch;
    }
}
