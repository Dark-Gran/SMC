package com.darkgran.smc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class IntroScreen implements Screen {

    private final SaveMeCircles game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Texture logo;
    private boolean active = false;
    private float alpha = 0;
    private boolean fadeDirection = true; //true in, false out

    private final static float INTRO_SPEED = 0.35f;

    public IntroScreen(final SaveMeCircles game) { //constructor
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SaveMeCircles.SW, SaveMeCircles.SH);
        viewport = new ExtendViewport(SaveMeCircles.SW, SaveMeCircles.SH, camera);
        viewport.apply();
        camera.position.set((float) SaveMeCircles.SW /2,(float) SaveMeCircles.SH /2,0);
        Gdx.input.setCursorCatched(true);
        logo = new Texture("images/DGLogo.jpg");
        delayAction(this::activate, 0.5f);
    }

    private void activate() { active = true; }

    private void endIntro() {
        Gdx.input.setInputProcessor(game.getInputMultiplexer());
        this.dispose();
        game.setScreen(new WorldScreen(game));
    }

    private void updateAlpha(float delta) {
        //if (delta > 0.03f) { delta = 0.03f; }
        alpha += fadeDirection ? (INTRO_SPEED *delta) : -(INTRO_SPEED *delta)*4;
        if (alpha >= 1) {
            fadeDirection = false;
            active = false;
            delayAction(this::activate, 0.9f);
        }
    }

    @Override
    public void render(float delta) {
        //control
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            endIntro();
        }

        //INTRO ANIMATION

        if (active) {
            if (alpha < 0 && !fadeDirection) { //animation over
                active = false;
                fadeDirection = true;
                delayAction(this::endIntro, 0.5f);
            } else {
                updateAlpha(delta);
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.setColor(1, 1, 1, (active || !fadeDirection) ? alpha : 0);

        game.batch.draw(logo, (float) (SaveMeCircles.SW / 2 - logo.getWidth() / 2), (float) (SaveMeCircles.SH / 2 - logo.getHeight() / 2));

        game.batch.end();
    }

    private void delayAction(Runnable runnable, float timerDelay) {
        Timer.schedule(new Timer.Task() {
            public void run() {
                if (runnable != null) { Gdx.app.postRunnable(runnable); }
            }
        }, timerDelay);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set((float) SaveMeCircles.SW/2,(float) SaveMeCircles.SH/2,0);
        camera.update();
    }

    @Override
    public void dispose() {
        logo.dispose();
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