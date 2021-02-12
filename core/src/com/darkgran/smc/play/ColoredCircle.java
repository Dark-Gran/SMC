package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.darkgran.smc.WorldScreen;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ColoredCircle extends Actor {
    private final double DEGREES_TO_RADIANS = Math.PI/180;
    private final float COMFORT_RADIUS = 0.1f;
    private final float ACTUAL_MIN_RADIUS = 0.001f;
    private final LevelStage levelStage;
    private final CircleBody circleBody;
    private ColorType colorType;
    private float radius;
    private float speed;
    private float angle;
    private float mergeBuffer = 0f;
    private boolean mergingAway = false;
    private boolean gone = false;

    public ColoredCircle(final LevelStage levelStage, float x, float y, float radius, float degrees, ColorType color) {
        this.levelStage = levelStage;
        if (radius < LevelStage.MIN_RADIUS) { radius = LevelStage.MIN_RADIUS; }
        this.setBounds(x-(radius+COMFORT_RADIUS), y-(radius+COMFORT_RADIUS), (radius+COMFORT_RADIUS)*2, (radius+COMFORT_RADIUS)*2);
        circleBody = new CircleBody(levelStage.getWorldScreen().getWorld(), this, x, y, radius);
        this.radius = radius;
        updateSpeed();
        this.angle = (float) (degrees*DEGREES_TO_RADIANS);
    }

    public void merge(ColoredCircle circle) {
        if (!circle.mergingAway) {
            mergeBuffer += circle.getRadius();
            circle.unsign();
        }
    }

    public void unsign() {
        mergingAway = true;
    }

    private void updateSpeed() {
        speed = 2f-radius*6;
        if (speed < 0) { speed = 0; }
    }

    public void update() {
        Body body = circleBody.getBody();
        //Merging
        if (mergingAway) {
            if (mergeBuffer > 0f) {
                mergeBuffer -= LevelStage.CHANGE_UP;
            } else if (getRadius()-LevelStage.CHANGE_UP >= ACTUAL_MIN_RADIUS) {
                mergeBuffer = 0f;
                setRadius(getRadius()-LevelStage.CHANGE_UP);
            } else {
                gone = true;
            }
        } else if (mergeBuffer > 0f) {
            if (mergeBuffer <= LevelStage.CHANGE_UP) {
                setRadius(getRadius()+mergeBuffer);
                mergeBuffer = 0f;
            } else {
                mergeBuffer -= LevelStage.CHANGE_UP;
                setRadius(getRadius()+LevelStage.CHANGE_UP);
            }
        }
        //Speed Cap
        double currentSpeed = Math.sqrt(Math.pow(body.getLinearVelocity().x, 2) + Math.pow(body.getLinearVelocity().y, 2));
        if ((float) currentSpeed != speed) {
            double speedX = speed * cos(angle);
            double speedY = speed * sin(angle);
            body.setLinearVelocity((float) speedX, (float) speedY);
        }
        //Screen Edge
        if (body.getPosition().x-radius > WorldScreen.WORLD_WIDTH || body.getPosition().x+radius < 0 || body.getPosition().y-radius > WorldScreen.WORLD_HEIGHT || body.getPosition().y+radius < 0) {
            float newX = body.getPosition().x;
            float newY = body.getPosition().y;
            if (body.getPosition().x > WorldScreen.WORLD_WIDTH) {
                newX = 0-radius;
            } else if (body.getPosition().x < 0) {
                newX = WorldScreen.WORLD_WIDTH+radius;
            }
            if (body.getPosition().y > WorldScreen.WORLD_HEIGHT) {
                newY = 0-radius;
            } else if (body.getPosition().y < 0) {
                newY = WorldScreen.WORLD_HEIGHT+radius;
            }
            body.setTransform(newX, newY, body.getAngle());
        }
        //Actor position
        refreshActorBounds();
    }

    public void drawShapes(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int segments = Math.round(radius*100);
        if (segments < 10) { segments = 10; }
        else if (segments > 100) { segments = 50; }
        shapeRenderer.circle(circleBody.getBody().getPosition().x, circleBody.getBody().getPosition().y, radius, segments);
        shapeRenderer.end();
    }

    private void refreshActorBounds() {
        this.setBounds(circleBody.getBody().getPosition().x-(radius+COMFORT_RADIUS), circleBody.getBody().getPosition().y-(radius+COMFORT_RADIUS), (radius+COMFORT_RADIUS)*2, (radius+COMFORT_RADIUS)*2);
    }

    public void setRadius(float radius) {
        if (radius < LevelStage.MIN_RADIUS && !mergingAway) { radius = LevelStage.MIN_RADIUS; }
        else if (radius < ACTUAL_MIN_RADIUS) { radius = ACTUAL_MIN_RADIUS; }
        this.radius = radius;
        refreshActorBounds();
        Shape shape = circleBody.getBody().getFixtureList().get(0).getShape();
        shape.setRadius(radius);
        updateSpeed();
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

    public boolean isGone() {
        return gone;
    }
}
