package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.*;

public class CircleBody {
    private final Body body;

    public CircleBody(final World world, final Object obj, float x, float y, double radius, BodyDef.BodyType bodyType) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = bodyType;

        body = world.createBody(myBodyDef);
        body.setUserData(obj);

        body.setTransform(x, y, 0f);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius((float) radius);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = circleShape;
        boxFixtureDef.density = 0.1f;
        boxFixtureDef.friction = 0f;
        boxFixtureDef.restitution = 1f;

        MassData md = new MassData();
        md.mass = 0.1f*(float) radius;
        body.setMassData(md);
        //md.I = 1;
        //md.center = body.getLocalCenter();

        body.createFixture(boxFixtureDef);

        body.setFixedRotation(false);
        body.setGravityScale(0f);
        body.setLinearDamping(0f);
        body.setAngularDamping(0f);

        circleShape.dispose();
    }

    public Body getBody() {
        return body;
    }

}
