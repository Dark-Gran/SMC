package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.*;

public class CircleBody {

    public CircleBody(final World world) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.DynamicBody;

        Body dynamicBody = world.createBody(myBodyDef);
        dynamicBody.setTransform(4.8f, 2.4f, 0f);
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(2f,2f);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = boxShape;
        boxFixtureDef.density = 1;
        dynamicBody.createFixture(boxFixtureDef);
    }

}
