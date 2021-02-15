package com.darkgran.smc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.darkgran.smc.SaveMeCircles;

public class DesktopLauncher {
	public static void main (String[] arg) {
		//System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "SMC";
		config.width = 1920; //1600
		config.height = 960; //800
		config.foregroundFPS = 60;
		config.samples = 4; //in-future, probably use textures instead of shapes (so this might not be needed)
		//config.resizable = false;
		//config.fullscreen = false;
		new LwjglApplication(new SaveMeCircles(), config);
	}
}
