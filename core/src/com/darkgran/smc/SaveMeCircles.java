package com.darkgran.smc;

import com.badlogic.gdx.Game;

public class SaveMeCircles extends Game {
	
	@Override
	public void create () {
		this.setScreen(new WorldScreen());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {

	}

}
