package com.darkgran.smc.play;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ColoredCircle {
    private final double DEGREES_TO_RADIANS = Math.PI/180;
    private final CircleBody circleBody;
    private MainColor color;
    private float speed;
    private float angle;

    public ColoredCircle(final Level level, float x, float y, float radius, MainColor color) {
        circleBody = new CircleBody(level.getWorldScreen().getWorld(), x, y, radius);
        speed = 2f;
        angle = (float) (ThreadLocalRandom.current().nextInt(0, 360)*DEGREES_TO_RADIANS);
    }

    public void update() {
        double currentSpeed = Math.sqrt(Math.pow(circleBody.getDynamicBody().getLinearVelocity().x, 2) + Math.pow(circleBody.getDynamicBody().getLinearVelocity().y, 2));
        if ((float) currentSpeed != speed) {
            double x = speed * cos(angle);
            double y = speed * sin(angle);
            circleBody.getDynamicBody().setLinearVelocity((float) x, (float) y);
        }
    }

}
