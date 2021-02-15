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
        ColoredCircle circleA = null;
        ColoredCircle circleB = null;
        if (contact.getFixtureA().getBody().getUserData() instanceof ColoredCircle) {
            circleA = (ColoredCircle) contact.getFixtureA().getBody().getUserData();
        }
        if (contact.getFixtureB().getBody().getUserData() instanceof ColoredCircle) {
            circleB = (ColoredCircle) contact.getFixtureB().getBody().getUserData();
        }
        if (circleA != null || circleB != null) {
            Object other = circleA != null ? contact.getFixtureB().getBody().getUserData() : contact.getFixtureA().getBody().getUserData();
            if (other instanceof Beam) {
                ColoredCircle circle = circleA != null ? circleA : circleB;
                if (((Beam) other).getColorType() != circle.getColorType()) {
                    contact.setEnabled(false);
                }
            }
            if (other instanceof Wall) {
                ColoredCircle circle = circleA != null ? circleA : circleB;
                if (((Wall) other).getColorType() == circle.getColorType()) {
                    contact.setRestitution(0f);
                }
            }
            if (circleA != null && circleB != null) {
                contact.setEnabled(false);
                if (!circleA.isDisabled() && !circleB.isDisabled()) {
                    if (circleA.getRadius() > circleB.getRadius()) {
                        circleA.interact(circleB);
                    } else {
                        circleB.interact(circleA);
                    }
                }
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
