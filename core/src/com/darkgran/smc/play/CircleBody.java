package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.*;

public class CircleBody {
    private final Body body;

    public CircleBody(final World world, float x, float y, float radius) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(myBodyDef);
        body.setTransform(x, y, 0f);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = circleShape;
        boxFixtureDef.density = 0.1f;
        boxFixtureDef.friction = 0f;
        boxFixtureDef.restitution = 0f;
        boxFixtureDef.filter.categoryBits = 1; //"I am a..."
        boxFixtureDef.filter.maskBits = 1; //"I will collide with..."
        body.createFixture(boxFixtureDef);

        body.setFixedRotation(false);

    }

    public Body getBody() {
        return body;
    }

}
