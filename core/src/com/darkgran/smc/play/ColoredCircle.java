package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.darkgran.smc.WorldScreen;

import static java.lang.Math.*;

public class ColoredCircle extends Actor {
    private final LevelStage levelStage;
    private CircleBody circleBody;
    private ColorType colorType;
    private float radius;
    private float direction;
    private float speed;
    private float growBuffer = 0f;
    private boolean mergingAway = false;
    private boolean gone = false;
    private boolean freshShard = false;
    private SimpleCounter interactionLock = new SimpleCounter(false, 30, 0);
    private SimpleCounter breakLock = new SimpleCounter(false, 60, 0);

    public ColoredCircle(final LevelStage levelStage, float x, float y, float radius, float degrees, ColorType colorType) {
        this.colorType = colorType;
        this.levelStage = levelStage;
        circleBody = new CircleBody(levelStage.getWorldScreen().getWorld(), this, x, y, radius);
        refreshActorBounds();
        this.radius = radius;
        this.direction = (float) (degrees*WorldScreen.DEGREES_TO_RADIANS);
        updateSpeedLimit();
        double speedX = speed * cos(direction);
        double speedY = speed * sin(direction);
        circleBody.getBody().setLinearVelocity((float) speedX, (float) speedY);
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
            float newRadius = radius/2;
            addToGrow(-(radius-newRadius));
            float newX = circleBody.getBody().getPosition().x + (breakPoint.x < circleBody.getBody().getPosition().x ? -newRadius*1.5f : newRadius*1.5f);
            float newY = circleBody.getBody().getPosition().y + (breakPoint.y < circleBody.getBody().getPosition().y ? -newRadius*1.5f : newRadius*1.5f);
            CircleInfo newCircle = new CircleInfo(newX, newY, (float) (circleBody.getBody().getAngle()/WorldScreen.DEGREES_TO_RADIANS), newRadius, colorType);
            levelStage.freshCircle(newCircle, false);
        }
    }

    public boolean canSplit() {
        return !interactionLock.isEnabled() && radius >= LevelStage.MIN_RADIUS*2;
    }

    public void update() {
        Body body = circleBody.getBody();
        //Locks against interactions etc.
        interactionLock.update();
        breakLock.update();
        //Size Change
        if (mergingAway) {
            if (growBuffer > 0f) {
                growBuffer -= LevelStage.RADIUS_CHANGE;
            } else if (getRadius()-LevelStage.RADIUS_CHANGE >= LevelStage.ACTUAL_MIN_RADIUS) {
                growBuffer = 0f;
                setRadius(getRadius()-LevelStage.RADIUS_CHANGE);
            } else {
                gone = true;
            }
        //TODO DEBUG
        } else if (growBuffer > 0f) {
            if (growBuffer > LevelStage.RADIUS_CHANGE) {
                growBuffer -= LevelStage.RADIUS_CHANGE;
                setRadius(getRadius()+LevelStage.RADIUS_CHANGE);
            } else {
                setRadius(getRadius()+growBuffer);
                growBuffer = 0f;
                freshShard = false;
            }
        } else if (growBuffer < 0f) {
            if (Math.abs(growBuffer) > LevelStage.RADIUS_CHANGE) {
                growBuffer += LevelStage.RADIUS_CHANGE;
                setRadius(getRadius()-LevelStage.RADIUS_CHANGE);
            } else {
                setRadius(getRadius()-growBuffer);
                growBuffer = 0f;
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
        if (body.getPosition().x-radius >= WorldScreen.WORLD_WIDTH || body.getPosition().x+radius <= 0 || body.getPosition().y-radius >= WorldScreen.WORLD_HEIGHT || body.getPosition().y+radius <= 0) {
            float newX = body.getPosition().x;
            float newY = body.getPosition().y;
            if (body.getPosition().x-radius >= WorldScreen.WORLD_WIDTH) {
                newX = 0-radius;
            } else if (body.getPosition().x+radius <= 0) {
                newX = WorldScreen.WORLD_WIDTH+radius;
            }
            if (body.getPosition().y-radius >= WorldScreen.WORLD_HEIGHT) {
                newY = 0-radius;
            } else if (body.getPosition().y+radius <= 0) {
                newY = WorldScreen.WORLD_HEIGHT+radius;
            }
            body.setTransform(newX, newY, body.getAngle());
        }
        //Actor position
        refreshActorBounds();
    }

    private void updateSpeedLimit() {
        speed = colorType.getSpeed() / (freshShard ? radius+growBuffer : (Math.max(radius, LevelStage.MIN_RADIUS)));
        if (speed < 0) { speed = 0; }
    }

    private void refreshActorBounds() {
        this.setBounds((circleBody.getBody().getPosition().x-(radius+LevelStage.COMFORT_RADIUS)), (circleBody.getBody().getPosition().y-(radius+LevelStage.COMFORT_RADIUS)), ((radius+LevelStage.COMFORT_RADIUS)*2), ((radius+LevelStage.COMFORT_RADIUS)*2));
    }

    public void setRadius(float radius) {
        if (radius < LevelStage.MIN_RADIUS && !mergingAway && !freshShard) { radius = LevelStage.MIN_RADIUS; }
        else if (radius < LevelStage.ACTUAL_MIN_RADIUS) { radius = LevelStage.ACTUAL_MIN_RADIUS; }
        this.radius = radius;
        if (circleBody.getBody().getFixtureList().size > 0) {
            Shape shape = circleBody.getBody().getFixtureList().get(0).getShape();
            shape.setRadius(radius);
        }
        MassData md = new MassData();
        md.mass = 0.1f*radius;
        circleBody.getBody().setMassData(md);
        refreshActorBounds();
        updateSpeedLimit();
    }

    public void drawShapes(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(this.colorType.getColor());
        int segments = Math.round(radius*200);
        if (segments < 10) { segments = 10; }
        else if (segments > 100) { segments = 50; }
        shapeRenderer.circle(circleBody.getBody().getPosition().x, circleBody.getBody().getPosition().y, radius, segments);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();
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

    public float getRadius() {
        return radius;
    }

    public ColorType getColorType() {
        return colorType;
    }

    public CircleBody getCircleBody() {
        return circleBody;
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

    public void setCircleBody(CircleBody circleBody) {
        this.circleBody = circleBody;
    }
}
