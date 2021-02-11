package com.darkgran.smc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldScreen implements Screen {
    //WorldSettings
    float FPS = 60.0f;
    float STEP_TIME = 1f / FPS;
    int VELOCITY_ITERATIONS = 15;
    int POSITION_ITERATIONS = 12;
    float WORLD_WIDTH = 9.6f;
    float WORLD_HEIGHT = 4.8f;

    private final Box2DDebugRenderer debugRenderer;
    public final OrthographicCamera camera;
    private final Viewport viewport;
    public World world;
    private float worldTimer = 0;

    public WorldScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2,0);
        Box2D.init();
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0, 0), true); //-15
    }

    private void addTestBody() { //temp
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.DynamicBody;

        Body dynamicBody = world.createBody(myBodyDef);
        dynamicBody.setTransform(4.8f, 2.4f, 0f);
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(WORLD_WIDTH/2-0.01f,WORLD_HEIGHT/2-0.01f);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = boxShape;
        boxFixtureDef.density = 1;
        dynamicBody.createFixture(boxFixtureDef);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        drawBox2DDebug();
        timeWorld(delta);
    }

    public void timeWorld(float delta) {
        worldTimer += Math.min(delta, 0.25f);
        if (worldTimer >= STEP_TIME) {
            worldTimer -= STEP_TIME;
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
        world.dispose();
        debugRenderer.dispose();
    }

    @Override
    public void show() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }
}
