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
import com.darkgran.smc.play.LevelStage;

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
    private LevelStage currentLevelStage;
    private final CollisionListener collisionListener;

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
        collisionListener = new CollisionListener();
        world.setContactListener(collisionListener);
        currentLevelStage = new LevelStage(this, viewport);
        smc.getInputMultiplexer().addProcessor(currentLevelStage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        currentLevelStage.act(delta);

        drawShapes();
        //drawBox2DDebug();

        timeWorld(delta);
    }

    private void drawShapes() {
        shapeRenderer.setProjectionMatrix(new Matrix4(camera.combined));
        currentLevelStage.drawShapes(shapeRenderer);
    }

    public void timeWorld(float delta) {
        worldTimer += Math.min(delta, 0.25f);
        if (worldTimer >= STEP_TIME) {
            worldTimer -= STEP_TIME;
            currentLevelStage.update();
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
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
        currentLevelStage.dispose();
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

}
