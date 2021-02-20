package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class ChainBoxBody {
    private final Body body;

    public ChainBoxBody(final World world, final Object object, float width, float height, float restitution, BodyDef.BodyType bodyType) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = bodyType;

        body = world.createBody(myBodyDef);
        body.setUserData(object);

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
        boxFixtureDef.restitution = restitution;
        body.createFixture(boxFixtureDef);

        body.setFixedRotation(false);

        shape.dispose();
    }

    public Body getBody() {
        return body;
    }

}
