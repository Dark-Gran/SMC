package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.*;

public class CircleBody {
    private final Body dynamicBody;

    public CircleBody(final World world, float x, float y, float radius) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.DynamicBody;

        dynamicBody = world.createBody(myBodyDef);
        dynamicBody.setTransform(x, y, 0f);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = circleShape;
        boxFixtureDef.density = 0.1f;
        boxFixtureDef.friction = 0f;
        boxFixtureDef.restitution = 0f;
        boxFixtureDef.isSensor = true;
        dynamicBody.createFixture(boxFixtureDef);

        dynamicBody.setFixedRotation(false);

    }

    public Body getDynamicBody() {
        return dynamicBody;
    }

}
