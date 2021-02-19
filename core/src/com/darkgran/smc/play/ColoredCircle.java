package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;
import com.darkgran.smc.WorldScreen;

import static java.lang.Math.*;

public class ColoredCircle extends CircleActor {
    private ColorType colorType;
    private float direction;
    private float speed;
    private int growBuffer = 0;
    private boolean mergingAway = false;
    private boolean gone = false;
    private boolean freshShard = false;
    private SimpleCounter interactionLock = new SimpleCounter(false, 30, 0);
    private SimpleCounter breakLock = new SimpleCounter(false, 60, 0);

    public ColoredCircle(final LevelStage levelStage, float x, float y, int radius, float degrees, ColorType colorType) {
        super(levelStage, x, y, radius, BodyDef.BodyType.DynamicBody);
        this.colorType = colorType;
        this.direction = (float) (degrees*WorldScreen.DEGREES_TO_RADIANS);
        updateSpeedLimit();
        double speedX = speed * cos(direction);
        double speedY = speed * sin(direction);
        getCircleBody().getBody().setLinearVelocity((float) speedX, (float) speedY);
    }

    public void interact(ColoredCircle circle, InteractionType interactionType) {
        if (!isDisabled() && !circle.isDisabled()) {
            switch (interactionType) {
                case NONE:
                    break;
                case MERGER:
                    merge(circle);
                    break;
                case BREAKER:
                    splitInHalf(new Vector2(circle.getCircleBody().getBody().getPosition().x, circle.getCircleBody().getBody().getPosition().y));
                    break;
            }
        }
    }

    public void merge(ColoredCircle circle) {
        growBuffer += circle.getRadius();
        circle.unsign();
    }

    public void unsign() {
        mergingAway = true;
    }

    private void splitInHalf(Vector2 breakPoint) {
        if (canSplit()) {
            setLockedFromInteractions(true);
            setUnbreakable(true);
            int newRadius = getRadius()/2;
            addToGrow(-(getRadius()-newRadius));
            float newX = getCircleBody().getBody().getPosition().x + (breakPoint.x < getCircleBody().getBody().getPosition().x ? -newRadius*1.5f : newRadius*1.5f);
            float newY = getCircleBody().getBody().getPosition().y + (breakPoint.y < getCircleBody().getBody().getPosition().y ? -newRadius*1.5f : newRadius*1.5f);
            CircleInfo newCircle = new CircleInfo(newX, newY, (float) (getCircleBody().getBody().getAngle()/WorldScreen.DEGREES_TO_RADIANS), newRadius, colorType);
            getLevelStage().freshCircle(newCircle, false);
        }
    }

    public boolean canSplit() {
        return !interactionLock.isEnabled() && getRadius() >= LevelStage.MIN_RADIUS*2;
    }

