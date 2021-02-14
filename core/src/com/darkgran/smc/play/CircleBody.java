package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.*;

public class CircleBody {
    private final Body body;

    public CircleBody(final World world, final ColoredCircle coloredCircle, float x, float y, float radius) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(myBodyDef);
        body.setUserData(coloredCircle);

        body.setTransform(x, y, 0f);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = circleShape;
        boxFixtureDef.density = 0.1f;
        boxFixtureDef.friction = 0f;
        boxFixtureDef.restitution = 0f;

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
