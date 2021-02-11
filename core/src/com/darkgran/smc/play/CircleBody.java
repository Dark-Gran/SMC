package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.*;

public class CircleBody {

    public CircleBody(final World world) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.DynamicBody;

        Body dynamicBody = world.createBody(myBodyDef);
        dynamicBody.setTransform(4.8f, 2.4f, 0f);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.2f);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = circleShape;
        boxFixtureDef.density = 10f;
        boxFixtureDef.friction = 0.5f;
        boxFixtureDef.restitution = 0.5f;
        dynamicBody.createFixture(boxFixtureDef);

        dynamicBody.setFixedRotation(false);

    }

}
