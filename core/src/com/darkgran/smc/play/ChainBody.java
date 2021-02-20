package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class ChainBody {
    private final Body body;

    public ChainBody(final World world, final Object object, Vector2[] vertices, float restitution, BodyDef.BodyType bodyType, Vector2 massCenter) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = bodyType;

        body = world.createBody(myBodyDef);
        body.setUserData(object);

        ChainShape shape = new ChainShape();

        shape.createLoop(vertices);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = shape;
        boxFixtureDef.density = 0.1f;
        boxFixtureDef.friction = 0.1f;
        boxFixtureDef.restitution = restitution;
        body.createFixture(boxFixtureDef);

        if (massCenter != null) {
            MassData md = body.getMassData();
            md.center.set(massCenter);
            body.setMassData(md);
        }

        body.setFixedRotation(false);
        body.setGravityScale(0f);
        body.setLinearDamping(0f);
        body.setAngularDamping(0f);

        shape.dispose();
    }

    public Body getBody() {
        return body;
    }

}
