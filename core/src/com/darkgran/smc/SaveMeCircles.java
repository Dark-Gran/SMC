package com.darkgran.smc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputMultiplexer;

public class SaveMeCircles extends Game {
	private final InputMultiplexer inputMultiplexer = new InputMultiplexer();

	@Override
	public void create () {
		this.setScreen(new WorldScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {

	}

	public InputMultiplexer getInputMultiplexer() {
		return inputMultiplexer;
	}

}
