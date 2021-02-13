package com.darkgran.smc.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class LevelLibrary {
    private Array<LevelInfo> levels;

    public void loadLocal(String path) {
        Json json = new Json();
        levels = json.fromJson(Array.class, LevelInfo.class, Gdx.files.internal(path) );
    }

    public LevelInfo getLevel(int id) {
        if (id >= 0) {
            for (LevelInfo levelInfo : levels) {
                if (levelInfo.getId() == id) {
                    return levelInfo;
                }
            }
        }
        return null;
    }

    public boolean levelExists(int id) {
        for (LevelInfo levelInfo : levels) {
            if (levelInfo.getId() == id) {
                return true;
            }
        }
        return false;
    }

}
