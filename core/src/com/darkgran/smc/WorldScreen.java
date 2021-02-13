package com.darkgran.smc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.smc.play.CollisionListener;
import com.darkgran.smc.play.ColoredCircle;
import com.darkgran.smc.play.LevelStage;

import java.util.ArrayList;

public class WorldScreen implements Screen {
    //WorldSettings
    float FPS = 60.0f;
    float STEP_TIME = 1f / FPS;
    final int VELOCITY_ITERATIONS = 15;
    final int POSITION_ITERATIONS = 12;
    public static final float WORLD_WIDTH = 9.6f;
    public static final float WORLD_HEIGHT = 4.8f;

    private final SaveMeCircles smc;
    private final Box2DDebugRenderer debugRenderer;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private World world;
    private float worldTimer = 0;
    private LevelStage levelStage;
    private final CollisionListener collisionListener;
    private ArrayList corpses = new ArrayList();

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
        world = new World(new Vector2(0, 0), true);
        levelStage = new LevelStage(this, viewport);
        smc.getInputMultiplexer().addProcessor(levelStage);
        collisionListener = new CollisionListener(levelStage);
        world.setContactListener(collisionListener);
        levelStage.loadLevel(0);
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        levelStage.act(delta);

        drawShapes();
        //drawBox2DDebug();

        timeWorld(delta);
    }

    private void drawShapes() {
        shapeRenderer.setProjectionMatrix(new Matrix4(camera.combined));
        levelStage.drawShapes(shapeRenderer);
    }

    public void timeWorld(float delta) {
        worldTimer += Math.min(delta, 0.25f);
        if (worldTimer >= STEP_TIME) {
            worldTimer -= STEP_TIME;
            levelStage.update();
            reapWorld();
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    private void reapWorld() {
        for (int i = 0; i < corpses.size(); i++) {
            if (corpses.get(i) != null) {
                Object corpse = corpses.get(i);
                if (corpse instanceof ColoredCircle) {
                    ColoredCircle circle = (ColoredCircle) corpse;
                    if (circle.getListeners().size > 0) {
                        circle.removeListener(circle.getListeners().get(0));
                    }
                    levelStage.removeCircle(circle);
                    if (circle == levelStage.getLastTouch()) {
                        levelStage.setLastTouch(null);
                    }
                    circle.remove();
                    world.destroyBody(circle.getCircleBody().getBody());
                }
                corpses.remove(corpse);
            }
        }
    }

    private void drawBox2DDebug() {
        Matrix4 debugMatrix = new Matrix4(camera.combined);
        debugMatrix.scale(1f, 1f, 1f);
        debugRenderer.setDrawBodies(true);
        debugRenderer.render(this.world, debugMatrix);
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
}
