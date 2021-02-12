package com.darkgran.smc.play;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionListener implements ContactListener {
    private final LevelStage levelStage;

    public CollisionListener(LevelStage levelStage) {
        this.levelStage = levelStage;
    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        if (contact.getFixtureA().getBody().getUserData() instanceof ColoredCircle && contact.getFixtureB().getBody().getUserData() instanceof ColoredCircle) {
            contact.setEnabled(false);
            ColoredCircle circleA = (ColoredCircle) contact.getFixtureA().getBody().getUserData();
            ColoredCircle circleB = (ColoredCircle) contact.getFixtureB().getBody().getUserData();
            if (circleA.getRadius() > circleB.getRadius()) {
                circleA.merge(circleB);
            } else {
                circleB.merge(circleA);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
