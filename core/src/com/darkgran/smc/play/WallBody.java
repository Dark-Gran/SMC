package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.darkgran.smc.WorldScreen;

public class WallBody {
    private final Body body;

    public WallBody(final World world, final Wall wall, float width, float height) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(myBodyDef);
        body.setUserData(wall);

        ChainShape shape = new ChainShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-width, -height);
        vertices[1] = new Vector2(-width, +height);
        vertices[2] = new Vector2(+width, +height);
        vertices[3] = new Vector2(+width, -height);

        shape.createLoop(vertices);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = shape;
        boxFixtureDef.density = 0.1f;
        boxFixtureDef.friction = 0.1f;
        boxFixtureDef.restitution = 1f;
        body.createFixture(boxFixtureDef);

        body.setFixedRotation(false);

        shape.dispose();
    }

    public Body getBody() {
        return body;
    }

}
