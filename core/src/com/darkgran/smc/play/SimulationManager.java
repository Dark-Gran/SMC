package com.darkgran.smc.play;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.darkgran.smc.WorldScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

public class SimulationManager {
    private final WorldScreen worldScreen;
    private final int VELOCITY_ITERATIONS;
    private final int POSITION_ITERATIONS;
    private final float STEP_TIME;
    private World worldSimulation;

    public SimulationManager(WorldScreen worldScreen, World worldSimulation, int VELOCITY_ITERATIONS, int POSITION_ITERATIONS, float STEP_TIME) {
        this.worldScreen = worldScreen;
        this.worldSimulation = worldSimulation;
        this.VELOCITY_ITERATIONS = VELOCITY_ITERATIONS;
        this.POSITION_ITERATIONS = POSITION_ITERATIONS;
        this.STEP_TIME = STEP_TIME;
    }

    public void resetSimulation(CollisionListener collisionListener, World copyWorld) {
        worldSimulation = new World(new Vector2(0, 0), false);;
        worldSimulation.setContactListener(collisionListener);
        Array<Body> bodies = new Array<>();
        copyWorld.getBodies(bodies);
        for (Body body : bodies) {
            copyBody(body, worldSimulation);
        }
    }

    public void drawSimulation(ShapeRenderer shapeRenderer, CollisionListener collisionListener, World copyWorld, boolean limitedDraw, Box2DDebugRenderer debugRenderer, Camera debugCamera) {
        resetSimulation(collisionListener, copyWorld);
        Array<Body> bodies;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        markStuckCircles();
        for (int i = 0; i <= 180; i++) {
            bodies = new Array<>();
            worldSimulation.getBodies(bodies);
            for (Body body : bodies) {
                float rad = 2f;
                if (body.getUserData() instanceof ColoredCircle) {
                    ColoredCircle circle = (ColoredCircle) body.getUserData();
                    if (!circle.isStuck()) {
                        applyCircleUpdate(circle, body);
                        if (!limitedDraw || Math.pow((body.getPosition().x - worldScreen.getMouseInWorld2D().x), 2) + Math.pow((body.getPosition().y - worldScreen.getMouseInWorld2D().y), 2) < Math.pow(rad, 2)) {
                            if (i % 10 == 0 && !circle.isFreshShard() && !circle.isMergingAway() && !circle.isGone()) {
                                shapeRenderer.setColor(circle.getColorType().getColor().r, circle.getColorType().getColor().g, circle.getColorType().getColor().b, 0.7f);
                                shapeRenderer.circle(body.getPosition().x, body.getPosition().y, 0.01f, 10);
                            }
                        }
                    } else {
                        body.getFixtureList().get(0).setSensor(true);
                    }
                }
            }
            worldSimulation.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();
        //debugRenderer.setDrawBodies(true);
        //debugRenderer.render(worldSimulation, new Matrix4(debugCamera.combined));
    }

    private void markStuckCircles() {
        HashMap<ColoredCircle, ArrayList<Vector2>> circles = new HashMap<>();
        for (Contact contact : worldScreen.getWorld().getContactList()) {
            if (contact.isTouching() && contact.getFixtureA().getBody().getUserData() != contact.getFixtureB().getBody().getUserData()) {
                //atm checking only for circle vs non-circle:
                if ((contact.getFixtureA().getBody().getUserData() instanceof ColoredCircle && !(contact.getFixtureB().getBody().getUserData() instanceof ColoredCircle)) || (contact.getFixtureB().getBody().getUserData() instanceof ColoredCircle && !(contact.getFixtureA().getBody().getUserData() instanceof ColoredCircle))) {
                    WorldManifold manifold = contact.getWorldManifold();
                    //if (manifold.getPoints().length > 0) {
                        ColoredCircle circle = contact.getFixtureA().getBody().getUserData() instanceof ColoredCircle ? (ColoredCircle) contact.getFixtureA().getBody().getUserData() : (ColoredCircle) contact.getFixtureB().getBody().getUserData();
                        if (!circle.isStuck()) {
                            Vector2[] checkPoints = getExtendedMidPoints(circle.getCircleBody().getBody().getPosition(), (float) circle.getRadius() / 4);
                            Body otherBody = circle.getCircleBody().getBody() == contact.getFixtureA().getBody() ? contact.getFixtureB().getBody() : contact.getFixtureA().getBody();
                            Array<Vector2> polygon = new Array();
                            if (otherBody.getUserData() instanceof PlayerCircle) {
                                boolean overlap = false;
                                for (float f : manifold.getSeparations()) { //standard overlap
                                    if (f < 0f) {
                                        overlap = true;
                                    }
                                }
                                circle.setStuck(overlap);
                            } else if (!otherBody.getFixtureList().get(0).isSensor() && otherBody.getUserData() instanceof ChainBoxBodyObject) {
                                ChainBoxBodyObject cbo = (ChainBoxBodyObject) otherBody.getUserData();
                                polygon.add(new Vector2(otherBody.getPosition().x - cbo.getWidth(), otherBody.getPosition().y - cbo.getHeight()));
                                polygon.add(new Vector2(otherBody.getPosition().x - cbo.getWidth(), otherBody.getPosition().y + cbo.getHeight()));
                                polygon.add(new Vector2(otherBody.getPosition().x + cbo.getWidth(), otherBody.getPosition().y + cbo.getHeight()));
                                polygon.add(new Vector2(otherBody.getPosition().x + cbo.getWidth(), otherBody.getPosition().y - cbo.getHeight()));
                                for (Vector2 checkPoint : checkPoints) {
                                    if (Intersector.isPointInPolygon(polygon, checkPoint)) {
                                        circle.setStuck(true);
                                    }
                                }
                                if (!circle.isStuck()) {
                                    ArrayList<Vector2> breakPoints = new ArrayList<>();
                                    for (Vector2 point : manifold.getPoints()) {
                                        if (!breakPoints.contains(point)) {
                                            breakPoints.add(point);
                                        }
                                    }
                                    while (breakPoints.contains(new Vector2(0, 0))) {
                                        breakPoints.remove(new Vector2(0, 0));
                                    }
                                    if (circles.containsKey(circle)) {
                                        circles.get(circle).addAll(breakPoints);
                                    } else {
                                        circles.put(circle, breakPoints);
                                    }
                                }
                            }
                        }
                    //}
                }
            }
        }
        for (Map.Entry<ColoredCircle, ArrayList<Vector2>> entry : circles.entrySet()) {
            ColoredCircle circle = entry.getKey();
            if (!circle.isStuck() && worldScreen.getLevelStage().getPlayerCircle() != null) {
                //in-future: check for other bodies than PlayerCircle that may get "teleported" inside a circle
                //in-future: precise compare? (instead of AABBs comparison?)
                Rectangle cR = new Rectangle((float) (circle.getCircleBody().getBody().getPosition().x-circle.getRadius()), (float) (circle.getCircleBody().getBody().getPosition().y-circle.getRadius()), (float) circle.getRadius()*2, (float) circle.getRadius()*2);
                if (cR.contains(worldScreen.getLevelStage().getPlayerCircle().getCircleBody().getBody().getPosition().x, worldScreen.getLevelStage().getPlayerCircle().getCircleBody().getBody().getPosition().y)) {
                    circle.setStuck(true);
                }
            }
            if (!circle.isStuck()) {
                ArrayList<Vector2> breakPoints = entry.getValue();
                if (breakPoints.size() > 1) {
                    Array<Vector2> polygon = new Array();
                    for (int i = 0; i < breakPoints.size(); i++) {
                        Vector2 newVector = new Vector2(breakPoints.get(i).x, breakPoints.get(i).y);
                        if (!(polygon.contains(newVector, false))) {
                            polygon.add(newVector);
                        }
                    }
                    if (polygon.size == 2) {
                        polygon = rectFromLine(polygon, 0.05f);
                    }
                    Vector2[] checkPoints = getExtendedMidPoints(circle.getCircleBody().getBody().getPosition(), (float) circle.getRadius() / 4);
                    for (Vector2 checkPoint : checkPoints) {
                        if (Intersector.isPointInPolygon(polygon, checkPoint)) {
                            circle.setStuck(true);
                        }
                    }
                }
            }
        }
    }

    private Vector2[] getExtendedMidPoints(Vector2 mid, float offset) {
        Vector2[] checkPoints = new Vector2[5];
        checkPoints[0] = mid;
        checkPoints[1] = new Vector2(mid.x + offset, mid.y + offset);
        checkPoints[2] = new Vector2(mid.x - offset, mid.y - offset);
        checkPoints[3] = new Vector2(mid.x - offset, mid.y + offset);
        checkPoints[4] = new Vector2(mid.x + offset, mid.y - offset);
        return checkPoints;
    }

    private void copyBody(Body body, World world) {
        Object obj = body.getUserData();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = obj instanceof GhostCircle ? BodyDef.BodyType.StaticBody : body.getType();

        Body newBody = world.createBody(bodyDef);
        newBody.setUserData(obj);
        newBody.setTransform(body.getPosition(), body.getAngle());
        newBody.setLinearVelocity(body.getLinearVelocity());

        Fixture fixture = body.getFixtureList().get(0);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = fixture.getShape();
        fixtureDef.density = fixture.getDensity();
        fixtureDef.restitution = fixture.getRestitution();
        fixtureDef.friction = fixture.getFriction();

        newBody.createFixture(fixtureDef);

        newBody.getFixtureList().get(0).setDensity(body.getFixtureList().get(0).getDensity());
        newBody.resetMassData();
        MassData md = new MassData();
        md.mass = body.getMassData().mass;
        newBody.setMassData(md);

        newBody.getFixtureList().get(0).setSensor(!(body.getUserData() instanceof GhostCircle) && body.getFixtureList().get(0).isSensor());

        newBody.setFixedRotation(body.isFixedRotation());
        newBody.setGravityScale(body.getGravityScale());
        newBody.setLinearDamping(body.getLinearDamping());
        newBody.setAngularDamping(body.getAngularDamping());
    }

    private double applyBodyRadius(Body body, double radius, boolean mergingAway, boolean freshShard, ColorType colorType) {
        if (radius < colorType.getMinRadius() && !mergingAway && !freshShard) { radius = colorType.getMinRadius(); }
        else if (radius < LevelStage.ACTUAL_MIN_RADIUS) { radius = LevelStage.ACTUAL_MIN_RADIUS; }
        if (body.getFixtureList().size > 0) {
            Shape shape = body.getFixtureList().get(0).getShape();
            shape.setRadius((float) radius);
        }
        MassData md = new MassData();
        md.mass = 0.1f*(float) radius;
        body.setMassData(md);
        body.resetMassData();
        return radius;
    }

    private void applyCircleUpdate(ColoredCircle circle, Body body) {
        //Grow
        boolean mA = circle.isMergingAway();
        double gB = circle.getGrowBuffer();
        boolean g = circle.isGone();
        boolean fS = circle.isFreshShard();
        double radius = circle.getRadius();
        if (mA) {
            if (gB > 0) {
                gB -= LevelStage.RADIUS_CHANGE;
            } else if (circle.getRadius()-LevelStage.RADIUS_CHANGE >= LevelStage.ACTUAL_MIN_RADIUS) {
                gB = 0;
                radius = applyBodyRadius(body, circle.getRadius()-LevelStage.RADIUS_CHANGE, mA, fS, circle.getColorType());
            } else {
                g = true;
            }
        } else if (gB > 0) {
            if (gB > LevelStage.RADIUS_CHANGE) {
                gB -= LevelStage.RADIUS_CHANGE;
                radius = applyBodyRadius(body, circle.getRadius()+LevelStage.RADIUS_CHANGE, mA, fS, circle.getColorType());
            } else {
                radius = applyBodyRadius(body, circle.getRadius()+gB, mA, fS, circle.getColorType());
                gB = 0;
                fS = false;
            }
        } else if (gB < 0) {
            if (Math.abs(gB) > LevelStage.RADIUS_CHANGE) {
                gB += LevelStage.RADIUS_CHANGE;
                radius = applyBodyRadius(body, circle.getRadius()-LevelStage.RADIUS_CHANGE, mA, fS, circle.getColorType());
            } else {
                radius = applyBodyRadius(body, circle.getRadius()+gB, mA, fS, circle.getColorType());
                gB = 0;
            }
        }
        if (radius >= circle.getColorType().getMinRadius()) {
            fS = false;
        }
        //Speed
        float speed = ColoredCircle.getSpeedLimit(circle.getColorType().getSpeed(), radius, fS, gB, circle.getColorType());
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
            double newX = body.getPosition().x;
            double newY = body.getPosition().y;
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
            body.setTransform((float) newX, (float) newY, body.getAngle());
        }
    }

