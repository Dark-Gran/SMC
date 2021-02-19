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

                InteractionType interactionType = getInteractionType(circleA, circleB);
                CollisionType collisionType = getCollisionType(circleA.getColorType(), circleB.getColorType(), interactionType);
                switch (collisionType) {
                    case IGNORED:
                        contact.setEnabled(false);
                        break;
                    case SOFT:
                        contact.setRestitution(0f);
                        break;
                }
                if (contact.getFixtureA().getBody().getWorld() == levelStage.getWorldScreen().getWorld() && contact.getFixtureB().getBody().getWorld() == levelStage.getWorldScreen().getWorld()) {
                    if (!circleA.isDisabled() && !circleB.isDisabled()) {
                        if (circleA.getRadius() > circleB.getRadius()) {
                            circleA.interact(circleB, interactionType);
                        } else {
                            circleB.interact(circleA, interactionType);
                        }
                    }
                }
            }
        }
    }

    private InteractionType getInteractionType(ColoredCircle circleA, ColoredCircle circleB) {
        if ((circleA.isUnbreakable() && circleB.getColorType() == ColorType.RED) || (circleB.isUnbreakable() && circleA.getColorType() == ColorType.RED)) {
            return InteractionType.PASS;
        }
        if (circleA.isLockedFromInteractions() || circleB.isLockedFromInteractions()) {
            return InteractionType.NONE;
        }
        if (circleA.getColorType() == circleB.getColorType()) {
            return InteractionType.MERGER;
        }
        if ((circleA.getColorType() == ColorType.RED && circleA.getRadius() < circleB.getRadius() && circleB.canSplit()) || (circleB.getColorType() == ColorType.RED && circleB.getRadius() < circleA.getRadius() && circleA.canSplit())) {
            return InteractionType.BREAKER;
        }
        return InteractionType.NONE;
    }

    private CollisionType getCollisionType(ColorType typeA, ColorType typeB, InteractionType interactionType) {
        if (interactionType == InteractionType.MERGER || interactionType == InteractionType.PASS || interactionType == InteractionType.BREAKER) {
            return CollisionType.IGNORED;
        }
        ColorType other;
        if (typeA == ColorType.WHITE || typeB == ColorType.WHITE) {
            other = typeA == ColorType.WHITE ? typeB : typeA;
            switch (other) {
                case BLUE:
                    return CollisionType.SOFT;
                case GREEN:
                case RED:
                    return CollisionType.STANDARD;
            }
        }
        if (typeA == ColorType.BLUE || typeB == ColorType.BLUE) {
            other = typeA == ColorType.BLUE ? typeB : typeA;
            switch (other) {
                case GREEN:
                    return CollisionType.STANDARD;
                case RED:
                    return CollisionType.SOFT;
            }
        }
        /*if (typeA == ColorType.GREEN || typeB == ColorType.GREEN) {
            other = typeA == ColorType.GREEN ? typeB : typeA;
            switch (other) {
                case RED:
                    return CollisionType.STANDARD;
            }
        }*/
        return CollisionType.STANDARD;
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
