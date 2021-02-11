package com.darkgran.smc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.darkgran.smc.SaveMeCircles;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "SMC";
		config.width = 1280;
		config.height = 640;
		config.foregroundFPS = 60;
		//config.resizable = false;
		//config.fullscreen = false;
		new LwjglApplication(new SaveMeCircles(), config);
	}
}