    private Array<Vector2> rectFromLine(Array<Vector2> line, float width) {
        Array<Vector2> polygon = new Array<>();
        for (Vector2 vector : line) {
            polygon.add(vector.cpy());
        }
        if (polygon.size == 2) {
            double angle = atan2(polygon.get(0).y-polygon.get(1).y,polygon.get(0).x-polygon.get(1).x);
            Vector2 point0 = new Vector2();
            point0.x = (float) (polygon.get(0).x + (width/2)*sin(angle));
            point0.y = (float) (polygon.get(0).y + (width/2)*cos(angle));
            Vector2 point1 = new Vector2();
            point1.x = (float) (polygon.get(0).x + (width/2)*sin(angle-PI));
            point1.y = (float) (polygon.get(0).y + (width/2)*cos(angle-PI));
            Vector2 point2 = new Vector2();
            point2.x = (float) (polygon.get(1).x + (width/2)*sin(angle));
            point2.y = (float) (polygon.get(1).y + (width/2)*cos(angle));
            Vector2 point3 = new Vector2();
            point3.x = (float) (polygon.get(1).x + (width/2)*sin(angle-PI));
            point3.y = (float) (polygon.get(1).y + (width/2)*cos(angle-PI));
            polygon.clear();
            polygon.add(point0);
            polygon.add(point1);
            polygon.add(point2);
            polygon.add(point3);
        }
        return polygon;
    }

}
