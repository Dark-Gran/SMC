package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.smc.SaveMeCircles;
import com.darkgran.smc.WorldScreen;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class LevelStage extends Stage {
    public static final float MIN_RADIUS = 0.05f; //for "not merging away" circles
    public static final float CHANGE_UP = 0.01f;
    public static final LevelLibrary LEVEL_LIBRARY = new LevelLibrary();
    private final WorldScreen worldScreen;
    private final HashMap<ColorType, ArrayList<ColoredCircle>> circles = new HashMap<>();
    private final EnumMap<ColorType, Float> colorPowers = new EnumMap<>(ColorType.class);
    private ColoredCircle lastTouch;
    private int currentLevel = -1;
    private boolean completed = false;
    private float timer = 0;
    private float frameCounter = 0;
    private int seconds = 0;
    private String introMessage;

    private final Texture continueTexture = new Texture("images/continue.png");
    public final ImageButton continueButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(continueTexture)));


    public LevelStage(final WorldScreen worldScreen, Viewport viewport) {
        super(viewport);
        this.worldScreen = worldScreen;
        LEVEL_LIBRARY.loadLocal("content/levels.json");
        continueButton.setPosition(Math.round(SaveMeCircles.SW/2-continueButton.getWidth()/2), Math.round(SaveMeCircles.SH/10-continueButton.getHeight()/2));
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
                ArrayList<ColoredCircle> whites = new ArrayList<>();
                float whitePower = 0f;
                ArrayList<ColoredCircle> blues = new ArrayList<>();
                float bluePower = 0f;
                for (CircleInfo circleInfo : levelInfo.getCircles()) {
                    if (circleInfo.getType() == ColorType.WHITE) {
                        whites.add(new ColoredCircle(this, circleInfo.getX(), circleInfo.getY(), circleInfo.getRadius(), circleInfo.getAngle(), ColorType.WHITE));
                        whitePower += Math.max(circleInfo.getRadius(), LevelStage.MIN_RADIUS);
                    } else {
                        blues.add(new ColoredCircle(this, circleInfo.getX(), circleInfo.getY(), circleInfo.getRadius(), circleInfo.getAngle(), ColorType.BLUE));
                        bluePower += Math.max(circleInfo.getRadius(), LevelStage.MIN_RADIUS);
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
                setupActors();
                introMessage = levelInfo.getIntro();
            } else {
                System.out.println("Level-Loading Error!");
            }
        }
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

    public void switchLevel(boolean forward) {
        int newID = forward ? currentLevel+1 : currentLevel-1;
        if (LEVEL_LIBRARY.levelExists(newID)) {
            disableContinue();
            clearLevel();
            loadLevel(newID);
        }
    }

    private void clearLevel() {
        introMessage = null;
        lastTouch = null;
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            for (ColoredCircle circle : entry.getValue()) {
                worldScreen.getWorld().destroyBody(circle.getCircleBody().getBody());
            }
        }
        circles.clear();
        colorPowers.clear();
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
        continueButton.remove();
        continueButton.removeListener(continueButton.getClickListener());
    }

    private void enableContinue() {
        this.addActor(continueButton);
        continueButton.addListener(new ClickListener()
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
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            for (ColoredCircle circle : entry.getValue()) {
                if (!circle.isGone()) {
                    circle.update();
                } else {
                    worldScreen.getCorpses().add(circle);
                }
            }
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && lastTouch != null) {
            if (lastTouch.isDisabled()) {
                lastTouch = null;
            } else {
                distributedSizeChange(lastTouch);
            }
        }
        if (checkCompletion() && !completed) {
            completed = true;
            enableContinue();
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
        drawText(worldScreen.getFont(), batch, String.valueOf(seconds), Math.round(SaveMeCircles.SW*9/10), Math.round(SaveMeCircles.SH/20), Color.WHITE);
    }

    private void drawLevelIntro(SpriteBatch batch, float time) {
        if (introMessage != null) {
            GlyphLayout layout = new GlyphLayout();
            layout.setText(new BitmapFont(), introMessage);
            float alpha = 1;
            if (time > 100) {
                alpha = ((150 - time) * 2) / 100;
            }
            drawText(worldScreen.getFont(), batch, introMessage, Math.round(SaveMeCircles.SW / 2 - layout.width), Math.round(SaveMeCircles.SH / 5 - layout.height), new Color(1, 1, 1, alpha));
        }
    }

    public void drawText(BitmapFont font, SpriteBatch batch, String txt, float x, float y, Color color) {
        font.setColor(color);
        font.draw(batch, txt, x, y);
    }

    public void removeCircle(ColoredCircle coloredCircle) {
        for (Map.Entry<ColorType, ArrayList<ColoredCircle>> entry : circles.entrySet()) {
            if (entry.getValue().contains(coloredCircle)) {
                entry.getValue().remove(coloredCircle);
                break;
            }
        }
    }

    public void dispose() {
        disableContinue();
        continueTexture.dispose();
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
