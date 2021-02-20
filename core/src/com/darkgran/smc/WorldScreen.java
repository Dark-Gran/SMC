package com.darkgran.smc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.smc.play.*;

import java.util.ArrayList;

import static com.darkgran.smc.play.LevelStage.LEVEL_LIBRARY;

public class WorldScreen implements Screen {
    public final static double DEGREES_TO_RADIANS = Math.PI/180;
    //WorldSettings
    private float FPS = 60.0f;
    final int VELOCITY_ITERATIONS = 15;
    final int POSITION_ITERATIONS = 12;
    public float STEP_TIME = 1f / FPS;
    public static final float WORLD_WIDTH = 9.6f;
    public static final float WORLD_HEIGHT = 4.8f;
    public static final float PPM = 200;
    public static float getMMP() { //reverse PPM
        return WorldScreen.WORLD_WIDTH / SaveMeCircles.SW;
    }

    private final SaveMeCircles smc;
    private final Box2DDebugRenderer debugRenderer;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private World world;
    private World worldSimulation;
    private SimulationManager simulationManager;
    private float worldTimer = 0;
    private LevelStage levelStage;
    private Stage UIStage;
    private final CollisionListener collisionListener;
    private ArrayList corpses = new ArrayList();
    private boolean reload = false;
    private int currentLevelID = 4;

    public final Vector2 mouseInWorld2D = new Vector2();
    public final Vector3 mouseInWorld3D = new Vector3();
    private final InputAdapter generalInputProcessor = new InputAdapter() {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (levelStage != null) {
                levelStage.setLastTouch(null);
                levelStage.removeGhost();
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (levelStage != null) {
                switch (keycode) {
                    case Input.Keys.LEFT:
                        if (LEVEL_LIBRARY.levelExists(currentLevelID-1)) {
                            reload = true;
                            currentLevelID -= 1;
                        }
                        break;
                    case Input.Keys.RIGHT:
                        if (LEVEL_LIBRARY.levelExists(currentLevelID+1)) {
                            reload = true;
                            currentLevelID += 1;
                        }
                        break;
                    case Input.Keys.R:
                        reload = true;
                        break;
                }
            }
            return true;
        }
    };

    private final BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/bahnschrift.fnt")); //in-future: move to atlas
    private final Texture continueTexture = new Texture("images/continue.png");
    private final ImageButton continueButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(continueTexture)));

    public WorldScreen(final SaveMeCircles smc) {
        this.smc = smc;
        Gdx.input.setInputProcessor(smc.getInputMultiplexer());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2,0);
        shapeRenderer = new ShapeRenderer();
        Box2D.init();
        debugRenderer = new Box2DDebugRenderer();
        World.setVelocityThreshold(0.1f);
        world = new World(new Vector2(0, 0), false);
        setupUIStage();
        levelStage = new LevelStage(this, UIStage, viewport);
        smc.getInputMultiplexer().addProcessor(UIStage);
        smc.getInputMultiplexer().addProcessor(levelStage);
        smc.getInputMultiplexer().addProcessor(generalInputProcessor);
        collisionListener = new CollisionListener(levelStage);
        world.setContactListener(collisionListener);
        levelStage.loadLevel(currentLevelID);
        Gdx.input.setCursorCatched(false);
        worldSimulation = new World(new Vector2(0, 0), false);
        simulationManager = new SimulationManager(this, worldSimulation, VELOCITY_ITERATIONS, POSITION_ITERATIONS, STEP_TIME);
        simulationManager.resetSimulation(collisionListener, world);
    }

    private void setupUIStage() {
        UIStage = new Stage(new ExtendViewport(SaveMeCircles.SW, SaveMeCircles.SH));
        continueButton.setPosition(Math.round(SaveMeCircles.SW/2-continueButton.getWidth()/2), Math.round(SaveMeCircles.SW/15-continueButton.getHeight()/2));
    }

    public void refreshMouse() {
        mouseInWorld3D.x = Gdx.input.getX();
        mouseInWorld3D.y = Gdx.input.getY();
        mouseInWorld3D.z = 0;
        camera.unproject(mouseInWorld3D);
        mouseInWorld2D.x = mouseInWorld3D.x;
        mouseInWorld2D.y = mouseInWorld3D.y;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        if (!reload) {

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                refreshMouse();
            }

            camera.update();

            shapeRenderer.setProjectionMatrix(camera.combined);
            levelStage.drawShapes(shapeRenderer);

            smc.batch.setProjectionMatrix((new Matrix4(camera.combined)).scale(WorldScreen.getMMP(), WorldScreen.getMMP(), 1));

            smc.batch.begin();
            smc.batch.setColor(1, 1, 1, 1f);
            levelStage.drawSprites(smc.batch);
            smc.batch.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            UIStage.act(delta);
            UIStage.draw();
            levelStage.act(delta);
            levelStage.draw();
            levelStage.getGhostCircle().updateBody();

            drawBox2DDebug(this.world);

            levelStage.tickTock();
            timeWorld(delta);

        } else {
            reload = false;
            levelStage.switchLevel(currentLevelID);
        }
    }

    public void timeWorld(float delta) {
        worldTimer += Math.min(delta, 0.25f);
        if (worldTimer >= STEP_TIME) {
            worldTimer -= STEP_TIME;
            levelStage.update();
            reapWorld();
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !levelStage.isCompleted()) {
                simulationManager.drawSimulation(shapeRenderer, collisionListener, world, debugRenderer, new Matrix4(camera.combined));
            }
        }
    }

    private void reapWorld() {
        for (int i = 0; i < corpses.size(); i++) {
            if (corpses.get(i) != null) {
                Object corpse = corpses.get(i);
                if (corpse instanceof Actor) {
                    Actor actor = (Actor) corpse;
                    if (actor.getListeners().size > 0) {
                        actor.removeListener(actor.getListeners().get(0));
                    }
                    actor.remove();
                }
                if (corpse instanceof CircleActor) {
                    CircleActor ca = (CircleActor) corpse;
                    if (ca.getCircleBody() != null) {
                        destroyBody(ca.getCircleBody().getBody());
                    }
                    ca.setCircleBody(null);
                }
                if (corpse instanceof ColoredCircle) {
                    ColoredCircle circle = (ColoredCircle) corpse;
                    levelStage.removeCircle(circle);
                    if (circle == levelStage.getLastTouch()) {
                        levelStage.setLastTouch(null);
                    }
                } else if (corpse instanceof PlayerCircle) {
                    levelStage.setPlayerCircle(null);
                }
                corpses.remove(corpse);
            }
        }
    }

    public void destroyBody(Body body) {
        world.destroyBody(body);
    }

    private void drawBox2DDebug(World world) {
        Matrix4 debugMatrix = new Matrix4(camera.combined);
        debugMatrix.scale(1f, 1f, 1f);
        debugRenderer.setDrawBodies(true);
        debugRenderer.render(world, debugMatrix);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void dispose() {
        levelStage.dispose();
        world.dispose();
        debugRenderer.dispose();
        continueTexture.dispose();
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void show() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    public SaveMeCircles getSmc() {
        return smc;
    }

    public ArrayList getCorpses() {
        return corpses;
    }

    public BitmapFont getFont() {
        return font;
    }

    public ImageButton getContinueButton() { return continueButton; }

    public int getCurrentLevelID() {
        return currentLevelID;
    }

    public void setCurrentLevelID(int currentLevelID) {
        this.currentLevelID = currentLevelID;
    }

    public Vector2 getMouseInWorld2D() {
        return mouseInWorld2D;
    }

    public float getSTEP_TIME() {
        return STEP_TIME;
    }
}
