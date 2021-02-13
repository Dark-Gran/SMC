package com.darkgran.smc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SaveMeCircles extends Game {
	public final static float SW = 1920;
	public final static float SH = 960;
	private final InputMultiplexer inputMultiplexer = new InputMultiplexer();
	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		this.setScreen(new IntroScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public InputMultiplexer getInputMultiplexer() {
		return inputMultiplexer;
	}

}
