package com.darkgran.smc.play;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import java.util.ArrayList;

public class TravelRayCallback implements RayCastCallback {
    private ArrayList<Vector2> points = new ArrayList<>();
    private ArrayList<Vector2> normals = new ArrayList<>();

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        points.add(new Vector2(point));
        normals.add(new Vector2(normal));
        return fraction;
    }

    public ArrayList<Vector2> getPoints() {
        return points;
    }

    public ArrayList<Vector2> getNormals() {
        return normals;
    }
}
