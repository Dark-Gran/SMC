package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.darkgran.smc.WorldScreen;

public class RotatableTriangle extends RotatableChainObject implements Spriter {
    private final Sprite sprite;

    public RotatableTriangle(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, Texture texture) {
        super(levelStage, x, y, vertices, angle, 0f);
        sprite = new Sprite(texture);
        updateSprite();
    }

    @Override
    public void createChainBody(LevelStage levelStage, float x, float y, Vector2[] vertices, float angle, float restitution, BodyDef.BodyType bodyType) {
        if (vertices.length == 3) {
            float[] polygon = new float[vertices.length * 2];
            int i = 0;
            for (Vector2 vertex : vertices) {
                polygon[i] = vertex.x;
                polygon[i + 1] = vertex.y;
                i += 2;
            }

            Vector2 boxMid = new Vector2(vertices[0].x+vertices[2].x/2, vertices[0].y+vertices[1].y/2);
            for (Vector2 vertex : vertices) {
                vertex.x -= boxMid.x;
                vertex.y -= boxMid.y;
            }
            setChainBody(new ChainBody(levelStage.getWorldScreen().getWorld(), this, vertices, restitution, bodyType, boxMid));
            getChainBody().getBody().setTransform(x, y, angle);
        } else {
            super.createChainBody(levelStage, x, y, vertices, angle, restitution, bodyType);
        }
    }

    @Override
    public void updateSprite() {
        sprite.setPosition(getChainBody().getBody().getPosition().x*WorldScreen.PPM-sprite.getWidth()/2, getChainBody().getBody().getPosition().y*WorldScreen.PPM-sprite.getHeight()/2);
        sprite.setRotation((float) (getChainBody().getBody().getAngle() / WorldScreen.DEGREES_TO_RADIANS));
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

}
