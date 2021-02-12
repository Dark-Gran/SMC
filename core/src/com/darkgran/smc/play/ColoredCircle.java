package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.darkgran.smc.WorldScreen;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ColoredCircle {
    private final double DEGREES_TO_RADIANS = Math.PI/180;
    private final CircleBody circleBody;
    private MainColor color;
    private float radius;
    private float speed;
    private float angle;

    public ColoredCircle(final Level level, float x, float y, float radius, MainColor color) {
        circleBody = new CircleBody(level.getWorldScreen().getWorld(), x, y, radius);
        if (radius < 0.01f) { radius = 0.01f; }
        this.radius = radius;
        this.speed = 2f;
        this.angle = (float) (ThreadLocalRandom.current().nextInt(0, 360)*DEGREES_TO_RADIANS);
    }

    public void update() {
        Body body = circleBody.getBody();
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
    }

    public void drawShapes(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(circleBody.getBody().getPosition().x, circleBody.getBody().getPosition().y, radius, Math.round(radius*100));
        shapeRenderer.end();
    }

}