    public void update() {
        Body body = getCircleBody().getBody();
        //Locks against interactions etc.
        interactionLock.update();
        breakLock.update();
        //Size Change
        if (mergingAway) {
            if (growBuffer > 0) {
                growBuffer -= LevelStage.RADIUS_CHANGE;
            } else if (getRadius()-LevelStage.RADIUS_CHANGE >= LevelStage.ACTUAL_MIN_RADIUS) {
                growBuffer = 0;
                setRadius(getRadius()-LevelStage.RADIUS_CHANGE);
            } else {
                gone = true;
            }
        //TODO DEBUG
        } else if (growBuffer > 0) {
            if (growBuffer > LevelStage.RADIUS_CHANGE) {
                growBuffer -= LevelStage.RADIUS_CHANGE;
                setRadius(getRadius()+LevelStage.RADIUS_CHANGE);
            } else {
                setRadius(getRadius()+growBuffer);
                growBuffer = 0;
                freshShard = false;
            }
        } else if (growBuffer < 0) {
            if (Math.abs(growBuffer) > LevelStage.RADIUS_CHANGE) {
                growBuffer += LevelStage.RADIUS_CHANGE;
                setRadius(getRadius()-LevelStage.RADIUS_CHANGE);
            } else {
                setRadius(getRadius()-growBuffer);
                growBuffer = 0;
            }
        }
        if (getRadius() >= LevelStage.MIN_RADIUS) {
            freshShard = false;
        }
        //Speed Cap
        double currentSpeed = Math.sqrt(Math.pow(body.getLinearVelocity().x, 2) + Math.pow(body.getLinearVelocity().y, 2));
        if ((float) currentSpeed != speed) {
            float angle = (float) Math.atan2(body.getLinearVelocity().y, body.getLinearVelocity().x);
            if (currentSpeed == 0) { angle += angle > PI ? -PI : PI; }
            double speedX = speed * cos(angle);
            double speedY = speed * sin(angle);
            body.setLinearVelocity((float) speedX, (float) speedY);
        }
        //Screen Edge
        if (body.getPosition().x-getRadius() >= WorldScreen.WORLD_WIDTH || body.getPosition().x+getRadius() <= 0 || body.getPosition().y-getRadius() >= WorldScreen.WORLD_HEIGHT || body.getPosition().y+getRadius() <= 0) {
            float newX = body.getPosition().x;
            float newY = body.getPosition().y;
            if (body.getPosition().x-getRadius() >= WorldScreen.WORLD_WIDTH) {
                newX = 0-getRadius();
            } else if (body.getPosition().x+getRadius() <= 0) {
                newX = WorldScreen.WORLD_WIDTH+getRadius();
            }
            if (body.getPosition().y-getRadius() >= WorldScreen.WORLD_HEIGHT) {
                newY = 0-getRadius();
            } else if (body.getPosition().y+getRadius() <= 0) {
                newY = WorldScreen.WORLD_HEIGHT+getRadius();
            }
            body.setTransform(newX, newY, body.getAngle());
        }
        //Actor position
        refreshActorBounds();
    }

    private void updateSpeedLimit() {
        speed = colorType.getSpeed() / (freshShard ? getRadius()+growBuffer : (Math.max(getRadius()/1000f, LevelStage.MIN_RADIUS/1000f)));
        if (speed < 0) { speed = 0; }
    }

    @Override
    public void refreshActorBounds() {
        this.setBounds((getCircleBody().getBody().getPosition().x-(getRadius()+LevelStage.COMFORT_RADIUS)), (getCircleBody().getBody().getPosition().y-(getRadius()+LevelStage.COMFORT_RADIUS)), ((getRadius()+LevelStage.COMFORT_RADIUS)*2), ((getRadius()+LevelStage.COMFORT_RADIUS)*2));
    }

    @Override
    public void setRadius(int radius) {
        if (radius < LevelStage.MIN_RADIUS && !mergingAway && !freshShard) { radius = LevelStage.MIN_RADIUS; }
        else if (radius < LevelStage.ACTUAL_MIN_RADIUS) { radius = LevelStage.ACTUAL_MIN_RADIUS; }
        super.setRadius(radius);
        if (getCircleBody().getBody().getFixtureList().size > 0) {
            Shape shape = getCircleBody().getBody().getFixtureList().get(0).getShape();
            shape.setRadius(radius);
        }
        MassData md = new MassData();
        md.mass = 0.1f*radius;
        getCircleBody().getBody().setMassData(md);
        refreshActorBounds();
        updateSpeedLimit();
    }

    public void addToGrow(float grow) {
        growBuffer += grow;
    }

    public boolean isLockedFromInteractions() {
        return interactionLock.isEnabled();
    }

    public void setLockedFromInteractions(boolean lockedFromInteractions) {
        this.interactionLock.setEnabled(lockedFromInteractions);
    }

    public boolean isUnbreakable() {
        return breakLock.isEnabled();
    }

    public void setUnbreakable(boolean unbreakable) {
        breakLock.setEnabled(unbreakable);
    }

    public ColorType getColorType() {
        return colorType;
    }

    public boolean isDisabled() {
        return isLockedFromInteractions() || mergingAway || gone;
    }

    public boolean isGone() {
        return gone;
    }

    public boolean isMergingAway() {
        return mergingAway;
    }

    public boolean isFreshShard() {
        return freshShard;
    }

    public void setFreshShard(boolean freshShard) {
        this.freshShard = freshShard;
    }

}
