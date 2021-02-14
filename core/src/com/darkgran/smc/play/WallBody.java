package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.*;

public class WallBody {
    private final Body body;

    public WallBody(final World world, final Wall wall, float x, float y, float width, float height) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(myBodyDef);
        body.setUserData(wall);

        body.setTransform(x, y, 0f);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width, height);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = polygonShape;
        boxFixtureDef.density = 0.1f;
        boxFixtureDef.friction = 0.1f;
        boxFixtureDef.restitution = 1f;
        body.createFixture(boxFixtureDef);

        body.setFixedRotation(false);

        polygonShape.dispose();
    }

    public Body getBody() {
        return body;
    }

}
